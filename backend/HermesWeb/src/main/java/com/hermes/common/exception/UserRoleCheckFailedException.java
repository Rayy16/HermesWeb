package com.hermes.common.exception;

public class UserRoleCheckFailedException extends BaseException {
    public UserRoleCheckFailedException() {
    }

    public UserRoleCheckFailedException(String message) {
        super(message);
    }

    public UserRoleCheckFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
