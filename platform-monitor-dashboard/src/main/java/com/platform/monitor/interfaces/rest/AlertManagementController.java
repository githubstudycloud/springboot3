package com.platform.monitor.interfaces.rest;

import com.platform.monitor.application.dto.AlertDTO;
import com.platform.monitor.application.dto.AlertEventDTO;
import com.platform.monitor.application.dto.AlertRuleDTO;
import com.platform.monitor.application.dto.AlertRuleConditionDTO;
import com.platform.monitor.application.service.AlertManagementAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 告警管理控制器
 */
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "告警管理", description = "告警和告警规则管理相关接口")
public class AlertManagementController {
    
    private final AlertManagementAppService alertManagementAppService;
    
    @PostMapping("/rules")
    @Operation(summary = "创建告警规则", description = "创建新的告警规则")
    public ResponseEntity<AlertRuleDTO> createAlertRule(
            @Parameter(description = "告警规则DTO", required = true) @RequestBody AlertRuleDTO alertRuleDTO) {
        log.debug("创建告警规则: {}", alertRuleDTO.getName());
        return ResponseEntity.ok(alertManagementAppService.createAlertRule(alertRuleDTO));
    }
    
    @PutMapping("/rules")
    @Operation(summary = "更新告警规则", description = "更新现有的告警规则")
    public ResponseEntity<AlertRuleDTO> updateAlertRule(
            @Parameter(description = "告警规则DTO", required = true) @RequestBody AlertRuleDTO alertRuleDTO) {
        log.debug("更新告警规则: {}", alertRuleDTO.getId());
        return ResponseEntity.ok(alertManagementAppService.updateAlertRule(alertRuleDTO));
    }
    
    @DeleteMapping("/rules/{ruleId}")
    @Operation(summary = "删除告警规则", description = "删除指定的告警规则")
    public ResponseEntity<Boolean> deleteAlertRule(
            @Parameter(description = "规则ID", required = true) @PathVariable String ruleId) {
        log.debug("删除告警规则: {}", ruleId);
        return ResponseEntity.ok(alertManagementAppService.deleteAlertRule(ruleId));
    }
    
    @PostMapping("/rules/{ruleId}/enable")
    @Operation(summary = "启用告警规则", description = "启用指定的告警规则")
    public ResponseEntity<AlertRuleDTO> enableAlertRule(
            @Parameter(description = "规则ID", required = true) @PathVariable String ruleId) {
        log.debug("启用告警规则: {}", ruleId);
        return ResponseEntity.ok(alertManagementAppService.enableAlertRule(ruleId));
    }
    
    @PostMapping("/rules/{ruleId}/disable")
    @Operation(summary = "禁用告警规则", description = "禁用指定的告警规则")
    public ResponseEntity<AlertRuleDTO> disableAlertRule(
            @Parameter(description = "规则ID", required = true) @PathVariable String ruleId) {
        log.debug("禁用告警规则: {}", ruleId);
        return ResponseEntity.ok(alertManagementAppService.disableAlertRule(ruleId));
    }
    
    @GetMapping("/rules/{ruleId}")
    @Operation(summary = "获取告警规则", description = "获取指定告警规则的详情")
    public ResponseEntity<AlertRuleDTO> getAlertRule(
            @Parameter(description = "规则ID", required = true) @PathVariable String ruleId) {
        log.debug("获取告警规则: {}", ruleId);
        return ResponseEntity.ok(alertManagementAppService.getAlertRule(ruleId));
    }
    
    @GetMapping("/rules")
    @Operation(summary = "获取所有告警规则", description = "获取系统中所有的告警规则")
    public ResponseEntity<List<AlertRuleDTO>> getAllAlertRules() {
        log.debug("获取所有告警规则");
        return ResponseEntity.ok(alertManagementAppService.getAllAlertRules());
    }
    
    @GetMapping("/rules/enabled")
    @Operation(summary = "获取启用的告警规则", description = "获取系统中所有启用的告警规则")
    public ResponseEntity<List<AlertRuleDTO>> getEnabledAlertRules() {
        log.debug("获取启用的告警规则");
        return ResponseEntity.ok(alertManagementAppService.getEnabledAlertRules());
    }
    
    @GetMapping("/rules/service/{serviceName}")
    @Operation(summary = "获取指定服务的告警规则", description = "获取应用于指定服务的告警规则")
    public ResponseEntity<List<AlertRuleDTO>> getAlertRulesByService(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName) {
        log.debug("获取服务 [{}] 的告警规则", serviceName);
        return ResponseEntity.ok(alertManagementAppService.getAlertRulesByService(serviceName));
    }
    
    @PostMapping("/rules/{ruleId}/conditions")
    @Operation(summary = "添加告警规则条件", description = "为指定告警规则添加条件")
    public ResponseEntity<AlertRuleDTO> addRuleCondition(
            @Parameter(description = "规则ID", required = true) @PathVariable String ruleId,
            @Parameter(description = "告警规则条件DTO", required = true) @RequestBody AlertRuleConditionDTO conditionDTO) {
        log.debug("为告警规则 [{}] 添加条件", ruleId);
        return ResponseEntity.ok(alertManagementAppService.addRuleCondition(ruleId, conditionDTO));
    }
    
    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "确认告警", description = "确认指定的告警")
    public ResponseEntity<AlertDTO> acknowledgeAlert(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId,
            @Parameter(description = "操作人", required = true) @RequestParam String operator,
            @Parameter(description = "备注信息") @RequestParam(required = false) String note) {
        log.debug("确认告警 [{}] 操作人 [{}]", alertId, operator);
        return ResponseEntity.ok(alertManagementAppService.acknowledgeAlert(alertId, operator, note));
    }
    
    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "解决告警", description = "解决指定的告警")
    public ResponseEntity<AlertDTO> resolveAlert(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId,
            @Parameter(description = "操作人", required = true) @RequestParam String operator,
            @Parameter(description = "备注信息") @RequestParam(required = false) String note) {
        log.debug("解决告警 [{}] 操作人 [{}]", alertId, operator);
        return ResponseEntity.ok(alertManagementAppService.resolveAlert(alertId, operator, note));
    }
    
    @PostMapping("/{alertId}/close")
    @Operation(summary = "关闭告警", description = "关闭指定的告警")
    public ResponseEntity<AlertDTO> closeAlert(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId,
            @Parameter(description = "操作人", required = true) @RequestParam String operator,
            @Parameter(description = "备注信息") @RequestParam(required = false) String note) {
        log.debug("关闭告警 [{}] 操作人 [{}]", alertId, operator);
        return ResponseEntity.ok(alertManagementAppService.closeAlert(alertId, operator, note));
    }
    
    @PostMapping("/{alertId}/suppress")
    @Operation(summary = "抑制告警", description = "抑制指定的告警")
    public ResponseEntity<AlertDTO> suppressAlert(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId,
            @Parameter(description = "操作人", required = true) @RequestParam String operator,
            @Parameter(description = "备注信息") @RequestParam(required = false) String note) {
        log.debug("抑制告警 [{}] 操作人 [{}]", alertId, operator);
        return ResponseEntity.ok(alertManagementAppService.suppressAlert(alertId, operator, note));
    }
    
    @GetMapping("/{alertId}")
    @Operation(summary = "获取告警详情", description = "获取指定告警的详情")
    public ResponseEntity<AlertDTO> getAlert(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId) {
        log.debug("获取告警详情 [{}]", alertId);
        return ResponseEntity.ok(alertManagementAppService.getAlert(alertId));
    }
    
    @GetMapping
    @Operation(summary = "获取所有告警", description = "获取系统中所有的告警")
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        log.debug("获取所有告警");
        return ResponseEntity.ok(alertManagementAppService.getAllAlerts());
    }
    
    @GetMapping("/active")
    @Operation(summary = "获取活跃告警", description = "获取系统中所有活跃的告警")
    public ResponseEntity<List<AlertDTO>> getActiveAlerts() {
        log.debug("获取活跃告警");
        return ResponseEntity.ok(alertManagementAppService.getActiveAlerts());
    }
    
    @GetMapping("/service/{serviceName}")
    @Operation(summary = "获取服务告警", description = "获取指定服务的告警")
    public ResponseEntity<List<AlertDTO>> getAlertsByService(
            @Parameter(description = "服务名称", required = true) @PathVariable String serviceName) {
        log.debug("获取服务 [{}] 的告警", serviceName);
        return ResponseEntity.ok(alertManagementAppService.getAlertsByService(serviceName));
    }
    
    @GetMapping("/instance/{serviceInstanceId}")
    @Operation(summary = "获取服务实例告警", description = "获取指定服务实例的告警")
    public ResponseEntity<List<AlertDTO>> getAlertsByServiceInstance(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId) {
        log.debug("获取服务实例 [{}] 的告警", serviceInstanceId);
        return ResponseEntity.ok(alertManagementAppService.getAlertsByServiceInstance(serviceInstanceId));
    }
    
    @GetMapping("/time-range")
    @Operation(summary = "获取时间范围内的告警", description = "获取指定时间范围内的告警")
    public ResponseEntity<List<AlertDTO>> getAlertsByTimeRange(
            @Parameter(description = "开始时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true) 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.debug("获取时间范围 [{}] 到 [{}] 的告警", startTime, endTime);
        return ResponseEntity.ok(alertManagementAppService.getAlertsByTimeRange(startTime, endTime));
    }
    
    @GetMapping("/paged")
    @Operation(summary = "分页获取告警", description = "分页获取告警列表")
    public ResponseEntity<List<AlertDTO>> getAlertsPaged(
            @Parameter(description = "页码", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", required = true) @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "firstOccurTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortOrder) {
        log.debug("分页获取告警 page=[{}], size=[{}], sortBy=[{}], sortOrder=[{}]", page, size, sortBy, sortOrder);
        return ResponseEntity.ok(alertManagementAppService.getAlertsPaged(page, size, sortBy, sortOrder));
    }
    
    @GetMapping("/{alertId}/history")
    @Operation(summary = "获取告警历史事件", description = "获取指定告警的历史事件")
    public ResponseEntity<List<AlertEventDTO>> getAlertHistory(
            @Parameter(description = "告警ID", required = true) @PathVariable String alertId) {
        log.debug("获取告警 [{}] 的历史事件", alertId);
        return ResponseEntity.ok(alertManagementAppService.getAlertHistory(alertId));
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取告警统计信息", description = "获取告警的统计信息")
    public ResponseEntity<Map<String, Object>> getAlertStatistics() {
        log.debug("获取告警统计信息");
        return ResponseEntity.ok(alertManagementAppService.getAlertStatistics());
    }
    
    @GetMapping("/trend")
    @Operation(summary = "获取告警趋势数据", description = "获取指定天数内的告警趋势数据")
    public ResponseEntity<Map<String, Integer>> getAlertTrend(
            @Parameter(description = "天数", required = true) @RequestParam(defaultValue = "7") int days) {
        log.debug("获取 [{}] 天内的告警趋势数据", days);
        return ResponseEntity.ok(alertManagementAppService.getAlertTrend(days));
    }
    
    @PostMapping("/batch")
    @Operation(summary = "批量处理告警", description = "批量处理多个告警")
    public ResponseEntity<Integer> batchProcessAlerts(
            @Parameter(description = "告警ID列表", required = true) @RequestBody List<String> alertIds,
            @Parameter(description = "操作类型", required = true) @RequestParam String action,
            @Parameter(description = "操作人", required = true) @RequestParam String operator,
            @Parameter(description = "备注信息") @RequestParam(required = false) String note) {
        log.debug("批量处理告警 action=[{}], operator=[{}]", action, operator);
        int count = alertManagementAppService.batchProcessAlerts(alertIds, action, operator, note);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/rules/test")
    @Operation(summary = "测试告警规则", description = "测试告警规则是否会触发")
    public ResponseEntity<Boolean> testAlertRule(
            @Parameter(description = "告警规则DTO", required = true) @RequestBody AlertRuleDTO alertRuleDTO,
            @Parameter(description = "测试值", required = true) @RequestParam double testValue) {
        log.debug("测试告警规则 [{}] 使用测试值 [{}]", alertRuleDTO.getName(), testValue);
        return ResponseEntity.ok(alertManagementAppService.testAlertRule(alertRuleDTO, testValue));
    }
    
    @PostMapping("/evaluation/{serviceInstanceId}")
    @Operation(summary = "手动触发告警规则评估", description = "手动触发指定服务实例的告警规则评估")
    public ResponseEntity<Integer> triggerAlertRuleEvaluation(
            @Parameter(description = "服务实例ID", required = true) @PathVariable String serviceInstanceId) {
        log.debug("手动触发服务实例 [{}] 的告警规则评估", serviceInstanceId);
        int count = alertManagementAppService.triggerAlertRuleEvaluation(serviceInstanceId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/severity")
    @Operation(summary = "按告警级别获取告警数量", description = "获取各告警级别的告警数量")
    public ResponseEntity<Map<String, Integer>> getAlertCountBySeverity() {
        log.debug("按告警级别获取告警数量");
        return ResponseEntity.ok(alertManagementAppService.getAlertCountBySeverity());
    }
    
    @GetMapping("/count/status")
    @Operation(summary = "按告警状态获取告警数量", description = "获取各告警状态的告警数量")
    public ResponseEntity<Map<String, Integer>> getAlertCountByStatus() {
        log.debug("按告警状态获取告警数量");
        return ResponseEntity.ok(alertManagementAppService.getAlertCountByStatus());
    }
    
    @GetMapping("/count/service")
    @Operation(summary = "按服务获取告警数量", description = "获取各服务的告警数量")
    public ResponseEntity<Map<String, Integer>> getAlertCountByService() {
        log.debug("按服务获取告警数量");
        return ResponseEntity.ok(alertManagementAppService.getAlertCountByService());
    }
    
    @GetMapping("/resolution-time")
    @Operation(summary = "获取告警处理时长统计", description = "获取告警的处理时长统计")
    public ResponseEntity<Map<String, Long>> getAlertResolutionTimeStatistics(
            @Parameter(description = "天数", required = true) @RequestParam(defaultValue = "7") int days) {
        log.debug("获取 [{}] 天内的告警处理时长统计", days);
        return ResponseEntity.ok(alertManagementAppService.getAlertResolutionTimeStatistics(days));
    }
    
    @GetMapping("/resolution-rate")
    @Operation(summary = "获取告警处理率", description = "获取指定天数内的告警处理率")
    public ResponseEntity<Double> getAlertResolutionRate(
            @Parameter(description = "天数", required = true) @RequestParam(defaultValue = "7") int days) {
        log.debug("获取 [{}] 天内的告警处理率", days);
        return ResponseEntity.ok(alertManagementAppService.getAlertResolutionRate(days));
    }
}
