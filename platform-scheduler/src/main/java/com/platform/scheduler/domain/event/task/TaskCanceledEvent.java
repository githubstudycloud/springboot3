package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务取消事件
 * 表示任务被手动取消
 * 
 * @author platform
 */
@Getter
public class TaskCanceledEvent extends TaskEvent {
    
    private final String reason;
    
    public TaskCanceledEvent(TaskInstanceId taskInstanceId, JobId jobId, String reason) {
        super(taskInstanceId, jobId);
        this.reason = reason;
    }
}
