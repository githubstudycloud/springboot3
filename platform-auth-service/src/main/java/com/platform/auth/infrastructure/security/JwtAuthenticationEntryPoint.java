package com.platform.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.common.model.ResponseResult;
import com.platform.common.model.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * <p>
 * 处理认证失败的情况，返回统一的JSON响应
 * </p>
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;
    
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response, 
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ResponseResult<Void> errorResponse = ResponseResult.failure(
                ResultCode.UNAUTHORIZED,
                authException.getMessage());
        
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
