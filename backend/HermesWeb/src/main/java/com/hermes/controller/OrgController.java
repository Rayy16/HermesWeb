package com.hermes.controller;

import com.hermes.annotation.NeedTokenApi;
import com.hermes.common.PageInfo;
import com.hermes.common.Result;
import com.hermes.common.enum_type.OrgRole;
import com.hermes.common.enum_type.UserRole;
import com.hermes.common.exception.BaseException;
import com.hermes.common.exception.ParamValidFailedException;
import com.hermes.common.exception.UserRoleCheckFailedException;
import com.hermes.common.helper.BaseContextHelper;
import com.hermes.dto.OrgInfoDTO;
import com.hermes.dto.PageQueryDTO;
import com.hermes.entity.OrgProfileInfo;
import com.hermes.service.OrgService;
import com.hermes.service.UserService;
import com.hermes.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/organization")
@Slf4j
public class OrgController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrgService orgService;

    private String safeGetCurrentUid() {
        String currentUid = BaseContextHelper.getCurrentUid();
        if (currentUid == null) {
            throw new ParamValidFailedException("jwt token can not be null");
        }
        return currentUid;
    }
    @GetMapping("/detail/{orgId}")
    @NeedTokenApi
    public Result getOrgInfo(
            @PathVariable Integer orgId
    ) {
        return new HandlerTemplate<Integer, Result>() {
            @Override
            protected Result doProcess(Integer request) {
                OrgInfoDTO orgInfoDTO = orgService.queryOrgInfoByOrgId(request);
                return Result.Success(orgInfoDTO);
            }

            @Override
            protected void validParam(Integer request) {
                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.INNER_MEMBER)) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not query org details", currentUid));
                }
            }
        }.process(orgId);
    }

    @GetMapping("/details")
    @NeedTokenApi
    public Result getOrgInfos(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page_size", required = false) Integer pageSize
    ) {
        PageQueryVO pageQueryVO = new PageQueryVO();
        pageQueryVO.setPage(page);
        pageQueryVO.setPageSize(pageSize);
        return new HandlerTemplate<PageQueryVO, Result>() {
            @Override
            protected void validParam(PageQueryVO request) {
                PageQueryVO.validatePageQueryVO(request);
                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.INNER_MEMBER)) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not query org details", currentUid));
                }
            }

            @Override
            protected Result doProcess(PageQueryVO request) {
                PageInfo pageInfo = PageInfo.build(request.getPage(), request.getPageSize());
                PageQueryDTO<OrgInfoDTO> orgInfoDTOPageQueryDTO = orgService.pageQueryOrgInfosByConds(null, pageInfo);
                return Result.Success(orgInfoDTOPageQueryDTO);
            }
        }.process(pageQueryVO);
    }

    @PostMapping
    @NeedTokenApi
    public Result createOrg(
            @RequestBody CreateOrgVO createOrgVO
    ) {
        return new HandlerTemplate<CreateOrgVO, Result>() {
            @Override
            protected void validParam(CreateOrgVO request) {
                if (request.getOrgName() == null || request.getOrgName().isEmpty()) {
                    throw new ParamValidFailedException("org name can not be null!");
                }
                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER)) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not create organization", currentUid));
                }
                if (request.getOrgDescribe() == null || request.getOrgDescribe().isEmpty()) {
                    request.setOrgDescribe("该组织暂无描述");
                }
                List<String> orgManagers = request.getOrgManagers();
                if (orgManagers == null || orgManagers.isEmpty()) {
                    request.setOrgManagers(Collections.singletonList(currentUid));
                }
            }

            @Override
            protected Result doProcess(CreateOrgVO request) {
                OrgProfileInfo orgProfileInfo = OrgProfileInfo.builder().
                        orgDescribe(request.getOrgDescribe()).
                        orgName(request.getOrgName()).build();
                orgService.createOrg(orgProfileInfo, request.getOrgManagers());
                return Result.Success(String.format("org %s <orgId = %d> create success", orgProfileInfo.getOrgName(), orgProfileInfo.getId()));
            }
        }.process(createOrgVO);
    }

    @PutMapping
    @NeedTokenApi
    public Result updateOrgProfile(
            @RequestBody UpdateOrgProfileVO updateOrgProfileVO
    ) {
        return new HandlerTemplate<UpdateOrgProfileVO, Result>() {

            @Override
            protected void validParam(UpdateOrgProfileVO request) {
                String currentUid = safeGetCurrentUid();
                if (request.getOrgId() == null) {
                    throw new ParamValidFailedException("org id can not be null");
                }
                if (request.getOrgName() == null && request.getOrgDescribe() == null) {
                    throw new BaseException("Invalid update request");
                }

                if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER) &&
                        !orgService.checkGivenOrgRole(request.getOrgId(), currentUid, OrgRole.ORG_MANAGER)
                ) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not update org <id = %d> profile", currentUid, request.getOrgId()));
                }
            }
            @Override
            protected Result doProcess(UpdateOrgProfileVO request) {
                OrgProfileInfo orgProfileUpdateConds = OrgProfileInfo.builder().
                        id(request.getOrgId()).orgName(request.getOrgName()).
                        orgDescribe(request.getOrgDescribe()).build();
                orgService.updateOrgProfile(orgProfileUpdateConds);
                return Result.Success(String.format("org <%d> update success", request.getOrgId()));
            }
        }.process(updateOrgProfileVO);
    }

    @DeleteMapping("/{orgId}")
    @NeedTokenApi
    public Result deleteOrg(
            @PathVariable Integer orgId
    ) {
        return new HandlerTemplate<Integer, Result>() {
            @Override
            protected Result doProcess(Integer request) {
                orgService.deleteOrg(request);
                log.info("org {} was deleted by user {}", orgId, safeGetCurrentUid());
                return Result.Success(String.format("org <id = %d> delete success", request));
            }

            @Override
            protected void validParam(Integer request) {
                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER)) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not delete org", currentUid));
                }
            }
        }.process(orgId);
    }

    @PostMapping("/people")
    @NeedTokenApi
    public Result addOrgPeople(
            @RequestBody AddOrgPeopleVO addOrgPeopleVO
    ) {
        return new HandlerTemplate<AddOrgPeopleVO, Result>() {
            @Override
            protected Result doProcess(AddOrgPeopleVO request) {
                orgService.addOrgPeople(request.getOrgId(), request.getUid(), OrgRole.valueOf(request.getOrgRoleCode()));
                return Result.Success("add people success");
            }

            @Override
            protected void validParam(AddOrgPeopleVO request) {
                Integer orgId = request.getOrgId();
                if (orgId == null) {
                    throw new ParamValidFailedException("org id can not be null");
                }
                String uid = request.getUid();
                if (uid == null || uid.isEmpty()) {
                    throw new ParamValidFailedException("user id can not be null");
                }
                Integer orgRoleCode = request.getOrgRoleCode();
                try {
                    OrgRole.valueOf(orgRoleCode);
                } catch (Exception e) {
                    throw new ParamValidFailedException(e.getMessage());
                }

                String currentUid = safeGetCurrentUid();
                if (!userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER) &&
                        !orgService.checkGivenOrgRole(orgId, currentUid, OrgRole.ORG_MANAGER)
                ) {
                    throw new UserRoleCheckFailedException(String.format("user <%s> can not add people for org <id = %d>", currentUid, orgId));
                }
            }
        }.process(addOrgPeopleVO);
    }

    @DeleteMapping("/people")
    @NeedTokenApi
    public Result removeOrgPeople(
            @RequestBody RemoveOrgPeopleVO removeOrgPeopleVO
    ) {
        return new HandlerTemplate<RemoveOrgPeopleVO, Result>() {
            @Override
            protected Result doProcess(RemoveOrgPeopleVO request) {
                orgService.removeOrgPeople(request.getOrgId(), request.getUid());
                return Result.Success("remove people success");
            }

            @Override
            protected void validParam(RemoveOrgPeopleVO request) {
                String currentUid = safeGetCurrentUid();
                Integer orgId = request.getOrgId();
                if (orgId == null) {
                    throw new ParamValidFailedException("org id can not be null");
                }
                String uid = request.getUid();
                if (uid == null || uid.isEmpty()) {
                    throw new ParamValidFailedException("user id can not be null");
                }

                // if need-remove people is org manager, it needs current user has super manager auth
                // else need-remove people is org member, it needs current user has org manager or super manager auth
                if (userService.checkUserRole(currentUid, UserRole.SUPER_MANAGER)) {
                    return;
                }
                if (orgService.checkGivenOrgRole(orgId, uid, OrgRole.ORG_MANAGER) ||
                        !orgService.checkGivenOrgRole(orgId, currentUid, OrgRole.ORG_MANAGER)) {
                    throw new ParamValidFailedException(String.format("current user <%s> can not remove people <%s> in org <id = %d>", currentUid, uid, orgId));
                }
            }
        }.process(removeOrgPeopleVO);
    }
}
