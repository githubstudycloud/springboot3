package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;

/**
 * 任务运行事件
 * 表示任务进入运行状态
 * 
 * @author platform
 */
public class TaskRunningEvent extends TaskEvent {
    
    public TaskRunningEvent(TaskInstanceId taskInstanceId, JobId jobId) {
        super(taskInstanceId, jobId);
    }
}
