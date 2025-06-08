package com.platform.domain.config.shared;

import java.time.LocalDateTime;

/**
 * 领域事件基础接口
 * DDD领域事件 - 定义所有领域事件的通用契约
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public interface DomainEvent {
    
    /**
     * 获取事件唯一标识
     */
    String getEventId();
    
    /**
     * 获取事件类型
     */
    String getEventType();
    
    /**
     * 获取聚合根ID
     */
    String getAggregateId();
    
    /**
     * 获取聚合根类型
     */
    String getAggregateType();
    
    /**
     * 获取事件发生时间
     */
    LocalDateTime getOccurredAt();
    
    /**
     * 获取事件版本
     */
    default String getEventVersion() {
        return "1.0";
    }
    
    /**
     * 获取事件源
     */
    default String getEventSource() {
        return "config-management-domain";
    }
    
    /**
     * 获取操作人员
     */
    String getOperator();
    
    /**
     * 检查是否为重要事件
     */
    default boolean isImportant() {
        return false;
    }
    
    /**
     * 检查是否需要持久化
     */
    default boolean needsPersistence() {
        return true;
    }
    
    /**
     * 检查是否需要异步处理
     */
    default boolean isAsyncProcessing() {
        return true;
    }
} 