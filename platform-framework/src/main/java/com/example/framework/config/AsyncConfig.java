package com.example.framework.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

/**
 * 异步执行配置，支持虚拟线程
 *
 * @author platform
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@ConditionalOnProperty(name = "platform.thread.virtual.enabled", havingValue = "true", matchIfMissing = false)
public class AsyncConfig {

    /**
     * 配置虚拟线程执行器
     *
     * @return 基于虚拟线程的异步任务执行器
     */
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
