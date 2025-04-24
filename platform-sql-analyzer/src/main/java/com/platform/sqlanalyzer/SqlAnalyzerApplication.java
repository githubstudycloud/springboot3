package com.platform.sqlanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SQL分析器主应用程序
 * 用于MySQL数据库性能分析和监控
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SqlAnalyzerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SqlAnalyzerApplication.class, args);
    }
}
