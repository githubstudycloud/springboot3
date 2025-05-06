package com.platform.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限领域模型
 * <p>
 * 作为核心域模型，不包含框架注解，保持领域模型的纯粹性
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    /**
     * 权限ID
     */
    private String id;
    
    /**
     * 权限编码（唯一标识）
     */
    private String code;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限类型
     */
    private String type;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 权限状态（0:禁用, 1:启用, -1:已删除）
     */
    private Integer status;
    
    /**
     * 父权限ID
     */
    private String parentId;
    
    /**
     * 权限路径
     */
    private String path;
    
    /**
     * 权限URL
     */
    private String url;
    
    /**
     * 权限方法（GET, POST, PUT, DELETE等）
     */
    private String method;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 是否显示
     */
    private Boolean visible;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否为菜单
     */
    public boolean isMenu() {
        return "menu".equalsIgnoreCase(this.type);
    }
    
    /**
     * 是否为按钮
     */
    public boolean isButton() {
        return "button".equalsIgnoreCase(this.type);
    }
    
    /**
     * 是否为接口
     */
    public boolean isApi() {
        return "api".equalsIgnoreCase(this.type);
    }
    
    /**
     * 是否有效
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
