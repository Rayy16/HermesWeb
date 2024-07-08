package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddOrgPeopleVO {
    Integer orgId;
    String uid;
    Integer orgRoleCode;

    @Override
    public String toString() {
        return "addOrgPeopleVO{" +
                "orgId=" + orgId +
                ", uid='" + uid + '\'' +
                ", orgRoleCode=" + orgRoleCode +
                '}';
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getOrgRoleCode() {
        return orgRoleCode;
    }

    public void setOrgRoleCode(Integer orgRoleCode) {
        this.orgRoleCode = orgRoleCode;
    }
}
