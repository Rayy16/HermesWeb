package com.hermes.dto;

import com.hermes.common.enum_type.UserRole;
import com.hermes.entity.UserInfo;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserInfoDTO {
    private String uid;
    private String userName;
    private String emailAccount;
    private Boolean doubleCheck;
    private String iconLink;
    private UserRole userRole;
    private Boolean isDeleted;

    public static UserInfoDTO fillFromUserInfo(UserInfo user) {
        return UserInfoDTO.builder().
                uid(user.getUid()).
                userName(user.getUserName()).
                emailAccount(user.getEmailAccount()).
                doubleCheck(user.getDoubleCheck()).
                iconLink(user.getIconLink()).
                userRole(UserRole.valueOf(user.getUserRole())).
                isDeleted(user.getIsDeleted()).
                build();
    }

    public static List<UserInfoDTO> fillFromUserInfos(List<UserInfo> users) {
        ArrayList<UserInfoDTO> userInfoDTOS = new ArrayList<>();
        for (UserInfo user : users) {
            userInfoDTOS.add(UserInfoDTO.fillFromUserInfo(user));
        }
        return userInfoDTOS;
    }
}
