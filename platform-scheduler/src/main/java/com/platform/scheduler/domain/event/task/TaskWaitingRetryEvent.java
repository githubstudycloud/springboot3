package com.platform.scheduler.domain.event.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import lombok.Getter;

/**
 * 任务等待重试事件
 * 表示任务执行失败，等待重试
 * 
 * @author platform
 */
@Getter
public class TaskWaitingRetryEvent extends TaskEvent {
    
    private final int retryCount;
    private final int maxRetryCount;
    
    public TaskWaitingRetryEvent(TaskInstanceId taskInstanceId, JobId jobId, int retryCount, int maxRetryCount) {
        super(taskInstanceId, jobId);
        this.retryCount = retryCount;
        this.maxRetryCount = maxRetryCount;
    }
}
