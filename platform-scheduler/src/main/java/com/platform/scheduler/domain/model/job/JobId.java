package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.model.common.AbstractId;

/**
 * 作业ID值对象
 * 
 * @author platform
 */
public class JobId extends AbstractId {
    
    private static final long serialVersionUID = 1L;
    
    public JobId(String id) {
        super(id);
    }
    
    /**
     * 创建新的作业ID
     *
     * @return 新的作业ID
     */
    public static JobId newId() {
        return new JobId(java.util.UUID.randomUUID().toString());
    }
}
