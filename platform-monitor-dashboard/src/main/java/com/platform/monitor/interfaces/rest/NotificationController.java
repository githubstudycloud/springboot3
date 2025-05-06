package com.platform.monitor.interfaces.rest;

import com.platform.monitor.application.dto.NotificationChannelDTO;
import com.platform.monitor.application.service.NotificationAppService;
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
 * 通知管理控制器
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "通知管理", description = "通知渠道和通知模板管理相关接口")
public class NotificationController {
    
    private final NotificationAppService notificationAppService;
    
    @PostMapping("/channels")
    @Operation(summary = "创建通知渠道", description = "创建新的通知渠道")
    public ResponseEntity<NotificationChannelDTO> createNotificationChannel(
            @Parameter(description = "通知渠道DTO", required = true) @RequestBody NotificationChannelDTO channelDTO) {
        log.debug("创建通知渠道: {}", channelDTO.getName());
        return ResponseEntity.ok(notificationAppService.createNotificationChannel(channelDTO));
    }
    
    @PutMapping("/channels")
    @Operation(summary = "更新通知渠道", description = "更新现有的通知渠道")
    public ResponseEntity<NotificationChannelDTO> updateNotificationChannel(
            @Parameter(description = "通知渠道DTO", required = true) @RequestBody NotificationChannelDTO channelDTO) {
        log.debug("更新通知渠道: {}", channelDTO.getId());
        return ResponseEntity.ok(notificationAppService.updateNotificationChannel(channelDTO));
    }
    
    @DeleteMapping("/channels/{channelId}")
    @Operation(summary = "删除通知渠道", description = "删除指定的通知渠道")
    public ResponseEntity<Boolean> deleteNotificationChannel(
            @Parameter(description = "通知渠道ID", required = true) @PathVariable String channelId) {
        log.debug("删除通知渠道: {}", channelId);
        return ResponseEntity.ok(notificationAppService.deleteNotificationChannel(channelId));
    }
    
    @PostMapping("/channels/{channelId}/enable")
    @Operation(summary = "启用通知渠道", description = "启用指定的通知渠道")
    public ResponseEntity<NotificationChannelDTO> enableNotificationChannel(
            @Parameter(description = "通知渠道ID", required = true) @PathVariable String channelId) {
        log.debug("启用通知渠道: {}", channelId);
        return ResponseEntity.ok(notificationAppService.enableNotificationChannel(channelId));
    }
    
    @PostMapping("/channels/{channelId}/disable")
    @Operation(summary = "禁用通知渠道", description = "禁用指定的通知渠道")
    public ResponseEntity<NotificationChannelDTO> disableNotificationChannel(
            @Parameter(description = "通知渠道ID", required = true) @PathVariable String channelId) {
        log.debug("禁用通知渠道: {}", channelId);
        return ResponseEntity.ok(notificationAppService.disableNotificationChannel(channelId));
    }
    
    @GetMapping("/channels/{channelId}")
    @Operation(summary = "获取通知渠道", description = "获取指定通知渠道的详情")
    public ResponseEntity<NotificationChannelDTO> getNotificationChannel(
            @Parameter(description = "通知渠道ID", required = true) @PathVariable String channelId) {
        log.debug("获取通知渠道: {}", channelId);
        return ResponseEntity.ok(notificationAppService.getNotificationChannel(channelId));
    }
    
    @GetMapping("/channels")
    @Operation(summary = "获取所有通知渠道", description = "获取系统中所有的通知渠道")
    public ResponseEntity<List<NotificationChannelDTO>> getAllNotificationChannels() {
        log.debug("获取所有通知渠道");
        return ResponseEntity.ok(notificationAppService.getAllNotificationChannels());
    }
    
    @GetMapping("/channels/enabled")
    @Operation(summary = "获取启用的通知渠道", description = "获取系统中所有启用的通知渠道")
    public ResponseEntity<List<NotificationChannelDTO>> getEnabledNotificationChannels() {
        log.debug("获取启用的通知渠道");
        return ResponseEntity.ok(notificationAppService.getEnabledNotificationChannels());
    }
    
    @GetMapping("/channels/type/{type}")
    @Operation(summary = "获取指定类型的通知渠道", description = "获取指定类型的通知渠道")
    public ResponseEntity<List<NotificationChannelDTO>> getNotificationChannelsByType(
            @Parameter(description = "通知渠道类型", required = true) @PathVariable String type) {
        log.debug("获取类型为 [{}] 的通知渠道", type);
        return ResponseEntity.ok(notificationAppService.getNotificationChannelsByType(type));
    }
    
    @PostMapping("/channels/{channelId}/test")
    @Operation(summary = "测试通知渠道", description = "发送测试消息验证通知渠道是否正常工作")
    public ResponseEntity<String> testNotificationChannel(
            @Parameter(description = "通知渠道ID", required = true) @PathVariable String channelId,
            @Parameter(description = "测试消息", required = true) @RequestParam String testMessage) {
        log.debug("测试通知渠道 [{}]", channelId);
        return ResponseEntity.ok(notificationAppService.testNotificationChannel(channelId, testMessage));
    }
    
    @GetMapping("/templates/{templateType}")
    @Operation(summary = "获取通知模板", description = "获取指定类型的通知模板")
    public ResponseEntity<String> getNotificationTemplate(
            @Parameter(description = "模板类型", required = true) @PathVariable String templateType) {
        log.debug("获取类型为 [{}] 的通知模板", templateType);
        return ResponseEntity.ok(notificationAppService.getNotificationTemplate(templateType));
    }
    
    @PutMapping("/templates/{templateType}")
    @Operation(summary = "更新通知模板", description = "更新指定类型的通知模板")
    public ResponseEntity<Boolean> updateNotificationTemplate(
            @Parameter(description = "模板类型", required = true) @PathVariable String templateType,
            @Parameter(description = "模板内容", required = true) @RequestBody String templateContent) {
        log.debug("更新类型为 [{}] 的通知模板", templateType);
        return ResponseEntity.ok(notificationAppService.updateNotificationTemplate(templateType, templateContent));
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取通知发送统计", description = "获取各通知渠道的发送统计")
    public ResponseEntity<Map<String, Map<String, Integer>>> getNotificationSendStatistics() {
        log.debug("获取通知发送统计");
        return ResponseEntity.ok(notificationAppService.getNotificationSendStatistics());
    }
    
    @GetMapping("/channel-types")
    @Operation(summary = "获取可用的通知渠道类型", description = "获取系统支持的所有通知渠道类型")
    public ResponseEntity<List<Map<String, String>>> getAvailableChannelTypes() {
        log.debug("获取可用的通知渠道类型");
        return ResponseEntity.ok(notificationAppService.getAvailableChannelTypes());
    }
    
    @PostMapping("/channels/validate")
    @Operation(summary = "验证通知渠道配置", description = "验证通知渠道配置是否有效")
    public ResponseEntity<String> validateChannelConfig(
            @Parameter(description = "通知渠道DTO", required = true) @RequestBody NotificationChannelDTO channelDTO) {
        log.debug("验证通知渠道配置: {}", channelDTO.getName());
        return ResponseEntity.ok(notificationAppService.validateChannelConfig(channelDTO));
    }
    
    @PostMapping("/send-custom")
    @Operation(summary = "发送自定义通知", description = "通过指定的通知渠道发送自定义通知")
    public ResponseEntity<Map<String, Boolean>> sendCustomNotification(
            @Parameter(description = "通知渠道ID列表", required = true) @RequestBody List<String> channelIds,
            @Parameter(description = "通知主题", required = true) @RequestParam String subject,
            @Parameter(description = "通知内容", required = true) @RequestParam String content) {
        log.debug("发送自定义通知, 主题: {}", subject);
        return ResponseEntity.ok(notificationAppService.sendCustomNotification(channelIds, subject, content));
    }
    
    @GetMapping("/history")
    @Operation(summary = "获取通知历史记录", description = "获取最近的通知历史记录")
    public ResponseEntity<List<Map<String, Object>>> getNotificationHistory(
            @Parameter(description = "记录数量限制", required = true) @RequestParam(defaultValue = "100") int limit) {
        log.debug("获取最近 {} 条通知历史记录", limit);
        return ResponseEntity.ok(notificationAppService.getNotificationHistory(limit));
    }
    
    @PostMapping("/resend/{notificationId}")
    @Operation(summary = "重发失败的通知", description = "重新发送失败的通知")
    public ResponseEntity<Boolean> resendFailedNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable String notificationId) {
        log.debug("重发失败的通知: {}", notificationId);
        return ResponseEntity.ok(notificationAppService.resendFailedNotification(notificationId));
    }
}
