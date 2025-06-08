package com.platform.domain.config.shared;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 领域事件实体
 * JPA实体 - 持久化领域事件数据
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "platform_domain_event", 
       indexes = {
           @Index(name = "idx_aggregate_id", columnList = "aggregateId"),
           @Index(name = "idx_event_type", columnList = "eventType"),
           @Index(name = "idx_occurred_at", columnList = "occurredAt"),
           @Index(name = "idx_operator", columnList = "operator"),
           @Index(name = "idx_aggregate_type", columnList = "aggregateType")
       })
public class DomainEventEntity {
    
    @Id
    @Column(length = 36)
    private String eventId;
    
    @Column(nullable = false, length = 100)
    private String eventType;
    
    @Column(nullable = false, length = 100)
    private String aggregateId;
    
    @Column(nullable = false, length = 50)
    private String aggregateType;
    
    @Column(nullable = false)
    private LocalDateTime occurredAt;
    
    @Column(length = 10)
    private String eventVersion;
    
    @Column(length = 50)
    private String eventSource;
    
    @Column(nullable = false, length = 100)
    private String operator;
    
    @Column(nullable = false)
    private Boolean important;
    
    @Column(nullable = false)
    private Boolean needsPersistence;
    
    @Column(nullable = false)
    private Boolean asyncProcessing;
    
    @Column(columnDefinition = "TEXT")
    private String eventData;
    
    @Column
    private LocalDateTime processedAt;
    
    @Column(length = 20)
    private String processStatus;
    
    @Column(length = 500)
    private String processError;
    
    // JPA required
    protected DomainEventEntity() {}
    
    /**
     * 从领域事件创建实体
     */
    public static DomainEventEntity fromDomainEvent(DomainEvent event) {
        DomainEventEntity entity = new DomainEventEntity();
        entity.eventId = event.getEventId();
        entity.eventType = event.getEventType();
        entity.aggregateId = event.getAggregateId();
        entity.aggregateType = event.getAggregateType();
        entity.occurredAt = event.getOccurredAt();
        entity.eventVersion = event.getEventVersion();
        entity.eventSource = event.getEventSource();
        entity.operator = event.getOperator();
        entity.important = event.isImportant();
        entity.needsPersistence = event.needsPersistence();
        entity.asyncProcessing = event.isAsyncProcessing();
        entity.processStatus = "PENDING";
        
        // 序列化事件数据（实际实现中可以使用JSON）
        entity.eventData = serializeEventData(event);
        
        return entity;
    }
    
    /**
     * 标记为已处理
     */
    public void markAsProcessed() {
        this.processedAt = LocalDateTime.now();
        this.processStatus = "PROCESSED";
    }
    
    /**
     * 标记为处理失败
     */
    public void markAsFailed(String error) {
        this.processedAt = LocalDateTime.now();
        this.processStatus = "FAILED";
        this.processError = error;
    }
    
    /**
     * 序列化事件数据
     */
    private static String serializeEventData(DomainEvent event) {
        // 简单实现，实际项目中可以使用Jackson等JSON库
        return String.format("{\"eventType\":\"%s\",\"aggregateId\":\"%s\",\"operator\":\"%s\"}", 
                           event.getEventType(), event.getAggregateId(), event.getOperator());
    }
    
    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    
    public String getAggregateType() { return aggregateType; }
    public void setAggregateType(String aggregateType) { this.aggregateType = aggregateType; }
    
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    
    public String getEventVersion() { return eventVersion; }
    public void setEventVersion(String eventVersion) { this.eventVersion = eventVersion; }
    
    public String getEventSource() { return eventSource; }
    public void setEventSource(String eventSource) { this.eventSource = eventSource; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    public Boolean getImportant() { return important; }
    public void setImportant(Boolean important) { this.important = important; }
    
    public Boolean getNeedsPersistence() { return needsPersistence; }
    public void setNeedsPersistence(Boolean needsPersistence) { this.needsPersistence = needsPersistence; }
    
    public Boolean getAsyncProcessing() { return asyncProcessing; }
    public void setAsyncProcessing(Boolean asyncProcessing) { this.asyncProcessing = asyncProcessing; }
    
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    
    public String getProcessError() { return processError; }
    public void setProcessError(String processError) { this.processError = processError; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEventEntity that = (DomainEventEntity) o;
        return Objects.equals(eventId, that.eventId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
    
    @Override
    public String toString() {
        return "DomainEventEntity{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", occurredAt=" + occurredAt +
                ", operator='" + operator + '\'' +
                '}';
    }
} 