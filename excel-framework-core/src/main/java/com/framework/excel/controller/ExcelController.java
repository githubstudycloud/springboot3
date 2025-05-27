package com.framework.excel.controller;

import com.alibaba.fastjson.JSON;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import com.framework.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一Excel控制器
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 下载Excel模板(支持业务条件)
     */
    @PostMapping("/template/{templateKey}")
    public void downloadTemplate(
        @PathVariable String templateKey,
        @RequestBody(required = false) BusinessConditions conditions,
        HttpServletResponse response
    ) {
        try {
            byte[] templateData = excelService.generateTemplate(templateKey, conditions);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + templateKey + "_template.xlsx");
            response.getOutputStream().write(templateData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("模板下载失败", e);
        }
    }
    
    /**
     * 导入Excel数据(支持业务条件)
     */
    @PostMapping("/import/{templateKey}")
    public ResponseEntity<ImportResult> importData(
        @PathVariable String templateKey,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "conditions", required = false) String conditionsJson
    ) {
        try {
            BusinessConditions conditions = parseConditions(conditionsJson);
            ImportResult result = excelService.importData(templateKey, file, conditions);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            ImportResult errorResult = new ImportResult();
            errorResult.setErrorCount(1);
            errorResult.getErrors().add(new ImportResult.ErrorInfo(0, e.getMessage()));
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
    
    /**
     * 导出Excel数据
     */
    @PostMapping("/export/{templateKey}")
    public void exportData(
        @PathVariable String templateKey,
        @RequestBody BusinessConditions conditions,
        HttpServletResponse response
    ) {
        try {
            byte[] excelData = excelService.exportData(templateKey, conditions);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + templateKey + "_export_" + System.currentTimeMillis() + ".xlsx");
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }
    
    /**
     * 解析业务条件JSON
     */
    private BusinessConditions parseConditions(String conditionsJson) {
        if (conditionsJson == null || conditionsJson.trim().isEmpty()) {
            return new BusinessConditions();
        }
        try {
            return JSON.parseObject(conditionsJson, BusinessConditions.class);
        } catch (Exception e) {
            throw new RuntimeException("业务条件JSON格式错误", e);
        }
    }
} 