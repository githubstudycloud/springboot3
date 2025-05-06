package com.example.platform.config.interfaces.controller;

import com.example.platform.common.model.response.Result;
import com.example.platform.config.application.dto.*;
import com.example.platform.config.application.service.ConfigApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配置中心API控制器
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final ConfigApplicationService configApplicationService;

    /**
     * 创建配置
     */
    @PostMapping
    public Result<ConfigItemDTO> createConfig(
            @RequestBody @Validated CreateConfigItemRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "admin") String userId) {
        log.info("Create config request received: {}, operator: {}", request, userId);
        ConfigItemDTO config = configApplicationService.createConfig(request, userId);
        return Result.success(config);
    }

    /**
     * 更新配置
     */
    @PutMapping("/{dataId}/{group}/{environment}")
    public Result<ConfigItemDTO> updateConfig(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment,
            @RequestBody @Validated UpdateConfigItemRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "admin") String userId) {
        log.info("Update config request received: {}/{}/{}, operator: {}", dataId, group, environment, userId);
        ConfigItemDTO config = configApplicationService.updateConfig(dataId, group, environment, request, userId);
        return Result.success(config);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{dataId}/{group}/{environment}")
    public Result<Void> deleteConfig(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment,
            @RequestHeader(value = "X-User-ID", defaultValue = "admin") String userId) {
        log.info("Delete config request received: {}/{}/{}, operator: {}", dataId, group, environment, userId);
        configApplicationService.deleteConfig(dataId, group, environment, userId);
        return Result.success();
    }

    /**
     * 获取配置
     */
    @GetMapping("/{dataId}/{group}/{environment}")
    public Result<ConfigItemDTO> getConfig(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment) {
        log.info("Get config request received: {}/{}/{}", dataId, group, environment);
        ConfigItemDTO config = configApplicationService.getConfig(dataId, group, environment);
        return Result.success(config);
    }

    /**
     * 获取配置历史版本
     */
    @GetMapping("/{dataId}/{group}/{environment}/versions")
    public Result<List<ConfigVersionHistoryDTO>> getConfigVersions(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment) {
        log.info("Get config versions request received: {}/{}/{}", dataId, group, environment);
        List<ConfigVersionHistoryDTO> versions = configApplicationService.getConfigVersions(dataId, group, environment);
        return Result.success(versions);
    }

    /**
     * 回滚配置
     */
    @PostMapping("/{dataId}/{group}/{environment}/rollback")
    public Result<ConfigItemDTO> rollbackConfig(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment,
            @RequestBody @Validated RollbackConfigItemRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "admin") String userId) {
        log.info("Rollback config request received: {}/{}/{} to version {}, operator: {}", 
                dataId, group, environment, request.getVersion(), userId);
        ConfigItemDTO config = configApplicationService.rollbackConfig(dataId, group, environment, request, userId);
        return Result.success(config);
    }

    /**
     * 按分组查询配置
     */
    @GetMapping("/group/{group}/{environment}")
    public Result<List<ConfigItemDTO>> getConfigsByGroup(
            @PathVariable String group,
            @PathVariable String environment) {
        log.info("Get configs by group request received: {}/{}", group, environment);
        List<ConfigItemDTO> configs = configApplicationService.getConfigsByGroup(group, environment);
        return Result.success(configs);
    }

    /**
     * 按环境查询配置
     */
    @GetMapping("/environment/{environment}")
    public Result<List<ConfigItemDTO>> getConfigsByEnvironment(
            @PathVariable String environment) {
        log.info("Get configs by environment request received: {}", environment);
        List<ConfigItemDTO> configs = configApplicationService.getConfigsByEnvironment(environment);
        return Result.success(configs);
    }

    /**
     * 搜索配置
     */
    @GetMapping("/search")
    public Result<List<ConfigItemDTO>> searchConfigs(
            @RequestParam String keyword,
            @RequestParam String environment) {
        log.info("Search configs request received: keyword={}, environment={}", keyword, environment);
        List<ConfigItemDTO> configs = configApplicationService.searchConfigs(keyword, environment);
        return Result.success(configs);
    }

    /**
     * 获取最近的变更事件
     */
    @GetMapping("/events/recent")
    public Result<List<ConfigChangeEventDTO>> getRecentChangeEvents(
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Get recent change events request received: limit={}", limit);
        List<ConfigChangeEventDTO> events = configApplicationService.getRecentChangeEvents(limit);
        return Result.success(events);
    }

    /**
     * 获取特定配置的变更事件
     */
    @GetMapping("/{dataId}/{group}/{environment}/events")
    public Result<List<ConfigChangeEventDTO>> getConfigChangeEvents(
            @PathVariable String dataId,
            @PathVariable String group,
            @PathVariable String environment) {
        log.info("Get config change events request received: {}/{}/{}", dataId, group, environment);
        List<ConfigChangeEventDTO> events = configApplicationService.getConfigChangeEvents(dataId, group, environment);
        return Result.success(events);
    }
}
