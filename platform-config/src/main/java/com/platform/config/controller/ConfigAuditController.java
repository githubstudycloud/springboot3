package com.platform.config.controller;

import com.platform.config.entity.ConfigAudit;
import com.platform.config.service.ConfigAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 配置审计日志API
 * 提供配置变更审计和日志查询的完整REST接口
 *
 * @author Platform Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/config/audit")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "配置审计日志", description = "配置审计管理API - 审计查询、报告生成、统计分析等功能")
public class ConfigAuditController {

    private final ConfigAuditService auditService;

    @Operation(summary = "查询审计日志", description = "根据应用和环境分页查询审计日志")
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getAuditLogs(
            @Parameter(description = "应用名称", required = true)
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true)
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "分页参数")
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConfigAudit> auditPage = auditService.getAuditsByAppAndProfile(application, profile, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "审计日志查询成功",
                "data", auditPage.map(this::buildAuditResponse),
                "pagination", Map.of(
                        "totalElements", auditPage.getTotalElements(),
                        "totalPages", auditPage.getTotalPages(),
                        "currentPage", auditPage.getNumber(),
                        "size", auditPage.getSize()
                )
        ));
    }

    @Operation(summary = "根据操作类型查询", description = "根据操作类型分页查询审计日志")
    @GetMapping("/logs/by-operation")
    public ResponseEntity<Map<String, Object>> getAuditsByOperation(
            @Parameter(description = "操作类型", required = true)
            @RequestParam @NotBlank String operation,
            
            @Parameter(description = "分页参数")
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConfigAudit> auditPage = auditService.getAuditsByOperation(operation, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "操作类型审计日志查询成功",
                "operation", operation,
                "data", auditPage.map(this::buildAuditResponse),
                "pagination", Map.of(
                        "totalElements", auditPage.getTotalElements(),
                        "totalPages", auditPage.getTotalPages(),
                        "currentPage", auditPage.getNumber(),
                        "size", auditPage.getSize()
                )
        ));
    }

    @Operation(summary = "根据操作人员查询", description = "根据操作人员分页查询审计日志")
    @GetMapping("/logs/by-operator")
    public ResponseEntity<Map<String, Object>> getAuditsByOperator(
            @Parameter(description = "操作人员", required = true)
            @RequestParam @NotBlank String operator,
            
            @Parameter(description = "分页参数")
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConfigAudit> auditPage = auditService.getAuditsByOperator(operator, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "操作人员审计日志查询成功",
                "operator", operator,
                "data", auditPage.map(this::buildAuditResponse),
                "pagination", Map.of(
                        "totalElements", auditPage.getTotalElements(),
                        "totalPages", auditPage.getTotalPages(),
                        "currentPage", auditPage.getNumber(),
                        "size", auditPage.getSize()
                )
        ));
    }

    @Operation(summary = "时间范围查询", description = "根据时间范围查询审计日志")
    @GetMapping("/logs/by-time-range")
    public ResponseEntity<Map<String, Object>> getAuditsByTimeRange(
            @Parameter(description = "开始时间", required = true)
            @RequestParam @NotNull 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            
            @Parameter(description = "结束时间", required = true)
            @RequestParam @NotNull 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        List<ConfigAudit> audits = auditService.getAuditsByTimeRange(startTime, endTime);

        return ResponseEntity.ok(Map.of(
                "message", "时间范围审计日志查询成功",
                "startTime", startTime,
                "endTime", endTime,
                "data", audits.stream().map(this::buildAuditResponse).toList(),
                "count", audits.size()
        ));
    }

    @Operation(summary = "查询失败操作", description = "分页查询操作失败的审计日志")
    @GetMapping("/logs/failed")
    public ResponseEntity<Map<String, Object>> getFailedOperations(
            @Parameter(description = "分页参数")
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConfigAudit> failedPage = auditService.getFailedOperations(pageable);

        return ResponseEntity.ok(Map.of(
                "message", "失败操作审计日志查询成功",
                "data", failedPage.map(this::buildAuditResponse),
                "pagination", Map.of(
                        "totalElements", failedPage.getTotalElements(),
                        "totalPages", failedPage.getTotalPages(),
                        "currentPage", failedPage.getNumber(),
                        "size", failedPage.getSize()
                )
        ));
    }

    @Operation(summary = "根据版本查询", description = "根据版本ID查询相关审计记录")
    @GetMapping("/logs/by-version/{versionId}")
    public ResponseEntity<Map<String, Object>> getAuditsByVersionId(
            @Parameter(description = "版本ID", required = true)
            @PathVariable @NotNull @Positive Long versionId) {

        List<ConfigAudit> audits = auditService.getAuditsByVersionId(versionId);

        return ResponseEntity.ok(Map.of(
                "message", "版本相关审计日志查询成功",
                "versionId", versionId,
                "data", audits.stream().map(this::buildAuditResponse).toList(),
                "count", audits.size()
        ));
    }

    @Operation(summary = "根据业务标识查询", description = "根据业务标识查询审计记录")
    @GetMapping("/logs/by-business/{businessId}")
    public ResponseEntity<Map<String, Object>> getAuditsByBusinessId(
            @Parameter(description = "业务标识", required = true)
            @PathVariable @NotBlank String businessId) {

        List<ConfigAudit> audits = auditService.getAuditsByBusinessId(businessId);

        return ResponseEntity.ok(Map.of(
                "message", "业务标识审计日志查询成功",
                "businessId", businessId,
                "data", audits.stream().map(this::buildAuditResponse).toList(),
                "count", audits.size()
        ));
    }

    @Operation(summary = "生成审计报告", description = "生成指定时间段的审计统计报告")
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> generateAuditReport(
            @Parameter(description = "开始日期")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate) {

        // 默认查询最近30天的数据
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }

        Map<String, Object> report = auditService.generateAuditReport(startDate);

        return ResponseEntity.ok(Map.of(
                "message", "审计报告生成成功",
                "reportTime", LocalDateTime.now(),
                "startDate", startDate,
                "report", report
        ));
    }

    @Operation(summary = "手动记录审计", description = "手动创建审计记录（管理员功能）")
    @PostMapping("/manual")
    public ResponseEntity<Map<String, Object>> recordManualAudit(
            @Parameter(description = "应用名称", required = true)
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true)
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "操作类型", required = true)
            @RequestParam @NotBlank String operation,
            
            @Parameter(description = "配置键")
            @RequestParam(required = false) String configKey,
            
            @Parameter(description = "旧值")
            @RequestParam(required = false) String oldValue,
            
            @Parameter(description = "新值")
            @RequestParam(required = false) String newValue,
            
            @Parameter(description = "操作人员", required = true)
            @RequestParam @NotBlank String operator,
            
            @Parameter(description = "版本ID")
            @RequestParam(required = false) Long versionId,
            
            @Parameter(description = "业务标识")
            @RequestParam(required = false) String businessId) {

        ConfigAudit audit = auditService.recordAudit(
                application, profile, operation, configKey, 
                oldValue, newValue, operator, versionId, businessId);

        return ResponseEntity.ok(Map.of(
                "message", "手动审计记录创建成功",
                "audit", buildAuditResponse(audit)
        ));
    }

    @Operation(summary = "记录失败操作", description = "记录操作失败的审计日志")
    @PostMapping("/failure")
    public ResponseEntity<Map<String, Object>> recordFailureAudit(
            @Parameter(description = "应用名称", required = true)
            @RequestParam @NotBlank String application,
            
            @Parameter(description = "环境配置", required = true)
            @RequestParam @NotBlank String profile,
            
            @Parameter(description = "操作类型", required = true)
            @RequestParam @NotBlank String operation,
            
            @Parameter(description = "操作人员", required = true)
            @RequestParam @NotBlank String operator,
            
            @Parameter(description = "错误信息", required = true)
            @RequestParam @NotBlank String errorMessage) {

        ConfigAudit audit = auditService.recordFailureAudit(
                application, profile, operation, operator, errorMessage);

        return ResponseEntity.ok(Map.of(
                "message", "失败操作审计记录创建成功",
                "audit", buildAuditResponse(audit)
        ));
    }

    @Operation(summary = "审计统计信息", description = "获取审计日志的统计信息")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAuditStatistics(
            @Parameter(description = "统计天数")
            @RequestParam(defaultValue = "7") int days) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        Map<String, Object> report = auditService.generateAuditReport(startDate);

        return ResponseEntity.ok(Map.of(
                "message", "审计统计查询成功",
                "statisticsPeriod", days + "天",
                "startDate", startDate,
                "statistics", report
        ));
    }

    /**
     * 构建审计响应对象
     */
    private Map<String, Object> buildAuditResponse(ConfigAudit audit) {
        return Map.of(
                "id", audit.getId(),
                "application", audit.getApplication(),
                "profile", audit.getProfile(),
                "operation", audit.getOperation(),
                "configKey", audit.getConfigKey() != null ? audit.getConfigKey() : "",
                "oldValue", audit.getOldValue() != null ? audit.getOldValue() : "",
                "newValue", audit.getNewValue() != null ? audit.getNewValue() : "",
                "operator", audit.getOperator(),
                "timestamp", audit.getTimestamp(),
                "source", audit.getSource() != null ? audit.getSource() : "",
                "clientIp", audit.getClientIp() != null ? audit.getClientIp() : "",
                "userAgent", audit.getUserAgent() != null ? audit.getUserAgent() : "",
                "result", audit.getResult(),
                "errorMessage", audit.getErrorMessage() != null ? audit.getErrorMessage() : "",
                "versionId", audit.getVersionId(),
                "duration", audit.getDuration() != null ? audit.getDuration() : 0,
                "businessId", audit.getBusinessId() != null ? audit.getBusinessId() : "",
                "remarks", audit.getRemarks() != null ? audit.getRemarks() : ""
        );
    }
}
