package com.platform.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * RocketMQ配置属性
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMQProperties {
    
    /**
     * 是否启用RocketMQ
     */
    private boolean enabled = true;
    
    /**
     * RocketMQ服务端点
     */
    private String endpoints = "localhost:8081";
    
    /**
     * 生产者配置
     */
    private Producer producer = new Producer();
    
    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();
    
    /**
     * 消息存储配置
     */
    private MessageStore messageStore = new MessageStore();
    
    /**
     * 生产者配置
     */
    @Data
    public static class Producer {
        /**
         * 生产者组
         */
        private String group = "default-producer-group";
        
        /**
         * 发送超时时间
         */
        private Duration sendTimeout = Duration.ofSeconds(3);
        
        /**
         * 最大尝试次数
         */
        private int maxAttempts = 3;
        
        /**
         * 是否启用事务
         */
        private boolean transactionEnabled = false;
        
        /**
         * 压缩阈值（字节）
         */
        private int compressThreshold = 4096;
    }
    
    /**
     * 消费者配置
     */
    @Data
    public static class Consumer {
        /**
         * 最大缓存消息数
         */
        private int maxCachedMessageCount = 1000;
        
        /**
         * 消费线程数
         */
        private int consumptionThreadCount = 20;
        
        /**
         * 消费超时时间
         */
        private Duration consumeTimeout = Duration.ofSeconds(15);
        
        /**
         * 批量消费大小
         */
        private int consumeBatchSize = 1;
        
        /**
         * 重试间隔
         */
        private Duration retryInterval = Duration.ofSeconds(30);
    }
    
    /**
     * 消息存储配置
     */
    @Data
    public static class MessageStore {
        /**
         * 是否启用消息存储
         */
        private boolean enabled = true;
        
        /**
         * 是否存储消息体
         */
        private boolean storeBody = true;
        
        /**
         * 消息体最大长度（超过此长度将被截断）
         */
        private int maxBodyLength = 65536;
        
        /**
         * 清理保留天数
         */
        private int retentionDays = 30;
        
        /**
         * 是否启用自动清理
         */
        private boolean autoClean = true;
        
        /**
         * 清理定时表达式
         */
        private String cleanCron = "0 0 2 * * ?";
    }
}