package com.hermes.common.enum_type;

import lombok.Getter;

@Getter
public enum UserRole {
    SUPER_MANAGER(1),
    INNER_MEMBER(2),
    PLAIN_USER(3);

    private final int code;

    UserRole(int code) {
        this.code = code;
    }

    public static UserRole valueOf(int code) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getCode() == code) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("No UserRole with code" + code);
    }
}