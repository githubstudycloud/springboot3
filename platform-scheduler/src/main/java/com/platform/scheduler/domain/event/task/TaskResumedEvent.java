package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;

/**
 * 任务恢复事件
 * 表示暂停的任务恢复执行
 * 
 * @author platform
 */
public class TaskResumedEvent extends TaskEvent {
    
    public TaskResumedEvent(TaskInstanceId taskInstanceId, JobId jobId) {
        super(taskInstanceId, jobId);
    }
}
