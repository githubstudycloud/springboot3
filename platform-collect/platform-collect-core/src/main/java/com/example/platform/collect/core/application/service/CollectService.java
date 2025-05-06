package com.example.platform.collect.core.application.service;

import com.example.platform.collect.core.domain.model.CollectTask;
import com.example.platform.collect.core.domain.model.DataSource;
import com.example.platform.collect.core.domain.model.ExecutionContext;
import com.example.platform.collect.core.domain.service.PipelineEngine;

import java.util.List;
import java.util.Map;

/**
 * 采集服务接口
 * 定义采集任务的管理和执行
 */
public interface CollectService {
    
    /**
     * 创建数据源
     *
     * @param dataSource 数据源信息
     * @return 创建结果
     */
    DataSourceResult createDataSource(DataSource dataSource);
    
    /**
     * 更新数据源
     *
     * @param dataSource 数据源信息
     * @return 更新结果
     */
    DataSourceResult updateDataSource(DataSource dataSource);
    
    /**
     * 删除数据源
     *
     * @param dataSourceId 数据源ID
     * @return 删除结果
     */
    OperationResult deleteDataSource(String dataSourceId);
    
    /**
     * 获取数据源
     *
     * @param dataSourceId 数据源ID
     * @return 数据源
     */
    DataSource getDataSource(String dataSourceId);
    
    /**
     * 查询数据源列表
     *
     * @param criteria 查询条件
     * @return 数据源列表
     */
    List<DataSource> queryDataSources(Map<String, Object> criteria);
    
    /**
     * 测试数据源连接
     *
     * @param dataSourceId 数据源ID
     * @return 测试结果
     */
    ConnectionTestResult testDataSourceConnection(String dataSourceId);
    
    /**
     * 创建采集任务
     *
     * @param task 采集任务信息
     * @return 创建结果
     */
    TaskResult createTask(CollectTask task);
    
    /**
     * 更新采集任务
     *
     * @param task 采集任务信息
     * @return 更新结果
     */
    TaskResult updateTask(CollectTask task);
    
    /**
     * 删除采集任务
     *
     * @param taskId 任务ID
     * @return 删除结果
     */
    OperationResult deleteTask(String taskId);
    
    /**
     * 获取采集任务
     *
     * @param taskId 任务ID
     * @return 采集任务
     */
    CollectTask getTask(String taskId);
    
    /**
     * 查询采集任务列表
     *
     * @param criteria 查询条件
     * @return 任务列表
     */
    List<CollectTask> queryTasks(Map<String, Object> criteria);
    
    /**
     * 启用采集任务
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    OperationResult enableTask(String taskId);
    
    /**
     * 禁用采集任务
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    OperationResult disableTask(String taskId);
    
    /**
     * 执行采集任务
     *
     * @param taskId 任务ID
     * @param parameters 执行参数
     * @return 执行结果
     */
    ExecutionResult executeTask(String taskId, Map<String, Object> parameters);
    
    /**
     * 暂停任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult pauseExecution(String executionId);
    
    /**
     * 恢复任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult resumeExecution(String executionId);
    
    /**
     * 取消任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult cancelExecution(String executionId);
    
    /**
     * 获取执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    PipelineEngine.ExecutionStatus getExecutionStatus(String executionId);
    
    /**
     * 获取执行历史
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @return 执行历史列表
     */
    List<ExecutionHistory> getExecutionHistory(String taskId, int limit);
    
    /**
     * 数据源结果类
     */
    class DataSourceResult {
        private final boolean success;
        private final String message;
        private final String dataSourceId;
        
        public DataSourceResult(boolean success, String message, String dataSourceId) {
            this.success = success;
            this.message = message;
            this.dataSourceId = dataSourceId;
        }
        
        public static DataSourceResult success(String message, String dataSourceId) {
            return new DataSourceResult(true, message, dataSourceId);
        }
        
        public static DataSourceResult failure(String message) {
            return new DataSourceResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getDataSourceId() {
            return dataSourceId;
        }
    }
    
    /**
     * 任务结果类
     */
    class TaskResult {
        private final boolean success;
        private final String message;
        private final String taskId;
        
        public TaskResult(boolean success, String message, String taskId) {
            this.success = success;
            this.message = message;
            this.taskId = taskId;
        }
        
        public static TaskResult success(String message, String taskId) {
            return new TaskResult(true, message, taskId);
        }
        
        public static TaskResult failure(String message) {
            return new TaskResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getTaskId() {
            return taskId;
        }
    }
    
    /**
     * 操作结果类
     */
    class OperationResult {
        private final boolean success;
        private final String message;
        
        public OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static OperationResult success(String message) {
            return new OperationResult(true, message);
        }
        
        public static OperationResult failure(String message) {
            return new OperationResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * 执行结果类
     */
    class ExecutionResult {
        private final boolean success;
        private final String message;
        private final String executionId;
        private final ExecutionContext context;
        
        public ExecutionResult(boolean success, String message, String executionId, 
                             ExecutionContext context) {
            this.success = success;
            this.message = message;
            this.executionId = executionId;
            this.context = context;
        }
        
        public static ExecutionResult success(String executionId, ExecutionContext context) {
            return new ExecutionResult(true, "Execution started successfully", 
                                     executionId, context);
        }
        
        public static ExecutionResult failure(String message) {
            return new ExecutionResult(false, message, null, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getExecutionId() {
            return executionId;
        }
        
        public ExecutionContext getContext() {
            return context;
        }
    }
    
    /**
     * 连接测试结果类
     */
    class ConnectionTestResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> details;
        
        public ConnectionTestResult(boolean success, String message, Map<String, Object> details) {
            this.success = success;
            this.message = message;
            this.details = details;
        }
        
        public static ConnectionTestResult success(String message, Map<String, Object> details) {
            return new ConnectionTestResult(true, message, details);
        }
        
        public static ConnectionTestResult failure(String message, Map<String, Object> details) {
            return new ConnectionTestResult(false, message, details);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Map<String, Object> getDetails() {
            return details;
        }
    }
    
    /**
     * 执行历史类
     */
    class ExecutionHistory {
        private final String executionId;
        private final String taskId;
        private final String taskName;
        private final long startTime;
        private final Long endTime;
        private final String status;
        private final int processedCount;
        private final int successCount;
        private final int failedCount;
        private final String executedBy;
        
        public ExecutionHistory(String executionId, String taskId, String taskName, 
                              long startTime, Long endTime, String status, 
                              int processedCount, int successCount, int failedCount, 
                              String executedBy) {
            this.executionId = executionId;
            this.taskId = taskId;
            this.taskName = taskName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.processedCount = processedCount;
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.executedBy = executedBy;
        }
        
        public String getExecutionId() {
            return executionId;
        }
        
        public String getTaskId() {
            return taskId;
        }
        
        public String getTaskName() {
            return taskName;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public Long getEndTime() {
            return endTime;
        }
        
        public String getStatus() {
            return status;
        }
        
        public int getProcessedCount() {
            return processedCount;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public String getExecutedBy() {
            return executedBy;
        }
        
        public long getDuration() {
            if (endTime == null) {
                return System.currentTimeMillis() - startTime;
            }
            return endTime - startTime;
        }
        
        public boolean isCompleted() {
            return "COMPLETED".equals(status);
        }
        
        public boolean isFailed() {
            return "FAILED".equals(status);
        }
    }
}
