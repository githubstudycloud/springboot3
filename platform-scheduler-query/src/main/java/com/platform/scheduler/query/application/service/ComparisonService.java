package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;
import com.platform.scheduler.query.domain.service.TaskAnalyticsService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 对比服务
 * 提供不同时间段任务执行数据的对比功能
 * 
 * @author platform
 */
@Slf4j
public class ComparisonService {

    private ComparisonService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 对比任务执行周期
     *
     * @param period1Start 第一个周期开始时间
     * @param period1End 第一个周期结束时间
     * @param period2Start 第二个周期开始时间
     * @param period2End 第二个周期结束时间
     * @param taskAnalyticsService 任务分析服务
     * @return 对比结果
     */
    public static Map<String, Object> compareTaskExecutionPeriods(
            LocalDateTime period1Start, 
            LocalDateTime period1End,
            LocalDateTime period2Start, 
            LocalDateTime period2End,
            TaskAnalyticsService taskAnalyticsService) {
        
        log.debug("对比任务执行数据: 周期1: {} - {}, 周期2: {} - {}", 
                period1Start, period1End, period2Start, period2End);
        
        if (period1Start == null || period1End == null || period2Start == null || period2End == null) {
            log.warn("对比任务执行数据时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            Map<String, Object> comparison = new HashMap<>();
            
            // 获取两个周期的任务统计指标
            TaskMetrics period1Metrics = taskAnalyticsService.getTaskMetrics(period1Start, period1End);
            TaskMetrics period2Metrics = taskAnalyticsService.getTaskMetrics(period2Start, period2End);
            
            // 添加周期信息
            comparison.put("period1", Map.of("start", period1Start, "end", period1End));
            comparison.put("period2", Map.of("start", period2Start, "end", period2End));
            
            // 构建对比数据
            Map<String, Object> metricsComparison = new HashMap<>();
            
            // 总任务数对比
            Map<String, Object> totalTasksComp = new HashMap<>();
            totalTasksComp.put("period1", period1Metrics.getTotalTasks());
            totalTasksComp.put("period2", period2Metrics.getTotalTasks());
            totalTasksComp.put("change", DataUtils.calculatePercentageChange(
                    period2Metrics.getTotalTasks(), period1Metrics.getTotalTasks()));
            metricsComparison.put("totalTasks", totalTasksComp);
            
            // 成功率对比
            Map<String, Object> successRateComp = new HashMap<>();
            successRateComp.put("period1", period1Metrics.getSuccessRate());
            successRateComp.put("period2", period2Metrics.getSuccessRate());
            successRateComp.put("change", DataUtils.calculatePercentageChange(
                    period2Metrics.getSuccessRate(), period1Metrics.getSuccessRate()));
            metricsComparison.put("successRate", successRateComp);
            
            // 失败率对比
            Map<String, Object> failureRateComp = new HashMap<>();
            failureRateComp.put("period1", period1Metrics.getFailureRate());
            failureRateComp.put("period2", period2Metrics.getFailureRate());
            failureRateComp.put("change", DataUtils.calculatePercentageChange(
                    period2Metrics.getFailureRate(), period1Metrics.getFailureRate()));
            metricsComparison.put("failureRate", failureRateComp);
            
            // 超时率对比
            Map<String, Object> timeoutRateComp = new HashMap<>();
            timeoutRateComp.put("period1", period1Metrics.getTimeoutRate());
            timeoutRateComp.put("period2", period2Metrics.getTimeoutRate());
            timeoutRateComp.put("change", DataUtils.calculatePercentageChange(
                    period2Metrics.getTimeoutRate(), period1Metrics.getTimeoutRate()));
            metricsComparison.put("timeoutRate", timeoutRateComp);
            
            // 平均执行时间对比
            Map<String, Object> avgExecTimeComp = new HashMap<>();
            avgExecTimeComp.put("period1", period1Metrics.getAverageExecutionTime());
            avgExecTimeComp.put("period2", period2Metrics.getAverageExecutionTime());
            avgExecTimeComp.put("change", DataUtils.calculatePercentageChange(
                    period2Metrics.getAverageExecutionTime(), period1Metrics.getAverageExecutionTime()));
            metricsComparison.put("averageExecutionTime", avgExecTimeComp);
            
            comparison.put("metricsComparison", metricsComparison);
            
            return comparison;
        } catch (Exception e) {
            log.error("对比任务执行数据发生异常", e);
            return Collections.emptyMap();
        }
    }
}
