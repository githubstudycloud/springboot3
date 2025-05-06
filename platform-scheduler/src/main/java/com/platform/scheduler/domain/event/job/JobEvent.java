package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.event.common.AbstractDomainEvent;
import com.platform.scheduler.domain.model.job.JobId;
import lombok.Getter;

/**
 * 作业事件抽象类
 * 作业领域模型相关事件的基类
 * 
 * @author platform
 */
@Getter
public abstract class JobEvent extends AbstractDomainEvent {
    
    private final JobId jobId;
    
    protected JobEvent(JobId jobId) {
        super();
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }
        this.jobId = jobId;
    }
}
