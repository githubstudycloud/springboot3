package com.platform.scheduler.domain.event.executor;

import com.platform.scheduler.domain.event.common.AbstractDomainEvent;
import com.platform.scheduler.domain.model.executor.ExecutorId;
import lombok.Getter;

/**
 * 执行器事件抽象类
 * 执行器领域模型相关事件的基类
 * 
 * @author platform
 */
@Getter
public abstract class ExecutorEvent extends AbstractDomainEvent {
    
    private final ExecutorId executorId;
    
    protected ExecutorEvent(ExecutorId executorId) {
        super();
        if (executorId == null) {
            throw new IllegalArgumentException("Executor ID cannot be null");
        }
        this.executorId = executorId;
    }
}
