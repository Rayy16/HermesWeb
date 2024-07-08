package com.hermes;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@Slf4j
public class HermesWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HermesWebApplication.class, args);
        log.info("server run");
    }
}
