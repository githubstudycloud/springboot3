package com.framework.excel.util.excel;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel数据写入器 - 可独立使用
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ExcelDataWriter {
    
    /**
     * 写入数据到Excel
     */
    public byte[] writeExcel(List<Map<String, Object>> dataList, ExcelTemplateConfig config, BusinessConditions conditions) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
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
            }
            
            // 写入数据行
            for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
                Row dataRow = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = dataList.get(rowIndex);
                
                for (int colIndex = 0; colIndex < visibleFields.size(); colIndex++) {
                    ExcelFieldConfig field = visibleFields.get(colIndex);
                    Cell cell = dataRow.createCell(colIndex);
                    
                    Object value = rowData.get(field.getFieldName());
                    setCellValue(cell, value, field);
                    cell.setCellStyle(dataStyle);
                }
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("生成Excel文件失败", e);
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
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置对齐
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
     * 设置单元格值
     */
    private void setCellValue(Cell cell, Object value, ExcelFieldConfig field) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        
        String dataType = field.getDataType();
        
        switch (dataType.toUpperCase()) {
            case "STRING":
                cell.setCellValue(value.toString());
                break;
            case "INTEGER":
            case "LONG":
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    try {
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } catch (NumberFormatException e) {
                        cell.setCellValue(value.toString());
                    }
                }
                break;
            case "DOUBLE":
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    try {
                        cell.setCellValue(Double.parseDouble(value.toString()));
                    } catch (NumberFormatException e) {
                        cell.setCellValue(value.toString());
                    }
                }
                break;
            case "DATE":
            case "DATETIME":
                if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                    
                    // 设置日期格式
                    Workbook workbook = cell.getSheet().getWorkbook();
                    CellStyle dateStyle = workbook.createCellStyle();
                    CreationHelper createHelper = workbook.getCreationHelper();
                    
                    String dateFormat = field.getDateFormat();
                    if (dateFormat == null || dateFormat.isEmpty()) {
                        dateFormat = "DATE".equals(dataType.toUpperCase()) ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
                    }
                    
                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));
                    cell.setCellStyle(dateStyle);
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
            case "BOOLEAN":
                if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else {
                    String str = value.toString().toLowerCase();
                    cell.setCellValue("true".equals(str) || "yes".equals(str) || "1".equals(str) || "是".equals(str));
                }
                break;
            case "JSON":
                cell.setCellValue(value.toString());
                break;
            default:
                cell.setCellValue(value.toString());
                break;
        }
    }
} 