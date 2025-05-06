package com.platform.monitor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 服务实例DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceDTO {
    
    /**
     * 实例ID
     */
    private String id;
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 实例IP地址
     */
    private String ipAddress;
    
    /**
     * 端口号
     */
    private int port;
    
    /**
     * 健康状态
     */
    private String healthStatus;
    
    /**
     * 健康状态显示名称
     */
    private String healthStatusDisplayName;
    
    /**
     * 最后健康检查时间
     */
    private LocalDateTime lastCheckTime;
    
    /**
     * 注册时间
     */
    private LocalDateTime registrationTime;
    
    /**
     * 元数据
     */
    private Map<String, String> metadata;
    
    /**
     * URI
     */
    private String uri;
    
    /**
     * 是否健康
     */
    private boolean healthy;
    
    /**
     * 版本
     */
    private String version;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * JDK版本
     */
    private String jdkVersion;
    
    /**
     * 上线时长（毫秒）
     */
    private long uptime;
    
    /**
     * 实例权重
     */
    private int weight;
    
    /**
     * 集群名称
     */
    private String clusterName;
}
