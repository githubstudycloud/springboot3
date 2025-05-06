package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.event.common.AbstractDomainEvent;
import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务事件抽象类
 * 任务领域模型相关事件的基类
 * 
 * @author platform
 */
@Getter
public abstract class TaskEvent extends AbstractDomainEvent {
    
    private final TaskInstanceId taskInstanceId;
    private final JobId jobId;
    
    protected TaskEvent(TaskInstanceId taskInstanceId, JobId jobId) {
        super();
        if (taskInstanceId == null) {
            throw new IllegalArgumentException("Task instance ID cannot be null");
        }
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }
        this.taskInstanceId = taskInstanceId;
        this.jobId = jobId;
    }
}
