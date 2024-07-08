package com.hermes.controller;

import com.hermes.annotation.NeedTokenApi;
import com.hermes.common.PageInfo;
import com.hermes.common.Result;
import com.hermes.common.constant.JwtTokenClaimConstant;
import com.hermes.common.enum_type.UserRole;
import com.hermes.common.enum_type.VerifyTarget;
import com.hermes.common.exception.ParamValidFailedException;
import com.hermes.common.exception.UserLoginFailedException;
import com.hermes.common.exception.UserRoleCheckFailedException;
import com.hermes.common.exception.VerifyCodeCheckFailedException;
import com.hermes.common.helper.BaseContextHelper;
import com.hermes.common.helper.JwtTokenHelper;
import com.hermes.common.helper.SecurityHelper;
import com.hermes.dto.PageQueryDTO;
import com.hermes.dto.UserInfoDTO;
import com.hermes.entity.UserInfo;
import com.hermes.service.SecretKeyService;
import com.hermes.service.StringCacheService;
import com.hermes.service.UserService;
import com.hermes.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Api(value = "user controller", tags = "user")
public class UserController {
    @Value("${hermes.service.jwt-token.token-ttl}")
    private Long tokenTtl;
    @Autowired
    private UserService userService;
    @Autowired
    private SecretKeyService secretKeyService;
    @Autowired
    private StringCacheService stringCacheService;
    @Autowired
    private RestTemplate selfCallRestTemplate;

    private String safeGetCurrentUid() {
        String currentUid = BaseContextHelper.getCurrentUid();
        if (currentUid == null) {
            throw new ParamValidFailedException("jwt token can not be null");
        }
        return currentUid;
    }

    @GetMapping("/details")
    @NeedTokenApi
    @ApiOperation(value = "query users' detail by condition",
            notes = "Fuzzy String matching is not currently implemented in the mapper layer")
    public Result getUserDetailsByCond(
            @RequestParam(name = "email_account", required = false) String emailAccountLike,
            @RequestParam(name = "user_name", required = false) String userNameLike,
            @RequestParam(name = "user_role", required = false) Integer userRole,
            @RequestParam(name = "is_deleted", required = false) Boolean isDeleted,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page_size", required = false) Integer pageSize
    ) {
        QueryUserDetailsVO queryUserDetailsVO = new QueryUserDetailsVO();
        queryUserDetailsVO.setUserNameLike(userNameLike);
        queryUserDetailsVO.setUserRole(userRole);
        queryUserDetailsVO.setEmailAccountLike(emailAccountLike);
        queryUserDetailsVO.setIsDeleted(isDeleted);
        queryUserDetailsVO.setPage(page);
        queryUserDetailsVO.setPageSize(pageSize);

        return new HandlerTemplate<QueryUserDetailsVO, Result>() {

            @Override
            protected void validParam(QueryUserDetailsVO request) {
                Integer userRole = request.getUserRole();
                if (userRole != null) {
                    try {
                        UserRole.valueOf(userRole);
                    } catch (IllegalArgumentException e) {
                        throw new ParamValidFailedException("invalid user role code: " + userRole);
                    }
                }
                PageQueryVO.validatePageQueryVO(request);
                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER)) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not query user details", currentUid));
                }
            }

            @Override
            protected Result doProcess(QueryUserDetailsVO request) {
                PageInfo pageInfo = PageInfo.build(request.getPage(), request.getPageSize());
                UserInfo queryConds = UserInfo.builder().
                        userRole(request.getUserRole()).
                        userName(request.getUserNameLike()).
                        emailAccount(request.getEmailAccountLike()).
                        isDeleted(request.getIsDeleted()).build();

                /* NOTE: Fuzzy String matching is not currently implemented in the mapper layer */
                PageQueryDTO<UserInfoDTO> userInfoDTOPageQueryDTO = userService.pageQueryUserInfosByConds(queryConds, pageInfo);
                return Result.Success(userInfoDTOPageQueryDTO);
            }
        }.process(queryUserDetailsVO);
    }

    @GetMapping("/profile")
    @NeedTokenApi
    @ApiOperation(value = "query current user details")
    public Result getUserDetails() {
        return new HandlerTemplate<Object, Result>() {
            @Override
            protected Result doProcess(Object request) {
                String currentUid = BaseContextHelper.getCurrentUid();
                UserInfoDTO userInfoDTO = userService.queryUserInfoByUid(currentUid);
                return Result.Success(userInfoDTO);
            }
        }.process(null);
    }

    @PutMapping("/profile/password")
    @NeedTokenApi
    @ApiOperation(value = "change current user password")
    public Result updateUserPassword(
            @RequestBody UpdateUserPasswordVO updateUserPasswordVO
            ) {
        return new HandlerTemplate<UpdateUserPasswordVO, Result>() {
            @Override
            protected void validParam(UpdateUserPasswordVO request) {
                String currentUid = safeGetCurrentUid();
                request.setUid(currentUid);

                String oldPassword = request.getOldPassword();
                String newPassword = request.getNewPassword();
                if (oldPassword == null || newPassword == null) {
                    throw new ParamValidFailedException("transport password can not be null, oldPassword = " + oldPassword + "newPassword = " + newPassword);
                }

                String publicKeyVersion = request.getPublicKeyVersion();
                PrivateKey privateKey = secretKeyService.getPrivateKeyAndCheckVersion(publicKeyVersion);
                if (privateKey == null) {
                    throw new ParamValidFailedException("public key is expired, please reacquire public key, old key-version = " + publicKeyVersion);
                }
                request.setPrivateKey(privateKey);
            }

            @Override
            protected Result doProcess(UpdateUserPasswordVO request) {
                String currentUid = request.getUid();
                UserInfoDTO oldUserInfo = userService.queryUserInfoByUid(currentUid);
                String decryptedOldPassword = secretKeyService.decryptData(request.getPrivateKey(), request.getOldPassword());

                if (userService.checkPassword(oldUserInfo.getEmailAccount(), decryptedOldPassword)) {
                    String decryptedNewPassword = secretKeyService.decryptData(request.getPrivateKey(), request.getNewPassword());
                    String newPassword = SecurityHelper.encodeData(decryptedNewPassword);
                    UserInfo newUserInfo = UserInfo.builder().uid(oldUserInfo.getUid()).password(newPassword).build();
                    userService.updateUser(newUserInfo);
                    return Result.Success();
                }
                return new Result(Result.CODE_PASSWORD_CHECK_FAILED, String.format("user <%s> password check failed, please re-enter", currentUid));
            }
        }.process(updateUserPasswordVO);
    }

    @PostMapping("/profile/email_account")
    @NeedTokenApi
    @ApiOperation(value = "request to change current user email-account",
            notes = "Returning success on this interface only means that that the change request was successfully issued.")
    public Result sendUpdateUserEmailAccount(
            @RequestBody UpdateUserEmailAccountVO updateUserEmailAccountVO
    ) {
        return new HandlerTemplate<UpdateUserEmailAccountVO, Result>() {
            @Override
            protected void validParam(UpdateUserEmailAccountVO request) {
                String emailAccount = request.getNewEmailAccount();
                String currentUid = safeGetCurrentUid();
                request.setUid(currentUid);

                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!emailAccount.matches(emailRegex)) {
                    throw new ParamValidFailedException("email account format error: " + emailAccount);
                }
            }

            @Override
            protected Result doProcess(UpdateUserEmailAccountVO request) {
                String uid = request.getUid();
                UserInfoDTO userInfoDTO = userService.queryUserInfoByUid(uid);
                String oldEmailAccount = userInfoDTO.getEmailAccount();
                String newEmailAccount = request.getNewEmailAccount();

                String verifyKey = VerifyTarget.CHANGE_EMAIL.getTarget() + ":" + oldEmailAccount;
                String verifyCode = stringCacheService.get(verifyKey);
                if (verifyCode == null || !verifyCode.equals(request.getVerifyCode())) {
                    throw new VerifyCodeCheckFailedException("verify code validate failed, input verify code: " + request.getVerifyCode());
                }
                stringCacheService.remove(verifyKey);
                String url = String.format("/common/verify_code?identifier=%s&verify_target=%s", newEmailAccount, VerifyTarget.REGISTER.getTarget());
                return selfCallRestTemplate.getForObject(url, Result.class);
            }
        }.process(updateUserEmailAccountVO);
    }

    @PutMapping("/profile/email_account")
    @NeedTokenApi
    @ApiOperation(value = "try to confirm the request for changing email-account",
            notes = "Only the success of this interface will mean the success of the current user's change of email-account")
    public Result confirmUpdateUserEmailAccount(
            @RequestBody UpdateUserEmailAccountVO updateUserEmailAccountVO
    ) {
        return new HandlerTemplate<UpdateUserEmailAccountVO, Result>() {
            @Override
            protected void validParam(UpdateUserEmailAccountVO request) {
                String emailAccount = request.getNewEmailAccount();
                String currentUid = safeGetCurrentUid();
                request.setUid(currentUid);

                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!emailAccount.matches(emailRegex)) {
                    throw new ParamValidFailedException("email account format error: " + emailAccount);
                }
            }

            @Override
            protected Result doProcess(UpdateUserEmailAccountVO request) {
                String uid = request.getUid();
                String newEmailAccount = request.getNewEmailAccount();

                String verifyKey = VerifyTarget.REGISTER.getTarget() + ":" + newEmailAccount;
                String verifyCode = stringCacheService.get(verifyKey);
                if (verifyCode == null || !verifyCode.equals(request.getVerifyCode())) {
                    throw new VerifyCodeCheckFailedException("verify code validate failed, input verify code: " + request.getVerifyCode());
                }
                stringCacheService.remove(verifyKey);
                UserInfo userInfo = UserInfo.builder().uid(uid).emailAccount(newEmailAccount).build();
                userService.updateUser(userInfo);
                return Result.Success();
            }
        }.process(updateUserEmailAccountVO);
    }

    @PutMapping("/profile")
    @NeedTokenApi
    @ApiOperation(value = "update current user's profile")
    public Result updateUserProfile(
            @RequestBody UpdateUserProfileVO updateUserProfileVO
            ) {
        return new HandlerTemplate<UpdateUserProfileVO, Result>() {
            @Override
            protected void validParam(UpdateUserProfileVO request) {
                String currentUid = BaseContextHelper.getCurrentUid();
                if (currentUid == null) {
                    throw new ParamValidFailedException("jwt token can not be null");
                }
                request.setUid(currentUid);
            }

            @Override
            protected Result doProcess(UpdateUserProfileVO request) {
                UserInfo userInfo = UserInfo.builder().
                        uid(request.getUid()).
                        userName(request.getUserName()).
                        iconLink(request.getIconLink()).
                        doubleCheck(request.getDoubleCheck()).build();
                userService.updateUser(userInfo);
                return Result.Success();
            }
        }.process(updateUserProfileVO);
    }

    @DeleteMapping("{uid}")
    @NeedTokenApi
    @ApiOperation(value = "soft delete user", notes = "only super manager")
    public Result softDeleteUser(
            @PathVariable String uid
    ) {
       return new HandlerTemplate<String, Result>() {
           @Override
           protected void validParam(String request) {
               String currentUid = safeGetCurrentUid();
               if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER)) {
                   throw new UserRoleCheckFailedException(String.format("user <%s> can not do delete operation", currentUid));
               }
           }

           @Override
           protected Result doProcess(String request) {
                userService.deleteUser(request);
                return Result.Success();
           }
       }.process(uid);
    }

    @GetMapping("/email_account/validation")
    @ApiOperation(value = "validate email-account", notes = "If we want to validate that the email account is registered, pass <expect_register> = true")
    public Result validateEmailAccount(
            @RequestParam(name = "email_account") String emailAccount,
            @RequestParam(name = "expect_register") Boolean expectRegister) {
        ValidateEmailAccountVO validateEmailAccountVO = new ValidateEmailAccountVO();
        validateEmailAccountVO.setEmailAccount(emailAccount);
        validateEmailAccountVO.setExpectRegister(expectRegister);
        return new HandlerTemplate<ValidateEmailAccountVO, Result>() {
            @Override
            protected void validParam(ValidateEmailAccountVO request) {
                String emailAccount = request.getEmailAccount();
                if (emailAccount == null || emailAccount.isEmpty() || request.getExpectRegister() == null) {
                    throw new ParamValidFailedException("email account or expect register can not be null");
                }
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!emailAccount.matches(emailRegex)) {
                    throw new ParamValidFailedException("email account format error: " + emailAccount);
                }
            }

            @Override
            protected Result doProcess(ValidateEmailAccountVO request) {
                if (userService.checkEmailAccount(request.getEmailAccount(), request.getExpectRegister())) {
                    return Result.Success();
                }
                return new Result(Result.CODE_EMAIL_ACCOUNT_CHECK_FAILED, String.format("email account <%s> validate error", request.getEmailAccount()));
            }
        }.process(validateEmailAccountVO);
    }

    @PostMapping("/register")
    @ApiOperation(value = "register user")
    public Result registerUser(
            @RequestBody RegisterUserVO registerUserVO
    ) {
        return new HandlerTemplate<RegisterUserVO, Result>() {
            @Override
            protected void validParam(RegisterUserVO request) {
                String userName = request.getUserName();
                String verifyCode = request.getVerifyCode();
                String emailAccount = request.getEmailAccount();
                String password = request.getPassword();
                String iconLink = request.getIconLink();
                String publicKeyVersion = request.getPublicKeyVersion();
                if (
                        userName == null || userName.isEmpty() || verifyCode == null || verifyCode.isEmpty()
                        || emailAccount == null || emailAccount.isEmpty() || password == null || password.isEmpty()
                ) {
                    throw new ParamValidFailedException("user info can not be null: " + request);
                }
                if (iconLink == null) {
                    request.setIconLink("");
                }
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!emailAccount.matches(emailRegex)) {
                    throw new ParamValidFailedException("email account format error: " + emailAccount);
                }
                PrivateKey privateKey = secretKeyService.getPrivateKeyAndCheckVersion(publicKeyVersion);
                if (privateKey == null) {
                    throw new ParamValidFailedException("public key is expired, please reacquire public key, old key-version = " + publicKeyVersion);
                }
                request.setPrivateKey(privateKey);
            }

            @Override
            protected Result doProcess(RegisterUserVO request) {
                String verifyKey = VerifyTarget.REGISTER.getTarget() + ":" + request.getEmailAccount();
                String verifyCode = stringCacheService.get(verifyKey);
                if (verifyCode == null || !verifyCode.equals(request.getVerifyCode())) {
                    throw new VerifyCodeCheckFailedException("verify code validate failed, input verify code: " + request.getVerifyCode());
                }
                stringCacheService.remove(verifyKey);

                String uid = UUID.randomUUID().toString();

                PrivateKey privateKey = request.getPrivateKey();
                String rawPassword = secretKeyService.decryptData(privateKey, request.getPassword());
                String encryptPassword = SecurityHelper.encodeData(rawPassword);

                UserInfo newUser = UserInfo.builder().
                        uid(uid).
                        userName(request.getUserName()).
                        emailAccount(request.getEmailAccount()).
                        password(encryptPassword).build();

                userService.createUser(newUser);
                return Result.Success();
            }
        }.process(registerUserVO);
    }

    @PostMapping("/login")
    @ApiOperation(value = "login user")
    public Result loginUser(
            @RequestBody LoginUserVO loginUserVO
    ) {
        return new HandlerTemplate<LoginUserVO, Result>() {
            @Override
            protected void validParam(LoginUserVO request) {
                if (request.getEncryptedPassword() == null || request.getEmailAccount() == null) {
                    throw new ParamValidFailedException("email account or password can not be null");
                }
                if (userService.checkEmailAccount(request.getEmailAccount(), false)) {
                    throw new ParamValidFailedException("email account <" + request.getEmailAccount() + "> not registered");
                }
                String publicKeyVersion = request.getPublicKeyVersion();
                PrivateKey privateKey = secretKeyService.getPrivateKeyAndCheckVersion(publicKeyVersion);
                if (privateKey == null) {
                    throw new ParamValidFailedException("public key is expired, please reacquire public key, old key-version = " + publicKeyVersion);
                }
                request.setPrivateKey(privateKey);
            }

            @Override
            protected Result doProcess(LoginUserVO request) {
                PrivateKey privateKey = request.getPrivateKey();
                String encryptedPassword = request.getEncryptedPassword();
                String decryptedPassword = secretKeyService.decryptData(privateKey, encryptedPassword);

                String emailAccount = request.getEmailAccount();
                UserInfo conds = UserInfo.builder().emailAccount(emailAccount).isDeleted(false).build();
                PageQueryDTO<UserInfoDTO> userInfoDTOs = userService.pageQueryUserInfosByConds(conds, null);
                if (userInfoDTOs.getTotal() < 1) {
                    throw new UserLoginFailedException("email account <" + emailAccount + "> was frozen");
                }


                if (!userService.checkPassword(emailAccount, decryptedPassword)) {
                    throw new UserLoginFailedException("Password is wrong, cannot pass the verification");
                }
                // email account is also unique index, we can offer a method
                // which could get a userInfoDTO by email account instead of List<userinfoDTO>
                // I'm too lazy to write anymore
                // In addition, double-checking has still not been implemented
                UserInfoDTO userInfoDTO = userInfoDTOs.getPageResult().get(0);
                Boolean doubleCheck = userInfoDTO.getDoubleCheck();
                String uid = userInfoDTO.getUid();
                if (!doubleCheck) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put(JwtTokenClaimConstant.USER_ID, uid);
                    String token = JwtTokenHelper.createToken(claims, tokenTtl);
                    return Result.Success(token);
                }
                // TODO :  redirect to double-check api
                return new Result(Result.CODE_REDIRECT, null, "redirect to ...");
            }
        }.process(loginUserVO);
    }

    @GetMapping("/test/encrypt")
    @ApiOperation(value = "encrypt data with public key", notes = "just be used in test environment")
    public Result encryptData(
            @RequestParam String data
    ) {
        List<String> publicKeyAndVersion = secretKeyService.getPublicKeyAndVersion();
        String version = publicKeyAndVersion.get(1);
        PublicKey publicKey = secretKeyService.getPublicKeyAndCheckVersion(version);
        String encryptData = secretKeyService.encryptData(publicKey, data);
        return Result.Success(encryptData);
    }
}
