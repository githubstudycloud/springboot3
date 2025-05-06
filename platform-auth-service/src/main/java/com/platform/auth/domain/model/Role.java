package com.platform.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色领域模型
 * <p>
 * 作为核心域模型，不包含框架注解，保持领域模型的纯粹性
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    /**
     * 角色ID
     */
    private String id;
    
    /**
     * 角色编码（唯一标识）
     */
    private String code;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 角色状态（0:禁用, 1:启用, -1:已删除）
     */
    private Integer status;
    
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
     * 权限集合
     */
    private Set<Permission> permissions = new HashSet<>();
    
    /**
     * 添加权限
     *
     * @param permission 权限
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
    
    /**
     * 移除权限
     *
     * @param permission 权限
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
    
    /**
     * 是否拥有指定权限
     *
     * @param permissionCode 权限编码
     * @return 是否拥有
     */
    public boolean hasPermission(String permissionCode) {
        return this.permissions.stream()
                .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }
    
    /**
     * 是否有效
     *
     * @return 是否有效
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
