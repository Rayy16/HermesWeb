package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateOrgVO {
    String orgName;
    String orgDescribe;
    List<String> orgManagers;

    @Override
    public String toString() {
        return "CreateOrgVO{" +
                "orgName='" + orgName + '\'' +
                ", orgDescribe='" + orgDescribe + '\'' +
                ", orgManagers=" + orgManagers +
                '}';
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

    public List<String> getOrgManagers() {
        return orgManagers;
    }

    public void setOrgManagers(List<String> orgManagers) {
        this.orgManagers = orgManagers;
    }

}
