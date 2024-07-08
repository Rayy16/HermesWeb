package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserEmailAccountVO {
    @ApiModelProperty(hidden = true)
    String uid;
    String newEmailAccount;
    String verifyCode;

    @Override
    public String toString() {
        return "UpdateUserEmailAccountVO{" +
                "uid='" + uid + '\'' +
                ", newEmailAccount='" + newEmailAccount + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                '}';
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNewEmailAccount() {
        return newEmailAccount;
    }

    public void setNewEmailAccount(String newEmailAccount) {
        this.newEmailAccount = newEmailAccount;
    }
}
