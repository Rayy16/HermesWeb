package com.hermes.common.exception;

public class UserLoginFailedException extends BaseException {
    public UserLoginFailedException() {
        super();
    }

    public UserLoginFailedException(String message) {
        super(message);
    }

    public UserLoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
