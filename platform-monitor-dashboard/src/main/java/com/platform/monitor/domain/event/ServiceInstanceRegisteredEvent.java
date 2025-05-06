package com.platform.monitor.domain.event;

import com.platform.monitor.domain.model.ServiceInstance;
import lombok.Getter;

/**
 * 服务实例注册事件
 */
@Getter
public class ServiceInstanceRegisteredEvent extends MonitorEvent {
    
    /**
     * 服务实例
     */
    private final ServiceInstance serviceInstance;
    
    /**
     * 构造函数
     *
     * @param serviceInstance 服务实例
     */
    public ServiceInstanceRegisteredEvent(ServiceInstance serviceInstance) {
        super(MonitorEventType.SERVICE_INSTANCE_REGISTERED, "platform-monitor-dashboard");
        this.serviceInstance = serviceInstance;
        
        // 添加事件数据
        addData("serviceInstanceId", serviceInstance.getId());
        addData("serviceName", serviceInstance.getServiceName());
        addData("ipAddress", serviceInstance.getIpAddress());
        addData("port", serviceInstance.getPort());
        addData("healthStatus", serviceInstance.getHealthStatus().getCode());
        addData("registrationTime", serviceInstance.getRegistrationTime());
    }
    
    @Override
    public String getDescription() {
        return String.format("服务实例已注册: %s (%s:%d)",
                serviceInstance.getServiceName(),
                serviceInstance.getIpAddress(),
                serviceInstance.getPort());
    }
}
