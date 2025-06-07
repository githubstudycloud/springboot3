package com.platform.config.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置审计日志实体
 * 用于记录配置的变更审计信息
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Entity
@Table(name = "config_audit",
       indexes = {
           @Index(name = "idx_audit_app_profile", columnList = "application, profile"),
           @Index(name = "idx_audit_operation", columnList = "operation"),
           @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
           @Index(name = "idx_audit_operator", columnList = "operator")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用名称
     */
    @Column(nullable = false, length = 100)
    private String application;

    /**
     * 环境配置
     */
    @Column(nullable = false, length = 50)
    private String profile;

    /**
     * 操作类型（CREATE, UPDATE, DELETE, ROLLBACK等）
     */
    @Column(nullable = false, length = 50)
    private String operation;

    /**
     * 配置键
     */
    @Column(length = 200)
    private String configKey;

    /**
     * 变更前的值
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * 变更后的值
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * 操作人员
     */
    @Column(nullable = false, length = 100)
    private String operator;

    /**
     * 操作时间
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * 操作来源（WEB, API, SCHEDULE等）
     */
    @Column(length = 50)
    private String source;

    /**
     * 客户端IP地址
     */
    @Column(length = 45)
    private String clientIp;

    /**
     * 用户代理信息
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * 操作结果（SUCCESS, FAILED等）
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String result = "SUCCESS";

    /**
     * 错误信息（如果操作失败）
     */
    @Column(length = 1000)
    private String errorMessage;

    /**
     * 关联的版本ID
     */
    @Column
    private Long versionId;

    /**
     * 操作耗时（毫秒）
     */
    @Column
    private Long duration;

    /**
     * 业务标识
     */
    @Column(length = 100)
    private String businessId;

    /**
     * 备注信息
     */
    @Column(length = 500)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
} 