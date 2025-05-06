package com.platform.scheduler.domain.event.executor;

import com.platform.scheduler.domain.model.executor.ExecutorId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 执行器离线事件
 * 表示执行器已断开连接或心跳超时
 * 
 * @author platform
 */
@Getter
public class ExecutorOfflineEvent extends ExecutorEvent {
    
    private final String reason;
    private final LocalDateTime lastHeartbeatTime;
    
    public ExecutorOfflineEvent(ExecutorId executorId, String reason, LocalDateTime lastHeartbeatTime) {
        super(executorId);
        this.reason = reason;
        this.lastHeartbeatTime = lastHeartbeatTime;
    }
}
