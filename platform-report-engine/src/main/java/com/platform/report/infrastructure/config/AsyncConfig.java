package com.platform.report.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    
    /**
     * 报表生成异步执行器
     */
    @Bean(name = "reportGenerationExecutor")
    public Executor reportGenerationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("report-generation-");
        executor.initialize();
        return executor;
    }
    
    /**
     * 报表分发异步执行器
     */
    @Bean(name = "reportDistributionExecutor")
    public Executor reportDistributionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("report-distribution-");
        executor.initialize();
        return executor;
    }
    
    /**
     * 计划任务执行器
     */
    @Bean(name = "scheduleTaskExecutor")
    public Executor scheduleTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("schedule-task-");
        executor.initialize();
        return executor;
    }
}
