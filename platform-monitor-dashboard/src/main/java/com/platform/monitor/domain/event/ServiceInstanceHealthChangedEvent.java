package com.platform.monitor.domain.event;

import com.platform.monitor.domain.model.HealthStatus;
import com.platform.monitor.domain.model.ServiceInstance;
import lombok.Getter;

/**
 * 服务实例健康状态变更事件
 */
@Getter
public class ServiceInstanceHealthChangedEvent extends MonitorEvent {
    
    /**
     * 服务实例
     */
    private final ServiceInstance serviceInstance;
    
    /**
     * 旧健康状态
     */
    private final HealthStatus oldHealthStatus;
    
    /**
     * 新健康状态
     */
    private final HealthStatus newHealthStatus;
    
    /**
     * 构造函数
     *
     * @param serviceInstance 服务实例
     * @param oldHealthStatus 旧健康状态
     * @param newHealthStatus 新健康状态
     */
    public ServiceInstanceHealthChangedEvent(ServiceInstance serviceInstance, 
                                            HealthStatus oldHealthStatus, 
                                            HealthStatus newHealthStatus) {
        super(MonitorEventType.SERVICE_INSTANCE_HEALTH_CHANGED, "platform-monitor-dashboard");
        this.serviceInstance = serviceInstance;
        this.oldHealthStatus = oldHealthStatus;
        this.newHealthStatus = newHealthStatus;
        
        // 添加事件数据
        addData("serviceInstanceId", serviceInstance.getId());
        addData("serviceName", serviceInstance.getServiceName());
        addData("ipAddress", serviceInstance.getIpAddress());
        addData("port", serviceInstance.getPort());
        addData("oldHealthStatus", oldHealthStatus.getCode());
        addData("newHealthStatus", newHealthStatus.getCode());
        addData("lastCheckTime", serviceInstance.getLastCheckTime());
    }
    
    @Override
    public String getDescription() {
        return String.format("服务实例健康状态变更: %s (%s:%d) %s -> %s",
                serviceInstance.getServiceName(),
                serviceInstance.getIpAddress(),
                serviceInstance.getPort(),
                oldHealthStatus.getDisplayName(),
                newHealthStatus.getDisplayName());
    }
    
    /**
     * 判断是否为健康状态降级
     *
     * @return 如果是健康状态降级则返回true
     */
    public boolean isHealthDegraded() {
        return oldHealthStatus == HealthStatus.UP && newHealthStatus != HealthStatus.UP;
    }
    
    /**
     * 判断是否为健康状态恢复
     *
     * @return 如果是健康状态恢复则返回true
     */
    public boolean isHealthRecovered() {
        return oldHealthStatus != HealthStatus.UP && newHealthStatus == HealthStatus.UP;
    }
}
