package com.example.platform.config.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 创建配置项请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConfigItemRequest {
    
    /**
     * 配置项dataId
     */
    @NotBlank(message = "配置项ID不能为空")
    @Size(max = 200, message = "配置项ID长度不能超过200字符")
    @Pattern(regexp = "^[a-zA-Z0-9.\\-_:]+$", message = "配置项ID只能包含字母、数字、点、横线、下划线和冒号")
    private String dataId;
    
    /**
     * 配置项分组
     */
    @NotBlank(message = "配置分组不能为空")
    @Size(max = 100, message = "配置分组长度不能超过100字符")
    @Pattern(regexp = "^[a-zA-Z0-9.\\-_]+$", message = "配置分组只能包含字母、数字、点、横线和下划线")
    private String group;
    
    /**
     * 配置内容
     */
    @NotNull(message = "配置内容不能为空")
    private String content;
    
    /**
     * 配置类型：yaml, properties, json, text等
     */
    @NotBlank(message = "配置类型不能为空")
    @Pattern(regexp = "^(yaml|properties|json|text|xml)$", message = "配置类型只能为yaml、properties、json、text或xml")
    private String type;
    
    /**
     * 环境标识：dev, test, prod等
     */
    @NotBlank(message = "环境标识不能为空")
    @Size(max = 50, message = "环境标识长度不能超过50字符")
    private String environment;
    
    /**
     * 描述信息
     */
    @Size(max = 500, message = "描述信息长度不能超过500字符")
    private String description;
    
    /**
     * 是否加密
     */
    private boolean encrypted;
}
