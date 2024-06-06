package com.hermes.mapper;

import com.hermes.common.PageInfo;
import com.hermes.entity.OrgProfileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrgProfileMapper {
    OrgProfileInfo selectOrgProfileById(Integer id);
    List<OrgProfileInfo> selectOrgsProfileByConds(@Param("conds") OrgProfileInfo conds, @Param("page") PageInfo pageInfo);
    Integer insertOrgProfile(OrgProfileInfo org);
    Integer updateOrgProfile(OrgProfileInfo org);
    Integer softDeleteById(Integer id);
    Integer countByConds(@Param("conds") OrgProfileInfo org);
}
