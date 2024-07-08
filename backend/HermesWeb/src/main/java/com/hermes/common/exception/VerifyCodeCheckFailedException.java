package com.hermes.common.exception;

public class VerifyCodeCheckFailedException extends BaseException {
    public VerifyCodeCheckFailedException() {
    }

    public VerifyCodeCheckFailedException(String message) {
        super(message);
    }

    public VerifyCodeCheckFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
