package com.platform.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户领域模型
 * <p>
 * 作为核心域模型，不包含框架注解，保持领域模型的纯粹性
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 用户ID
     */
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 电子邮件
     */
    private String email;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 用户状态（0:禁用, 1:启用, -1:已删除）
     */
    private Integer status;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 角色集合
     */
    private Set<Role> roles = new HashSet<>();
    
    /**
     * 账号是否未过期
     */
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * 账号是否未锁定
     */
    public boolean isAccountNonLocked() {
        return status != null && status == 1;
    }
    
    /**
     * 密码是否未过期
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * 账号是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    /**
     * 添加角色
     *
     * @param role 角色
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    /**
     * 移除角色
     *
     * @param role 角色
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
    
    /**
     * 是否拥有指定角色
     *
     * @param roleCode 角色编码
     * @return 是否拥有
     */
    public boolean hasRole(String roleCode) {
        return this.roles.stream()
                .anyMatch(role -> role.getCode().equals(roleCode));
    }
    
    /**
     * 是否拥有指定权限
     *
     * @param permissionCode 权限编码
     * @return 是否拥有
     */
    public boolean hasPermission(String permissionCode) {
        return this.roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }
}
