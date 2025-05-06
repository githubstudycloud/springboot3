package com.example.platform.config.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 配置项数据库实体
 */
@Entity
@Table(name = "t_config_item", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"data_id", "group_name", "environment"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
     * 配置内容
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    /**
     * 配置类型：yaml, properties, json, text等
     */
    @Column(name = "type", length = 50)
    private String type;
    
    /**
     * 环境标识：dev, test, prod等
     */
    @Column(name = "environment", nullable = false, length = 50)
    private String environment;
    
    /**
     * 描述信息
     */
    @Column(name = "description", length = 500)
    private String description;
    
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
     * 最后修改时间
     */
    @Column(name = "last_modified_time", nullable = false)
    private LocalDateTime lastModifiedTime;
    
    /**
     * 创建人
     */
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    /**
     * 最后修改人
     */
    @Column(name = "last_modified_by", nullable = false, length = 100)
    private String lastModifiedBy;
}
