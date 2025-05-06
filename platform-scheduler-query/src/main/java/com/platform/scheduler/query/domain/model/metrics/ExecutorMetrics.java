package com.platform.scheduler.query.domain.model.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 执行器统计指标值对象
 * 用于表示特定执行器的任务执行统计
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class ExecutorMetrics {

    /**
     * 执行器ID
     */
    private final String executorId;
    
    /**
     * 执行器名称
     */
    private final String executorName;
    
    /**
     * 执行的任务总数
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
     * 平均执行时间(毫秒)
     */
    private final double averageExecutionTime;
    
    /**
     * 当前负载
     */
    private final int currentLoad;
    
    /**
     * 最大并发数
     */
    private final int maxConcurrency;
    
    /**
     * CPU使用率
     */
    private final double cpuUsage;
    
    /**
     * 内存使用率
     */
    private final double memoryUsage;
    
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
     * 获取资源使用率
     *
     * @return 资源使用率(0-100)
     */
    public double getResourceUsage() {
        double cpuWeight = 0.4;
        double memoryWeight = 0.4;
        double loadWeight = 0.2;
        
        double loadRatio = maxConcurrency > 0 ? 
                (double) currentLoad / maxConcurrency * 100.0 : 0.0;
        
        return cpuWeight * cpuUsage + 
               memoryWeight * memoryUsage + 
               loadWeight * loadRatio;
    }
    
    /**
     * 计算执行器健康分数
     *
     * @return 健康分数(0-100)
     */
    public double calculateHealthScore() {
        double successWeight = 0.5;
        double resourceWeight = 0.3;
        double timeoutWeight = 0.2;
        
        double successScore = getSuccessRate();
        double resourceScore = 100 - getResourceUsage();
        double timeoutScore = 100 - getTimeoutRate();
        
        return successWeight * successScore + 
               resourceWeight * resourceScore + 
               timeoutWeight * timeoutScore;
    }
}
