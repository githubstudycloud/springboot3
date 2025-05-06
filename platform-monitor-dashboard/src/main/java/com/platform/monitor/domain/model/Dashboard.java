package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 仪表板领域模型
 */
@Getter
public class Dashboard extends AbstractEntity<String> {
    
    /**
     * 仪表板ID
     */
    private final String id;
    
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
    private final String createdBy;
    
    /**
     * 创建时间
     */
    private final LocalDateTime createdAt;
    
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
    private final List<DashboardPanel> panels;
    
    /**
     * 是否公开（所有人可见）
     */
    private boolean isPublic;
    
    /**
     * 授权用户集合
     */
    private final Set<String> authorizedUsers;
    
    /**
     * 刷新间隔（秒）
     */
    private int refreshIntervalSeconds;
    
    /**
     * 构造函数
     *
     * @param id 仪表板ID
     * @param name 仪表板名称
     * @param description 仪表板描述
     * @param createdBy 创建人
     * @param isPublic 是否公开
     * @param refreshIntervalSeconds 刷新间隔（秒）
     */
    public Dashboard(String id, String name, String description, String createdBy, 
                    boolean isPublic, int refreshIntervalSeconds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
        this.refreshIntervalSeconds = refreshIntervalSeconds;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.panels = new ArrayList<>();
        this.authorizedUsers = new HashSet<>();
        
        // 默认添加创建者为授权用户
        this.authorizedUsers.add(createdBy);
    }
    
    /**
     * 更新仪表板基本信息
     *
     * @param name 仪表板名称
     * @param description 仪表板描述
     * @param isPublic 是否公开
     * @param refreshIntervalSeconds 刷新间隔（秒）
     * @return 当前仪表板实例
     */
    public Dashboard updateBasicInfo(String name, String description, boolean isPublic, int refreshIntervalSeconds) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.refreshIntervalSeconds = refreshIntervalSeconds;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 更新仪表板布局
     *
     * @param layout 布局信息（JSON格式）
     * @return 当前仪表板实例
     */
    public Dashboard updateLayout(String layout) {
        this.layout = layout;
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加面板
     *
     * @param panel 仪表板面板
     * @return 当前仪表板实例
     */
    public Dashboard addPanel(DashboardPanel panel) {
        this.panels.add(panel);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除面板
     *
     * @param panelId 面板ID
     * @return 当前仪表板实例
     */
    public Dashboard removePanel(String panelId) {
        this.panels.removeIf(panel -> panel.getId().equals(panelId));
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 添加授权用户
     *
     * @param userId 用户ID
     * @return 当前仪表板实例
     */
    public Dashboard addAuthorizedUser(String userId) {
        this.authorizedUsers.add(userId);
        this.updatedAt = LocalDateTime.now();
        return this;
    }
    
    /**
     * 移除授权用户
     *
     * @param userId 用户ID
     * @return 当前仪表板实例
     */
    public Dashboard removeAuthorizedUser(String userId) {
        // 不允许移除创建者
        if (!userId.equals(this.createdBy)) {
            this.authorizedUsers.remove(userId);
            this.updatedAt = LocalDateTime.now();
        }
        return this;
    }
    
    /**
     * 检查用户是否有权限访问仪表板
     *
     * @param userId 用户ID
     * @return 如果有权限则返回true
     */
    public boolean canAccess(String userId) {
        return isPublic || authorizedUsers.contains(userId);
    }
    
    /**
     * 根据ID查找面板
     *
     * @param panelId 面板ID
     * @return 面板实例，如果不存在则返回null
     */
    public DashboardPanel findPanelById(String panelId) {
        return panels.stream()
                .filter(panel -> panel.getId().equals(panelId))
                .findFirst()
                .orElse(null);
    }
}
