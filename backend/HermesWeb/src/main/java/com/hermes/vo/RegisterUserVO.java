package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterUserVO extends SecretKeyVO {
    private String userName;
    private String emailAccount;
    private String password;
    private String iconLink;
    private String verifyCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public String toString() {
        return "RegisterUserVO{" +
                "userName='" + userName + '\'' +
                ", emailAccount='" + emailAccount + '\'' +
                ", password='" + password + '\'' +
                ", iconLink='" + iconLink + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", publicKeyVersion='" + publicKeyVersion + '\'' +
                ", privateKey=" + privateKey +
                '}';
    }
}
