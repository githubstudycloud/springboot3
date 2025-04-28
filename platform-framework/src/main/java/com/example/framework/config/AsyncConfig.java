package com.example.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步执行配置
 *
 * @author platform
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(name = "platform.thread.virtual.enabled", havingValue = "true", matchIfMissing = false)
public class AsyncConfig {

    /**
     * 配置线程池执行器
     *
     * @return 异步任务执行器
     */
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}