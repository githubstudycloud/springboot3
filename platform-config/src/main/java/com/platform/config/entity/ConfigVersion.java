package com.platform.config.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置版本实体
 * 用于记录配置的历史版本信息
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Entity
@Table(name = "config_version", 
       indexes = {
           @Index(name = "idx_app_profile", columnList = "application, profile"),
           @Index(name = "idx_version", columnList = "version"),
           @Index(name = "idx_create_time", columnList = "createTime")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVersion {

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
     * 版本号
     */
    @Column(nullable = false, length = 50, unique = true)
    private String version;

    /**
     * 配置内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 配置内容的MD5哈希值，用于快速比较
     */
    @Column(length = 32)
    private String contentHash;

    /**
     * 版本描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 操作人员
     */
    @Column(nullable = false, length = 100)
    private String operator;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;

    /**
     * 是否为当前激活版本
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = false;

    /**
     * 版本标签（如：stable, beta, hotfix等）
     */
    @Column(length = 50)
    private String tag;

    /**
     * 父版本ID（用于追踪版本演化）
     */
    @Column
    private Long parentVersionId;

    /**
     * 版本大小（字节）
     */
    @Column
    private Long contentSize;

    /**
     * 备注信息
     */
    @Column(length = 1000)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (contentSize == null && content != null) {
            contentSize = (long) content.getBytes().length;
        }
    }
}
