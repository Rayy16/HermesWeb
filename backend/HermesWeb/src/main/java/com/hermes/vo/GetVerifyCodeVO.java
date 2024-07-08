package com.hermes.vo;

public class GetVerifyCodeVO {
    String identifier;
    String verifyTarget;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVerifyTarget() {
        return verifyTarget;
    }

    public void setVerifyTarget(String verifyTarget) {
        this.verifyTarget = verifyTarget;
    }
}
