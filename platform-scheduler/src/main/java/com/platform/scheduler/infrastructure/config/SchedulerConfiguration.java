package com.platform.scheduler.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 调度系统配置类
 * 配置系统所需的线程池、调度器等组件
 *
 * @author platform
 */
@Configuration
@EnableAsync
public class SchedulerConfiguration {
    
    @Value("${scheduler.executor.core-pool-size:10}")
    private int corePoolSize;
    
    @Value("${scheduler.executor.max-pool-size:50}")
    private int maxPoolSize;
    
    @Value("${scheduler.executor.queue-capacity:200}")
    private int queueCapacity;
    
    @Value("${scheduler.executor.keep-alive-seconds:60}")
    private int keepAliveSeconds;
    
    @Value("${scheduler.executor.thread-name-prefix:scheduler-executor-}")
    private String threadNamePrefix;
    
    /**
     * 配置任务执行线程池
     *
     * @return 线程池执行器
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);
        
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // 初始化线程池
        executor.initialize();
        return executor;
    }
    
    /**
     * 配置任务监控线程池
     *
     * @return 线程池执行器
     */
    @Bean("monitorExecutor")
    public Executor monitorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("scheduler-monitor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
