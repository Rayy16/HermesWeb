package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserProfileVO {
    String userName;
    Boolean doubleCheck;
    String iconLink;
    @ApiModelProperty(hidden = true)
    String uid;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getDoubleCheck() {
        return doubleCheck;
    }

    public void setDoubleCheck(Boolean doubleCheck) {
        this.doubleCheck = doubleCheck;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
