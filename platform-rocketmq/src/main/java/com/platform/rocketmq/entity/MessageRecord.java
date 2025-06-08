package com.platform.rocketmq.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 消息记录实体
 * 用于存储接收到的消息
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class MessageRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 主题
     */
    private String topic;
    
    /**
     * 标签
     */
    private String tag;
    
    /**
     * 消费者组
     */
    private String consumerGroup;
    
    /**
     * 消息体
     */
    private String messageBody;
    
    /**
     * 消息键
     */
    private String messageKey;
    
    /**
     * 消费状态（SUCCESS, FAILED, RETRY）
     */
    private String consumeStatus;
    
    /**
     * 重试次数
     */
    private Integer retryTimes;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 接收时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 消费时间
     */
    private LocalDateTime consumeTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 消费状态枚举
     */
    public enum ConsumeStatus {
        /**
         * 消费成功
         */
        SUCCESS,
        /**
         * 消费失败
         */
        FAILED,
        /**
         * 重试中
         */
        RETRY,
        /**
         * 处理中
         */
        PROCESSING
    }
}