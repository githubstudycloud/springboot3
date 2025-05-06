package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.Metric;
import com.platform.monitor.domain.model.MetricType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 指标仓储接口
 */
public interface MetricRepository {
    
    /**
     * 保存指标数据
     *
     * @param metric 指标数据
     * @return 保存后的指标数据
     */
    Metric save(Metric metric);
    
    /**
     * 根据ID查找指标数据
     *
     * @param id 指标ID
     * @return 指标数据可选结果
     */
    Optional<Metric> findById(String id);
    
    /**
     * 根据服务实例ID和指标类型查找最新指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @return 最新指标数据可选结果
     */
    Optional<Metric> findLatestByServiceInstanceIdAndMetricType(String serviceInstanceId, MetricType metricType);
    
    /**
     * 查找指定时间范围内的指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param metricType 指标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指标数据列表
     */
    List<Metric> findByServiceInstanceIdAndMetricTypeAndTimestampBetween(
            String serviceInstanceId, MetricType metricType, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据服务实例ID查找最新指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @return 指标数据列表
     */
    List<Metric> findLatestByServiceInstanceId(String serviceInstanceId);
    
    /**
     * 根据服务实例ID和标签查找最新指标数据
     *
     * @param serviceInstanceId 服务实例ID
     * @param tags 标签映射
     * @return 指标数据列表
     */
    List<Metric> findLatestByServiceInstanceIdAndTags(String serviceInstanceId, Map<String, String> tags);
    
    /**
     * 删除指定时间之前的指标数据
     *
     * @param time 时间点
     * @return 删除的记录数
     */
    long deleteByTimestampBefore(LocalDateTime time);
    
    /**
     * 批量保存指标数据
     *
     * @param metrics 指标数据列表
     * @return 保存后的指标数据列表
     */
    List<Metric> saveAll(List<Metric> metrics);
    
    /**
     * 获取指定服务和指标类型的聚合值
     *
     * @param serviceName 服务名称
     * @param metricType 指标类型
     * @param aggregationFunction 聚合函数（avg, max, min, sum）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聚合值
     */
    double getAggregatedValue(String serviceName, MetricType metricType, 
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
                                               LocalDateTime startTime, LocalDateTime endTime, int granularitySeconds);
    
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
}
