package com.demo.store.mgmt.tool;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToolApplication {
    private static final Logger logger = LoggerFactory.getLogger(ToolApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args);
    }

    @PostConstruct
    void init() {
        logger.info("Hello world!");
    }
}
