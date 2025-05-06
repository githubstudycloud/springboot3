package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 告警规则DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleDTO {
    
    /**
     * 告警规则ID
     */
    private String id;
    
    /**
     * 告警规则名称
     */
    private String name;
    
    /**
     * 告警规则描述
     */
    private String description;
    
    /**
     * 告警级别
     */
    private String severity;
    
    /**
     * 告警级别显示名称
     */
    private String severityDisplayName;
    
    /**
     * 指标类型
     */
    private String metricType;
    
    /**
     * 指标类型显示名称
     */
    private String metricTypeDisplayName;
    
    /**
     * 告警规则条件列表
     */
    private List<AlertRuleConditionDTO> conditions;
    
    /**
     * 通知渠道集合
     */
    private Set<String> notificationChannels;
    
    /**
     * 规则是否启用
     */
    private boolean enabled;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 服务名称限制
     */
    private Set<String> serviceNames;
    
    /**
     * 告警标签
     */
    private Set<String> tags;
    
    /**
     * 告警恢复阈值
     */
    private Double recoveryThreshold;
    
    /**
     * 是否自动关闭已恢复的告警
     */
    private boolean autoCloseRecovered;
    
    /**
     * 活跃告警数量
     */
    private int activeAlertCount;
    
    /**
     * 最后触发时间
     */
    private LocalDateTime lastTriggeredAt;
}
