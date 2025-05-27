package com.framework.excel.util.excel;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.config.DropdownConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.DropdownOption;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel模板生成器 - 可独立使用
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ExcelTemplateGenerator {
    
    private DropdownResolver dropdownResolver;
    
    public ExcelTemplateGenerator(DropdownResolver dropdownResolver) {
        this.dropdownResolver = dropdownResolver;
    }
    
    /**
     * 生成Excel模板
     */
    public byte[] generateTemplate(ExcelTemplateConfig config, BusinessConditions conditions) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 获取可见字段
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields(), conditions);
            
            // 创建表头行
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < visibleFields.size(); i++) {
                ExcelFieldConfig field = visibleFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(field.getColumnName());
                cell.setCellStyle(headerStyle);
                
                // 设置列宽
                sheet.setColumnWidth(i, field.getWidth() * 256);
                
                // 添加下拉数据验证
                addDataValidation(sheet, field, i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("生成Excel模板失败", e);
        }
    }
    
    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        // 设置对齐
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * 获取可见字段
     */
    private List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields, BusinessConditions conditions) {
        if (fields == null) {
            return List.of();
        }
        
        List<ExcelFieldConfig> visibleFields = fields.stream()
                .filter(f -> f.getVisible() != null && f.getVisible())
                .collect(Collectors.toList());
        
        // 如果指定了可见字段，则进一步过滤
        if (conditions != null && conditions.getVisibleFields() != null && !conditions.getVisibleFields().isEmpty()) {
            visibleFields = visibleFields.stream()
                    .filter(f -> conditions.getVisibleFields().contains(f.getFieldName()))
                    .collect(Collectors.toList());
        }
        
        // 按列索引排序
        return visibleFields.stream()
                .sorted((f1, f2) -> Integer.compare(f1.getColumnIndex(), f2.getColumnIndex()))
                .collect(Collectors.toList());
    }
    
    /**
     * 添加数据验证（下拉框）
     */
    private void addDataValidation(Sheet sheet, ExcelFieldConfig field, int columnIndex) {
        DropdownConfig dropdownConfig = field.getDropdownConfigObject();
        if (dropdownConfig == null) {
            return;
        }
        
        if ("STATIC".equals(dropdownConfig.getType())) {
            addStaticDropdown(sheet, dropdownConfig, columnIndex);
        }
        // 关联表下拉框在DropdownResolver中已经解析为静态选项
        else if ("RELATED_TABLE".equals(dropdownConfig.getType()) && dropdownConfig.getStaticOptions() != null) {
            addStaticDropdown(sheet, dropdownConfig, columnIndex);
        }
    }
    
    /**
     * 添加静态下拉框
     */
    private void addStaticDropdown(Sheet sheet, DropdownConfig dropdownConfig, int columnIndex) {
        List<DropdownOption> options = dropdownConfig.getStaticOptions();
        if (options == null || options.isEmpty()) {
            return;
        }
        
        String[] optionArray = options.stream()
                .map(opt -> opt.getLabel())
                .toArray(String[]::new);
        
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(optionArray);
        CellRangeAddressList range = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);
        DataValidation validation = helper.createValidation(constraint, range);
        validation.setShowErrorBox(true);
        validation.createErrorBox("输入错误", "请从下拉列表中选择有效值");
        sheet.addValidationData(validation);
    }
} 