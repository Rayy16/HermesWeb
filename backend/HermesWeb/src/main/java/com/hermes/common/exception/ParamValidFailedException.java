package com.hermes.common.exception;

public class ParamValidFailedException extends BaseException {
    public ParamValidFailedException() {
    }

    public ParamValidFailedException(String message) {
        super(message);
    }

    public ParamValidFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
