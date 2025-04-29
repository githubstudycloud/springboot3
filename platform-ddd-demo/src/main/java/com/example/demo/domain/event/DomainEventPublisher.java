package com.example.demo.domain.event;

import com.example.framework.domain.BaseDomainEvent;

/**
 * 领域事件发布者接口
 */
public interface DomainEventPublisher {
    /**
     * 发布领域事件
     */
    void publish(BaseDomainEvent event);
}