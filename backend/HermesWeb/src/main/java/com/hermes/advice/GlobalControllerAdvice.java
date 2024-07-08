package com.hermes.advice;

import com.hermes.common.Result;
import com.hermes.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception ex) {
        log.error("catch unknown exception: {}", ex.getMessage());
        return new Result(Result.CODE_SERVER_UNKNOWN_ERROR, "unknown error, please call the admin");
    }

    @ExceptionHandler(value = BaseException.class)
    public Result baseExceptionHandler(BaseException ex) {
        log.error("catch runtime exception: {}", ex.getMessage());
        return new Result(Result.CODE_RUNTIME_EXCEPTION_FAILED, ex.getMessage());
    }
}
