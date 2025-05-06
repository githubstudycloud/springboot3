package com.platform.scheduler.query.domain.model.metrics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 作业统计指标值对象
 * 用于表示特定作业的任务执行统计
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class JobMetrics {

    /**
     * 作业ID
     */
    private final String jobId;
    
    /**
     * 作业名称
     */
    private final String jobName;
    
    /**
     * 作业类型
     */
    private final String jobType;
    
    /**
     * 总执行次数
     */
    private final long totalExecutions;
    
    /**
     * 成功执行次数
     */
    private final long successExecutions;
    
    /**
     * 失败执行次数
     */
    private final long failedExecutions;
    
    /**
     * 超时执行次数
     */
    private final long timeoutExecutions;
    
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
     * 平均重试次数
     */
    private final double averageRetryCount;
    
    /**
     * 最后一次执行时间
     */
    private final java.time.LocalDateTime lastExecutionTime;
    
    /**
     * 最后一次执行状态
     */
    private final String lastExecutionStatus;
    
    /**
     * 获取作业执行成功率
     *
     * @return 成功率(0-100)
     */
    public double getSuccessRate() {
        if (totalExecutions <= 0) {
            return 0.0;
        }
        return (double) successExecutions / totalExecutions * 100.0;
    }
    
    /**
     * 获取作业执行失败率
     *
     * @return 失败率(0-100)
     */
    public double getFailureRate() {
        if (totalExecutions <= 0) {
            return 0.0;
        }
        return (double) failedExecutions / totalExecutions * 100.0;
    }
    
    /**
     * 获取作业执行超时率
     *
     * @return 超时率(0-100)
     */
    public double getTimeoutRate() {
        if (totalExecutions <= 0) {
            return 0.0;
        }
        return (double) timeoutExecutions / totalExecutions * 100.0;
    }
    
    /**
     * 计算作业健康分数
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
     * 判断作业是否健康
     *
     * @return 如果健康分数大于等于70分则返回true
     */
    public boolean isHealthy() {
        return calculateHealthScore() >= 70.0;
    }
    
    /**
     * 判断作业是否需要关注
     * 失败率高或超时率高的作业需要关注
     *
     * @return 如果需要关注则返回true
     */
    public boolean needsAttention() {
        return getFailureRate() > 20.0 || getTimeoutRate() > 10.0;
    }
    
    /**
     * 获取执行时间趋势描述
     *
     * @param previousAvgExecutionTime 前一周期平均执行时间
     * @return 趋势描述
     */
    public String getExecutionTimeTrend(double previousAvgExecutionTime) {
        if (previousAvgExecutionTime <= 0 || averageExecutionTime <= 0) {
            return "无历史数据";
        }
        
        double changePercent = (averageExecutionTime - previousAvgExecutionTime) / previousAvgExecutionTime * 100.0;
        
        if (Math.abs(changePercent) < 5.0) {
            return "稳定";
        } else if (changePercent < 0) {
            return String.format("提升 %.1f%%", Math.abs(changePercent));
        } else {
            return String.format("恶化 %.1f%%", changePercent);
        }
    }
}
