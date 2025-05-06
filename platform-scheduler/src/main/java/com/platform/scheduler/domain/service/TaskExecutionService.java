package com.platform.scheduler.domain.service;

import com.platform.scheduler.domain.model.executor.ExecutorId;
import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstance;
import com.platform.scheduler.domain.model.task.TaskInstanceId;

import java.util.Map;
import java.util.Optional;

/**
 * 任务执行服务接口
 * 定义任务执行相关的领域服务
 * 
 * @author platform
 */
public interface TaskExecutionService {
    
    /**
     * 创建任务实例
     *
     * @param jobId 作业ID
     * @param parameters 执行参数
     * @param scheduledStartTime 计划开始时间
     * @return 创建的任务实例
     */
    TaskInstance createTaskInstance(JobId jobId, Map<String, String> parameters, java.time.LocalDateTime scheduledStartTime);
    
    /**
     * 分配任务到执行器
     *
     * @param taskInstanceId 任务实例ID
     * @param executorId 执行器ID
     * @return 是否成功分配
     */
    boolean assignTaskToExecutor(TaskInstanceId taskInstanceId, ExecutorId executorId);
    
    /**
     * 启动任务执行
     *
     * @param taskInstanceId 任务实例ID
     * @return 是否成功启动
     */
    boolean startTaskExecution(TaskInstanceId taskInstanceId);
    
    /**
     * 暂停任务执行
     *
     * @param taskInstanceId 任务实例ID
     * @param reason 暂停原因
     * @return 是否成功暂停
     */
    boolean pauseTaskExecution(TaskInstanceId taskInstanceId, String reason);
    
    /**
     * 恢复任务执行
     *
     * @param taskInstanceId 任务实例ID
     * @return 是否成功恢复
     */
    boolean resumeTaskExecution(TaskInstanceId taskInstanceId);
    
    /**
     * 完成任务执行
     *
     * @param taskInstanceId 任务实例ID
     * @param result 执行结果
     * @return 是否成功完成
     */
    boolean completeTaskExecution(TaskInstanceId taskInstanceId, String result);
    
    /**
     * 标记任务执行失败
     *
     * @param taskInstanceId 任务实例ID
     * @param errorMessage 错误信息
     * @return 是否成功标记
     */
    boolean failTaskExecution(TaskInstanceId taskInstanceId, String errorMessage);
    
    /**
     * 取消任务执行
     *
     * @param taskInstanceId 任务实例ID
     * @param reason 取消原因
     * @return 是否成功取消
     */
    boolean cancelTaskExecution(TaskInstanceId taskInstanceId, String reason);
    
    /**
     * 超时处理
     * 检查和处理超时的任务
     *
     * @return 处理的任务数量
     */
    int handleTimeoutTasks();
    
    /**
     * 重试处理
     * 检查和处理等待重试的任务
     *
     * @return 处理的任务数量
     */
    int handleRetryTasks();
    
    /**
     * 添加任务日志
     *
     * @param taskInstanceId 任务实例ID
     * @param logLevel 日志级别
     * @param message 日志消息
     * @return 是否成功添加
     */
    boolean addTaskLog(TaskInstanceId taskInstanceId, String logLevel, String message);
    
    /**
     * 根据作业ID查找最近一次执行的任务实例
     *
     * @param jobId 作业ID
     * @return 最近一次任务实例
     */
    Optional<TaskInstance> findLatestTaskByJobId(JobId jobId);
    
    /**
     * 根据执行器ID查找正在执行的任务
     *
     * @param executorId 执行器ID
     * @return 正在执行的任务列表
     */
    java.util.List<TaskInstance> findRunningTasksByExecutorId(ExecutorId executorId);
    
    /**
     * 清理历史任务数据
     *
     * @param retentionDays 保留天数
     * @return 清理的任务数量
     */
    int cleanHistoricalTasks(int retentionDays);
}
