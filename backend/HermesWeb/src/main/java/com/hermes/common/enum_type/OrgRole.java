package com.hermes.common.enum_type;

import lombok.Getter;

@Getter
public enum OrgRole {
    ORG_MANAGER(1),
    ORG_MEMBER(2);

    private final int code;
    OrgRole(int code) {
        this.code = code;
    }
    public static OrgRole valueOf(int code) {
        for (OrgRole orgRole : OrgRole.values()) {
            if (orgRole.getCode() == code) {
                return orgRole;
            }
        }
        throw new IllegalArgumentException("No OrgRole with code" + code);
    }

}
