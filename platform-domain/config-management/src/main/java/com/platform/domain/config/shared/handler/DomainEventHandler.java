package com.platform.domain.config.shared.handler;

import com.platform.domain.config.shared.DomainEvent;

/**
 * 领域事件处理器接口
 * DDD事件处理 - 定义领域事件处理的通用契约
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public interface DomainEventHandler<T extends DomainEvent> {
    
    /**
     * 处理领域事件
     */
    void handle(T event);
    
    /**
     * 获取处理的事件类型
     */
    Class<T> getEventType();
    
    /**
     * 获取处理器名称
     */
    default String getHandlerName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * 检查是否可以处理该事件
     */
    default boolean canHandle(DomainEvent event) {
        return getEventType().isAssignableFrom(event.getClass());
    }
    
    /**
     * 获取处理优先级（越小优先级越高）
     */
    default int getPriority() {
        return 100;
    }
    
    /**
     * 检查是否为异步处理器
     */
    default boolean isAsync() {
        return false;
    }
} 