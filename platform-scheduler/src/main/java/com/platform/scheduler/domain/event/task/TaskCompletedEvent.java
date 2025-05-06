package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务完成事件
 * 表示任务成功完成执行
 * 
 * @author platform
 */
@Getter
public class TaskCompletedEvent extends TaskEvent {
    
    private final String result;
    
    public TaskCompletedEvent(TaskInstanceId taskInstanceId, JobId jobId, String result) {
        super(taskInstanceId, jobId);
        this.result = result;
    }
}
