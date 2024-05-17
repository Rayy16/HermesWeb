package com.hermes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class HermesWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HermesWebApplication.class, args);
        log.info("server run");
    }
}
