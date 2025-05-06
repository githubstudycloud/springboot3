package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;

/**
 * 作业启用事件
 * 表示作业状态变更为已启用，可被调度执行
 * 
 * @author platform
 */
public class JobEnabledEvent extends JobEvent {
    
    public JobEnabledEvent(JobId jobId) {
        super(jobId);
    }
}
