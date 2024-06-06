package com.hermes.common.enum_type;

import lombok.Getter;

@Getter
public enum TemplateType {
    VERIFY_CODE("VERIFY_CODE");

    final private String type;
    TemplateType(String type) {
        this.type = type;
    }
}
