package com.platform.scheduler.core.scheduler;

import java.util.Date;
import java.util.List;

import com.platform.scheduler.model.Task;

/**
 * 任务调度器接口
 * 
 * @author platform
 */
public interface TaskScheduler {
    
    /**
     * 启动任务调度器
     */
    void start();
    
    /**
     * 停止任务调度器
     */
    void stop();
    
    /**
     * 暂停任务调度器
     */
    void pause();
    
    /**
     * 恢复任务调度器
     */
    void resume();
    
    /**
     * 扫描待执行任务
     * 
     * @return 待执行任务列表
     */
    List<Task> scanTasks();
    
    /**
     * 计算任务下次执行时间
     * 
     * @param task 任务
     * @return 下次执行时间
     */
    Date calculateNextExecutionTime(Task task);
    
    /**
     * 立即执行任务
     * 
     * @param taskId 任务ID
     * @return 执行ID
     */
    Long executeTask(Long taskId);
    
    /**
     * 终止任务执行
     * 
     * @param executionId 执行ID
     * @return 是否成功
     */
    boolean terminateExecution(Long executionId);
    
    /**
     * 是否正在运行
     * 
     * @return 是否正在运行
     */
    boolean isRunning();
}
