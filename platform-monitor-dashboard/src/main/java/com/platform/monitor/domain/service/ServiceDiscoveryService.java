package com.platform.monitor.domain.service;

import com.platform.monitor.domain.model.ServiceInstance;

import java.util.List;

/**
 * 服务发现领域服务接口
 */
public interface ServiceDiscoveryService {
    
    /**
     * 发现服务实例
     * 
     * @return 发现的服务实例列表
     */
    List<ServiceInstance> discoverServices();
    
    /**
     * 获取服务实例详情
     * 
     * @param serviceId 服务ID
     * @return 服务实例
     */
    ServiceInstance getServiceInstance(String serviceId);
    
    /**
     * 检查服务实例健康状态
     * 
     * @param serviceInstance 服务实例
     * @return 更新后的服务实例
     */
    ServiceInstance checkServiceHealth(ServiceInstance serviceInstance);
    
    /**
     * 获取所有服务名称
     * 
     * @return 服务名称列表
     */
    List<String> getAllServiceNames();
    
    /**
     * 获取指定服务的实例列表
     * 
     * @param serviceName 服务名称
     * @return 服务实例列表
     */
    List<ServiceInstance> getInstancesByServiceName(String serviceName);
    
    /**
     * 注册服务实例
     * 
     * @param serviceInstance 服务实例
     * @return 注册后的服务实例
     */
    ServiceInstance registerServiceInstance(ServiceInstance serviceInstance);
    
    /**
     * 注销服务实例
     * 
     * @param serviceId 服务实例ID
     * @return 操作是否成功
     */
    boolean deregisterServiceInstance(String serviceId);
    
    /**
     * 获取服务实例数量统计
     * 
     * @return 服务实例数量映射（服务名称 -> 实例数量）
     */
    java.util.Map<String, Integer> getServiceInstanceCounts();
    
    /**
     * 获取健康服务实例数量统计
     * 
     * @return 健康服务实例数量映射（服务名称 -> 健康实例数量）
     */
    java.util.Map<String, Integer> getHealthyServiceInstanceCounts();
    
    /**
     * 获取服务健康状态比例
     * 
     * @param serviceName 服务名称
     * @return 健康实例比例（0-1之间的小数）
     */
    double getServiceHealthPercentage(String serviceName);
    
    /**
     * 同步所有服务实例信息
     * 
     * @return 同步后的服务实例列表
     */
    List<ServiceInstance> syncAllServices();
}
