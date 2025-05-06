package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务启动事件
 * 表示任务开始准备执行
 * 
 * @author platform
 */
@Getter
public class TaskStartedEvent extends TaskEvent {
    
    private final String executorId;
    
    public TaskStartedEvent(TaskInstanceId taskInstanceId, JobId jobId, String executorId) {
        super(taskInstanceId, jobId);
        this.executorId = executorId;
    }
}
