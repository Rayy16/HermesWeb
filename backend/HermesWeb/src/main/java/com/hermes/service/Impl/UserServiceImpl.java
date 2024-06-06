package com.hermes.service.Impl;

import com.hermes.common.PageInfo;
import com.hermes.common.enum_type.UserRole;
import com.hermes.common.helper.SecurityHelper;
import com.hermes.dto.PageQueryDTO;
import com.hermes.dto.UserInfoDTO;
import com.hermes.entity.UserInfo;
import com.hermes.mapper.UserMapper;
import com.hermes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserInfoDTO queryUserInfoByUid(String uid) {
        UserInfo userInfo = userMapper.selectUserByUid(uid);
        return UserInfoDTO.fillFromUserInfo(userInfo);
    }

    @Override
    public PageQueryDTO<UserInfoDTO> pageQueryUserInfosByConds(UserInfo conds, PageInfo page) {
        List<UserInfo> userInfos = userMapper.selectUsersByConds(conds, page);
        Integer total = userMapper.countByConds(conds);
        return PageQueryDTO.<UserInfoDTO>builder().
                pageResult(UserInfoDTO.fillFromUserInfos(userInfos)).
                total(total).build();
    }

    @Override
    public Boolean checkUserRole(String uid, UserRole threshold) {
        UserInfo userInfo = userMapper.selectUserByUid(uid);
        return userInfo.getUserRole() >= threshold.getCode();
    }

    @Override
    public Boolean checkEmailAccount(String emailAccount, Boolean expectedRegister) {
        // if user was frozen(soft delete), he/she's still occupying the email account. No one else can use it.
        UserInfo conds = UserInfo.builder().emailAccount(emailAccount).build();
        Integer cnt = userMapper.countByConds(conds);
        return expectedRegister ? cnt == 1 : cnt == 0;
    }

    @Override
    public Boolean checkPassword(String emailAccount, String decryptedPassword) {
        if (!checkEmailAccount(emailAccount, true)) {
            return false;
        }
        List<UserInfo> userInfos = userMapper.selectUsersByConds(
                UserInfo.builder().
                    emailAccount(emailAccount).
                    isDeleted(false).build(),
    null);

        if (userInfos == null || userInfos.isEmpty()) {
            throw new RuntimeException("email account <" + emailAccount + "> is already registry, but user can not be login");
        }

        return SecurityHelper.matches(userInfos.get(0).getPassword(), decryptedPassword);
    }

    @Override
    public void createUser(UserInfo user) {
        userMapper.insertUser(user);
    }

    @Override
    public void updateUser(UserInfo user) {
        userMapper.updateUser(user);
    }

    @Override
    public void deleteUser(String uid) {
        userMapper.softDeleteByUid(uid);
    }

    @Override
    public void deleteUsers(List<String> uids) {
        userMapper.softDeleteByUids(uids);
    }
}
