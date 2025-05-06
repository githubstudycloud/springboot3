package com.platform.auth.domain.service;

import com.platform.auth.domain.model.User;

import java.util.Optional;

/**
 * 认证领域服务接口
 * <p>
 * 负责用户认证相关的核心业务逻辑
 * </p>
 */
public interface AuthenticationDomainService {
    
    /**
     * 验证用户凭证
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证成功的用户
     */
    Optional<User> authenticate(String username, String password);
    
    /**
     * 生成访问令牌
     *
     * @param user 用户
     * @return 访问令牌
     */
    String generateAccessToken(User user);
    
    /**
     * 生成刷新令牌
     *
     * @param user 用户
     * @return 刷新令牌
     */
    String generateRefreshToken(User user);
    
    /**
     * 验证令牌有效性
     *
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    String getUserIdFromToken(String token);
    
    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    String getUsernameFromToken(String token);
    
    /**
     * 无效化令牌（登出）
     *
     * @param token 令牌
     */
    void invalidateToken(String token);
    
    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    String refreshAccessToken(String refreshToken);
    
    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    String encryptPassword(String password);
    
    /**
     * 验证密码
     *
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
}
