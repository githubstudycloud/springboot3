package com.platform.scheduler.core.executor;

import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskResult;

/**
 * 任务执行器接口
 * 
 * @author platform
 */
public interface TaskExecutor {
    
    /**
     * 执行任务
     * 
     * @param task 任务
     * @param executionId 执行ID
     * @return 执行结果
     * @throws Exception 执行异常
     */
    TaskResult execute(Task task, Long executionId) throws Exception;
    
    /**
     * 获取支持的任务类型
     * 
     * @return 任务类型
     */
    String getType();
    
    /**
     * 中断任务执行
     * 
     * @param executionId 执行ID
     * @return 是否成功
     */
    boolean terminate(Long executionId);
    
    /**
     * 设置任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param progress 进度(0-100)
     * @param statusDesc 状态描述
     */
    void updateProgress(Long taskId, Long executionId, int progress, String statusDesc);
}
