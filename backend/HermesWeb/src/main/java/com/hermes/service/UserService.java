package com.hermes.service;

import com.hermes.common.PageInfo;
import com.hermes.common.enum_type.UserRole;
import com.hermes.dto.PageQueryDTO;
import com.hermes.dto.UserInfoDTO;
import com.hermes.entity.UserInfo;

import java.util.List;

public interface UserService {
    UserInfoDTO queryUserInfoByUid(String uid);
    PageQueryDTO<UserInfoDTO> pageQueryUserInfosByConds(UserInfo conds, PageInfo page);

    Boolean checkUserRole(String uid, UserRole threshold);
    Boolean checkEmailAccount(String emailAccount, Boolean expectedRegister);
    Boolean checkPassword(String emailAccount, String decryptedPassword);

    void createUser(UserInfo user);
    void updateUser(UserInfo user);
    void deleteUser(String uid);
    void deleteUsers(List<String> uids);
}
