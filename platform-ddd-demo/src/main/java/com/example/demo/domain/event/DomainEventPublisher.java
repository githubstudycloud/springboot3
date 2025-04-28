package com.example.demo.domain.event;

/**
 * 领域事件发布者接口
 */
public interface DomainEventPublisher {
    /**
     * 发布领域事件
     */
    void publish(DomainEvent event);
}