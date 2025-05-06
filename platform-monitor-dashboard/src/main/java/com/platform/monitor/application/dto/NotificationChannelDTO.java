package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通知渠道DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationChannelDTO {
    
    /**
     * 通知渠道ID
     */
    private String id;
    
    /**
     * 通知渠道名称
     */
    private String name;
    
    /**
     * 通知渠道类型
     */
    private String type;
    
    /**
     * 通知渠道类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 通知渠道配置
     */
    private Map<String, String> config;
    
    /**
     * 是否启用
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
     * 通知渠道描述
     */
    private String description;
    
    /**
     * 配置是否有效
     */
    private boolean configValid;
    
    /**
     * 关联告警规则数量
     */
    private int alertRuleCount;
    
    /**
     * 最后通知发送时间
     */
    private LocalDateTime lastNotifyTime;
    
    /**
     * 成功发送数量
     */
    private int successCount;
    
    /**
     * 失败发送数量
     */
    private int failureCount;
}
