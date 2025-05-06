package com.platform.scheduler.domain.model.task;

import com.platform.scheduler.domain.model.common.AbstractId;

/**
 * 任务实例ID值对象
 * 
 * @author platform
 */
public class TaskInstanceId extends AbstractId {
    
    private static final long serialVersionUID = 1L;
    
    public TaskInstanceId(String id) {
        super(id);
    }
    
    /**
     * 创建新的任务实例ID
     *
     * @return 新的任务实例ID
     */
    public static TaskInstanceId newId() {
        return new TaskInstanceId(java.util.UUID.randomUUID().toString());
    }
}
