package com.hermes.vo;

public class ValidateEmailAccountVO {
    String emailAccount;
    Boolean expectRegister;

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public Boolean getExpectRegister() {
        return expectRegister;
    }

    public void setExpectRegister(Boolean expectRegister) {
        this.expectRegister = expectRegister;
    }
}
