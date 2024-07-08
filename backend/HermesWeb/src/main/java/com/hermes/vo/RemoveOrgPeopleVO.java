package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RemoveOrgPeopleVO {
    Integer orgId;
    String uid;

    @Override
    public String toString() {
        return "RemoveOrgPeopleVO{" +
                "orgId=" + orgId +
                ", uid='" + uid + '\'' +
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
}
