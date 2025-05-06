package com.platform.scheduler.domain.model.task;

import com.platform.scheduler.domain.event.common.DomainEventPublisher;
import com.platform.scheduler.domain.model.common.AggregateRoot;
import com.platform.scheduler.domain.model.job.JobId;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务实例聚合根
 * 表示作业的一次执行实例，跟踪任务的执行状态、结果和日志
 * 
 * @author platform
 */
@Getter
public class TaskInstance implements AggregateRoot<TaskInstanceId> {
    
    private final TaskInstanceId id;
    private final JobId jobId;
    private final String jobName;
    private final String schedulePlan;
    private final String executorId;
    private TaskStatus status;
    private Integer retryCount;
    private final Integer maxRetryCount;
    private final Integer retryInterval;
    private final Integer timeout;
    private Integer priority;
    private final Map<String, String> parameters;
    private final List<TaskLogEntry> logEntries;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime endTime;
    private Long durationInMillis;
    private String result;
    private String errorMessage;
    private String createdBy;
    private LocalDateTime createdAt;
    
    /**
     * 私有构造方法，通过Builder创建实例
     */
    private TaskInstance(Builder builder) {
        this.id = builder.id != null ? builder.id : TaskInstanceId.newId();
        this.jobId = builder.jobId;
        this.jobName = builder.jobName;
        this.schedulePlan = builder.schedulePlan;
        this.executorId = builder.executorId;
        this.status = builder.status != null ? builder.status : TaskStatus.WAITING;
        this.retryCount = builder.retryCount != null ? builder.retryCount : 0;
        this.maxRetryCount = builder.maxRetryCount;
        this.retryInterval = builder.retryInterval;
        this.timeout = builder.timeout;
        this.priority = builder.priority != null ? builder.priority : 5;
        this.parameters = new HashMap<>(builder.parameters);
        this.logEntries = new ArrayList<>(builder.logEntries);
        this.scheduledStartTime = builder.scheduledStartTime;
        this.actualStartTime = builder.actualStartTime;
        this.endTime = builder.endTime;
        this.durationInMillis = builder.durationInMillis;
        this.result = builder.result;
        this.errorMessage = builder.errorMessage;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        
        validate();
    }
    
    /**
     * 验证任务实例信息的完整性和有效性
     */
    private void validate() {
        if (jobId == null) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }
        if (jobName == null || jobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }
        if (retryCount < 0) {
            throw new IllegalArgumentException("Retry count cannot be negative");
        }
        if (maxRetryCount != null && maxRetryCount < 0) {
            throw new IllegalArgumentException("Max retry count cannot be negative");
        }
        if (retryInterval != null && retryInterval < 0) {
            throw new IllegalArgumentException("Retry interval cannot be negative");
        }
        if (timeout != null && timeout < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        if (priority != null && (priority < 1 || priority > 10)) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }
    }
    
    /**
     * 启动任务
     *
     * @param executorId 执行器ID
     * @param eventPublisher 事件发布器
     * @return 任务是否成功启动
     */
    public boolean start(String executorId, DomainEventPublisher eventPublisher) {
        if (status != TaskStatus.WAITING && status != TaskStatus.WAITING_RETRY) {
            return false;
        }
        
        this.status = TaskStatus.PREPARING;
        this.executorId = executorId;
        this.actualStartTime = LocalDateTime.now();
        
        addLogEntry(TaskLogEntry.info("任务开始准备执行"));
        
        if (eventPublisher != null) {
            // 发布任务启动事件
            eventPublisher.publish(new TaskStartedEvent(this.id, this.jobId, executorId));
        }
        
        return true;
    }
    
    /**
     * 更新任务为运行中状态
     *
     * @param eventPublisher 事件发布器
     */
    public void markRunning(DomainEventPublisher eventPublisher) {
        if (status != TaskStatus.PREPARING) {
            throw new IllegalStateException("Task must be in PREPARING state to mark as RUNNING");
        }
        
        this.status = TaskStatus.RUNNING;
        addLogEntry(TaskLogEntry.info("任务开始执行"));
        
        if (eventPublisher != null) {
            // 发布任务运行事件
            eventPublisher.publish(new TaskRunningEvent(this.id, this.jobId));
        }
    }
    
    /**
     * 标记任务为暂停状态
     *
     * @param reason 暂停原因
     * @param eventPublisher 事件发布器
     */
    public void pause(String reason, DomainEventPublisher eventPublisher) {
        if (status != TaskStatus.RUNNING) {
            throw new IllegalStateException("Task must be in RUNNING state to pause");
        }
        
        this.status = TaskStatus.PAUSED;
        addLogEntry(TaskLogEntry.warn("任务已暂停: " + (reason != null ? reason : "未提供暂停原因")));
        
        if (eventPublisher != null) {
            // 发布任务暂停事件
            eventPublisher.publish(new TaskPausedEvent(this.id, this.jobId, reason));
        }
    }
    
    /**
     * 恢复暂停的任务
     *
     * @param eventPublisher 事件发布器
     */
    public void resume(DomainEventPublisher eventPublisher) {
        if (status != TaskStatus.PAUSED) {
            throw new IllegalStateException("Task must be in PAUSED state to resume");
        }
        
        this.status = TaskStatus.RUNNING;
        addLogEntry(TaskLogEntry.info("任务已恢复执行"));
        
        if (eventPublisher != null) {
            // 发布任务恢复事件
            eventPublisher.publish(new TaskResumedEvent(this.id, this.jobId));
        }
    }
    
    /**
     * 完成任务
     *
     * @param result 任务执行结果
     * @param eventPublisher 事件发布器
     */
    public void complete(String result, DomainEventPublisher eventPublisher) {
        if (!status.isActive()) {
            throw new IllegalStateException("Task must be in active state to complete");
        }
        
        this.status = TaskStatus.COMPLETED;
        this.result = result;
        this.endTime = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.durationInMillis = Duration.between(this.actualStartTime, this.endTime).toMillis();
        }
        
        addLogEntry(TaskLogEntry.info("任务执行完成"));
        
        if (eventPublisher != null) {
            // 发布任务完成事件
            eventPublisher.publish(new TaskCompletedEvent(this.id, this.jobId, result));
        }
    }
    
    /**
     * 标记任务失败
     *
     * @param errorMessage 错误信息
     * @param eventPublisher 事件发布器
     */
    public void fail(String errorMessage, DomainEventPublisher eventPublisher) {
        if (!status.isActive() && status != TaskStatus.WAITING && status != TaskStatus.WAITING_RETRY) {
            throw new IllegalStateException("Task must be in active or waiting state to fail");
        }
        
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.durationInMillis = Duration.between(this.actualStartTime, this.endTime).toMillis();
        }
        
        // 检查是否可以重试
        if (maxRetryCount != null && maxRetryCount > 0 && retryCount < maxRetryCount) {
            this.status = TaskStatus.WAITING_RETRY;
            this.retryCount++;
            addLogEntry(TaskLogEntry.error("任务执行失败，等待第 " + retryCount + " 次重试。错误: " + errorMessage));
            
            if (eventPublisher != null) {
                // 发布任务重试事件
                eventPublisher.publish(new TaskWaitingRetryEvent(this.id, this.jobId, retryCount, maxRetryCount));
            }
        } else {
            this.status = TaskStatus.FAILED;
            addLogEntry(TaskLogEntry.error("任务执行失败: " + errorMessage));
            
            if (eventPublisher != null) {
                // 发布任务失败事件
                eventPublisher.publish(new TaskFailedEvent(this.id, this.jobId, errorMessage));
            }
        }
    }
    
    /**
     * 取消任务
     *
     * @param reason 取消原因
     * @param eventPublisher 事件发布器
     */
    public void cancel(String reason, DomainEventPublisher eventPublisher) {
        if (status.isTerminated()) {
            throw new IllegalStateException("Cannot cancel a terminated task");
        }
        
        this.status = TaskStatus.CANCELED;
        this.endTime = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.durationInMillis = Duration.between(this.actualStartTime, this.endTime).toMillis();
        }
        
        addLogEntry(TaskLogEntry.warn("任务已取消: " + (reason != null ? reason : "未提供取消原因")));
        
        if (eventPublisher != null) {
            // 发布任务取消事件
            eventPublisher.publish(new TaskCanceledEvent(this.id, this.jobId, reason));
        }
    }
    
    /**
     * 标记任务超时
     *
     * @param eventPublisher 事件发布器
     */
    public void timeout(DomainEventPublisher eventPublisher) {
        if (!status.isActive()) {
            throw new IllegalStateException("Task must be in active state to timeout");
        }
        
        this.status = TaskStatus.TIMEOUT;
        this.endTime = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.durationInMillis = Duration.between(this.actualStartTime, this.endTime).toMillis();
        }
        
        addLogEntry(TaskLogEntry.error("任务执行超时"));
        
        if (eventPublisher != null) {
            // 发布任务超时事件
            eventPublisher.publish(new TaskTimeoutEvent(this.id, this.jobId));
        }
    }
    
    /**
     * 添加任务日志条目
     *
     * @param logEntry 日志条目
     */
    public void addLogEntry(TaskLogEntry logEntry) {
        if (logEntry != null) {
            this.logEntries.add(logEntry);
        }
    }
    
    /**
     * 获取任务日志条目列表
     *
     * @return 日志条目列表的不可修改视图
     */
    public List<TaskLogEntry> getLogEntries() {
        return Collections.unmodifiableList(logEntries);
    }
    
    /**
     * 判断是否可以重试
     *
     * @return 如果当前状态可重试且未达到最大重试次数，则返回true
     */
    public boolean canRetry() {
        return status.isRetryable() 
                && maxRetryCount != null 
                && maxRetryCount > 0 
                && retryCount < maxRetryCount;
    }
    
    /**
     * 计算下次重试时间
     *
     * @return 下次重试时间，如果不可重试则返回null
     */
    public LocalDateTime getNextRetryTime() {
        if (status != TaskStatus.WAITING_RETRY || retryInterval == null || retryInterval <= 0) {
            return null;
        }
        
        return endTime != null ? 
                endTime.plusSeconds(retryInterval) : 
                LocalDateTime.now().plusSeconds(retryInterval);
    }
    
    /**
     * 判断任务是否已超时
     *
     * @param currentTime 当前时间
     * @return 如果任务已超过超时时间，则返回true
     */
    public boolean isTimedOut(LocalDateTime currentTime) {
        if (status != TaskStatus.RUNNING || timeout == null || timeout <= 0 || actualStartTime == null) {
            return false;
        }
        
        return Duration.between(actualStartTime, currentTime).getSeconds() > timeout;
    }
    
    /**
     * 获取构建器
     *
     * @return 任务实例构建器
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 任务实例构建器
     */
    public static class Builder {
        private TaskInstanceId id;
        private JobId jobId;
        private String jobName;
        private String schedulePlan;
        private String executorId;
        private TaskStatus status;
        private Integer retryCount;
        private Integer maxRetryCount;
        private Integer retryInterval;
        private Integer timeout;
        private Integer priority;
        private final Map<String, String> parameters = new HashMap<>();
        private final List<TaskLogEntry> logEntries = new ArrayList<>();
        private LocalDateTime scheduledStartTime;
        private LocalDateTime actualStartTime;
        private LocalDateTime endTime;
        private Long durationInMillis;
        private String result;
        private String errorMessage;
        private String createdBy;
        private LocalDateTime createdAt;
        
        public Builder withId(TaskInstanceId id) {
            this.id = id;
            return this;
        }
        
        public Builder withJobId(JobId jobId) {
            this.jobId = jobId;
            return this;
        }
        
        public Builder withJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }
        
        public Builder withSchedulePlan(String schedulePlan) {
            this.schedulePlan = schedulePlan;
            return this;
        }
        
        public Builder withExecutorId(String executorId) {
            this.executorId = executorId;
            return this;
        }
        
        public Builder withStatus(TaskStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder withRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }
        
        public Builder withMaxRetryCount(Integer maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
            return this;
        }
        
        public Builder withRetryInterval(Integer retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }
        
        public Builder withTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }
        
        public Builder withPriority(Integer priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder withParameter(String key, String value) {
            if (key != null && !key.trim().isEmpty()) {
                this.parameters.put(key, value);
            }
            return this;
        }
        
        public Builder withParameters(Map<String, String> parameters) {
            if (parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }
        
        public Builder withLogEntry(TaskLogEntry logEntry) {
            if (logEntry != null) {
                this.logEntries.add(logEntry);
            }
            return this;
        }
        
        public Builder withLogEntries(List<TaskLogEntry> logEntries) {
            if (logEntries != null) {
                this.logEntries.addAll(logEntries);
            }
            return this;
        }
        
        public Builder withScheduledStartTime(LocalDateTime scheduledStartTime) {
            this.scheduledStartTime = scheduledStartTime;
            return this;
        }
        
        public Builder withActualStartTime(LocalDateTime actualStartTime) {
            this.actualStartTime = actualStartTime;
            return this;
        }
        
        public Builder withEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder withDurationInMillis(Long durationInMillis) {
            this.durationInMillis = durationInMillis;
            return this;
        }
        
        public Builder withResult(String result) {
            this.result = result;
            return this;
        }
        
        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public Builder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }
        
        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public TaskInstance build() {
            return new TaskInstance(this);
        }
    }
}
