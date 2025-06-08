package com.platform.domain.config.shared;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领域事件存储接口
 * DDD基础设施 - 持久化领域事件
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public interface DomainEventStore {
    
    /**
     * 保存领域事件
     */
    void save(DomainEvent event);
    
    /**
     * 批量保存领域事件
     */
    void saveAll(List<DomainEvent> events);
    
    /**
     * 根据聚合根ID查找事件
     */
    List<DomainEvent> findByAggregateId(String aggregateId);
    
    /**
     * 根据事件类型查找事件
     */
    List<DomainEvent> findByEventType(String eventType);
    
    /**
     * 根据时间范围查找事件
     */
    List<DomainEvent> findByTimeRange(LocalDateTime from, LocalDateTime to);
    
    /**
     * 根据操作人查找事件
     */
    List<DomainEvent> findByOperator(String operator);
    
    /**
     * 查找重要事件
     */
    List<DomainEvent> findImportantEvents(LocalDateTime from, LocalDateTime to);
    
    /**
     * 删除过期事件
     */
    void deleteExpiredEvents(LocalDateTime expiredBefore);
    
    /**
     * 统计事件数量
     */
    long countEvents();
    
    /**
     * 根据聚合根类型统计事件数量
     */
    long countEventsByAggregateType(String aggregateType);
} 