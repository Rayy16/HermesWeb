package com.hermes.service.Impl;

import com.hermes.common.enum_type.OrgRole;
import com.hermes.common.PageInfo;
import com.hermes.common.enum_type.UserRole;
import com.hermes.common.exception.BaseException;
import com.hermes.dto.OrgInfoDTO;
import com.hermes.dto.PageQueryDTO;
import com.hermes.entity.OrgProfileInfo;
import com.hermes.entity.UserInfo;
import com.hermes.entity.OrgUserInfo;
import com.hermes.mapper.OrgProfileMapper;
import com.hermes.mapper.UserMapper;
import com.hermes.mapper.OrgUserMapper;
import com.hermes.service.OrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class OrgServiceImpl implements OrgService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrgProfileMapper orgProfileMapper;
    @Autowired
    private OrgUserMapper orgUserMapper;
    @Override
    public OrgInfoDTO queryOrgInfoByOrgId(Integer orgId) {
        OrgProfileInfo orgProfileInfo = orgProfileMapper.selectOrgProfileById(orgId);
        OrgUserInfo selectConds = OrgUserInfo.builder().orgId(orgId).build();
        List<OrgUserInfo> userOrgInfos = orgUserMapper.selectOrgUsersByConds(selectConds, null);
        List<UserInfo> managers = new ArrayList<>();
        List<UserInfo> members = new ArrayList<>();
        for (OrgUserInfo info: userOrgInfos) {
            UserInfo user = userMapper.selectUserByUid(info.getUid());
            if (OrgRole.ORG_MANAGER.getCode() == info.getOrgRole()) {
                managers.add(user);
            } else if (OrgRole.ORG_MEMBER.getCode() == info.getOrgRole()) {
                members.add(user);
            }
        }
        return OrgInfoDTO.fillFromOrgProfileAndUserInfo(orgProfileInfo, managers, members);
    }

    @Override
    public PageQueryDTO<OrgInfoDTO> pageQueryOrgInfosByConds(OrgProfileInfo conds, PageInfo page) {
        Integer total = orgProfileMapper.countByConds(conds);
        List<OrgProfileInfo> orgProfileInfos = orgProfileMapper.selectOrgsProfileByConds(conds, page);
        List<OrgInfoDTO> pageResult = new ArrayList<>();
        for (OrgProfileInfo orgProfileInfo: orgProfileInfos) {
            OrgInfoDTO orgInfoDTO = queryOrgInfoByOrgId(orgProfileInfo.getId());
            pageResult.add(orgInfoDTO);
        }
        return PageQueryDTO.<OrgInfoDTO>builder().
                pageResult(pageResult).
                total(total).build();
    }

    @Override
    @Transactional
    public void createOrg(OrgProfileInfo org, List<String> managerIds) {
       checkUsersExisted(managerIds);

        orgProfileMapper.insertOrgProfile(org);
        OrgUserInfo userOrgInfo = OrgUserInfo.builder().
                orgId(org.getId()).
                orgRole(OrgRole.ORG_MANAGER.getCode()).build();

        /*
         * BAD CODE! multiple calls instead of one
         * Normally there aren't a lot of managerIds passed in
         * when creating an org, so I got lazy
         */
        for (String managerId: managerIds) {
            userOrgInfo.setUid(managerId);
            autoUpgradeUserRole(managerId);
            orgUserMapper.insertOrgUser(userOrgInfo);
        }
    }

    @Override
    public void updateOrgProfile(OrgProfileInfo org) {
        orgProfileMapper.updateOrgProfile(org);
    }
    /**
     * the add/remove org manager/member methods all are part of the updateOrgUser method
     * */
    @Override
    @Transactional
    public void addOrgPeople(Integer orgId, String uid, OrgRole orgRole) {
        checkOrgExisted(orgId);
        checkUserExisted(uid);
        autoUpgradeUserRole(uid);
        log.info("associate user<{}> with org<{}> for org role<{}>", uid, orgId, orgRole.name());
        OrgUserInfo orgUserInfo = OrgUserInfo.builder().
                orgRole(orgRole.getCode()).
                orgId(orgId).uid(uid).build();
        orgUserMapper.insertOrgUser(orgUserInfo);
    }

    @Override
    @Transactional
    public void removeOrgPeople(Integer orgId, String uid) {
        checkOrgExisted(orgId);
        checkUserExisted(uid);

        OrgUserInfo orgUserInfo = orgUserMapper.selectOrgUserByUidAndOrgId(orgId, uid);
        if (orgUserInfo == null) {
            throw new BaseException("unexpected org's user info for " + orgId + " - " + uid);
        }

        if(orgUserInfo.getOrgRole().equals(OrgRole.ORG_MANAGER.getCode())) {
            if (checkGivenOrgSoleManager(orgId, uid)) {
                throw new BaseException(String.format("remove failed, user<%s> is the sole manager of org<%d>", uid, orgId));
            }
        }
        orgUserMapper.deleteOrgUser(orgUserInfo);
        // if user does not associate with any org after delete org's user,
        if (!checkAssociateAnyOrg(uid)) {
            log.info("user<{}> does not associate with any org after remove from org<{}>, degrade user role!", uid, orgId);
            UserInfo userInfo = userMapper.selectUserByUid(uid);
            userInfo.setUserRole(UserRole.PLAIN_USER.getCode());
            userMapper.updateUser(userInfo);
        }
    }

    /**
     * delete org and auto degrade user role
     * ~ bad code ~
     * Should not call the database in a for-loop
     * */
    @Override
    @Transactional
    public void deleteOrg(Integer orgId) {
        orgProfileMapper.softDeleteById(orgId);
        OrgUserInfo queryOrgPeopleConds = OrgUserInfo.builder().orgId(orgId).build();
        List<OrgUserInfo> orgUserInfos = orgUserMapper.selectOrgUsersByConds(queryOrgPeopleConds, null);
        orgUserMapper.batchDeleteOrgUserByOrgId(orgId);

        for (OrgUserInfo orgUserInfo: orgUserInfos) {
            String uid = orgUserInfo.getUid();
            // if user not associate with any org, degrade user role
            if (!checkAssociateAnyOrg(uid)) {
                UserInfo degradeUserRoleConds = UserInfo.builder().
                        uid(uid).userRole(UserRole.PLAIN_USER.getCode()).build();
                userMapper.updateUser(degradeUserRoleConds);
            }
        }
    }

    @Override
    public Boolean checkAssociateAnyOrg(String uid) {
        OrgUserInfo conds = OrgUserInfo.builder().uid(uid).build();
        Integer cnt = orgUserMapper.countByConds(conds);
        return !cnt.equals(0);
    }

    @Override
    public Boolean checkGivenOrgSoleManager(Integer orgId, String uid) {
        OrgUserInfo queryOrgManagerConds = OrgUserInfo.builder().
                orgId(orgId).orgRole(OrgRole.ORG_MANAGER.getCode()).build();
        List<OrgUserInfo> orgUserInfos = orgUserMapper.selectOrgUsersByConds(queryOrgManagerConds, null);
        if (orgUserInfos == null || orgUserInfos.isEmpty()) {
            throw new BaseException("unexpected org's user info for " + orgId);
        }
        if (orgUserInfos.size() != 1) {
            return false;
        }
        return orgUserInfos.get(0).getUid().equals(uid);
    }

    /**
     * ~ bad code ~
     * Should not call the database in a for-loop
     * */
    @Override
    public Boolean checkAnyOrgSoleManager(String uid) {
        OrgUserInfo queryAssociateOrgConds = OrgUserInfo.builder().
                uid(uid).orgRole(OrgRole.ORG_MANAGER.getCode()).build();

        List<OrgUserInfo> orgUserInfos = orgUserMapper.selectOrgUsersByConds(queryAssociateOrgConds, null);

        for (OrgUserInfo orgUserInfo: orgUserInfos) {
            if (checkGivenOrgSoleManager(orgUserInfo.getOrgId(), uid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkGivenOrgRole(Integer orgId, String uid, OrgRole orgRole) {
        OrgUserInfo queryOrgPeopleConds = OrgUserInfo.builder().
                orgId(orgId).uid(uid).build();
        List<OrgUserInfo> orgUserInfos = orgUserMapper.selectOrgUsersByConds(queryOrgPeopleConds, null);
        if (orgUserInfos == null || orgUserInfos.isEmpty()) {
            throw new BaseException("unexpected org's user info for " + orgId);
        }
        return orgUserInfos.get(0).getOrgRole() <= orgRole.getCode();
    }

    /**
     * auto upgrade user role if necessarily
     * */
    private void autoUpgradeUserRole(String uid) {
        UserInfo userInfo = userMapper.selectUserByUid(uid);
        if (userInfo.getUserRole().equals(UserRole.PLAIN_USER.getCode())) {
            userInfo.setUserRole(UserRole.INNER_MEMBER.getCode());
            userMapper.updateUser(userInfo);
        }
    }

    private void checkOrgExisted(Integer orgId) {
        log.info("try check org <{}> existed", orgId);
        OrgProfileInfo orgProfileInfo = orgProfileMapper.selectOrgProfileById(orgId);
        if (orgProfileInfo == null || orgProfileInfo.getIsDeleted()) {
            throw new BaseException("org: " + orgId + "does not existed");
        }
    }
    private void checkUserExisted(String uid) {
        log.info("try check user <{}> existed", uid);
        UserInfo userInfo = userMapper.selectUserByUid(uid);
        if (userInfo == null || userInfo.getIsDeleted()) {
            throw new BaseException("user: " + uid + "does not existed");
        }
    }
    private void checkUsersExisted(List<String> uids) {
        log.info("try check users [{}] existed", String.join(",", uids));
        List<UserInfo> userInfos = userMapper.selectUsersByUids(uids, null);
        if (userInfos == null) {
            throw new BaseException("Users: " + String.join(",", uids) + "does not exited");
        }
        Set<String> users = new HashSet<>();
        for (UserInfo userInfo: userInfos) {
            if (userInfo.getIsDeleted()) {
                continue;
            }
            users.add(userInfo.getUid());
        }
        for (String uid: uids) {
            if (!users.contains(uid)) {
                throw new BaseException("user: " + uid + "does not existed");
            }
        }
    }
}
