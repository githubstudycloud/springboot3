package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 指标数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO {
    
    /**
     * 指标ID
     */
    private String id;
    
    /**
     * 指标名称
     */
    private String name;
    
    /**
     * 指标描述
     */
    private String description;
    
    /**
     * a指标类型
     */
    private String metricType;
    
    /**
     * 指标类型显示名称
     */
    private String metricTypeDisplayName;
    
    /**
     * 指标值
     */
    private double value;
    
    /**
     * 指标单位
     */
    private String unit;
    
    /**
     * 指标单位符号
     */
    private String unitSymbol;
    
    /**
     * 采集时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 服务实例ID
     */
    private String serviceInstanceId;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 指标标签
     */
    private Map<String, String> tags;
    
    /**
     * 格式化的指标值
     */
    private String formattedValue;
    
    /**
     * 变化率（与上次指标值相比）
     */
    private Double changeRate;
    
    /**
     * 比较值（同比/环比）
     */
    private Double compareValue;
    
    /**
     * 阈值状态（0-正常，1-警告，2-紧急）
     */
    private Integer thresholdStatus;
}
