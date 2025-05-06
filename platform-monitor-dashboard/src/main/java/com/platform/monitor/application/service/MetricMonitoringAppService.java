package com.platform.monitor.application.service;

import com.platform.monitor.application.dto.MetricDTO;
import com.platform.monitor.application.dto.MetricStatisticsDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 指标监控应用服务接口
 */
public interface MetricMonitoringAppService {
    
    /**
     * 获取服务实例的最新指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @return 指标数据DTO列表
     */
    List<MetricDTO> getLatestMetrics(String serviceInstanceId);
    
    /**
     * 获取指定服务实例和指标类型的最新指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @return 指标数据DTO
     */
    MetricDTO getLatestMetric(String serviceInstanceId, String metricType);
    
    /**
     * 获取指定时间范围内的指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据DTO列表
     */
    List<MetricDTO> getMetricsInTimeRange(String serviceInstanceId, String metricType,
                                        LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定服务的聚合指标数据
     *
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param aggregationFunction 聚合函数（avg, max, min, sum）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聚合后的指标值
     */
    double getAggregatedMetric(String serviceName, String metricType,
                              String aggregationFunction, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指标时间序列数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularitySeconds 时间粒度（秒）
     * @return 时间序列数据（时间点 -> 值）
     */
    Map<String, Double> getTimeSeriesData(String serviceInstanceId, String metricType,
                                         LocalDateTime startTime, LocalDateTime endTime,
                                         int granularitySeconds);
    
    /**
     * 获取服务的时间序列数据
     *
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularitySeconds 时间粒度（秒）
     * @return 时间序列数据（实例ID -> 时间点 -> 值）
     */
    Map<String, Map<String, Double>> getServiceTimeSeriesData(String serviceName, String metricType,
                                                             LocalDateTime startTime, LocalDateTime endTime,
                                                             int granularitySeconds);
    
    /**
     * 手动触发指标收集
     *
     * @param serviceInstanceId 服务实例ID
     * @return 收集的指标数量
     */
    int triggerMetricCollection(String serviceInstanceId);
    
    /**
     * 批量触发指标收集
     *
     * @param serviceInstanceIds 服务实例ID列表
     * @return 收集的指标数量
     */
    int batchTriggerMetricCollection(List<String> serviceInstanceIds);
    
    /**
     * 获取指标统计数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标统计数据DTO
     */
    MetricStatisticsDTO getMetricStatistics(String serviceInstanceId, String metricType,
                                          LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取服务指标统计数据
     *
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标统计数据DTO
     */
    MetricStatisticsDTO getServiceMetricStatistics(String serviceName, String metricType,
                                                 LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取系统级别指标概览
     *
     * @return 系统指标概览（指标类型 -> 指标值）
     */
    Map<String, Double> getSystemMetricsOverview();
    
    /**
     * 获取服务级别指标概览
     *
     * @param serviceName 服务名称
     * @return 服务指标概览（指标类型 -> 指标值）
     */
    Map<String, Double> getServiceMetricsOverview(String serviceName);
    
    /**
     * 清理过期指标数据
     *
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    long cleanupExpiredMetrics(int retentionDays);
}
