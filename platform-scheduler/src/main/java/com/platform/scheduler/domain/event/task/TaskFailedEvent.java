package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务失败事件
 * 表示任务执行失败
 * 
 * @author platform
 */
@Getter
public class TaskFailedEvent extends TaskEvent {
    
    private final String errorMessage;
    
    public TaskFailedEvent(TaskInstanceId taskInstanceId, JobId jobId, String errorMessage) {
        super(taskInstanceId, jobId);
        this.errorMessage = errorMessage;
    }
}
