package com.platform.monitor.interfaces.rest;

import com.platform.monitor.application.dto.MetricDTO;
import com.platform.monitor.application.dto.MetricStatisticsDTO;
import com.platform.monitor.application.service.MetricMonitoringAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 指标监控控制器
 */
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "指标监控", description = "系统指标监控相关接口")
public class MetricMonitoringController {
    
    private final MetricMonitoringAppService metricMonitoringAppService;
    
    @GetMapping("/instance/{serviceInstanceId}/latest")
    @Operation(summary = "获取服务实例的最新指标数据", description = "获取指定服务实例的所有最新指标数据")
    public ResponseEntity<List<MetricDTO>> getLatestMetrics(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId) {
        log.debug("获取服务实例 [{}] 的最新指标数据", serviceInstanceId);
        return ResponseEntity.ok(metricMonitoringAppService.getLatestMetrics(serviceInstanceId));
    }
    
    @GetMapping("/instance/{serviceInstanceId}/metric/{metricType}/latest")
    @Operation(summary = "获取特定指标的最新数据", description = "获取指定服务实例和指标类型的最新指标数据")
    public ResponseEntity<MetricDTO> getLatestMetric(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType) {
        log.debug("获取服务实例 [{}] 的指标类型 [{}] 的最新数据", serviceInstanceId, metricType);
        return ResponseEntity.ok(metricMonitoringAppService.getLatestMetric(serviceInstanceId, metricType));
    }
    
    @GetMapping("/instance/{serviceInstanceId}/metric/{metricType}/history")
    @Operation(summary = "获取指标历史数据", description = "获取指定时间范围内的指标历史数据")
    public ResponseEntity<List<MetricDTO>> getMetricsInTimeRange(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("获取服务实例 [{}] 的指标类型 [{}] 在时间范围 [{}] 到 [{}] 的历史数据", 
                serviceInstanceId, metricType, startTime, endTime);
        return ResponseEntity.ok(metricMonitoringAppService.getMetricsInTimeRange(
                serviceInstanceId, metricType, startTime, endTime));
    }
    
    @GetMapping("/service/{serviceName}/metric/{metricType}/aggregated")
    @Operation(summary = "获取服务聚合指标数据", description = "获取指定服务和指标类型的聚合指标数据")
    public ResponseEntity<Double> getAggregatedMetric(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "聚合函数", required = true) @RequestParam String aggregationFunction,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("获取服务 [{}] 的指标类型 [{}] 使用聚合函数 [{}] 在时间范围 [{}] 到 [{}] 的聚合数据", 
                serviceName, metricType, aggregationFunction, startTime, endTime);
        return ResponseEntity.ok(metricMonitoringAppService.getAggregatedMetric(
                serviceName, metricType, aggregationFunction, startTime, endTime));
    }
    
    @GetMapping("/instance/{serviceInstanceId}/metric/{metricType}/timeseries")
    @Operation(summary = "获取指标时间序列数据", description = "获取指定时间范围和粒度的指标时间序列数据")
    public ResponseEntity<Map<String, Double>> getTimeSeriesData(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "时间粒度（秒）", required = true) @RequestParam int granularitySeconds) {
        log.debug("获取服务实例 [{}] 的指标类型 [{}] 在时间范围 [{}] 到 [{}] 时间粒度 [{}秒] 的时间序列数据", 
                serviceInstanceId, metricType, startTime, endTime, granularitySeconds);
        return ResponseEntity.ok(metricMonitoringAppService.getTimeSeriesData(
                serviceInstanceId, metricType, startTime, endTime, granularitySeconds));
    }
    
    @GetMapping("/service/{serviceName}/metric/{metricType}/timeseries")
    @Operation(summary = "获取服务时间序列数据", description = "获取指定服务在指定时间范围和粒度的时间序列数据")
    public ResponseEntity<Map<String, Map<String, Double>>> getServiceTimeSeriesData(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "时间粒度（秒）", required = true) @RequestParam int granularitySeconds) {
        log.debug("获取服务 [{}] 的指标类型 [{}] 在时间范围 [{}] 到 [{}] 时间粒度 [{}秒] 的时间序列数据", 
                serviceName, metricType, startTime, endTime, granularitySeconds);
        return ResponseEntity.ok(metricMonitoringAppService.getServiceTimeSeriesData(
                serviceName, metricType, startTime, endTime, granularitySeconds));
    }
    
    @PostMapping("/instance/{serviceInstanceId}/collect")
    @Operation(summary = "手动触发指标收集", description = "手动触发指定服务实例的指标收集")
    public ResponseEntity<Integer> triggerMetricCollection(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId) {
        log.debug("手动触发服务实例 [{}] 的指标收集", serviceInstanceId);
        int count = metricMonitoringAppService.triggerMetricCollection(serviceInstanceId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/instances/batch-collect")
    @Operation(summary = "批量触发指标收集", description = "批量触发多个服务实例的指标收集")
    public ResponseEntity<Integer> batchTriggerMetricCollection(
            @Parameter(description = "服务实例ID列表", required = true) @RequestBody List<String> serviceInstanceIds) {
        log.debug("批量触发服务实例的指标收集: {}", serviceInstanceIds);
        int count = metricMonitoringAppService.batchTriggerMetricCollection(serviceInstanceIds);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/instance/{serviceInstanceId}/metric/{metricType}/statistics")
    @Operation(summary = "获取指标统计数据", description = "获取指定服务实例和指标类型在指定时间范围内的统计数据")
    public ResponseEntity<MetricStatisticsDTO> getMetricStatistics(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("获取服务实例 [{}] 的指标类型 [{}] 在时间范围 [{}] 到 [{}] 的统计数据", 
                serviceInstanceId, metricType, startTime, endTime);
        return ResponseEntity.ok(metricMonitoringAppService.getMetricStatistics(
                serviceInstanceId, metricType, startTime, endTime));
    }
    
    @GetMapping("/service/{serviceName}/metric/{metricType}/statistics")
    @Operation(summary = "获取服务指标统计数据", description = "获取指定服务和指标类型在指定时间范围内的统计数据")
    public ResponseEntity<MetricStatisticsDTO> getServiceMetricStatistics(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName,
            @Parameter(description = "指标类型", required = true) @PathVariable String metricType,
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("获取服务 [{}] 的指标类型 [{}] 在时间范围 [{}] 到 [{}] 的统计数据", 
                serviceName, metricType, startTime, endTime);
        return ResponseEntity.ok(metricMonitoringAppService.getServiceMetricStatistics(
                serviceName, metricType, startTime, endTime));
    }
    
    @GetMapping("/system/overview")
    @Operation(summary = "获取系统级别指标概览", description = "获取系统级别的指标概览数据")
    public ResponseEntity<Map<String, Double>> getSystemMetricsOverview() {
        log.debug("获取系统级别指标概览");
        return ResponseEntity.ok(metricMonitoringAppService.getSystemMetricsOverview());
    }
    
    @GetMapping("/service/{serviceName}/overview")
    @Operation(summary = "获取服务级别指标概览", description = "获取指定服务的指标概览数据")
    public ResponseEntity<Map<String, Double>> getServiceMetricsOverview(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName) {
        log.debug("获取服务 [{}] 的指标概览", serviceName);
        return ResponseEntity.ok(metricMonitoringAppService.getServiceMetricsOverview(serviceName));
    }
    
    @DeleteMapping("/expired")
    @Operation(summary = "清理过期指标数据", description = "清理超过指定保留天数的指标数据")
    public ResponseEntity<Long> cleanupExpiredMetrics(
            @Parameter(description = "保留天数", required = true) @RequestParam(defaultValue = "30") int retentionDays) {
        log.debug("清理过期指标数据，保留 [{}] 天", retentionDays);
        long count = metricMonitoringAppService.cleanupExpiredMetrics(retentionDays);
        return ResponseEntity.ok(count);
    }
}
