package com.hermes.common;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static int CODE_SUCCESS = 2000;
    public static int CODE_REDIRECT = 3001;
    public static int CODE_PASSWORD_CHECK_FAILED = 4001;
    public static int CODE_EMAIL_ACCOUNT_CHECK_FAILED = 4002;
    public static int CODE_SERVER_UNKNOWN_ERROR = 5000;
    public static int CODE_RUNTIME_EXCEPTION_FAILED = 5001;
    public static int CODE_EMAIL_SEND_FAILED = 5002;

    public static Result Success(Object data) {
        return new Result(CODE_SUCCESS, data);
    }
    public static Result Success() {
        return new Result(CODE_SUCCESS);
    }

    public Result(int code) {
        this.code = code;
    }
    public Result(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
    public Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }
    public Result(int code, String msg, Object data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }
}
