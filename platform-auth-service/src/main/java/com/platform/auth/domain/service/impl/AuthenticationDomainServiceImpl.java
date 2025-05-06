package com.platform.auth.domain.service.impl;

import com.platform.auth.domain.model.User;
import com.platform.auth.domain.repository.UserRepository;
import com.platform.auth.domain.service.AuthenticationDomainService;
import com.platform.auth.infrastructure.security.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证领域服务实现
 * <p>
 * 实现JWT令牌生成、验证等认证相关功能
 * </p>
 */
@Service
public class AuthenticationDomainServiceImpl implements AuthenticationDomainService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationDomainServiceImpl.class);
    
    /**
     * Redis中JWT黑名单前缀
     */
    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";
    
    /**
     * 刷新令牌前缀
     */
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final Key key;
    
    public AuthenticationDomainServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProperties jwtProperties,
            RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.isEnabled())
                .filter(user -> verifyPassword(password, user.getPassword()));
    }
    
    @Override
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());
        
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(jwtProperties.getIssuer())
                .setId(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("tenantId", user.getTenantId())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    @Override
    public String generateRefreshToken(User user) {
        // 刷新令牌有效期设为访问令牌的5倍
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration() * 5);
        
        String refreshToken = REFRESH_TOKEN_PREFIX + UUID.randomUUID().toString();
        
        // 存储刷新令牌到Redis，绑定用户名
        redisTemplate.opsForValue().set(
                refreshToken,
                user.getUsername(),
                jwtProperties.getExpiration() * 5,
                TimeUnit.MILLISECONDS
        );
        
        return refreshToken;
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            if (Boolean.TRUE.equals(redisTemplate.hasKey(JWT_BLACKLIST_PREFIX + token))) {
                return false;
            }
            
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("无效的JWT令牌: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("无效的JWT签名: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌参数无效: {}", e.getMessage());
        }
        return false;
    }
    
    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        return claims.get("userId", String.class);
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        return claims.getSubject();
    }
    
    @Override
    public void invalidateToken(String token) {
        try {
            // 解析令牌以获取过期时间
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
                    
            // 计算剩余有效时间
            Date expiration = claims.getExpiration();
            long ttl = Math.max(0, expiration.getTime() - System.currentTimeMillis());
            
            // 将令牌加入黑名单，过期时间与令牌相同
            redisTemplate.opsForValue().set(
                    JWT_BLACKLIST_PREFIX + token,
                    "1",
                    ttl,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.error("无法使令牌无效: {}", e.getMessage());
        }
    }
    
    @Override
    public String refreshAccessToken(String refreshToken) {
        // 从Redis中获取用户名
        String username = redisTemplate.opsForValue().get(refreshToken);
        
        if (username != null) {
            // 查询用户
            return userRepository.findByUsername(username)
                    .filter(User::isEnabled)
                    .map(this::generateAccessToken)
                    .orElse(null);
        }
        
        return null;
    }
    
    @Override
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
