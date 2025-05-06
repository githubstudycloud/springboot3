package com.platform.scheduler.domain.event.common;

import java.time.LocalDateTime;

/**
 * 领域事件接口
 * 领域事件是发生在系统内部的事件，表示领域对象状态变化或业务事件。
 * 
 * @author platform
 */
public interface DomainEvent {
    
    /**
     * 获取事件发生时间
     *
     * @return 事件时间
     */
    LocalDateTime getOccurredOn();
    
    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    String getEventType();
}
