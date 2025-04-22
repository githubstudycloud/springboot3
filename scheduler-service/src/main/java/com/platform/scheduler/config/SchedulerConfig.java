package com.platform.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 调度器配置类
 * 
 * @author platform
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    /**
     * 配置任务调度器
     * 
     * @return ThreadPoolTaskScheduler
     */
    @Bean
    public ThreadPoolTaskScheduler springTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // 线程池大小
        scheduler.setPoolSize(5);
        // 线程名前缀
        scheduler.setThreadNamePrefix("spring-task-scheduler-");
        // 等待所有任务完成再关闭线程池
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        scheduler.setAwaitTerminationSeconds(60);
        // 任务执行异常处理
        scheduler.setErrorHandler(throwable -> {
            throwable.printStackTrace();
        });
        return scheduler;
    }
}
