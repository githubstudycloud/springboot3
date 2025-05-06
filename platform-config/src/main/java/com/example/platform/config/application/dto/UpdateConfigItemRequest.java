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
 * 更新配置项请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigItemRequest {
    
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
     * 描述信息
     */
    @Size(max = 500, message = "描述信息长度不能超过500字符")
    private String description;
    
    /**
     * 是否加密
     */
    private boolean encrypted;
    
    /**
     * 变更原因
     */
    @NotBlank(message = "变更原因不能为空")
    @Size(max = 500, message = "变更原因长度不能超过500字符")
    private String changeReason;
}
