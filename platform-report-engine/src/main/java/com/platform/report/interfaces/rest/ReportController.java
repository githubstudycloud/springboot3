package com.platform.report.interfaces.rest;

import com.platform.report.application.dto.report.*;
import com.platform.report.application.service.ReportApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报表REST控制器
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "报表管理", description = "提供报表相关的生成和管理API")
public class ReportController {
    
    private final ReportApplicationService reportApplicationService;
    
    @PostMapping
    @Operation(summary = "创建报表", description = "创建新的报表实例")
    public ResponseEntity<String> createReport(@RequestBody CreateReportCommand command) {
        String reportId = reportApplicationService.createReport(command);
        return ResponseEntity.ok(reportId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取报表详情", description = "根据ID获取报表详细信息")
    public ResponseEntity<ReportDetailDTO> getReportDetail(@PathVariable String id) {
        ReportDetailDTO report = reportApplicationService.getReportDetail(id);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping
    @Operation(summary = "查询报表列表", description = "根据条件查询报表列表")
    public ResponseEntity<List<ReportDTO>> getReportList(
            @ModelAttribute ReportQueryDTO query) {
        List<ReportDTO> reports = reportApplicationService.getReportList(query);
        return ResponseEntity.ok(reports);
    }
    
    @PostMapping("/{id}/generate")
    @Operation(summary = "生成报表", description = "生成指定报表的内容")
    public ResponseEntity<Boolean> generateReport(@PathVariable String id) {
        boolean success = reportApplicationService.generateReport(id);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/generate-async")
    @Operation(summary = "异步生成报表", description = "异步生成指定报表的内容")
    public ResponseEntity<String> generateReportAsync(@PathVariable String id) {
        String taskId = reportApplicationService.generateReportAsync(id);
        return ResponseEntity.ok(taskId);
    }
    
    @GetMapping("/{id}/generation-status")
    @Operation(summary = "获取生成状态", description = "获取报表生成的状态")
    public ResponseEntity<ReportGenerationStatusDTO> getGenerationStatus(@PathVariable String id) {
        ReportGenerationStatusDTO status = reportApplicationService.getGenerationStatus(id);
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/{id}/regenerate")
    @Operation(summary = "重新生成报表", description = "使用新参数重新生成报表")
    public ResponseEntity<Boolean> regenerateReport(
            @PathVariable String id,
            @RequestBody RegenerateReportCommand command) {
        command.setReportId(id);
        boolean success = reportApplicationService.regenerateReport(command);
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/{id}/download")
    @Operation(summary = "下载报表", description = "下载指定报表的内容")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable String id,
            @RequestParam(required = false) String versionId,
            @RequestParam String downloadBy) {
        ReportContentDTO content = reportApplicationService.downloadReport(id, versionId, downloadBy);
        ByteArrayResource resource = new ByteArrayResource(content.getContent());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + content.getFileName())
                .contentType(MediaType.parseMediaType(content.getContentType()))
                .contentLength(content.getSize())
                .body(resource);
    }
    
    @GetMapping("/{id}/versions")
    @Operation(summary = "获取报表版本列表", description = "获取指定报表的版本列表")
    public ResponseEntity<List<ReportVersionDTO>> getReportVersions(@PathVariable String id) {
        List<ReportVersionDTO> versions = reportApplicationService.getReportVersions(id);
        return ResponseEntity.ok(versions);
    }
    
    @GetMapping("/{id}/versions/compare")
    @Operation(summary = "比较报表版本", description = "比较指定报表的两个版本")
    public ResponseEntity<ReportVersionComparisonDTO> compareReportVersions(
            @PathVariable String id,
            @RequestParam String versionId1,
            @RequestParam String versionId2) {
        ReportVersionComparisonDTO comparison = 
                reportApplicationService.compareReportVersions(id, versionId1, versionId2);
        return ResponseEntity.ok(comparison);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除报表", description = "删除指定报表")
    public ResponseEntity<Boolean> deleteReport(
            @PathVariable String id,
            @RequestParam String operatedBy) {
        boolean success = reportApplicationService.deleteReport(id, operatedBy);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制报表", description = "复制现有报表创建新报表")
    public ResponseEntity<String> copyReport(
            @PathVariable String id,
            @RequestParam String newName,
            @RequestParam String createdBy) {
        String reportId = reportApplicationService.copyReport(id, newName, createdBy);
        return ResponseEntity.ok(reportId);
    }
}
