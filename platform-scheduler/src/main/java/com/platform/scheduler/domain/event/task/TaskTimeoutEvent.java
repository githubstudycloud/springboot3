package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;

/**
 * 任务超时事件
 * 表示任务执行时间超过设定的超时时间
 * 
 * @author platform
 */
public class TaskTimeoutEvent extends TaskEvent {
    
    public TaskTimeoutEvent(TaskInstanceId taskInstanceId, JobId jobId) {
        super(taskInstanceId, jobId);
    }
}
