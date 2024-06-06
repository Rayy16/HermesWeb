package com.hermes.dto;

import com.hermes.entity.OrgProfileInfo;
import com.hermes.entity.UserInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrgInfoDTO {
    private Integer orgId;
    private String orgName;
    private String orgDescribe;
    private List<UserInfoDTO> orgManagers;
    private List<UserInfoDTO> orgMembers;

    public static OrgInfoDTO fillFromOrgProfileAndUserInfo(OrgProfileInfo orgProfile, List<UserInfo> managers, List<UserInfo> members) {
        return OrgInfoDTO.builder().
                orgId(orgProfile.getId()).
                orgName(orgProfile.getOrgName()).
                orgDescribe(orgProfile.getOrgDescribe()).
                orgManagers(UserInfoDTO.fillFromUserInfos(managers)).
                orgMembers(UserInfoDTO.fillFromUserInfos(managers)).build();
    }
}
