package com.example.framework.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 *
 * @author platform
 * @since 1.0.0
 */
@Getter
public abstract class BaseDomainEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 事件ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final LocalDateTime occurredOn;

    /**
     * 构造函数
     */
    protected BaseDomainEvent() {
        this.eventId = UUID.randomUUID().toString().replace("-", "");
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}