package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;

/**
 * 作业删除事件
 * 表示作业状态变更为已删除（逻辑删除）
 * 
 * @author platform
 */
public class JobDeletedEvent extends JobEvent {
    
    public JobDeletedEvent(JobId jobId) {
        super(jobId);
    }
}
