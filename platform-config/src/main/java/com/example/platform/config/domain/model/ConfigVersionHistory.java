package com.example.platform.config.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配置版本历史领域模型
 * 
 * <p>记录配置项的历史版本，支持配置回滚操作</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVersionHistory {
    /**
     * 历史记录ID
     */
    private Long id;
    
    /**
     * 配置项ID
     */
    private Long configItemId;
    
    /**
     * 配置项dataId
     */
    private String dataId;
    
    /**
     * 配置项分组
     */
    private String group;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 配置内容
     */
    private String content;
    
    /**
     * 环境标识：dev, test, prod等
     */
    private String environment;
    
    /**
     * 是否加密
     */
    private boolean encrypted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 变更原因
     */
    private String changeReason;
    
    /**
     * 是否当前版本
     *
     * @return 是否当前版本
     */
    public boolean isCurrentVersion() {
        // 通常需要与当前配置项进行对比，这里简化处理
        return version != null && version > 0;
    }
}
