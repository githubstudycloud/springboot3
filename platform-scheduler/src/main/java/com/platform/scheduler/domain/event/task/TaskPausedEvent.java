package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务暂停事件
 * 表示任务被临时暂停
 * 
 * @author platform
 */
@Getter
public class TaskPausedEvent extends TaskEvent {
    
    private final String reason;
    
    public TaskPausedEvent(TaskInstanceId taskInstanceId, JobId jobId, String reason) {
        super(taskInstanceId, jobId);
        this.reason = reason;
    }
}
