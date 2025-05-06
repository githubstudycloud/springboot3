package com.example.platform.config.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置项数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItemDTO {
    
    /**
     * 配置项ID
     */
    private Long id;
    
    /**
     * 配置项dataId
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
    private String createdTime;
    
    /**
     * 最后修改时间
     */
    private String lastModifiedTime;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 最后修改人
     */
    private String lastModifiedBy;
}
