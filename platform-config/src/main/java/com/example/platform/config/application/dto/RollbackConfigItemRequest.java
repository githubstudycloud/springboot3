package com.example.platform.config.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 回滚配置项请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RollbackConfigItemRequest {
    
    /**
     * 目标版本号
     */
    @NotNull(message = "目标版本号不能为空")
    @Min(value = 1, message = "目标版本号必须大于等于1")
    private Integer version;
}
