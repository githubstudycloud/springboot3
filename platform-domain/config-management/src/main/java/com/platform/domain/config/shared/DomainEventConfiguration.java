package com.platform.domain.config.shared;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 领域事件配置类
 * Spring Boot自动配置 - 配置领域事件相关的Bean
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Configuration
@EnableAsync
public class DomainEventConfiguration {
    
    /**
     * 配置领域事件发布器
     */
    @Bean
    @ConditionalOnMissingBean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DomainEventPublisher(applicationEventPublisher);
    }
    
    /**
     * 配置异步执行器
     */
    @Bean(name = "domainEventTaskExecutor")
    public org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor domainEventTaskExecutor() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor executor = 
            new org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("DomainEvent-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        return executor;
    }
} 