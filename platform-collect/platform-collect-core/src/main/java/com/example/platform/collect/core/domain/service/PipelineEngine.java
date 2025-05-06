package com.example.platform.collect.core.domain.service;

import com.example.platform.collect.core.domain.model.CollectTask;
import com.example.platform.collect.core.domain.model.ExecutionContext;
import com.example.platform.collect.core.domain.model.TaskStatus;

/**
 * 流水线执行引擎接口
 * 定义采集流水线的执行逻辑
 */
public interface PipelineEngine {
    
    /**
     * 执行采集任务
     *
     * @param task 采集任务
     * @param context 执行上下文
     * @return 执行结果
     */
    ExecutionResult execute(CollectTask task, ExecutionContext context);
    
    /**
     * 暂停任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult pause(String executionId);
    
    /**
     * 恢复任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult resume(String executionId);
    
    /**
     * 取消任务执行
     *
     * @param executionId 执行ID
     * @return 操作结果
     */
    OperationResult cancel(String executionId);
    
    /**
     * 获取任务执行状态
     *
     * @param executionId 执行ID
     * @return 执行状态
     */
    ExecutionStatus getStatus(String executionId);
    
    /**
     * 执行结果类
     */
    class ExecutionResult {
        private final boolean success;
        private final String message;
        private final String executionId;
        private final TaskStatus status;
        private final ExecutionContext context;
        
        public ExecutionResult(boolean success, String message, String executionId, 
                             TaskStatus status, ExecutionContext context) {
            this.success = success;
            this.message = message;
            this.executionId = executionId;
            this.status = status;
            this.context = context;
        }
        
        public static ExecutionResult success(String executionId, ExecutionContext context) {
            return new ExecutionResult(true, "Execution completed successfully", 
                                     executionId, TaskStatus.COMPLETED, context);
        }
        
        public static ExecutionResult inProgress(String executionId, ExecutionContext context) {
            return new ExecutionResult(true, "Execution in progress", 
                                     executionId, TaskStatus.RUNNING, context);
        }
        
        public static ExecutionResult failure(String message, String executionId, ExecutionContext context) {
            return new ExecutionResult(false, message, 
                                     executionId, TaskStatus.FAILED, context);
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
        
        public TaskStatus getStatus() {
            return status;
        }
        
        public ExecutionContext getContext() {
            return context;
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
     * 执行状态类
     */
    class ExecutionStatus {
        private final String executionId;
        private final TaskStatus status;
        private final double progress;
        private final String currentStage;
        private final long startTime;
        private final Long endTime;
        private final int processedCount;
        private final int successCount;
        private final int failedCount;
        
        public ExecutionStatus(String executionId, TaskStatus status, double progress, 
                             String currentStage, long startTime, Long endTime, 
                             int processedCount, int successCount, int failedCount) {
            this.executionId = executionId;
            this.status = status;
            this.progress = progress;
            this.currentStage = currentStage;
            this.startTime = startTime;
            this.endTime = endTime;
            this.processedCount = processedCount;
            this.successCount = successCount;
            this.failedCount = failedCount;
        }
        
        public String getExecutionId() {
            return executionId;
        }
        
        public TaskStatus getStatus() {
            return status;
        }
        
        public double getProgress() {
            return progress;
        }
        
        public String getCurrentStage() {
            return currentStage;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public Long getEndTime() {
            return endTime;
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
        
        public long getDuration() {
            long end = endTime != null ? endTime : System.currentTimeMillis();
            return end - startTime;
        }
        
        public boolean isCompleted() {
            return status == TaskStatus.COMPLETED;
        }
        
        public boolean isFailed() {
            return status == TaskStatus.FAILED;
        }
        
        public boolean isRunning() {
            return status == TaskStatus.RUNNING;
        }
        
        public boolean isPaused() {
            return status == TaskStatus.PAUSED;
        }
    }
}
