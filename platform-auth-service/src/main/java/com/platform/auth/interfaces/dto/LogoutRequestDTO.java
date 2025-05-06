package com.platform.auth.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登出请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDTO {
    
    /**
     * 访问令牌
     */
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;
}
