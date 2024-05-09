package com.hermes.service.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class ServiceTemplate<T, R> {
    private Logger logger;

    private void setLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public R process(T request) {
        setLogger(this.getClass());

        logger.info("process start, request=" + request);
        long startTime = System.currentTimeMillis();

        try {
            validParam(request);
            R response = doProcess(request);
            long endTime = System.currentTimeMillis();
            long costTime = endTime-startTime;
            logger.info("process end, response="+response+", costTime="+costTime);
            return response;
        } catch (Exception e) {
            logger.error("process error, exception:"+ Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    protected abstract R doProcess(T request);

    protected abstract void validParam(T request);
}
