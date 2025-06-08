package com.platform.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * RocketMQ消息监听器注解
 * 用于标记消息监听器类
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMQMessageListener {
    
    /**
     * 消费者组
     */
    String consumerGroup();
    
    /**
     * 主题
     */
    String topic();
    
    /**
     * 标签（默认为*，表示所有标签）
     */
    String tag() default "*";
    
    /**
     * 消费模式（默认为并发消费）
     */
    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;
    
    /**
     * 消费起始位置
     */
    ConsumeFromWhere consumeFromWhere() default ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
    
    /**
     * 是否启用（可通过配置动态控制）
     */
    String enabled() default "true";
    
    /**
     * 最大重试次数（-1表示无限重试）
     */
    int maxRetryTimes() default 3;
    
    /**
     * 消费超时时间（单位：毫秒）
     */
    long consumeTimeout() default 15000L;
    
    /**
     * 消费模式枚举
     */
    enum ConsumeMode {
        /**
         * 并发消费
         */
        CONCURRENTLY,
        /**
         * 顺序消费
         */
        ORDERLY
    }
    
    /**
     * 消费起始位置枚举
     */
    enum ConsumeFromWhere {
        /**
         * 从最后的偏移量开始消费
         */
        CONSUME_FROM_LAST_OFFSET,
        /**
         * 从队列开始位置消费
         */
        CONSUME_FROM_FIRST_OFFSET,
        /**
         * 从指定时间戳开始消费
         */
        CONSUME_FROM_TIMESTAMP
    }
}