package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 指标数据统计DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricStatisticsDTO {
    
    /**
     * 最大值
     */
    private double maxValue;
    
    /**
     * 最小值
     */
    private double minValue;
    
    /**
     * 平均值
     */
    private double avgValue;
    
    /**
     * 总和
     */
    private double sumValue;
    
    /**
     * 数据点数量
     */
    private int count;
    
    /**
     * 最新值
     */
    private double latestValue;
    
    /**
     * 最大值时间点
     */
    private LocalDateTime maxValueTime;
    
    /**
     * 最小值时间点
     */
    private LocalDateTime minValueTime;
    
    /**
     * 最新值时间点
     */
    private LocalDateTime latestValueTime;
    
    /**
     * 标准差
     */
    private double standardDeviation;
    
    /**
     * 方差
     */
    private double variance;
    
    /**
     * 百分位数
     */
    private Map<String, Double> percentiles;
    
    /**
     * 环比变化率
     */
    private double periodChangeRate;
    
    /**
     * 同比变化率
     */
    private double yearOverYearChangeRate;
    
    /**
     * 指标单位
     */
    private String unit;
    
    /**
     * 指标类型
     */
    private String metricType;
    
    /**
     * 指标类型显示名称
     */
    private String metricTypeDisplayName;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 服务实例ID
     */
    private String serviceInstanceId;
    
    /**
     * 查询开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 查询结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 时间粒度（秒）
     */
    private int granularitySeconds;
}
