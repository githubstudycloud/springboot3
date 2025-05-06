package com.platform.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 请求上下文工具类
 */
public class RequestContextUtil {
    
    /**
     * 请求ID的Header名称
     */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    
    /**
     * 用户ID的Header名称
     */
    public static final String USER_ID_HEADER = "X-User-Id";
    
    /**
     * 租户ID的Header名称
     */
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
    
    /**
     * 请求ID在请求属性中的键名
     */
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    
    /**
     * 获取当前请求属性
     *
     * @return 请求属性
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }
    
    /**
     * 获取当前请求对象
     *
     * @return 请求对象
     */
    public static Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(getRequestAttributes())
                .map(ServletRequestAttributes::getRequest);
    }
    
    /**
     * 获取当前响应对象
     *
     * @return 响应对象
     */
    public static Optional<HttpServletResponse> getResponse() {
        return Optional.ofNullable(getRequestAttributes())
                .map(ServletRequestAttributes::getResponse);
    }
    
    /**
     * 获取请求ID
     * <p>
     * 优先从请求属性中获取，如果不存在则从请求头中获取，如果仍不存在则生成新的请求ID
     *
     * @return 请求ID
     */
    public static String getRequestId() {
        // 1. 尝试从请求属性中获取
        HttpServletRequest request = getRequest().orElse(null);
        if (request != null) {
            Object requestIdAttr = request.getAttribute(REQUEST_ID_ATTRIBUTE);
            if (requestIdAttr != null) {
                return requestIdAttr.toString();
            }
            
            // 2. 尝试从请求头中获取
            String requestIdHeader = request.getHeader(REQUEST_ID_HEADER);
            if (StringUtils.isNotBlank(requestIdHeader)) {
                request.setAttribute(REQUEST_ID_ATTRIBUTE, requestIdHeader);
                return requestIdHeader;
            }
        }
        
        // 3. 生成新的请求ID
        String newRequestId = generateRequestId();
        if (request != null) {
            request.setAttribute(REQUEST_ID_ATTRIBUTE, newRequestId);
        }
        return newRequestId;
    }
    
    /**
     * 获取用户ID
     *
     * @return 用户ID，如果不存在则返回null
     */
    public static String getUserId() {
        return getRequest()
                .map(request -> request.getHeader(USER_ID_HEADER))
                .orElse(null);
    }
    
    /**
     * 获取租户ID
     *
     * @return 租户ID，如果不存在则返回null
     */
    public static String getTenantId() {
        return getRequest()
                .map(request -> request.getHeader(TENANT_ID_HEADER))
                .orElse(null);
    }
    
    /**
     * 获取客户端IP地址
     *
     * @return 客户端IP地址
     */
    public static String getClientIp() {
        return getRequest().map(RequestContextUtil::getClientIp).orElse("未知");
    }
    
    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "未知";
        }
        
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        
        String ip = null;
        for (String headerName : headerNames) {
            ip = request.getHeader(headerName);
            if (isValidIp(ip)) {
                break;
            }
        }
        
        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多级代理的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip == null ? "未知" : ip;
    }
    
    /**
     * 生成请求ID
     *
     * @return 新的请求ID
     */
    private static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 检查IP地址是否有效
     *
     * @param ip IP地址
     * @return 如果IP地址有效则返回true，否则返回false
     */
    private static boolean isValidIp(String ip) {
        return ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip);
    }
}
