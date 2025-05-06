package com.platform.scheduler.domain.event.common;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件抽象基类
 * 提供领域事件基础实现
 * 
 * @author platform
 */
@Getter
public abstract class AbstractDomainEvent implements DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    protected AbstractDomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
    
    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
