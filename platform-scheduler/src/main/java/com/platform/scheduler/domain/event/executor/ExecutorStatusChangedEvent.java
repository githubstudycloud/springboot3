package com.platform.scheduler.domain.event.executor;

import com.platform.scheduler.domain.model.executor.ExecutorId;
import com.platform.scheduler.domain.model.executor.ExecutorStatus;
import lombok.Getter;

/**
 * 执行器状态变更事件
 * 表示执行器状态发生变化
 * 
 * @author platform
 */
@Getter
public class ExecutorStatusChangedEvent extends ExecutorEvent {
    
    private final ExecutorStatus oldStatus;
    private final ExecutorStatus newStatus;
    private final String reason;
    
    public ExecutorStatusChangedEvent(ExecutorId executorId, ExecutorStatus oldStatus, ExecutorStatus newStatus, String reason) {
        super(executorId);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.reason = reason;
    }
}
