package com.hermes.common.enum_type;

import lombok.Getter;

@Getter
public enum MailFilterAction {
    REJECT(0),
    ACCEPT(1);

    final private int code;
    MailFilterAction(int code) {
        this.code = code;
    }
}
