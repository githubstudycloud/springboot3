package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.Metric;
import com.platform.monitor.domain.model.MetricType;
import com.platform.monitor.domain.model.ServiceInstance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 指标收集领域服务接口
 */
public interface MetricCollectionService {
    
    /**
     * 从服务实例收集指标数据
     * 
     * @param serviceInstance 服务实例
     * @return 收集的指标数据列表
     */
    List<Metric> collectMetrics(ServiceInstance serviceInstance);
    
    /**
     * 收集指定类型的指标数据
     * 
     * @param serviceInstance 服务实例
     * @param metricType 指标类型
     * @return 收集的指标数据
     */
    Metric collectMetric(ServiceInstance serviceInstance, MetricType metricType);
    
    /**
     * 收集所有服务实例的指标数据
     * 
     * @return 收集的指标数据列表
     */
    List<Metric> collectAllInstancesMetrics();
    
    /**
     * 获取指定服务实例的最新指标数据
     * 
     * @param serviceInstanceId 服务实例ID
     * @return 最新指标数据列表
     */
    List<Metric> getLatestMetrics(String serviceInstanceId);
    
    /**
     * 获取指定服务实例和指标类型的最新指标数据
     * 
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @return 最新指标数据
     */
    Metric getLatestMetric(String serviceInstanceId, MetricType metricType);
    
    /**
     * 获取指定时间范围内的指标数据
     * 
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    List<Metric> getMetricsInTimeRange(String serviceInstanceId, MetricType metricType, 
                                     LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定服务的聚合指标数据
     * 
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param aggregationFunction 聚合函数（avg, max, min, sum）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聚合值
     */
    double getAggregatedMetric(String serviceName, MetricType metricType, 
                             String aggregationFunction, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取指定时间范围和粒度的时间序列数据
     * 
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularitySeconds 时间粒度（秒）
     * @return 时间序列数据（时间点 -> 值）
     */
    Map<LocalDateTime, Double> getTimeSeriesData(String serviceInstanceId, MetricType metricType, 
                                                LocalDateTime startTime, LocalDateTime endTime, 
                                                int granularitySeconds);
    
    /**
     * 获取指定服务的时间序列数据
     * 
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularitySeconds 时间粒度（秒）
     * @return 时间序列数据（实例ID -> 时间点 -> 值）
     */
    Map<String, Map<LocalDateTime, Double>> getServiceTimeSeriesData(String serviceName, MetricType metricType, 
                                                                    LocalDateTime startTime, LocalDateTime endTime, 
                                                                    int granularitySeconds);
    
    /**
     * 清理过期指标数据
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    long cleanupExpiredMetrics(int retentionDays);
}
