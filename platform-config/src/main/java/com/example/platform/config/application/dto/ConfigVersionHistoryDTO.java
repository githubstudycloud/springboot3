package com.example.platform.config.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置版本历史数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVersionHistoryDTO {
    
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
     * 创建时间
     */
    private String createdTime;
    
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
     */
    private boolean currentVersion;
}
