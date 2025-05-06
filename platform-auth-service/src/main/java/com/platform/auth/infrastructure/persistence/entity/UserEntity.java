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
 * 用户实体类
 * <p>
 * 遵循六边形架构，实体类属于基础设施层，包含与持久化相关的框架注解
 * </p>
 */
@Data
@Entity
@Table(name = "auth_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    
    /**
     * 用户ID
     */
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    /**
     * 用户名
     */
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    
    /**
     * 密码
     */
    @Column(name = "password", length = 100, nullable = false)
    private String password;
    
    /**
     * 电子邮件
     */
    @Column(name = "email", length = 100)
    private String email;
    
    /**
     * 手机号
     */
    @Column(name = "mobile", length = 20)
    private String mobile;
    
    /**
     * 昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;
    
    /**
     * 头像URL
     */
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    /**
     * 用户状态（0:禁用, 1:启用, -1:已删除）
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 租户ID
     */
    @Column(name = "tenant_id", length = 36)
    private String tenantId;
    
    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;
    
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
     * 用户角色关联
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "auth_user_role", 
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
    
    /**
     * 添加角色
     *
     * @param role 角色
     */
    public void addRole(RoleEntity role) {
        this.roles.add(role);
    }
    
    /**
     * 移除角色
     *
     * @param role 角色
     */
    public void removeRole(RoleEntity role) {
        this.roles.remove(role);
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
