package com.platform.scheduler.domain.event.job;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.job.ScheduleStrategy;
import lombok.Getter;

/**
 * 作业调度策略更新事件
 * 表示作业的调度策略已更新
 * 
 * @author platform
 */
@Getter
public class JobScheduleUpdatedEvent extends JobEvent {
    
    private final ScheduleStrategy scheduleStrategy;
    
    public JobScheduleUpdatedEvent(JobId jobId, ScheduleStrategy scheduleStrategy) {
        super(jobId);
        this.scheduleStrategy = scheduleStrategy;
    }
}
