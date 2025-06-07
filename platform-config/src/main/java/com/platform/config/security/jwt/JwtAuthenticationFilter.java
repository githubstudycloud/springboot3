package com.platform.config.security.jwt;

import com.platform.config.metrics.ConfigBusinessMetrics;
import com.platform.config.service.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 从请求中提取JWT token并验证用户身份
 *
 * @author Platform Team
 * @since 2.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final ConfigBusinessMetrics businessMetrics;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) 
            throws ServletException, IOException {

        // 记录并发请求
        businessMetrics.recordConcurrentRequest(request.getRequestURI());
        
        // 开始响应时间计时
        var responseTimer = businessMetrics.startResponseTimer();

        try {
            String token = extractTokenFromRequest(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    // 验证并处理JWT token
                    if (jwtTokenProvider.validateToken(token)) {
                        String username = jwtTokenProvider.getUsernameFromToken(token);
                        
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        if (userDetails != null) {
                            UsernamePasswordAuthenticationToken authToken = 
                                new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            
                            // 记录配置访问（如果是配置请求）
                            recordConfigAccess(request, username);
                            
                            log.debug("用户认证成功: {}, URI: {}", username, request.getRequestURI());
                        }
                    } else {
                        log.debug("JWT token验证失败");
                        recordAuthFailure(request, "INVALID_TOKEN");
                    }
                } catch (ExpiredJwtException e) {
                    log.warn("JWT token已过期: {}", e.getMessage());
                    recordAuthFailure(request, "EXPIRED_TOKEN");
                    setAuthErrorResponse(response, "JWT token已过期");
                    return;
                } catch (UnsupportedJwtException e) {
                    log.warn("不支持的JWT token: {}", e.getMessage());
                    recordAuthFailure(request, "UNSUPPORTED_TOKEN");
                    setAuthErrorResponse(response, "不支持的JWT token格式");
                    return;
                } catch (MalformedJwtException e) {
                    log.warn("JWT token格式错误: {}", e.getMessage());
                    recordAuthFailure(request, "MALFORMED_TOKEN");
                    setAuthErrorResponse(response, "JWT token格式错误");
                    return;
                } catch (SignatureException e) {
                    log.warn("JWT token签名无效: {}", e.getMessage());
                    recordAuthFailure(request, "INVALID_SIGNATURE");
                    setAuthErrorResponse(response, "JWT token签名无效");
                    return;
                } catch (Exception e) {
                    log.error("JWT token处理异常", e);
                    recordAuthFailure(request, "TOKEN_PROCESSING_ERROR");
                    setAuthErrorResponse(response, "认证处理异常");
                    return;
                }
            }

            filterChain.doFilter(request, response);
            
            // 结束响应时间计时
            businessMetrics.stopResponseTimer(responseTimer, request.getRequestURI(), request.getMethod());

        } catch (Exception e) {
            log.error("认证过滤器异常", e);
            businessMetrics.stopResponseTimer(responseTimer, request.getRequestURI(), "ERROR");
            throw e;
        }
    }

    /**
     * 从请求中提取JWT token
     *
     * @param request HTTP请求
     * @return JWT token字符串，如果不存在则返回null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 也支持从查询参数中获取token（仅用于特殊情况）
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            log.debug("从查询参数中获取token: {}", request.getRequestURI());
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 记录配置访问
     *
     * @param request HTTP请求
     * @param username 用户名
     */
    private void recordConfigAccess(HttpServletRequest request, String username) {
        String uri = request.getRequestURI();
        
        // 解析配置请求路径
        if (uri.contains("/api/config/")) {
            String[] pathParts = uri.split("/");
            if (pathParts.length >= 4) {
                String application = pathParts[3];
                String profile = pathParts.length > 4 ? pathParts[4] : "default";
                String configKey = pathParts.length > 5 ? pathParts[5] : "default";
                
                businessMetrics.recordConfigAccess(application, profile, configKey);
            }
        } else if (uri.matches("/.+/.+")) {
            // Spring Cloud Config格式: /application/profile
            String[] pathParts = uri.substring(1).split("/");
            if (pathParts.length >= 2) {
                String application = pathParts[0];
                String profile = pathParts[1];
                
                businessMetrics.recordConfigAccess(application, profile, "default");
            }
        }
    }

    /**
     * 记录认证失败
     *
     * @param request HTTP请求
     * @param reason 失败原因
     */
    private void recordAuthFailure(HttpServletRequest request, String reason) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        log.warn("认证失败: URI={}, IP={}, UserAgent={}, Reason={}", 
                request.getRequestURI(), clientIp, userAgent, reason);
        
        // 记录审计失败
        businessMetrics.recordAuditOperation("AUTHENTICATION", "UNKNOWN", "UNKNOWN", "FAILURE");
    }

    /**
     * 设置认证错误响应
     *
     * @param response HTTP响应
     * @param message 错误消息
     */
    private void setAuthErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}", 
            message, java.time.Instant.now().toString()));
    }

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过这些路径的JWT验证
        return path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/prometheus") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api/auth/") ||
               path.equals("/favicon.ico");
    }
} 