package com.platform.scheduler.query.application.service;

import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;
import com.platform.scheduler.query.domain.service.TaskAnalyticsService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表生成服务
 * 提供各种报表生成和导出功能
 * 
 * @author platform
 */
@Slf4j
public class ReportGenerationService {

    private ReportGenerationService() {
        // 私有构造方法，防止实例化
    }

    /**
     * 生成任务执行报表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reportType 报表类型(SUMMARY, DETAILED, EXECUTIVE)
     * @param taskAnalyticsService 任务分析服务
     * @param taskAnalyticsRepository 任务分析仓储
     * @param anomalyDetectionService 异常检测服务
     * @return 报表数据
     */
    public static Map<String, Object> generateTaskExecutionReport(
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            String reportType,
            TaskAnalyticsService taskAnalyticsService,
            com.platform.scheduler.query.application.port.out.TaskAnalyticsRepository taskAnalyticsRepository,
            com.platform.scheduler.query.application.port.out.AnomalyDetectionService anomalyDetectionService) {
        
        log.debug("生成任务执行报表: {} - {}, reportType={}", startTime, endTime, reportType);
        
        if (startTime == null || endTime == null) {
            log.warn("生成任务执行报表时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "SUMMARY"; // 默认报表类型
        }
        
        try {
            Map<String, Object> report = new HashMap<>();
            
            // 添加报表基本信息
            report.put("reportType", reportType);
            report.put("startTime", startTime);
            report.put("endTime", endTime);
            report.put("generatedAt", LocalDateTime.now());
            
            // 添加任务统计信息
            TaskMetrics taskMetrics = taskAnalyticsService.getTaskMetrics(startTime, endTime);
            report.put("taskMetrics", taskMetrics);
            
            // 根据报表类型添加不同的详细信息
            switch (reportType.toUpperCase()) {
                case "SUMMARY":
                    // 只包含基本统计信息
                    break;
                    
                case "DETAILED":
                    // 添加详细的任务状态分布
                    report.put("statusDistribution", taskAnalyticsRepository.getTaskStatusDistribution(startTime, endTime));
                    
                    // 添加执行时间分布
                    List<Long> durations = taskAnalyticsRepository.getTaskDurations(startTime, endTime);
                    report.put("durationDistribution", DataUtils.buildDurationDistribution(durations, 60)); // 60秒为一个桶
                    
                    // 添加任务重试分布
                    report.put("retryDistribution", taskAnalyticsRepository.getTaskRetryDistribution(startTime, endTime));
                    
                    // 添加执行器负载排行
                    report.put("executorLoadRanking", 
                            RankingService.getExecutorLoadRanking(5, executorMetricsRepository));
                    
                    // 添加失败任务排行
                    report.put("failureRateRanking", 
                            RankingService.getTaskFailureRateRanking(startTime, endTime, 5, jobMetricsRepository));
                    break;
                    
                case "EXECUTIVE":
                    // 添加关键绩效指标
                    Map<String, Object> kpis = new HashMap<>();
                    kpis.put("successRate", taskMetrics.getSuccessRate());
                    kpis.put("failureRate", taskMetrics.getFailureRate());
                    kpis.put("timeoutRate", taskMetrics.getTimeoutRate());
                    kpis.put("averageExecutionTime", taskMetrics.getAverageExecutionTime());
                    kpis.put("systemHealthScore", 
                            taskAnalyticsService.calculateSystemHealthScore(startTime, endTime));
                    
                    // 与上一周期相比的变化
                    LocalDateTime previousStartTime = startTime.minus(Period.between(startTime.toLocalDate(), endTime.toLocalDate()));
                    TaskMetrics previousMetrics = taskAnalyticsService.getTaskMetrics(previousStartTime, startTime);
                    
                    Map<String, Object> changes = new HashMap<>();
                    changes.put("successRateChange", DataUtils.calculatePercentageChange(
                            taskMetrics.getSuccessRate(), previousMetrics.getSuccessRate()));
                    changes.put("failureRateChange", DataUtils.calculatePercentageChange(
                            taskMetrics.getFailureRate(), previousMetrics.getFailureRate()));
                    changes.put("averageExecutionTimeChange", DataUtils.calculatePercentageChange(
                            taskMetrics.getAverageExecutionTime(), previousMetrics.getAverageExecutionTime()));
                    changes.put("totalTasksChange", DataUtils.calculatePercentageChange(
                            taskMetrics.getTotalTasks(), previousMetrics.getTotalTasks()));
                    
                    kpis.put("changes", changes);
                    report.put("kpis", kpis);
                    
                    // 添加异常任务列表
                    report.put("anomalousTasks", 
                            AnomalyDetectionHelper.detectAnomalousTasks(15.0, anomalyDetectionService));
                    break;
                    
                default:
                    log.warn("未知的报表类型: {}, 使用默认SUMMARY类型", reportType);
                    break;
            }
            
            return report;
        } catch (Exception e) {
            log.error("生成任务执行报表发生异常", e);
            return Collections.emptyMap();
        }
    }

    private static com.platform.scheduler.query.application.port.out.JobMetricsRepository jobMetricsRepository;
    private static com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorMetricsRepository;

    // 注入方法，用于依赖注入
    public static void setRepositories(
            com.platform.scheduler.query.application.port.out.JobMetricsRepository jobRepo,
            com.platform.scheduler.query.application.port.out.ExecutorMetricsRepository executorRepo) {
        jobMetricsRepository = jobRepo;
        executorMetricsRepository = executorRepo;
    }
}
