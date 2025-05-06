package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 仪表板DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    
    /**
     * 仪表板ID
     */
    private String id;
    
    /**
     * 仪表板名称
     */
    private String name;
    
    /**
     * 仪表板描述
     */
    private String description;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 创建人名称
     */
    private String createdByName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 仪表板布局
     */
    private String layout;
    
    /**
     * 面板列表
     */
    private List<DashboardPanelDTO> panels;
    
    /**
     * 是否公开
     */
    private boolean isPublic;
    
    /**
     * 授权用户集合
     */
    private Set<String> authorizedUsers;
    
    /**
     * 授权用户名称集合
     */
    private Set<String> authorizedUserNames;
    
    /**
     * 刷新间隔（秒）
     */
    private int refreshIntervalSeconds;
    
    /**
     * 面板数量
     */
    private int panelCount;
    
    /**
     * 是否有访问权限
     */
    private boolean canAccess;
    
    /**
     * 是否为所有者
     */
    private boolean isOwner;
}
