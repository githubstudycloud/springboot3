package com.example.demo.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件接口
 */
public interface DomainEvent {
    /**
     * 事件发生时间
     */
    LocalDateTime occurredOn();
}