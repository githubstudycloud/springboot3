package com.example.platform.config.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 配置版本历史数据库实体
 */
@Entity
@Table(name = "t_config_version_history", 
       indexes = {
           @Index(name = "idx_config_item_id", columnList = "config_item_id"),
           @Index(name = "idx_config_version", columnList = "config_item_id, version")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVersionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 配置项ID
     */
    @Column(name = "config_item_id", nullable = false)
    private Long configItemId;
    
    /**
     * 配置项dataId
     */
    @Column(name = "data_id", nullable = false, length = 200)
    private String dataId;
    
    /**
     * 配置项分组
     */
    @Column(name = "group_name", nullable = false, length = 100)
    private String group;
    
    /**
     * 版本号
     */
    @Column(name = "version", nullable = false)
    private Integer version;
    
    /**
     * 配置内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 环境标识：dev, test, prod等
     */
    @Column(name = "environment", nullable = false, length = 50)
    private String environment;
    
    /**
     * 是否加密
     */
    @Column(name = "encrypted", nullable = false)
    private boolean encrypted;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    /**
     * 创建人
     */
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    /**
     * 变更原因
     */
    @Column(name = "change_reason", length = 500)
    private String changeReason;
}
