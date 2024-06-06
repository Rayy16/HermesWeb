package com.hermes.service;

import com.hermes.common.enum_type.OrgRole;
import com.hermes.common.PageInfo;
import com.hermes.dto.OrgInfoDTO;
import com.hermes.dto.PageQueryDTO;
import com.hermes.entity.OrgProfileInfo;

import java.util.List;

public interface OrgService {
    OrgInfoDTO queryOrgInfoByOrgId(Integer orgId);
    PageQueryDTO<OrgInfoDTO> pageQueryOrgInfosByConds(OrgProfileInfo conds, PageInfo page);
    void createOrg(OrgProfileInfo org, List<String> managerIds);
    void updateOrgProfile(OrgProfileInfo org);
    void addOrgPeople(Integer orgId, String uid, OrgRole orgRole);
    void removeOrgPeople(Integer orgId, String uid);
    /**
     * delete org and auto degrade user role;
     * this method is strongly discouraged, cascading operations (degrade user roles)
     * caused by deleting org with many people is very time-consuming
     * */
    void deleteOrg(Integer orgId);
    /**
     * if user associate with any org, return true
     * */
    Boolean checkAssociateAnyOrg(String uid);
    /**
     * if user is the sole manager of given org, return true;
     * */
    Boolean checkGivenOrgSoleManager(Integer orgId, String uid);
    /**
     * if user is the sole manager of any org, return true
     * */
    Boolean checkAnyOrgSoleManager(String uid);

}
