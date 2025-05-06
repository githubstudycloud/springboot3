package com.platform.monitor.interfaces.rest;

import com.platform.monitor.application.dto.ServiceInstanceDTO;
import com.platform.monitor.application.service.ServiceMonitoringAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 服务监控控制器
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "服务监控", description = "服务实例监控相关接口")
public class ServiceMonitoringController {
    
    private final ServiceMonitoringAppService serviceMonitoringAppService;
    
    @GetMapping
    @Operation(summary = "获取所有服务实例", description = "获取系统中所有服务实例的列表")
    public ResponseEntity<List<ServiceInstanceDTO>> getAllServiceInstances() {
        log.debug("获取所有服务实例");
        return ResponseEntity.ok(serviceMonitoringAppService.getAllServiceInstances());
    }
    
    @GetMapping("/{serviceName}/instances")
    @Operation(summary = "获取指定服务的实例列表", description = "根据服务名称获取服务实例列表")
    public ResponseEntity<List<ServiceInstanceDTO>> getServiceInstancesByServiceName(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName) {
        log.debug("获取服务 [{}] 的实例列表", serviceName);
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceInstancesByServiceName(serviceName));
    }
    
    @GetMapping("/instances/{instanceId}")
    @Operation(summary = "获取服务实例详情", description = "根据实例ID获取服务实例详细信息")
    public ResponseEntity<ServiceInstanceDTO> getServiceInstanceDetail(
            @Parameter(description = "实例ID", required = true) @PathVariable String instanceId) {
        log.debug("获取服务实例 [{}] 详情", instanceId);
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceInstanceDetail(instanceId));
    }
    
    @PostMapping("/discovery")
    @Operation(summary = "手动触发服务发现", description = "手动触发服务发现过程")
    public ResponseEntity<Integer> triggerServiceDiscovery() {
        log.debug("手动触发服务发现");
        int count = serviceMonitoringAppService.triggerServiceDiscovery();
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/instances/{instanceId}/health-check")
    @Operation(summary = "手动触发服务实例健康检查", description = "手动触发指定服务实例的健康检查")
    public ResponseEntity<ServiceInstanceDTO> triggerHealthCheck(
            @Parameter(description = "实例ID", required = true) @PathVariable String instanceId) {
        log.debug("手动触发服务实例 [{}] 健康检查", instanceId);
        ServiceInstanceDTO instance = serviceMonitoringAppService.triggerHealthCheck(instanceId);
        return ResponseEntity.ok(instance);
    }
    
    @PostMapping("/instances/batch-health-check")
    @Operation(summary = "批量触发服务实例健康检查", description = "批量触发多个服务实例的健康检查")
    public ResponseEntity<List<ServiceInstanceDTO>> batchTriggerHealthCheck(
            @Parameter(description = "实例ID列表", required = true) @RequestBody List<String> instanceIds) {
        log.debug("批量触发服务实例健康检查: {}", instanceIds);
        List<ServiceInstanceDTO> instances = serviceMonitoringAppService.batchTriggerHealthCheck(instanceIds);
        return ResponseEntity.ok(instances);
    }
    
    @GetMapping("/names")
    @Operation(summary = "获取所有服务名称", description = "获取系统中所有服务的名称列表")
    public ResponseEntity<List<String>> getAllServiceNames() {
        log.debug("获取所有服务名称");
        return ResponseEntity.ok(serviceMonitoringAppService.getAllServiceNames());
    }
    
    @GetMapping("/health-statistics")
    @Operation(summary = "获取服务健康状态统计", description = "获取各服务的健康状态统计信息")
    public ResponseEntity<Map<String, Map<String, Integer>>> getServiceHealthStatistics() {
        log.debug("获取服务健康状态统计");
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceHealthStatistics());
    }
    
    @DeleteMapping("/instances/{instanceId}")
    @Operation(summary = "注销服务实例", description = "从服务注册中心注销指定服务实例")
    public ResponseEntity<Boolean> deregisterServiceInstance(
            @Parameter(description = "实例ID", required = true) @PathVariable String instanceId) {
        log.debug("注销服务实例 [{}]", instanceId);
        boolean result = serviceMonitoringAppService.deregisterServiceInstance(instanceId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/instance-counts")
    @Operation(summary = "获取服务实例数量统计", description = "获取各服务的实例数量统计")
    public ResponseEntity<Map<String, Integer>> getServiceInstanceCounts() {
        log.debug("获取服务实例数量统计");
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceInstanceCounts());
    }
    
    @GetMapping("/healthy-instance-counts")
    @Operation(summary = "获取健康服务实例数量统计", description = "获取各服务的健康实例数量统计")
    public ResponseEntity<Map<String, Integer>> getHealthyServiceInstanceCounts() {
        log.debug("获取健康服务实例数量统计");
        return ResponseEntity.ok(serviceMonitoringAppService.getHealthyServiceInstanceCounts());
    }
    
    @GetMapping("/{serviceName}/health-history")
    @Operation(summary = "获取服务健康状态历史数据", description = "获取指定服务的健康状态历史数据")
    public ResponseEntity<Map<String, Double>> getServiceHealthHistory(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName,
            @Parameter(description = "历史小时数", required = true) @RequestParam(defaultValue = "24") int hours) {
        log.debug("获取服务 [{}] 的健康状态历史数据", serviceName);
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceHealthHistory(serviceName, hours));
    }
    
    @GetMapping("/metadata-statistics")
    @Operation(summary = "获取服务元数据统计", description = "获取指定元数据键的统计信息")
    public ResponseEntity<Map<String, Integer>> getServiceMetadataStatistics(
            @Parameter(description = "元数据键", required = true) @RequestParam String metadataKey) {
        log.debug("获取服务元数据 [{}] 统计", metadataKey);
        return ResponseEntity.ok(serviceMonitoringAppService.getServiceMetadataStatistics(metadataKey));
    }
}
