package com.platform.domain.config.shared;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 抽象领域事件基类
 * DDD领域事件 - 提供领域事件的通用实现
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class AbstractDomainEvent implements DomainEvent {
    
    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final String aggregateType;
    private final LocalDateTime occurredAt;
    private final String operator;
    
    protected AbstractDomainEvent(String eventType, String aggregateId, 
                                String aggregateType, String operator) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.operator = operator;
        this.occurredAt = LocalDateTime.now();
    }
    
    @Override
    public String getEventId() {
        return eventId;
    }
    
    @Override
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String getAggregateId() {
        return aggregateId;
    }
    
    @Override
    public String getAggregateType() {
        return aggregateType;
    }
    
    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
    
    @Override
    public String getOperator() {
        return operator;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDomainEvent that = (AbstractDomainEvent) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return "DomainEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", occurredAt=" + occurredAt +
                ", operator='" + operator + '\'' +
                '}';
    }
} 