package com.platform.scheduler.query.domain.model.task;

import com.platform.scheduler.domain.model.task.TaskLogEntry;
import com.platform.scheduler.domain.model.task.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 任务实例视图对象
 * 面向查询的任务实例只读视图，不包含行为和领域逻辑
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class TaskInstanceView {
    
    /**
     * 任务实例ID
     */
    private final String id;
    
    /**
     * 作业ID
     */
    private final String jobId;
    
    /**
     * 作业名称
     */
    private final String jobName;
    
    /**
     * 调度计划
     */
    private final String schedulePlan;
    
    /**
     * 执行器ID
     */
    private final String executorId;
    
    /**
     * 状态
     */
    private final TaskStatus status;
    
    /**
     * 重试次数
     */
    private final Integer retryCount;
    
    /**
     * 最大重试次数
     */
    private final Integer maxRetryCount;
    
    /**
     * 优先级
     */
    private final Integer priority;
    
    /**
     * 计划开始时间
     */
    private final LocalDateTime scheduledStartTime;
    
    /**
     * 实际开始时间
     */
    private final LocalDateTime actualStartTime;
    
    /**
     * 结束时间
     */
    private final LocalDateTime endTime;
    
    /**
     * 持续时间(毫秒)
     */
    private final Long durationInMillis;
    
    /**
     * 执行结果
     */
    private final String result;
    
    /**
     * 错误信息
     */
    private final String errorMessage;
    
    /**
     * 创建者
     */
    private final String createdBy;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 任务参数
     */
    private final Map<String, String> parameters;
    
    /**
     * 任务日志条目
     */
    private final List<TaskLogEntry> logEntries;
    
    /**
     * 获取持续时间的格式化字符串
     *
     * @return 格式化后的持续时间
     */
    public String getFormattedDuration() {
        if (durationInMillis == null || durationInMillis <= 0) {
            return "N/A";
        }
        
        long totalSeconds = durationInMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分%d秒", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d分%d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    /**
     * 获取任务参数
     *
     * @return 不可修改的参数Map
     */
    public Map<String, String> getParameters() {
        return parameters != null ? Collections.unmodifiableMap(parameters) : Collections.emptyMap();
    }
    
    /**
     * 获取任务日志条目
     *
     * @return 不可修改的日志条目列表
     */
    public List<TaskLogEntry> getLogEntries() {
        return logEntries != null ? Collections.unmodifiableList(logEntries) : Collections.emptyList();
    }
    
    /**
     * 检查任务是否成功完成
     *
     * @return 如果状态为COMPLETED则返回true
     */
    public boolean isSuccessful() {
        return status == TaskStatus.COMPLETED;
    }
    
    /**
     * 检查任务是否失败
     *
     * @return 如果状态为FAILED则返回true
     */
    public boolean isFailed() {
        return status == TaskStatus.FAILED;
    }
    
    /**
     * 检查任务是否超时
     *
     * @return 如果状态为TIMEOUT则返回true
     */
    public boolean isTimeout() {
        return status == TaskStatus.TIMEOUT;
    }
    
    /**
     * 检查任务是否已终止
     *
     * @return 如果任务已终止则返回true
     */
    public boolean isTerminated() {
        return status != null && status.isTerminated();
    }
    
    /**
     * 检查任务是否正在执行
     *
     * @return 如果任务正在执行则返回true
     */
    public boolean isRunning() {
        return status == TaskStatus.RUNNING;
    }
    
    /**
     * 计算任务的健康状态分数
     * 
     * @return 健康分数，范围0-100，100表示完全健康
     */
    public int calculateHealthScore() {
        if (status == TaskStatus.COMPLETED) {
            return 100;
        } else if (status == TaskStatus.FAILED) {
            return 0;
        } else if (status == TaskStatus.TIMEOUT) {
            return 20;
        } else if (status == TaskStatus.WAITING_RETRY) {
            // 根据重试次数折算健康分数
            int maxScore = 70;
            if (maxRetryCount == null || maxRetryCount <= 0 || retryCount == null) {
                return 50;
            }
            return Math.max(30, maxScore - (retryCount * 20));
        } else if (status == TaskStatus.CANCELED) {
            return 40;
        } else if (status == TaskStatus.PAUSED) {
            return 60;
        } else if (status.isActive()) {
            return 80; // 活动状态通常是健康的
        } else {
            return 50; // 默认中等健康度
        }
    }
}
