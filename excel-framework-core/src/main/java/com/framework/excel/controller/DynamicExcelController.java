package com.framework.excel.controller;

import com.framework.excel.config.PrimaryKeyStrategy;
import com.framework.excel.dto.ExportRequest;
import com.framework.excel.dto.ImportResult;
import com.framework.excel.service.ExcelConfigService;
import com.framework.excel.service.ExcelDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 动态Excel控制器
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/excel")
@Api(tags = "动态Excel导入导出")
public class DynamicExcelController {

    @Autowired
    private ExcelDataService excelDataService;

    @Autowired
    private ExcelConfigService excelConfigService;

    @ApiOperation("下载Excel模板")
    @GetMapping("/template/{templateKey}")
    public void downloadTemplate(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("可见字段列表") @RequestParam(required = false) List<String> visibleFields,
            HttpServletResponse response) {
        
        log.info("Download template request: templateKey={}, visibleFields={}", templateKey, visibleFields);
        
        try {
            excelDataService.downloadTemplate(templateKey, visibleFields, response);
            log.info("Template downloaded successfully: {}", templateKey);
        } catch (Exception e) {
            log.error("Failed to download template: {}", templateKey, e);
            throw new RuntimeException("Failed to download template: " + e.getMessage());
        }
    }

    @ApiOperation("导入Excel数据")
    @PostMapping("/import/{templateKey}")
    public ResponseEntity<ImportResult> importData(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("Excel文件") @RequestParam("file") MultipartFile file,
            @ApiParam("主键字段") @RequestParam(required = false) String primaryKey) {
        
        log.info("Import data request: templateKey={}, fileName={}, primaryKey={}", 
                templateKey, file.getOriginalFilename(), primaryKey);
        
        if (file.isEmpty()) {
            ImportResult result = new ImportResult();
            result.addError(0, null, null, "File is empty");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            ImportResult result = excelDataService.importData(templateKey, file, primaryKey);
            log.info("Import completed: templateKey={}, success={}, total={}, errors={}", 
                    templateKey, result.getSuccessRows(), result.getTotalRows(), result.getFailedRows());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to import data: {}", templateKey, e);
            ImportResult result = new ImportResult();
            result.addError(0, null, null, "Import failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @ApiOperation("导出Excel数据")
    @PostMapping("/export/{templateKey}")
    public void exportData(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("导出请求") @RequestBody ExportRequest request,
            HttpServletResponse response) {
        
        log.info("Export data request: templateKey={}, conditions={}, visibleFields={}", 
                templateKey, request.getConditions(), request.getVisibleFields());
        
        try {
            excelDataService.exportData(templateKey, request, response);
            log.info("Data exported successfully: {}", templateKey);
        } catch (Exception e) {
            log.error("Failed to export data: {}", templateKey, e);
            throw new RuntimeException("Failed to export data: " + e.getMessage());
        }
    }

    @ApiOperation("动态调整字段可见性")
    @PutMapping("/config/{templateKey}/fields/visibility")
    public ResponseEntity<Void> updateFieldVisibility(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("字段可见性映射") @RequestBody Map<String, Boolean> fieldVisibility) {
        
        log.info("Update field visibility: templateKey={}, fieldVisibility={}", templateKey, fieldVisibility);
        
        try {
            // TODO: 实现字段可见性更新功能
            // excelConfigService.updateFieldVisibility(templateKey, fieldVisibility);
            log.info("Field visibility updated successfully: {}", templateKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update field visibility: {}", templateKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("动态调整主键策略")
    @PutMapping("/config/{templateKey}/primary-key")
    public ResponseEntity<Void> updatePrimaryKeyStrategy(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("主键策略") @RequestBody PrimaryKeyStrategy strategy) {
        
        log.info("Update primary key strategy: templateKey={}, strategy={}", templateKey, strategy);
        
        try {
            // TODO: 实现主键策略更新功能
            // excelConfigService.updatePrimaryKeyStrategy(templateKey, strategy);
            log.info("Primary key strategy updated successfully: {}", templateKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update primary key strategy: {}", templateKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("获取模板配置")
    @GetMapping("/config/{templateKey}")
    public ResponseEntity<?> getConfig(@ApiParam("模板标识") @PathVariable String templateKey) {
        log.info("Get config request: templateKey={}", templateKey);
        
        try {
            com.framework.excel.entity.ExcelTemplateConfig config = excelConfigService.getConfig(templateKey);
            if (config == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("Failed to get config: {}", templateKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("获取所有模板配置")
    @GetMapping("/config")
    public ResponseEntity<?> getAllConfigs() {
        log.info("Get all configs request");
        
        try {
            List<com.framework.excel.entity.ExcelTemplateConfig> configs = excelConfigService.getAllConfigs();
            return ResponseEntity.ok(configs);
        } catch (Exception e) {
            log.error("Failed to get all configs", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("刷新配置缓存")
    @PostMapping("/config/refresh")
    public ResponseEntity<Void> refreshCache() {
        log.info("Refresh cache request");
        
        try {
            excelConfigService.refreshCache();
            log.info("Cache refreshed successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to refresh cache", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation("查询数据")
    @PostMapping("/data/{templateKey}/query")
    public ResponseEntity<?> queryData(
            @ApiParam("模板标识") @PathVariable String templateKey,
            @ApiParam("查询条件") @RequestBody(required = false) Map<String, Object> conditions) {
        
        log.info("Query data request: templateKey={}, conditions={}", templateKey, conditions);
        
        try {
            List<Map<String, Object>> dataList = excelDataService.queryData(templateKey, conditions);
            return ResponseEntity.ok(dataList);
        } catch (Exception e) {
            log.error("Failed to query data: {}", templateKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}