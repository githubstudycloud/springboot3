package com.platform.scheduler.domain.event.common;

/**
 * 领域事件发布器接口
 * 负责发布领域事件到相应的处理器
 * 
 * @author platform
 */
public interface DomainEventPublisher {
    
    /**
     * 发布领域事件
     *
     * @param event 领域事件对象
     */
    void publish(DomainEvent event);
}
