package com.platform.monitor.application.service;

import com.platform.monitor.application.dto.ServiceInstanceDTO;

import java.util.List;
import java.util.Map;

/**
 * 服务监控应用服务接口
 */
public interface ServiceMonitoringAppService {
    
    /**
     * 获取所有服务实例
     *
     * @return 服务实例DTO列表
     */
    List<ServiceInstanceDTO> getAllServiceInstances();
    
    /**
     * 获取指定服务的实例列表
     *
     * @param serviceName 服务名称
     * @return 服务实例DTO列表
     */
    List<ServiceInstanceDTO> getServiceInstancesByServiceName(String serviceName);
    
    /**
     * 获取服务实例详情
     *
     * @param instanceId 实例ID
     * @return 服务实例DTO
     */
    ServiceInstanceDTO getServiceInstanceDetail(String instanceId);
    
    /**
     * 手动触发服务发现
     *
     * @return 发现的服务实例数量
     */
    int triggerServiceDiscovery();
    
    /**
     * 手动触发服务实例健康检查
     *
     * @param instanceId 实例ID
     * @return 更新后的服务实例DTO
     */
    ServiceInstanceDTO triggerHealthCheck(String instanceId);
    
    /**
     * 批量触发服务实例健康检查
     *
     * @param instanceIds 实例ID列表
     * @return 已更新的服务实例DTO列表
     */
    List<ServiceInstanceDTO> batchTriggerHealthCheck(List<String> instanceIds);
    
    /**
     * 获取所有服务名称
     *
     * @return 服务名称列表
     */
    List<String> getAllServiceNames();
    
    /**
     * 获取服务健康状态统计
     *
     * @return 服务健康状态统计（服务名称 -> 统计数据）
     */
    Map<String, Map<String, Integer>> getServiceHealthStatistics();
    
    /**
     * 注销服务实例
     *
     * @param instanceId 实例ID
     * @return 操作是否成功
     */
    boolean deregisterServiceInstance(String instanceId);
    
    /**
     * 获取服务实例数量统计
     *
     * @return 服务实例数量统计（服务名称 -> 实例数量）
     */
    Map<String, Integer> getServiceInstanceCounts();
    
    /**
     * 获取健康服务实例数量统计
     *
     * @return 健康服务实例数量统计（服务名称 -> 健康实例数量）
     */
    Map<String, Integer> getHealthyServiceInstanceCounts();
    
    /**
     * 获取服务健康状态历史数据
     *
     * @param serviceName 服务名称
     * @param hours 小时数
     * @return 健康状态历史数据（时间戳 -> 健康比例）
     */
    Map<String, Double> getServiceHealthHistory(String serviceName, int hours);
    
    /**
     * 获取服务元数据统计
     *
     * @param metadataKey 元数据键
     * @return 元数据统计（元数据值 -> 实例数量）
     */
    Map<String, Integer> getServiceMetadataStatistics(String metadataKey);
}
