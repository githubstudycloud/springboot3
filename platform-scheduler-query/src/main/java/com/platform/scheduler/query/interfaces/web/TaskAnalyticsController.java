package com.platform.scheduler.query.interfaces.web;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.query.domain.model.metrics.ExecutorMetrics;
import com.platform.scheduler.query.domain.model.metrics.JobMetrics;
import com.platform.scheduler.query.domain.model.metrics.TaskMetrics;
import com.platform.scheduler.query.domain.service.TaskAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务统计分析控制器
 * 提供任务统计分析的REST API
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/v1/task-analytics")
@RequiredArgsConstructor
@Tag(name = "任务统计分析", description = "提供任务统计分析和度量接口")
public class TaskAnalyticsController {

    private final TaskAnalyticsService taskAnalyticsService;

    @GetMapping("/metrics")
    @Operation(summary = "获取任务统计指标")
    public ResponseEntity<TaskMetrics> getTaskMetrics(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        TaskMetrics metrics = taskAnalyticsService.getTaskMetrics(startTime, endTime);
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/job/{jobId}/metrics")
    @Operation(summary = "获取作业统计指标")
    public ResponseEntity<JobMetrics> getJobMetrics(
            @PathVariable("jobId") 
            @Parameter(description = "作业ID", required = true) String jobId,
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        return taskAnalyticsService.getJobMetrics(new JobId(jobId), startTime, endTime)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/executor/{executorId}/metrics")
    @Operation(summary = "获取执行器统计指标")
    public ResponseEntity<ExecutorMetrics> getExecutorMetrics(
            @PathVariable("executorId") 
            @Parameter(description = "执行器ID", required = true) String executorId,
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        return taskAnalyticsService.getExecutorMetrics(executorId, startTime, endTime)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/success-rate-trend")
    @Operation(summary = "获取任务成功率趋势")
    public ResponseEntity<Map<LocalDate, Double>> getTaskSuccessRateTrend(
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "开始日期", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "结束日期", required = true) LocalDate endDate) {
        
        Map<LocalDate, Double> trend = taskAnalyticsService.getTaskSuccessRateTrend(startDate, endDate);
        return ResponseEntity.ok(trend);
    }
    
    @GetMapping("/performance-ranking")
    @Operation(summary = "获取任务性能排行榜")
    public ResponseEntity<List<JobMetrics>> getTaskPerformanceRanking(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "limit", defaultValue = "10") 
            @Parameter(description = "最大记录数") int limit,
            @RequestParam(value = "ascending", defaultValue = "true") 
            @Parameter(description = "是否按升序排序") boolean ascending) {
        
        List<JobMetrics> ranking = taskAnalyticsService.getTaskPerformanceRanking(
                startTime, endTime, limit, ascending);
        return ResponseEntity.ok(ranking);
    }
    
    @GetMapping("/failure-rate-ranking")
    @Operation(summary = "获取任务失败率排行榜")
    public ResponseEntity<List<JobMetrics>> getTaskFailureRateRanking(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "limit", defaultValue = "10") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<JobMetrics> ranking = taskAnalyticsService.getTaskFailureRateRanking(
                startTime, endTime, limit);
        return ResponseEntity.ok(ranking);
    }
    
    @GetMapping("/executor-load-ranking")
    @Operation(summary = "获取执行器负载排行榜")
    public ResponseEntity<List<ExecutorMetrics>> getExecutorLoadRanking(
            @RequestParam(value = "limit", defaultValue = "10") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<ExecutorMetrics> ranking = taskAnalyticsService.getExecutorLoadRanking(limit);
        return ResponseEntity.ok(ranking);
    }
    
    @GetMapping("/health-score")
    @Operation(summary = "计算系统整体健康分数")
    public ResponseEntity<Double> calculateSystemHealthScore(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        double healthScore = taskAnalyticsService.calculateSystemHealthScore(startTime, endTime);
        return ResponseEntity.ok(healthScore);
    }
    
    @GetMapping("/peak-hours")
    @Operation(summary = "获取每日任务执行峰值时间")
    public ResponseEntity<Map<LocalDate, Integer>> getTaskExecutionPeakHours(
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "开始日期", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "结束日期", required = true) LocalDate endDate) {
        
        Map<LocalDate, Integer> peakHours = taskAnalyticsService.getTaskExecutionPeakHours(startDate, endDate);
        return ResponseEntity.ok(peakHours);
    }
    
    @GetMapping("/anomalous-tasks")
    @Operation(summary = "分析并预测异常任务")
    public ResponseEntity<List<Map<String, Object>>> detectAnomalousTasks(
            @RequestParam(value = "thresholdPercentage", defaultValue = "20.0") 
            @Parameter(description = "阈值百分比") double thresholdPercentage) {
        
        List<Map<String, Object>> anomalousTasks = taskAnalyticsService.detectAnomalousTasks(thresholdPercentage);
        return ResponseEntity.ok(anomalousTasks);
    }
    
    @GetMapping("/job/{jobId}/execution-time-trend")
    @Operation(summary = "获取作业执行时间趋势")
    public ResponseEntity<Map<LocalDate, Double>> getJobExecutionTimeTrend(
            @PathVariable("jobId") 
            @Parameter(description = "作业ID", required = true) String jobId,
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "开始日期", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "结束日期", required = true) LocalDate endDate) {
        
        Map<LocalDate, Double> trend = taskAnalyticsService.getJobExecutionTimeTrend(
                new JobId(jobId), startDate, endDate);
        return ResponseEntity.ok(trend);
    }
    
    @GetMapping("/report")
    @Operation(summary = "生成任务执行报表")
    public ResponseEntity<Map<String, Object>> generateTaskExecutionReport(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "reportType", defaultValue = "SUMMARY") 
            @Parameter(description = "报表类型(SUMMARY, DETAILED, EXECUTIVE)") String reportType) {
        
        Map<String, Object> report = taskAnalyticsService.generateTaskExecutionReport(
                startTime, endTime, reportType);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/resource-utilization")
    @Operation(summary = "获取资源利用率统计")
    public ResponseEntity<Map<String, Object>> getResourceUtilizationStats(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        Map<String, Object> stats = taskAnalyticsService.getResourceUtilizationStats(startTime, endTime);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/compare-periods")
    @Operation(summary = "对比任务执行周期")
    public ResponseEntity<Map<String, Object>> compareTaskExecutionPeriods(
            @RequestParam(value = "period1Start") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "第一个周期开始时间", required = true) LocalDateTime period1Start,
            @RequestParam(value = "period1End") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "第一个周期结束时间", required = true) LocalDateTime period1End,
            @RequestParam(value = "period2Start") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "第二个周期开始时间", required = true) LocalDateTime period2Start,
            @RequestParam(value = "period2End") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "第二个周期结束时间", required = true) LocalDateTime period2End) {
        
        Map<String, Object> comparison = taskAnalyticsService.compareTaskExecutionPeriods(
                period1Start, period1End, period2Start, period2End);
        return ResponseEntity.ok(comparison);
    }
}
