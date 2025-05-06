package com.example.platform.collect.core.infrastructure.adapter;

import com.example.platform.collect.core.domain.model.CollectTask;

import java.util.List;
import java.util.Map;

/**
 * 调度系统适配器接口
 * 实现与平台统一调度系统的集成
 */
public interface SchedulerAdapter {
    
    /**
     * 注册采集任务到调度系统
     *
     * @param task 采集任务
     * @param scheduleConfig 调度配置
     * @return 注册结果
     */
    RegistrationResult registerTask(CollectTask task, ScheduleConfig scheduleConfig);
    
    /**
     * 更新调度系统中的任务
     *
     * @param task 采集任务
     * @param scheduleConfig 调度配置
     * @return 更新结果
     */
    RegistrationResult updateTask(CollectTask task, ScheduleConfig scheduleConfig);
    
    /**
     * 从调度系统中注销任务
     *
     * @param taskId 任务ID
     * @return 注销结果
     */
    OperationResult unregisterTask(String taskId);
    
    /**
     * 暂停任务调度
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    OperationResult pauseTask(String taskId);
    
    /**
     * 恢复任务调度
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    OperationResult resumeTask(String taskId);
    
    /**
     * 立即触发任务执行
     *
     * @param taskId 任务ID
     * @param parameters 执行参数
     * @return 操作结果
     */
    OperationResult triggerTask(String taskId, Map<String, Object> parameters);
    
    /**
     * 获取任务调度状态
     *
     * @param taskId 任务ID
     * @return 调度状态
     */
    ScheduleStatus getTaskStatus(String taskId);
    
    /**
     * 获取任务下次执行时间
     *
     * @param taskId 任务ID
     * @return 下次执行时间（毫秒时间戳）
     */
    Long getNextExecutionTime(String taskId);
    
    /**
     * 获取任务执行历史
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @return 执行历史列表
     */
    List<ExecutionRecord> getExecutionHistory(String taskId, int limit);
    
    /**
     * 设置任务依赖关系
     *
     * @param taskId 任务ID
     * @param dependsOnTaskIds 依赖任务ID列表
     * @param dependencyConfig 依赖配置
     * @return 操作结果
     */
    OperationResult setDependencies(String taskId, List<String> dependsOnTaskIds, DependencyConfig dependencyConfig);
    
    /**
     * 调度配置类
     */
    class ScheduleConfig {
        private String cronExpression;
        private Long fixedDelay;
        private Long fixedRate;
        private Long initialDelay;
        private String timezone;
        private boolean concurrent;
        private int retryCount;
        private long retryInterval;
        private String description;
        
        // 构造器、getter和setter方法
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final ScheduleConfig config = new ScheduleConfig();
            
            public Builder withCron(String cronExpression) {
                config.cronExpression = cronExpression;
                return this;
            }
            
            public Builder withFixedDelay(long fixedDelay) {
                config.fixedDelay = fixedDelay;
                return this;
            }
            
            public Builder withFixedRate(long fixedRate) {
                config.fixedRate = fixedRate;
                return this;
            }
            
            public Builder withInitialDelay(long initialDelay) {
                config.initialDelay = initialDelay;
                return this;
            }
            
            public Builder withTimezone(String timezone) {
                config.timezone = timezone;
                return this;
            }
            
            public Builder allowConcurrent(boolean concurrent) {
                config.concurrent = concurrent;
                return this;
            }
            
            public Builder withRetry(int count, long interval) {
                config.retryCount = count;
                config.retryInterval = interval;
                return this;
            }
            
            public Builder withDescription(String description) {
                config.description = description;
                return this;
            }
            
            public ScheduleConfig build() {
                return config;
            }
        }
        
        // Getters
        public String getCronExpression() {
            return cronExpression;
        }
        
        public Long getFixedDelay() {
            return fixedDelay;
        }
        
        public Long getFixedRate() {
            return fixedRate;
        }
        
        public Long getInitialDelay() {
            return initialDelay;
        }
        
        public String getTimezone() {
            return timezone;
        }
        
        public boolean isConcurrent() {
            return concurrent;
        }
        
        public int getRetryCount() {
            return retryCount;
        }
        
        public long getRetryInterval() {
            return retryInterval;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 依赖配置类
     */
    class DependencyConfig {
        private boolean waitForAll;
        private String condition;
        private long timeout;
        private DependencyType type;
        private String description;
        
        // 构造器、getter和setter方法
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final DependencyConfig config = new DependencyConfig();
            
            public Builder waitForAll(boolean waitForAll) {
                config.waitForAll = waitForAll;
                return this;
            }
            
            public Builder withCondition(String condition) {
                config.condition = condition;
                return this;
            }
            
            public Builder withTimeout(long timeout) {
                config.timeout = timeout;
                return this;
            }
            
            public Builder withType(DependencyType type) {
                config.type = type;
                return this;
            }
            
            public Builder withDescription(String description) {
                config.description = description;
                return this;
            }
            
            public DependencyConfig build() {
                return config;
            }
        }
        
        // Getters
        public boolean isWaitForAll() {
            return waitForAll;
        }
        
        public String getCondition() {
            return condition;
        }
        
        public long getTimeout() {
            return timeout;
        }
        
        public DependencyType getType() {
            return type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public enum DependencyType {
            HARD, // 强依赖，依赖任务失败则当前任务不执行
            SOFT  // 软依赖，依赖任务失败后当前任务仍可执行
        }
    }
    
    /**
     * 注册结果类
     */
    class RegistrationResult {
        private final boolean success;
        private final String message;
        private final String schedulerId;
        
        public RegistrationResult(boolean success, String message, String schedulerId) {
            this.success = success;
            this.message = message;
            this.schedulerId = schedulerId;
        }
        
        public static RegistrationResult success(String message, String schedulerId) {
            return new RegistrationResult(true, message, schedulerId);
        }
        
        public static RegistrationResult failure(String message) {
            return new RegistrationResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getSchedulerId() {
            return schedulerId;
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
     * 调度状态枚举
     */
    enum ScheduleStatus {
        REGISTERED,  // 已注册
        RUNNING,     // 正在运行
        PAUSED,      // 已暂停
        DISABLED,    // 已禁用
        COMPLETED,   // 已完成
        ERROR,       // 错误状态
        UNREGISTERED // 未注册
    }
    
    /**
     * 执行记录类
     */
    class ExecutionRecord {
        private final String executionId;
        private final String taskId;
        private final long startTime;
        private final Long endTime;
        private final String status;
        private final String result;
        private final String errorMessage;
        
        public ExecutionRecord(String executionId, String taskId, long startTime, 
                               Long endTime, String status, String result, String errorMessage) {
            this.executionId = executionId;
            this.taskId = taskId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.result = result;
            this.errorMessage = errorMessage;
        }
        
        public String getExecutionId() {
            return executionId;
        }
        
        public String getTaskId() {
            return taskId;
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
        
        public String getResult() {
            return result;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public long getDuration() {
            if (endTime == null) {
                return System.currentTimeMillis() - startTime;
            }
            return endTime - startTime;
        }
    }
}
