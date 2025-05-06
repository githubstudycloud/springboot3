package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指标查询领域模型
 */
@Getter
public class MetricQuery extends AbstractEntity<String> {

    /**
     * 查询ID
     */
    private final String id;
    
    /**
     * 查询名称
     */
    private String name;
    
    /**
     * 指标类型
     */
    private MetricType metricType;
    
    /**
     * 查询表达式
     */
    private String expression;
    
    /**
     * 查询语言（如PromQL、SQL等）
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
    private final Map<String, String> tagFilters;
    
    /**
     * 结果转换函数
     */
    private String transformFunction;
    
    /**
     * 查询参数
     */
    private final Map<String, Object> parameters;
    
    /**
     * 结果限制
     */
    private Integer resultLimit;
    
    /**
     * 服务实例ID列表
     */
    private final List<String> serviceInstanceIds;
    
    /**
     * 服务名称列表
     */
    private final List<String> serviceNames;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 构造函数
     *
     * @param id 查询ID
     * @param name 查询名称
     * @param metricType 指标类型
     * @param expression 查询表达式
     * @param queryLanguage 查询语言
     * @param timeRangeSeconds 时间范围（秒）
     * @param granularitySeconds 时间粒度（秒）
     */
    public MetricQuery(String id, String name, MetricType metricType, String expression, 
                      String queryLanguage, int timeRangeSeconds, int granularitySeconds) {
        this.id = id;
        this.name = name;
        this.metricType = metricType;
        this.expression = expression;
        this.queryLanguage = queryLanguage;
        this.timeRangeSeconds = timeRangeSeconds;
        this.granularitySeconds = granularitySeconds;
        this.tagFilters = new HashMap<>();
        this.parameters = new HashMap<>();
        this.serviceInstanceIds = new ArrayList<>();
        this.serviceNames = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新查询基本信息
     *
     * @param name 查询名称
     * @param metricType 指标类型
     * @param expression 查询表达式
     * @param queryLanguage 查询语言
     * @param timeRangeSeconds 时间范围（秒）
     * @param granularitySeconds 时间粒度（秒）
     * @return 当前查询实例
     */
    public MetricQuery updateBasicInfo(String name, MetricType metricType, String expression, 
                                     String queryLanguage, int timeRangeSeconds, int granularitySeconds) {
        this.name = name;
        this.metricType = metricType;
        this.expression = expression;
        this.queryLanguage = queryLanguage;
        this.timeRangeSeconds = timeRangeSeconds;
        this.granularitySeconds = granularitySeconds;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 设置聚合函数
     *
     * @param aggregationFunction 聚合函数
     * @return 当前查询实例
     */
    public MetricQuery setAggregationFunction(String aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 设置转换函数
     *
     * @param transformFunction 转换函数
     * @return 当前查询实例
     */
    public MetricQuery setTransformFunction(String transformFunction) {
        this.transformFunction = transformFunction;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 设置结果限制
     *
     * @param resultLimit 结果限制
     * @return 当前查询实例
     */
    public MetricQuery setResultLimit(Integer resultLimit) {
        this.resultLimit = resultLimit;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加标签过滤条件
     *
     * @param key 标签键
     * @param value 标签值
     * @return 当前查询实例
     */
    public MetricQuery addTagFilter(String key, String value) {
        this.tagFilters.put(key, value);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除标签过滤条件
     *
     * @param key 标签键
     * @return 当前查询实例
     */
    public MetricQuery removeTagFilter(String key) {
        this.tagFilters.remove(key);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加查询参数
     *
     * @param key 参数键
     * @param value 参数值
     * @return 当前查询实例
     */
    public MetricQuery addParameter(String key, Object value) {
        this.parameters.put(key, value);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加服务实例ID
     *
     * @param serviceInstanceId 服务实例ID
     * @return 当前查询实例
     */
    public MetricQuery addServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceIds.add(serviceInstanceId);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加服务名称
     *
     * @param serviceName 服务名称
     * @return 当前查询实例
     */
    public MetricQuery addServiceName(String serviceName) {
        this.serviceNames.add(serviceName);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 获取查询的时间范围描述
     *
     * @return 时间范围描述
     */
    public String getTimeRangeDescription() {
        if (timeRangeSeconds < 60) {
            return String.format("过去 %d 秒", timeRangeSeconds);
        } else if (timeRangeSeconds < 3600) {
            return String.format("过去 %d 分钟", timeRangeSeconds / 60);
        } else if (timeRangeSeconds < 86400) {
            return String.format("过去 %d 小时", timeRangeSeconds / 3600);
        } else {
            return String.format("过去 %d 天", timeRangeSeconds / 86400);
        }
    }
    
    /**
     * 获取查询的时间粒度描述
     *
     * @return 时间粒度描述
     */
    public String getGranularityDescription() {
        if (granularitySeconds < 60) {
            return String.format("%d 秒", granularitySeconds);
        } else if (granularitySeconds < 3600) {
            return String.format("%d 分钟", granularitySeconds / 60);
        } else if (granularitySeconds < 86400) {
            return String.format("%d 小时", granularitySeconds / 3600);
        } else {
            return String.format("%d 天", granularitySeconds / 86400);
        }
    }
}
