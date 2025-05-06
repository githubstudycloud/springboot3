package com.platform.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * 租户实体类
 * <p>
 * 遵循六边形架构，实体类属于基础设施层，包含与持久化相关的框架注解
 * </p>
 */
@Data
@Entity
@Table(name = "auth_tenant")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {
    
    /**
     * 租户ID
     */
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    /**
     * 租户编码
     */
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;
    
    /**
     * 租户名称
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    /**
     * 租户状态（0:禁用, 1:启用, -1:已删除）
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 联系人
     */
    @Column(name = "contact_person", length = 50)
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;
    
    /**
     * 描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    /**
     * 最大用户数
     */
    @Column(name = "max_user_count")
    private Integer maxUserCount;
    
    /**
     * 当前用户数
     */
    @Column(name = "current_user_count")
    private Integer currentUserCount;
    
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
        if (this.currentUserCount == null) {
            this.currentUserCount = 0; // 默认用户数
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
