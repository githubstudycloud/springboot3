package com.platform.report.interfaces.rest;

import com.platform.report.application.dto.template.*;
import com.platform.report.application.service.TemplateApplicationService;
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
import java.util.Map;

/**
 * 报表模板REST控制器
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "报表模板管理", description = "提供报表模板相关的CRUD API")
public class TemplateController {
    
    private final TemplateApplicationService templateApplicationService;
    
    @PostMapping
    @Operation(summary = "创建报表模板", description = "创建新的报表模板")
    public ResponseEntity<String> createTemplate(@RequestBody CreateTemplateCommand command) {
        String templateId = templateApplicationService.createTemplate(command);
        return ResponseEntity.ok(templateId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取模板详情", description = "根据ID获取模板详细信息")
    public ResponseEntity<TemplateDetailDTO> getTemplateDetail(@PathVariable String id) {
        TemplateDetailDTO template = templateApplicationService.getTemplateDetail(id);
        return ResponseEntity.ok(template);
    }
    
    @GetMapping
    @Operation(summary = "查询模板列表", description = "根据条件查询模板列表")
    public ResponseEntity<List<TemplateDTO>> getTemplateList(
            @ModelAttribute TemplateQueryDTO query) {
        List<TemplateDTO> templates = templateApplicationService.getTemplateList(query);
        return ResponseEntity.ok(templates);
    }
    
    @PutMapping("/{id}/basic-info")
    @Operation(summary = "更新模板基本信息", description = "更新模板的名称和描述")
    public ResponseEntity<Boolean> updateTemplateBasicInfo(
            @PathVariable String id,
            @RequestBody UpdateTemplateBasicInfoCommand command) {
        command.setTemplateId(id);
        boolean success = templateApplicationService.updateTemplateBasicInfo(command);
        return ResponseEntity.ok(success);
    }
    
    @PutMapping("/{id}/layout")
    @Operation(summary = "更新模板布局", description = "更新模板的布局配置")
    public ResponseEntity<Boolean> updateTemplateLayout(
            @PathVariable String id,
            @RequestBody UpdateTemplateLayoutCommand command) {
        command.setTemplateId(id);
        boolean success = templateApplicationService.updateTemplateLayout(command);
        return ResponseEntity.ok(success);
    }
    
    @PutMapping("/{id}/style")
    @Operation(summary = "更新模板样式", description = "更新模板的样式配置")
    public ResponseEntity<Boolean> updateTemplateStyle(
            @PathVariable String id,
            @RequestBody UpdateTemplateStyleCommand command) {
        command.setTemplateId(id);
        boolean success = templateApplicationService.updateTemplateStyle(command);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/components")
    @Operation(summary = "添加模板组件", description = "向模板添加新的组件")
    public ResponseEntity<String> addTemplateComponent(
            @PathVariable String id,
            @RequestBody AddTemplateComponentCommand command) {
        command.setTemplateId(id);
        String componentId = templateApplicationService.addTemplateComponent(command);
        return ResponseEntity.ok(componentId);
    }
    
    @PutMapping("/{id}/components/{componentId}")
    @Operation(summary = "更新模板组件", description = "更新模板中的组件")
    public ResponseEntity<Boolean> updateTemplateComponent(
            @PathVariable String id,
            @PathVariable String componentId,
            @RequestBody UpdateTemplateComponentCommand command) {
        command.setTemplateId(id);
        command.setComponentId(componentId);
        boolean success = templateApplicationService.updateTemplateComponent(command);
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/{id}/components/{componentId}")
    @Operation(summary = "删除模板组件", description = "删除模板中的组件")
    public ResponseEntity<Boolean> deleteTemplateComponent(
            @PathVariable String id,
            @PathVariable String componentId) {
        boolean success = templateApplicationService.deleteTemplateComponent(id, componentId);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布模板", description = "将模板状态修改为已发布")
    public ResponseEntity<Boolean> publishTemplate(@PathVariable String id) {
        boolean success = templateApplicationService.publishTemplate(id);
        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/{id}/archive")
    @Operation(summary = "归档模板", description = "将模板状态修改为已归档")
    public ResponseEntity<Boolean> archiveTemplate(@PathVariable String id) {
        boolean success = templateApplicationService.archiveTemplate(id);
        return ResponseEntity.ok(success);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板", description = "删除指定模板")
    public ResponseEntity<Boolean> deleteTemplate(@PathVariable String id) {
        boolean success = templateApplicationService.deleteTemplate(id);
        return ResponseEntity.ok(success);
    }
    
    @GetMapping("/{id}/export")
    @Operation(summary = "导出模板", description = "导出模板定义")
    public ResponseEntity<Resource> exportTemplate(@PathVariable String id) {
        byte[] data = templateApplicationService.exportTemplate(id);
        ByteArrayResource resource = new ByteArrayResource(data);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template_" + id + ".json")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(data.length)
                .body(resource);
    }
    
    @PostMapping("/import")
    @Operation(summary = "导入模板", description = "导入模板定义")
    public ResponseEntity<String> importTemplate(
            @RequestParam("file") byte[] data,
            @RequestParam("createdBy") String createdBy) {
        String templateId = templateApplicationService.importTemplate(data, createdBy);
        return ResponseEntity.ok(templateId);
    }
    
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制模板", description = "复制现有模板创建新模板")
    public ResponseEntity<String> copyTemplate(
            @PathVariable String id,
            @RequestParam("newName") String newName,
            @RequestParam("createdBy") String createdBy) {
        String templateId = templateApplicationService.copyTemplate(id, newName, createdBy);
        return ResponseEntity.ok(templateId);
    }
    
    @PostMapping("/{id}/preview")
    @Operation(summary = "预览模板", description = "根据模板和参数生成预览内容")
    public ResponseEntity<Resource> previewTemplate(
            @PathVariable String id,
            @RequestBody Map<String, Object> parameters) {
        byte[] data = templateApplicationService.previewTemplate(id, parameters);
        ByteArrayResource resource = new ByteArrayResource(data);
        
        // 根据模板类型确定内容类型，这里假设是PDF
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=preview_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(data.length)
                .body(resource);
    }
    
    @GetMapping("/{id}/parameters")
    @Operation(summary = "获取模板参数", description = "获取模板的参数列表")
    public ResponseEntity<List<TemplateParameterDTO>> getTemplateParameters(@PathVariable String id) {
        List<TemplateParameterDTO> parameters = templateApplicationService.getTemplateParameters(id);
        return ResponseEntity.ok(parameters);
    }
}
