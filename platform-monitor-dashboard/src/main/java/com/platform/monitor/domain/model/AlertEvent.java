package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 告警事件领域模型
 */
@Getter
public class AlertEvent {
    
    /**
     * 事件类型
     */
    private final String eventType;
    
    /**
     * 事件名称
     */
    private final String eventName;
    
    /**
     * 操作人
     */
    private final String operator;
    
    /**
     * 事件描述
     */
    private final String description;
    
    /**
     * 事件时间
     */
    private final LocalDateTime eventTime;
    
    /**
     * 构造函数
     *
     * @param eventType 事件类型
     * @param eventName 事件名称
     * @param operator 操作人
     * @param description 事件描述
     */
    public AlertEvent(String eventType, String eventName, String operator, String description) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.operator = operator;
        this.description = description;
        this.eventTime = LocalDateTime.now();
    }
}
