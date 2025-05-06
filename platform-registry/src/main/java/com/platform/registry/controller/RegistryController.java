package com.platform.registry.controller;

import com.platform.common.model.ResponseResult;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 注册中心控制器
 * <p>
 * 提供服务注册信息的查询接口
 * </p>
 */
@RestController
@RequestMapping("/registry")
public class RegistryController {

    private final DiscoveryClient discoveryClient;

    /**
     * 构造方法
     *
     * @param discoveryClient 服务发现客户端
     */
    public RegistryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 获取所有服务ID
     *
     * @return 服务ID列表
     */
    @GetMapping("/services")
    public ResponseResult<List<String>> getServices() {
        List<String> services = discoveryClient.getServices();
        return ResponseResult.success(services);
    }

    /**
     * 获取指定服务的实例列表
     *
     * @param serviceId 服务ID
     * @return 服务实例列表
     */
    @GetMapping("/service/{serviceId}/instances")
    public ResponseResult<List<Map<String, Object>>> getServiceInstances(@PathVariable String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        
        List<Map<String, Object>> instanceInfos = instances.stream()
                .map(instance -> Map.of(
                        "instanceId", instance.getInstanceId(),
                        "serviceId", instance.getServiceId(),
                        "host", instance.getHost(),
                        "port", instance.getPort(),
                        "uri", instance.getUri().toString(),
                        "metadata", instance.getMetadata(),
                        "scheme", instance.getScheme(),
                        "secure", instance.isSecure()
                ))
                .collect(Collectors.toList());
                
        return ResponseResult.success(instanceInfos);
    }
    
    /**
     * 获取注册中心状态信息
     *
     * @return 状态信息
     */
    @GetMapping("/status")
    public ResponseResult<Map<String, Object>> getStatus() {
        List<String> services = discoveryClient.getServices();
        int serviceCount = services.size();
        
        long instanceCount = services.stream()
                .mapToLong(service -> discoveryClient.getInstances(service).size())
                .sum();
                
        Map<String, Object> status = Map.of(
                "services", serviceCount,
                "instances", instanceCount,
                "discovery", discoveryClient.description()
        );
        
        return ResponseResult.success(status);
    }
}
