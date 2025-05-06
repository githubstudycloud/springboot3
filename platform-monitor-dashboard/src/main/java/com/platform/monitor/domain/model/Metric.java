package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 指标数据领域模型
 */
@Getter
public class Metric extends AbstractEntity<String> {
    
    /**
     * 指标ID
     */
    private final String id;
    
    /**
     * 指标名称
     */
    private final String name;
    
    /**
     * 指标描述
     */
    private final String description;
    
    /**
     * 指标类型
     */
    private final MetricType metricType;
    
    /**
     * 指标值
     */
    private double value;
    
    /**
     * 指标单位
     */
    private final MetricUnit unit;
    
    /**
     * 采集时间
     */
    private final LocalDateTime timestamp;
    
    /**
     * 服务实例ID
     */
    private final String serviceInstanceId;
    
    /**
     * 指标标签
     */
    private final Map<String, String> tags;
    
    /**
     * 构造函数
     * 
     * @param id 指标ID
     * @param name 指标名称
     * @param description 指标描述
     * @param metricType 指标类型
     * @param value 指标值
     * @param unit 指标单位
     * @param serviceInstanceId 服务实例ID
     * @param tags 指标标签
     */
    public Metric(String id, String name, String description, MetricType metricType, 
                 double value, MetricUnit unit, String serviceInstanceId,
                 Map<String, String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.metricType = metricType;
        this.value = value;
        this.unit = unit;
        this.timestamp = LocalDateTime.now();
        this.serviceInstanceId = serviceInstanceId;
        this.tags = tags != null ? new HashMap<>(tags) : new HashMap<>();
    }
    
    /**
     * 更新指标值
     * 
     * @param newValue 新指标值
     * @return 更新后的指标实例
     */
    public Metric withValue(double newValue) {
        return new Metric(
            this.id,
            this.name,
            this.description,
            this.metricType,
            newValue,
            this.unit,
            this.serviceInstanceId,
            this.tags
        );
    }
    
    /**
     * 获取带单位的指标值显示
     * 
     * @return 带单位的指标值字符串
     */
    public String getFormattedValue() {
        // 对特殊单位进行格式化处理
        if (MetricUnit.PERCENTAGE.equals(unit)) {
            return String.format("%.2f%s", value, unit.getSymbol());
        }
        
        // 处理字节单位的自动转换
        if (MetricUnit.BYTES.equals(unit) && value > 1024) {
            if (value < 1024 * 1024) {
                return String.format("%.2f KB", value / 1024);
            } else if (value < 1024 * 1024 * 1024) {
                return String.format("%.2f MB", value / (1024 * 1024));
            } else if (value < 1024 * 1024 * 1024 * 1024) {
                return String.format("%.2f GB", value / (1024 * 1024 * 1024));
            } else {
                return String.format("%.2f TB", value / (1024 * 1024 * 1024 * 1024));
            }
        }
        
        // 默认格式
        if (unit.getSymbol().isEmpty()) {
            return String.format("%.2f", value);
        } else {
            return String.format("%.2f %s", value, unit.getSymbol());
        }
    }
    
    /**
     * 获取指标标签值
     * 
     * @param key 标签键
     * @return 标签值，如果不存在则返回null
     */
    public String getTagValue(String key) {
        return tags.get(key);
    }
    
    /**
     * 判断指标是否超过指定阈值
     * 
     * @param threshold 阈值
     * @return 如果超过阈值则返回true，否则返回false
     */
    public boolean exceedsThreshold(double threshold) {
        return this.value > threshold;
    }
    
    /**
     * 判断指标是否低于指定阈值
     * 
     * @param threshold 阈值
     * @return 如果低于阈值则返回true，否则返回false
     */
    public boolean belowThreshold(double threshold) {
        return this.value < threshold;
    }
}
