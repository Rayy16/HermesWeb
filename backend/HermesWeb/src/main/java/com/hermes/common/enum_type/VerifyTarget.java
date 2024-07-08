package com.hermes.common.enum_type;

import lombok.Getter;

@Getter
public enum VerifyTarget {
    LOGIN("login"),
    REGISTER("register"),
    CHANGE_EMAIL("change_email");

    final private String target;
    VerifyTarget(String target) {
        this.target = target;
    }

    public static void main(String[] args) {
        VerifyTarget verifyTarget = VerifyTarget.valueOf("REGISTER");
        System.out.println("verifyTarget = " + verifyTarget);
    }
}
