package com.platform.config.controller;

import com.platform.config.entity.ConfigVersion;
import com.platform.config.service.ConfigManagementService;
import com.platform.config.service.ConfigVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 响应式配置API
 * 提供基于WebFlux的异步、非阻塞配置管理接口
 *
 * @author Platform Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/reactive/config")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "响应式配置API", description = "基于WebFlux的异步配置管理API - 支持响应式编程和事件流")
public class ReactiveConfigController {

    private final ConfigManagementService configManagementService;
    private final ConfigVersionService versionService;

    @Operation(summary = "响应式获取配置", description = "异步获取指定应用和环境的配置")
    @GetMapping("/get")
    public Mono<Map<String, Object>> getConfigAsync(
            @Parameter(description = "应用名称", required = true)
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true)
            @RequestParam @NotBlank String profile) {

        return Mono.fromCallable(() -> {
                    Map<String, Object> status = configManagementService.getConfigStatus();
                    return Map.of(
                            "message", "响应式配置获取成功",
                            "application", application,
                            "profile", profile,
                            "timestamp", LocalDateTime.now(),
                            "status", status
                    );
                })
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(throwable -> {
                    log.error("响应式配置获取失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(Map.of(
                            "message", "配置获取失败",
                            "error", throwable.getMessage(),
                            "application", application,
                            "profile", profile
                    ));
                });
    }

    @Operation(summary = "配置状态监控流", description = "实时监控配置状态变化")
    @GetMapping(value = "/status/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getConfigStatusStream() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(tick -> {
                    Map<String, Object> status = configManagementService.getConfigStatus();
                    status.put("timestamp", LocalDateTime.now());
                    status.put("tick", tick);
                    return status;
                })
                .take(Duration.ofMinutes(10))
                .onErrorResume(throwable -> {
                    log.error("配置状态监控流失败: {}", throwable.getMessage(), throwable);
                    return Flux.just(Map.of(
                            "error", "状态监控流失败",
                            "message", throwable.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
                });
    }

    @Operation(summary = "响应式版本创建", description = "异步创建配置版本")
    @PostMapping("/versions/create")
    public Mono<Map<String, Object>> createVersionAsync(
            @Parameter(description = "应用名称", required = true)
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true)
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "配置内容", required = true)
            @RequestBody String content,
            
            @Parameter(description = "操作人员")
            @RequestParam(defaultValue = "system") String operator) {

        return Mono.fromCallable(() -> {
                    ConfigVersion version = versionService.saveVersion(
                            application, profile, content, operator);
                    return Map.of(
                            "message", "响应式版本创建成功",
                            "version", buildVersionResponse(version),
                            "timestamp", LocalDateTime.now()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(15))
                .onErrorResume(throwable -> {
                    log.error("响应式版本创建失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(Map.of(
                            "message", "版本创建失败",
                            "error", throwable.getMessage(),
                            "application", application,
                            "profile", profile
                    ));
                });
    }

    private Map<String, Object> buildVersionResponse(ConfigVersion version) {
        return Map.of(
                "id", version.getId(),
                "version", version.getVersion(),
                "application", version.getApplication(),
                "profile", version.getProfile(),
                "operator", version.getOperator(),
                "createTime", version.getCreateTime(),
                "active", version.getActive()
        );
    }
}
