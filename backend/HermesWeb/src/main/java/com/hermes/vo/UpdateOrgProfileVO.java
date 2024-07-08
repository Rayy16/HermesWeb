package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateOrgProfileVO {
    Integer orgId;

    @Override
    public String toString() {
        return "UpdateOrgProfileVO{" +
                "orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                ", orgDescribe='" + orgDescribe + '\'' +
                '}';
    }

    String orgName;
    String orgDescribe;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDescribe() {
        return orgDescribe;
    }

    public void setOrgDescribe(String orgDescribe) {
        this.orgDescribe = orgDescribe;
    }
}
