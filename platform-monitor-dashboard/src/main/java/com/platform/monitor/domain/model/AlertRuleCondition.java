package com.platform.monitor.domain.model;

import lombok.Getter;

/**
 * 告警规则条件领域模型
 */
@Getter
public class AlertRuleCondition {
    
    /**
     * 条件类型
     */
    private final AlertRuleConditionType conditionType;
    
    /**
     * 阈值
     */
    private final double threshold;
    
    /**
     * 辅助阈值（用于BETWEEN和NOT_BETWEEN条件类型）
     */
    private final Double secondaryThreshold;
    
    /**
     * 持续时间（秒），指标需要连续满足条件多长时间才会触发告警
     */
    private final int durationSeconds;
    
    /**
     * 构造函数
     * 
     * @param conditionType 条件类型
     * @param threshold 阈值
     * @param durationSeconds 持续时间（秒）
     */
    public AlertRuleCondition(AlertRuleConditionType conditionType, double threshold, int durationSeconds) {
        this(conditionType, threshold, null, durationSeconds);
    }
    
    /**
     * 构造函数
     * 
     * @param conditionType 条件类型
     * @param threshold 阈值
     * @param secondaryThreshold 辅助阈值
     * @param durationSeconds 持续时间（秒）
     */
    public AlertRuleCondition(AlertRuleConditionType conditionType, double threshold, Double secondaryThreshold, int durationSeconds) {
        this.conditionType = conditionType;
        this.threshold = threshold;
        this.secondaryThreshold = secondaryThreshold;
        this.durationSeconds = durationSeconds;
        
        validate();
    }
    
    /**
     * 验证规则条件的有效性
     */
    private void validate() {
        // 对于范围类条件，必须提供两个阈值
        if ((conditionType == AlertRuleConditionType.BETWEEN || conditionType == AlertRuleConditionType.NOT_BETWEEN) 
                && secondaryThreshold == null) {
            throw new IllegalArgumentException("范围条件类型必须提供第二阈值");
        }
        
        // 对于范围类条件，第一个阈值必须小于第二个阈值
        if ((conditionType == AlertRuleConditionType.BETWEEN || conditionType == AlertRuleConditionType.NOT_BETWEEN) 
                && threshold >= secondaryThreshold) {
            throw new IllegalArgumentException("范围条件的第一阈值必须小于第二阈值");
        }
        
        // 持续时间必须大于等于0
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("持续时间必须大于等于0");
        }
    }
    
    /**
     * 判断指标值是否满足条件
     * 
     * @param metricValue 指标值
     * @return 如果满足条件则返回true，否则返回false
     */
    public boolean evaluate(double metricValue) {
        switch (conditionType) {
            case GREATER_THAN:
                return metricValue > threshold;
            case GREATER_THAN_OR_EQUAL:
                return metricValue >= threshold;
            case LESS_THAN:
                return metricValue < threshold;
            case LESS_THAN_OR_EQUAL:
                return metricValue <= threshold;
            case EQUAL:
                // 使用容差处理浮点数比较
                return Math.abs(metricValue - threshold) < 0.0000001;
            case NOT_EQUAL:
                return Math.abs(metricValue - threshold) >= 0.0000001;
            case BETWEEN:
                return metricValue >= threshold && metricValue <= secondaryThreshold;
            case NOT_BETWEEN:
                return metricValue < threshold || metricValue > secondaryThreshold;
            default:
                return false;
        }
    }
    
    /**
     * 获取条件的文本描述
     * 
     * @return 条件的文本描述
     */
    public String getDescription() {
        if (conditionType == AlertRuleConditionType.BETWEEN || conditionType == AlertRuleConditionType.NOT_BETWEEN) {
            return String.format("%s [%.2f, %.2f] 持续 %d 秒", 
                    conditionType.getDisplayName(), threshold, secondaryThreshold, durationSeconds);
        } else {
            return String.format("%s %.2f 持续 %d 秒", 
                    conditionType.getDisplayName(), threshold, durationSeconds);
        }
    }
}
