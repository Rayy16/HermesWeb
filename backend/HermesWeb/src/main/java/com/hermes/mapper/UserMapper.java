package com.hermes.mapper;

import com.hermes.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.hermes.common.PageInfo;

import java.util.List;

@Mapper
public interface UserMapper {
    UserInfo selectUserByUid(String uid);
    List<UserInfo> selectUsersByUids(@Param("uids") List<String> uids, @Param("page") PageInfo pageInfo);
    List<UserInfo> selectUsersByEmailAccounts(@Param("emailAccounts") List<String> emailAccounts);
    /*
        Reserve a method which have a sorted field
        List<UserInfo> selectOrderedUsersByConds(@Param("conds) UserInfo conds, @Param("page") Page pageInfo, @Param("order") Order orderInfo);
     */
    List<UserInfo> selectUsersByConds(@Param("conds") UserInfo conds, @Param("page") PageInfo pageInfo);
    Integer insertUser(UserInfo user);
    Integer updateUser(UserInfo user);
    Integer softDeleteByUid(String uid);
    Integer softDeleteByUids(@Param("uids") List<String> uids);
    Integer countByConds(@Param("conds") UserInfo conds);
}
