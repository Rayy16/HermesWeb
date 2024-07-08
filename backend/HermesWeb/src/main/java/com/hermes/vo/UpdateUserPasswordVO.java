package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserPasswordVO extends SecretKeyVO {
    String oldPassword;
    String newPassword;
    @ApiModelProperty(hidden = true)
    String uid;

    @Override
    public String toString() {
        return "UpdateUserPasswordVO{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", uid='" + uid + '\'' +
                ", publicKeyVersion='" + publicKeyVersion + '\'' +
                ", privateKey=" + privateKey +
                '}';
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
