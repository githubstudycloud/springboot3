package com.platform.scheduler.query.domain.model.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务统计指标值对象
 * 用于表示任务执行的统计指标
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class TaskMetrics {

    /**
     * 统计周期开始时间
     */
    private final LocalDateTime startTime;
    
    /**
     * 统计周期结束时间
     */
    private final LocalDateTime endTime;
    
    /**
     * 总任务数
     */
    private final long totalTasks;
    
    /**
     * 成功任务数
     */
    private final long successTasks;
    
    /**
     * 失败任务数
     */
    private final long failedTasks;
    
    /**
     * 超时任务数
     */
    private final long timeoutTasks;
    
    /**
     * 取消任务数
     */
    private final long canceledTasks;
    
    /**
     * 正在运行任务数
     */
    private final long runningTasks;
    
    /**
     * 等待中任务数
     */
    private final long waitingTasks;
    
    /**
     * 平均执行时间(毫秒)
     */
    private final double averageExecutionTime;
    
    /**
     * 最长执行时间(毫秒)
     */
    private final long maxExecutionTime;
    
    /**
     * 最短执行时间(毫秒)
     */
    private final long minExecutionTime;
    
    /**
     * 按作业分组的统计数据
     */
    @Builder.Default
    private final Map<String, JobMetrics> jobMetrics = new HashMap<>();
    
    /**
     * 按执行器分组的统计数据
     */
    @Builder.Default
    private final Map<String, ExecutorMetrics> executorMetrics = new HashMap<>();
    
    /**
     * 获取任务成功率
     *
     * @return 成功率(0-100)
     */
    public double getSuccessRate() {
        if (totalTasks <= 0) {
            return 0.0;
        }
        return (double) successTasks / totalTasks * 100.0;
    }
    
    /**
     * 获取任务失败率
     *
     * @return 失败率(0-100)
     */
    public double getFailureRate() {
        if (totalTasks <= 0) {
            return 0.0;
        }
        return (double) failedTasks / totalTasks * 100.0;
    }
    
    /**
     * 获取任务超时率
     *
     * @return 超时率(0-100)
     */
    public double getTimeoutRate() {
        if (totalTasks <= 0) {
            return 0.0;
        }
        return (double) timeoutTasks / totalTasks * 100.0;
    }
    
    /**
     * 获取按作业分组的统计数据
     *
     * @return 作业统计数据Map
     */
    public Map<String, JobMetrics> getJobMetrics() {
        return Collections.unmodifiableMap(jobMetrics);
    }
    
    /**
     * 获取按执行器分组的统计数据
     *
     * @return 执行器统计数据Map
     */
    public Map<String, ExecutorMetrics> getExecutorMetrics() {
        return Collections.unmodifiableMap(executorMetrics);
    }
    
    /**
     * 计算平均健康分数
     *
     * @return 健康分数(0-100)
     */
    public double calculateHealthScore() {
        double successWeight = 0.6;
        double timeoutWeight = 0.2;
        double executionTimeWeight = 0.2;
        
        double successScore = getSuccessRate();
        double timeoutScore = 100 - getTimeoutRate();
        
        // 计算执行时间得分 (假设平均执行时间越短越好)
        double executionTimeScore = 100.0;
        if (averageExecutionTime > 0 && maxExecutionTime > 0) {
            executionTimeScore = Math.max(0, 100 - (averageExecutionTime / maxExecutionTime * 100));
        }
        
        return successWeight * successScore + 
               timeoutWeight * timeoutScore + 
               executionTimeWeight * executionTimeScore;
    }
    
    /**
     * 创建空统计结果
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 空统计结果
     */
    public static TaskMetrics empty(LocalDateTime startTime, LocalDateTime endTime) {
        return TaskMetrics.builder()
                .startTime(startTime)
                .endTime(endTime)
                .totalTasks(0)
                .successTasks(0)
                .failedTasks(0)
                .timeoutTasks(0)
                .canceledTasks(0)
                .runningTasks(0)
                .waitingTasks(0)
                .averageExecutionTime(0)
                .maxExecutionTime(0)
                .minExecutionTime(0)
                .build();
    }
}
