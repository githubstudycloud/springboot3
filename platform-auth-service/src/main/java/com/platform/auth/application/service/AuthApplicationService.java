package com.platform.auth.application.service;

import com.platform.auth.domain.model.Permission;
import com.platform.auth.domain.model.Role;
import com.platform.auth.domain.model.User;
import com.platform.auth.domain.service.AuthenticationDomainService;
import com.platform.auth.domain.service.UserDomainService;
import com.platform.auth.infrastructure.security.JwtProperties;
import com.platform.auth.interfaces.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 认证应用服务
 * <p>
 * 处理认证相关的应用逻辑，协调领域服务的调用
 * </p>
 */
@Service
public class AuthApplicationService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthApplicationService.class);
    
    private final AuthenticationDomainService authenticationDomainService;
    private final UserDomainService userDomainService;
    private final JwtProperties jwtProperties;
    
    public AuthApplicationService(
            AuthenticationDomainService authenticationDomainService,
            UserDomainService userDomainService,
            JwtProperties jwtProperties) {
        this.authenticationDomainService = authenticationDomainService;
        this.userDomainService = userDomainService;
        this.jwtProperties = jwtProperties;
    }
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param ipAddress 客户端IP地址
     * @return 登录响应
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest, String ipAddress) {
        // 验证用户凭证
        Optional<User> userOpt = authenticationDomainService.authenticate(
                loginRequest.getUsername(), loginRequest.getPassword());
                
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        User user = userOpt.get();
        
        // 检查租户
        if (loginRequest.getTenantId() != null) {
            if (!loginRequest.getTenantId().equals(user.getTenantId())) {
                throw new IllegalArgumentException("租户ID不匹配");
            }
        }
        
        // 生成令牌
        String accessToken = authenticationDomainService.generateAccessToken(user);
        String refreshToken = authenticationDomainService.generateRefreshToken(user);
        
        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userDomainService.updateUser(user);
        
        // 提取角色和权限
        Set<String> roles = user.getRoles().stream()
                .filter(Role::isEnabled)
                .map(Role::getCode)
                .collect(Collectors.toSet());
                
        Set<String> permissions = user.getRoles().stream()
                .filter(Role::isEnabled)
                .flatMap(role -> role.getPermissions().stream())
                .filter(Permission::isEnabled)
                .map(Permission::getCode)
                .collect(Collectors.toSet());
                
        // 构建响应
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .tokenType(jwtProperties.getTokenPrefix().trim())
                .refreshToken(refreshToken)
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
    
    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {
        // 检查用户名是否已存在
        if (userDomainService.isUsernameExists(registerRequest.getUsername())) {
            return RegisterResponseDTO.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }
        
        // 检查电子邮件是否已存在
        if (registerRequest.getEmail() != null && 
                userDomainService.isEmailExists(registerRequest.getEmail())) {
            return RegisterResponseDTO.builder()
                    .success(false)
                    .message("电子邮件已存在")
                    .build();
        }
        
        // 检查手机号是否已存在
        if (registerRequest.getMobile() != null &&
                userDomainService.isMobileExists(registerRequest.getMobile())) {
            return RegisterResponseDTO.builder()
                    .success(false)
                    .message("手机号已存在")
                    .build();
        }
        
        // 创建用户
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .email(registerRequest.getEmail())
                .mobile(registerRequest.getMobile())
                .nickname(registerRequest.getNickname())
                .tenantId(registerRequest.getTenantId())
                .roles(new HashSet<>())
                .build();
        
        User savedUser = userDomainService.createUser(user);
        
        // 构建响应
        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .success(true)
                .message("注册成功")
                .build();
    }
    
    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 刷新令牌响应
     */
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        
        // 刷新访问令牌
        String newAccessToken = authenticationDomainService.refreshAccessToken(refreshToken);
        
        if (newAccessToken == null) {
            throw new IllegalArgumentException("无效的刷新令牌");
        }
        
        // 构建响应
        return RefreshTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .tokenType(jwtProperties.getTokenPrefix().trim())
                .expiresIn(jwtProperties.getExpiration() / 1000)
                .build();
    }
    
    /**
     * 用户登出
     *
     * @param logoutRequest 登出请求
     */
    public void logout(LogoutRequestDTO logoutRequest) {
        // 使令牌无效
        authenticationDomainService.invalidateToken(logoutRequest.getAccessToken());
    }
}
