package com.hermes.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Slf4j
public abstract class HandlerTemplate<T, R> {
    public R process(T request) {
        log.info("start invoke, request = {}", request);
        StopWatch stopWatch = new StopWatch("process");
        stopWatch.start();

        try {
            validParam(request);
            R resp = doProcess(request);
            stopWatch.stop();
            log.info("end invoke, response = {}, costTime = {}", resp, stopWatch.getLastTaskTimeMillis());
            return resp;
        } catch (Exception e) {
            log.error("error invoke, exception = {}", Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }

    protected abstract R doProcess(T request);
    protected void validParam(T request) {

    }
}
