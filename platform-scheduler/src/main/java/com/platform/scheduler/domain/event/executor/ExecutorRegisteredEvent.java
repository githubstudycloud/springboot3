package com.platform.scheduler.domain.event.executor;

import com.platform.scheduler.domain.model.executor.ExecutorId;
import com.platform.scheduler.domain.model.executor.ExecutorType;
import lombok.Getter;

/**
 * 执行器注册事件
 * 表示新的执行器节点注册到系统
 * 
 * @author platform
 */
@Getter
public class ExecutorRegisteredEvent extends ExecutorEvent {
    
    private final String name;
    private final ExecutorType type;
    private final String host;
    private final Integer port;
    
    public ExecutorRegisteredEvent(ExecutorId executorId, String name, ExecutorType type, String host, Integer port) {
        super(executorId);
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
    }
}
