package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 告警规则条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleConditionDTO {
    
    /**
     * 条件类型
     */
    private String conditionType;
    
    /**
     * 条件类型显示名称
     */
    private String conditionTypeDisplayName;
    
    /**
     * 条件类型运算符
     */
    private String operator;
    
    /**
     * 阈值
     */
    private double threshold;
    
    /**
     * 辅助阈值
     */
    private Double secondaryThreshold;
    
    /**
     * 持续时间（秒）
     */
    private int durationSeconds;
    
    /**
     * 条件描述
     */
    private String description;
}
