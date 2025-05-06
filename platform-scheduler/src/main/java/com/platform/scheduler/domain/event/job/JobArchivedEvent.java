package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;

/**
 * 作业归档事件
 * 表示作业状态变更为已归档，不再使用但保留记录
 * 
 * @author platform
 */
public class JobArchivedEvent extends JobEvent {
    
    public JobArchivedEvent(JobId jobId) {
        super(jobId);
    }
}
