package com.platform.monitor.domain.repository;

import com.platform.monitor.domain.model.HealthStatus;
import com.platform.monitor.domain.model.ServiceInstance;

import java.util.List;
import java.util.Optional;

/**
 * 服务实例仓储接口
 */
public interface ServiceInstanceRepository {
    
    /**
     * 保存服务实例
     *
     * @param serviceInstance 服务实例
     * @return 保存后的服务实例
     */
    ServiceInstance save(ServiceInstance serviceInstance);
    
    /**
     * 根据ID查找服务实例
     *
     * @param id 服务实例ID
     * @return 服务实例可选结果
     */
    Optional<ServiceInstance> findById(String id);
    
    /**
     * 根据服务名称查找服务实例列表
     *
     * @param serviceName 服务名称
     * @return 服务实例列表
     */
    List<ServiceInstance> findByServiceName(String serviceName);
    
    /**
     * 根据健康状态查找服务实例列表
     *
     * @param healthStatus 健康状态
     * @return 服务实例列表
     */
    List<ServiceInstance> findByHealthStatus(HealthStatus healthStatus);
    
    /**
     * 删除服务实例
     *
     * @param id 服务实例ID
     */
    void deleteById(String id);
    
    /**
     * 获取所有服务实例
     *
     * @return 所有服务实例列表
     */
    List<ServiceInstance> findAll();
    
    /**
     * 获取服务名称列表
     *
     * @return 服务名称列表
     */
    List<String> findAllServiceNames();
    
    /**
     * 获取指定服务的健康实例数量
     *
     * @param serviceName 服务名称
     * @return 健康实例数量
     */
    long countHealthyInstancesByServiceName(String serviceName);
    
    /**
     * 获取指定服务的实例总数
     *
     * @param serviceName 服务名称
     * @return 实例总数
     */
    long countInstancesByServiceName(String serviceName);
    
    /**
     * 批量保存服务实例
     *
     * @param serviceInstances 服务实例列表
     * @return 保存后的服务实例列表
     */
    List<ServiceInstance> saveAll(List<ServiceInstance> serviceInstances);
    
    /**
     * 删除指定服务名称的所有实例
     *
     * @param serviceName 服务名称
     */
    void deleteByServiceName(String serviceName);
    
    /**
     * 查找需要健康检查的服务实例
     *
     * @param checkIntervalSeconds 健康检查间隔（秒）
     * @return 需要健康检查的服务实例列表
     */
    List<ServiceInstance> findInstancesForHealthCheck(int checkIntervalSeconds);
}
