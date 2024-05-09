package com.hermes.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class Log4j2 {
    @Bean("logger")
    public static Logger getLogger() {
        return LoggerFactory.getLogger(Log4j2.class);
    }
}
