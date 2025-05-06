package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 指标查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricQueryDTO {
    
    /**
     * 查询ID
     */
    private String id;
    
    /**
     * 查询名称
     */
    private String name;
    
    /**
     * 指标类型
     */
    private String metricType;
    
    /**
     * 指标类型显示名称
     */
    private String metricTypeDisplayName;
    
    /**
     * 查询表达式
     */
    private String expression;
    
    /**
     * 查询语言
     */
    private String queryLanguage;
    
    /**
     * 聚合函数
     */
    private String aggregationFunction;
    
    /**
     * 时间范围（秒）
     */
    private int timeRangeSeconds;
    
    /**
     * 时间粒度（秒）
     */
    private int granularitySeconds;
    
    /**
     * 标签过滤条件
     */
    private Map<String, String> tagFilters;
    
    /**
     * 结果转换函数
     */
    private String transformFunction;
    
    /**
     * 查询参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 结果限制
     */
    private Integer resultLimit;
    
    /**
     * 服务实例ID列表
     */
    private List<String> serviceInstanceIds;
    
    /**
     * 服务名称列表
     */
    private List<String> serviceNames;
    
    /**
     * 时间范围描述
     */
    private String timeRangeDescription;
    
    /**
     * 时间粒度描述
     */
    private String granularityDescription;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
