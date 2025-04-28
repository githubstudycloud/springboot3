package com.example.demo.domain.model.common;

import com.example.demo.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * 所有聚合根应继承此类，实现领域事件的注册和发布
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 注册领域事件
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 获取领域事件的不可变列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 获取并清除所有未发布的领域事件
     */
    public List<DomainEvent> popDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
    
    /**
     * 清除所有未发布的领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
