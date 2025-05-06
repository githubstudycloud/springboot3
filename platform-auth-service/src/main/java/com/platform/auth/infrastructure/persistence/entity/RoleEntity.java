package com.platform.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * <p>
 * 遵循六边形架构，实体类属于基础设施层，包含与持久化相关的框架注解
 * </p>
 */
@Data
@Entity
@Table(name = "auth_role")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    
    /**
     * 角色ID
     */
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    /**
     * 角色编码
     */
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;
    
    /**
     * 角色名称
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    /**
     * 角色描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 角色状态（0:禁用, 1:启用, -1:已删除）
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 租户ID
     */
    @Column(name = "tenant_id", length = 36)
    private String tenantId;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 角色权限关联
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "auth_role_permission", 
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionEntity> permissions = new HashSet<>();
    
    /**
     * 添加权限
     *
     * @param permission 权限
     */
    public void addPermission(PermissionEntity permission) {
        this.permissions.add(permission);
    }
    
    /**
     * 移除权限
     *
     * @param permission 权限
     */
    public void removePermission(PermissionEntity permission) {
        this.permissions.remove(permission);
    }
    
    /**
     * 实体创建前回调
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = now;
        }
        if (this.status == null) {
            this.status = 1; // 默认启用
        }
    }
    
    /**
     * 实体更新前回调
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
