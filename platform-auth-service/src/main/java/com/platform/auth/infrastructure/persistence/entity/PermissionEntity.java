package com.platform.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * 权限实体类
 * <p>
 * 遵循六边形架构，实体类属于基础设施层，包含与持久化相关的框架注解
 * </p>
 */
@Data
@Entity
@Table(name = "auth_permission")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    
    /**
     * 权限ID
     */
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", length = 36, nullable = false)
    private String id;
    
    /**
     * 权限编码
     */
    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;
    
    /**
     * 权限名称
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    /**
     * 权限类型（menu:菜单, button:按钮, api:接口）
     */
    @Column(name = "type", length = 20, nullable = false)
    private String type;
    
    /**
     * 权限描述
     */
    @Column(name = "description", length = 255)
    private String description;
    
    /**
     * 权限状态（0:禁用, 1:启用, -1:已删除）
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 父权限ID
     */
    @Column(name = "parent_id", length = 36)
    private String parentId;
    
    /**
     * 权限路径
     */
    @Column(name = "path", length = 255)
    private String path;
    
    /**
     * 权限URL
     */
    @Column(name = "url", length = 255)
    private String url;
    
    /**
     * 权限方法（GET,POST,PUT,DELETE等）
     */
    @Column(name = "method", length = 10)
    private String method;
    
    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort;
    
    /**
     * 图标
     */
    @Column(name = "icon", length = 50)
    private String icon;
    
    /**
     * 是否显示
     */
    @Column(name = "visible")
    private Boolean visible;
    
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
        if (this.visible == null) {
            this.visible = true; // 默认显示
        }
        if (this.sort == null) {
            this.sort = 0; // 默认排序
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
