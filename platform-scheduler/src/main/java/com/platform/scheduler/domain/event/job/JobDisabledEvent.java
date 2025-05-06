package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;

/**
 * 作业禁用事件
 * 表示作业状态变更为已禁用，暂停调度
 * 
 * @author platform
 */
public class JobDisabledEvent extends JobEvent {
    
    public JobDisabledEvent(JobId jobId) {
        super(jobId);
    }
}
