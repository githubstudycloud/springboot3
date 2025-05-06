package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;
import com.platform.scheduler.query.domain.service.TaskAnalyticsService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康监控服务
 * 提供系统健康监控和评分功能
 * 
 * @author platform
 */
@Slf4j
public class HealthMonitoringService {

    private HealthMonitoringService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 计算系统整体健康分数
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param taskAnalyticsService 任务分析服务
     * @param executorMetricsRepository 执行器度量仓储
     * @return 系统健康分数(0-100)
     */
    public static double calculateSystemHealthScore(
            LocalDateTime startTime, 
            LocalDateTime endTime,
            TaskAnalyticsService taskAnalyticsService,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository) {
        
        log.debug("计算系统整体健康分数: {} - {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            log.warn("计算系统整体健康分数时时间范围参数无效");
            return 0.0;
        }
        
        try {
            // 获取任务统计指标
            TaskMetrics taskMetrics = taskAnalyticsService.getTaskMetrics(startTime, endTime);
            
            // 获取执行器健康状态
            List<ExecutorMetrics> executorMetricsList = executorMetricsRepository.getAllCurrentExecutorMetrics();
            
            // 计算任务健康分数（占70%）
            double taskHealthScore = taskMetrics.calculateHealthScore();
            
            // 计算执行器健康分数（占30%）
            double executorHealthScore = 0.0;
            if (!executorMetricsList.isEmpty()) {
                executorHealthScore = executorMetricsList.stream()
                        .mapToDouble(ExecutorMetrics::calculateHealthScore)
                        .average()
                        .orElse(0.0);
            }
            
            // 加权计算整体健康分数
            return taskHealthScore * 0.7 + executorHealthScore * 0.3;
        } catch (Exception e) {
            log.error("计算系统整体健康分数发生异常", e);
            return 0.0;
        }
    }
}
