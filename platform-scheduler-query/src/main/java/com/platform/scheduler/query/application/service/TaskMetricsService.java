package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import com.platform.scheduler.query.domain.model.metrics.JobMetrics;
import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务指标服务
 * 提供计算和获取任务统计指标的方法
 * 
 * @author platform
 */
@Slf4j
public class TaskMetricsService {

    private TaskMetricsService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 获取指定时间范围内的任务统计指标
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param taskAnalyticsRepository 任务分析仓储
     * @param jobMetricsRepository 作业度量仓储
     * @param executorMetricsRepository 执行器度量仓储
     * @return 任务统计指标
     */
    public static TaskMetrics getTaskMetrics(
            LocalDateTime startTime,
            LocalDateTime endTime,
            com.platform.scheduler.query.application.port.out.TaskAnalyticsRepository taskAnalyticsRepository,
            com.platform.scheduler.query.application.port.out.JobMetricsRepository jobMetricsRepository,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository) {
        
        log.debug("获取任务统计指标: {} - {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务统计指标时时间范围参数无效");
            return TaskMetrics.empty(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        }
        
        try {
            // 获取基础统计数据
            TaskMetrics baseMetrics = taskAnalyticsRepository.getTaskMetrics(startTime, endTime);
            
            // 获取各作业的统计数据
            Map<String, JobMetrics> jobMetricsMap = jobMetricsRepository.getJobMetricsInPeriod(startTime, endTime);
            
            // 获取各执行器的统计数据
            Map<String, ExecutorMetrics> executorMetricsMap = executorMetricsRepository.getExecutorMetricsInPeriod(startTime, endTime);
            
            // 构建完整的统计指标
            return TaskMetrics.builder()
                    .startTime(baseMetrics.getStartTime())
                    .endTime(baseMetrics.getEndTime())
                    .totalTasks(baseMetrics.getTotalTasks())
                    .successTasks(baseMetrics.getSuccessTasks())
                    .failedTasks(baseMetrics.getFailedTasks())
                    .timeoutTasks(baseMetrics.getTimeoutTasks())
                    .canceledTasks(baseMetrics.getCanceledTasks())
                    .runningTasks(baseMetrics.getRunningTasks())
                    .waitingTasks(baseMetrics.getWaitingTasks())
                    .averageExecutionTime(baseMetrics.getAverageExecutionTime())
                    .maxExecutionTime(baseMetrics.getMaxExecutionTime())
                    .minExecutionTime(baseMetrics.getMinExecutionTime())
                    .jobMetrics(jobMetricsMap)
                    .executorMetrics(executorMetricsMap)
                    .build();
        } catch (Exception e) {
            log.error("获取任务统计指标发生异常", e);
            return TaskMetrics.empty(startTime, endTime);
        }
    }
}
