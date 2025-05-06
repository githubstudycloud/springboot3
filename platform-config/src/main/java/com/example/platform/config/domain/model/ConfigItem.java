package com.example.platform.config.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置项领域模型
 * 
 * <p>配置项是配置中心的核心领域对象，表示一个具体的配置内容</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItem {
    /**
     * 配置项ID
     */
    private Long id;
    
    /**
     * 配置项dataId (唯一标识)
     */
    private String dataId;
    
    /**
     * 配置项分组
     */
    private String group;
    
    /**
     * 配置内容
     */
    private String content;
    
    /**
     * 配置类型：yaml, properties, json, text等
     */
    private String type;
    
    /**
     * 环境标识：dev, test, prod等
     */
    private String environment;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 是否加密
     */
    private boolean encrypted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedTime;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 最后修改人
     */
    private String lastModifiedBy;
    
    /**
     * 配置版本历史记录
     */
    @Builder.Default
    private List<ConfigVersionHistory> versionHistories = new ArrayList<>();
    
    /**
     * 添加配置版本历史
     *
     * @param configVersionHistory 配置版本历史
     */
    public void addVersionHistory(ConfigVersionHistory configVersionHistory) {
        this.versionHistories.add(configVersionHistory);
    }
    
    /**
     * 是否需要加密
     *
     * @return 是否需要加密
     */
    public boolean needsEncryption() {
        return this.encrypted && !isContentEncrypted();
    }
    
    /**
     * 判断内容是否已经加密
     *
     * @return 是否已经加密
     */
    public boolean isContentEncrypted() {
        // 简单判断，实际可能需要更复杂的逻辑
        return this.content != null && this.content.startsWith("ENC:");
    }
}
