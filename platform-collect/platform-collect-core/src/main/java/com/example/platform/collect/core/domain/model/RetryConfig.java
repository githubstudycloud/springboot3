package com.example.platform.collect.core.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 重试配置模型
 * 定义任务执行过程中的重试策略
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetryConfig {
    
    /**
     * 最大重试次数
     */
    private int maxRetries;
    
    /**
     * 重试间隔（毫秒）
     */
    private long retryInterval;
    
    /**
     * 是否使用指数退避策略
     * 若为true，则每次重试的间隔时间将按指数增长
     */
    private boolean exponentialBackoff;
    
    /**
     * 最大退避时间（毫秒）
     * 使用指数退避策略时，重试间隔的最大值
     */
    private long maxBackoffInterval;
    
    /**
     * 构造函数
     */
    @Builder
    public RetryConfig(Integer maxRetries, Long retryInterval, 
                      Boolean exponentialBackoff, Long maxBackoffInterval) {
        this.maxRetries = maxRetries != null ? maxRetries : 3;
        this.retryInterval = retryInterval != null ? retryInterval : 1000;
        this.exponentialBackoff = exponentialBackoff != null ? exponentialBackoff : true;
        this.maxBackoffInterval = maxBackoffInterval != null ? maxBackoffInterval : 30000;
    }
    
    /**
     * 计算第n次重试的等待时间
     *
     * @param retryCount 当前重试次数
     * @return 等待时间（毫秒）
     */
    public long calculateRetryDelay(int retryCount) {
        if (!exponentialBackoff || retryCount <= 1) {
            return retryInterval;
        }
        
        // 使用指数退避策略计算等待时间：interval * 2^(retryCount-1)
        long delay = retryInterval * (long) Math.pow(2, retryCount - 1);
        
        // 确保不超过最大退避时间
        return Math.min(delay, maxBackoffInterval);
    }
    
    /**
     * 是否应该进行重试
     *
     * @param currentRetryCount 当前已重试次数
     * @return 是否应该重试
     */
    public boolean shouldRetry(int currentRetryCount) {
        return currentRetryCount < maxRetries;
    }
}
