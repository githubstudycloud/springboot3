package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;
import lombok.Getter;

/**
 * 作业创建事件
 * 表示一个新的作业被创建
 * 
 * @author platform
 */
@Getter
public class JobCreatedEvent extends JobEvent {
    
    private final String jobName;
    private final String jobType;
    
    public JobCreatedEvent(JobId jobId, String jobName, String jobType) {
        super(jobId);
        this.jobName = jobName;
        this.jobType = jobType;
    }
}
