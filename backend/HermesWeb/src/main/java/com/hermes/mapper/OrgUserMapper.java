package com.hermes.mapper;

import com.hermes.common.PageInfo;
import com.hermes.entity.OrgUserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgUserMapper {
    OrgUserInfo selectOrgUserByUidAndOrgId(@Param("orgId") Integer orgId, @Param("uid") String uid);
    List<OrgUserInfo> selectOrgUsersByConds(@Param("conds") OrgUserInfo conds, @Param("page") PageInfo pageInfo);
    Integer insertOrgUser(OrgUserInfo userOrg);
    Integer updateOrgUser(OrgUserInfo userOrg);
    Integer deleteOrgUser(OrgUserInfo userOrgInfo);
    Integer batchDeleteOrgUserByOrgId(Integer orgId);
    Integer countByConds(@Param("conds") OrgUserInfo conds);
}
