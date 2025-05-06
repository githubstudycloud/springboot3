package com.platform.monitor.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 服务实例领域模型
 */
@Getter
public class ServiceInstance extends AbstractEntity<String> {
    
    /**
     * 实例ID
     */
    private final String id;
    
    /**
     * 服务名称
     */
    private final String serviceName;
    
    /**
     * 实例IP地址
     */
    private final String ipAddress;
    
    /**
     * 端口号
     */
    private final int port;
    
    /**
     * 健康状态
     */
    private HealthStatus healthStatus;
    
    /**
     * 最后健康检查时间
     */
    private LocalDateTime lastCheckTime;
    
    /**
     * 注册时间
     */
    private final LocalDateTime registrationTime;
    
    /**
     * 元数据
     */
    private final ServiceInstanceMetadata metadata;
    
    /**
     * 构造函数
     * 
     * @param id 实例ID
     * @param serviceName 服务名称
     * @param ipAddress IP地址
     * @param port 端口号
     * @param healthStatus 健康状态
     * @param metadata 实例元数据
     */
    public ServiceInstance(String id, String serviceName, String ipAddress, int port, 
                          HealthStatus healthStatus, ServiceInstanceMetadata metadata) {
        this.id = id;
        this.serviceName = serviceName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.healthStatus = healthStatus;
        this.lastCheckTime = LocalDateTime.now();
        this.registrationTime = LocalDateTime.now();
        this.metadata = metadata;
    }
    
    /**
     * 更新服务实例健康状态
     * 
     * @param newStatus 新的健康状态
     */
    public void updateHealthStatus(HealthStatus newStatus) {
        this.healthStatus = newStatus;
        this.lastCheckTime = LocalDateTime.now();
    }
    
    /**
     * 判断服务实例是否健康
     * 
     * @return 如果健康则返回true，否则返回false
     */
    public boolean isHealthy() {
        return HealthStatus.UP.equals(this.healthStatus);
    }
    
    /**
     * 获取服务实例URI
     * 
     * @return 服务实例URI
     */
    public String getUri() {
        boolean isSecure = metadata != null && Boolean.parseBoolean(metadata.getMetadataValue("secure", "false"));
        String protocol = isSecure ? "https" : "http";
        String contextPath = metadata != null ? 
                metadata.getMetadataValue("context-path", "") : "";
                
        if (contextPath.startsWith("/")) {
            contextPath = contextPath.substring(1);
        }
        
        return String.format("%s://%s:%d/%s", protocol, ipAddress, port, contextPath);
    }
    
    /**
     * 判断服务实例是否应该触发健康检查
     * 
     * @param checkIntervalSeconds 健康检查间隔（秒）
     * @return 如果应该触发健康检查则返回true，否则返回false
     */
    public boolean shouldTriggerHealthCheck(int checkIntervalSeconds) {
        return lastCheckTime.plusSeconds(checkIntervalSeconds).isBefore(LocalDateTime.now());
    }
}
