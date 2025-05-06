package com.platform.auth.interfaces.rest;

import com.platform.auth.application.service.AuthApplicationService;
import com.platform.auth.interfaces.dto.*;
import com.platform.common.model.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 * <p>
 * 提供登录、注册、刷新令牌等接口
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthApplicationService authApplicationService;
    
    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request HTTP请求
     * @return 登录结果（包含Token）
     */
    @PostMapping("/login")
    public ResponseResult<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletRequest request) {
        
        String ipAddress = getClientIp(request);
        LoginResponseDTO responseDTO = authApplicationService.login(loginRequest, ipAddress);
        
        return ResponseResult.success(responseDTO);
    }
    
    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseResult<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest) {
        
        RegisterResponseDTO responseDTO = authApplicationService.register(registerRequest);
        
        return ResponseResult.success(responseDTO);
    }
    
    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 刷新结果（包含新Token）
     */
    @PostMapping("/refresh-token")
    public ResponseResult<RefreshTokenResponseDTO> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        
        RefreshTokenResponseDTO responseDTO = authApplicationService.refreshToken(refreshTokenRequest);
        
        return ResponseResult.success(responseDTO);
    }
    
    /**
     * 用户登出
     *
     * @param logoutRequest 登出请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseResult<Void> logout(
            @Valid @RequestBody LogoutRequestDTO logoutRequest) {
        
        authApplicationService.logout(logoutRequest);
        
        return ResponseResult.success();
    }
    
    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !xForwardedFor.equalsIgnoreCase("unknown")) {
            // X-Forwarded-For可能包含多个IP，第一个是客户端真实IP
            int index = xForwardedFor.indexOf(",");
            if (index != -1) {
                return xForwardedFor.substring(0, index);
            }
            return xForwardedFor;
        }
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown")) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
