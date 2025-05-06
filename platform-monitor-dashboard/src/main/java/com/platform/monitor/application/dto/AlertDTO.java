package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    
    /**
     * 告警ID
     */
    private String id;
    
    /**
     * 告警名称
     */
    private String name;
    
    /**
     * 告警描述
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
     * 指标值
     */
    private double metricValue;
    
    /**
     * 指标单位
     */
    private String metricUnit;
    
    /**
     * 格式化的指标值
     */
    private String formattedMetricValue;
    
    /**
     * 告警规则ID
     */
    private String ruleId;
    
    /**
     * 告警规则名称
     */
    private String ruleName;
    
    /**
     * 服务实例ID
     */
    private String serviceInstanceId;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 告警状态
     */
    private String status;
    
    /**
     * 告警状态显示名称
     */
    private String statusDisplayName;
    
    /**
     * 首次出现时间
     */
    private LocalDateTime firstOccurTime;
    
    /**
     * 最近更新时间
     */
    private LocalDateTime lastUpdateTime;
    
    /**
     * 告警处理时间
     */
    private LocalDateTime handledTime;
    
    /**
     * 处理人
     */
    private String handledBy;
    
    /**
     * 处理备注
     */
    private String handlingNote;
    
    /**
     * 告警上下文数据
     */
    private Map<String, String> context;
    
    /**
     * 告警事件历史
     */
    private List<AlertEventDTO> events;
    
    /**
     * 是否活跃
     */
    private boolean active;
    
    /**
     * 持续时间（毫秒）
     */
    private long duration;
    
    /**
     * 是否已处理
     */
    private boolean handled;
    
    /**
     * 实例IP地址
     */
    private String instanceIpAddress;
    
    /**
     * 实例端口
     */
    private Integer instancePort;
}
