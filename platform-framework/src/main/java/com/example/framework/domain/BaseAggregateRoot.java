package com.example.framework.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 *
 * @param <ID> ID类型
 * @author platform
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class BaseAggregateRoot<ID> extends BaseEntity {
    private static final long serialVersionUID = 1L;
    
    /**
     * 唯一标识
     */
    protected ID id;
    
    /**
     * 领域事件列表
     */
    private transient final List<BaseDomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 添加领域事件
     *
     * @param event 领域事件
     */
    protected void registerEvent(BaseDomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 清除领域事件
     */
    public void clearEvents() {
        this.domainEvents.clear();
    }
    
    /**
     * 获取领域事件列表（不可修改）
     *
     * @return 领域事件列表
     */
    public List<BaseDomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}