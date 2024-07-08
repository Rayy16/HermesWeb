package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginUserVO extends SecretKeyVO {
    private String emailAccount;
    private String encryptedPassword;

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public String toString() {
        return "LoginUserVO{" +
                "emailAccount='" + emailAccount + '\'' +
                ", encryptedPassword='" + encryptedPassword + '\'' +
                ", publicKeyVersion='" + publicKeyVersion + '\'' +
                ", privateKey=" + privateKey +
                '}';
    }
}
