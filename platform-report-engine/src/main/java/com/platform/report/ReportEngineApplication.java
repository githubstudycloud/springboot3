package com.platform.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 报表引擎应用启动类
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ReportEngineApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ReportEngineApplication.class, args);
    }
}
