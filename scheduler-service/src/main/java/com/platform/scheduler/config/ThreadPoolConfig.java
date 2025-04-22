package com.platform.scheduler.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置类
 * 
 * @author platform
 */
@Configuration
public class ThreadPoolConfig {
    
    /**
     * 最大并行任务数
     */
    @Value("${scheduler.task.max-parallel-tasks:100}")
    private int maxParallelTasks;
    
    /**
     * 任务执行线程池
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(maxParallelTasks / 2);
        // 最大线程数
        executor.setMaxPoolSize(maxParallelTasks);
        // 队列容量
        executor.setQueueCapacity(maxParallelTasks * 2);
        // 线程名前缀
        executor.setThreadNamePrefix("task-executor-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
    
    /**
     * 任务调度线程池
     */
    @Bean
    public ThreadPoolTaskExecutor schedulerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(10);
        // 队列容量
        executor.setQueueCapacity(20);
        // 线程名前缀
        executor.setThreadNamePrefix("scheduler-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}
