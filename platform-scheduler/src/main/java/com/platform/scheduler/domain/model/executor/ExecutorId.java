package com.platform.scheduler.domain.model.executor;

import com.platform.scheduler.domain.model.common.AbstractId;

/**
 * 执行器ID值对象
 * 
 * @author platform
 */
public class ExecutorId extends AbstractId {
    
    private static final long serialVersionUID = 1L;
    
    public ExecutorId(String id) {
        super(id);
    }
    
    /**
     * 创建新的执行器ID
     *
     * @return 新的执行器ID
     */
    public static ExecutorId newId() {
        return new ExecutorId(java.util.UUID.randomUUID().toString());
    }
}
