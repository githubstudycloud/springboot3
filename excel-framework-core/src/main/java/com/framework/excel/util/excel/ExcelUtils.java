package com.framework.excel.util.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framework.excel.entity.config.DropdownConfig;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.dto.DropdownOption;
import com.framework.excel.exception.ExcelProcessException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel工具类 - 可独立使用
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ExcelUtils {
    
    /**
     * 生成Excel模板
     */
    public static byte[] generateTemplate(ExcelTemplateConfig config) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 创建表头行
            Row headerRow = sheet.createRow(0);
            
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
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
            throw new ExcelProcessException("生成Excel模板失败", e);
        }
    }
    
    /**
     * 生成Excel模板（指定可见字段）
     */
    public static byte[] generateTemplate(ExcelTemplateConfig config, List<String> visibleFieldNames) {
        if (visibleFieldNames != null && !visibleFieldNames.isEmpty()) {
            // 过滤出指定的可见字段
            List<ExcelFieldConfig> filteredFields = config.getFields().stream()
                    .filter(f -> visibleFieldNames.contains(f.getFieldName()))
                    .collect(Collectors.toList());
            
            // 创建新的配置副本
            ExcelTemplateConfig newConfig = new ExcelTemplateConfig();
            newConfig.setSheetName(config.getSheetName());
            newConfig.setFields(filteredFields);
            
            return generateTemplate(newConfig);
        }
        return generateTemplate(config);
    }
    
    /**
     * 解析Excel文件
     */
    public static List<Map<String, Object>> parseExcel(MultipartFile file, ExcelTemplateConfig config) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields());
            
            // 跳过标题行，从第二行开始读取数据
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // 跳过标题行
            }
            
            int rowIndex = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> rowData = parseRow(row, visibleFields, rowIndex);
                if (!isEmptyRow(rowData)) {
                    dataList.add(rowData);
                }
                rowIndex++;
            }
            
        } catch (Exception e) {
            throw new ExcelProcessException("解析Excel文件失败", e);
        }
        
        return dataList;
    }
    
    /**
     * 导出数据到Excel
     */
    public static byte[] exportData(List<Map<String, Object>> dataList, ExcelTemplateConfig config) {
        return exportData(dataList, config, null);
    }
    
    /**
     * 导出数据到Excel（指定导出字段）
     */
    public static byte[] exportData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, List<String> exportFields) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet(config.getSheetName());
            
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            List<ExcelFieldConfig> visibleFields;
            if (exportFields != null && !exportFields.isEmpty()) {
                // 根据导出字段过滤
                visibleFields = config.getFields().stream()
                        .filter(f -> f.getVisible() && exportFields.contains(f.getFieldName()))
                        .sorted(Comparator.comparing(ExcelFieldConfig::getColumnIndex))
                        .collect(Collectors.toList());
            } else {
                visibleFields = getVisibleFields(config.getFields());
            }
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < visibleFields.size(); i++) {
                ExcelFieldConfig field = visibleFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(field.getColumnName());
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, field.getWidth() * 256);
            }
            
            // 填充数据
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
            throw new ExcelProcessException("导出Excel文件失败", e);
        }
    }
    
    // 私有辅助方法
    private static List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields) {
        if (fields == null) {
            return new ArrayList<>();
        }
        
        return fields.stream()
                .filter(f -> f.getVisible() != null && f.getVisible())
                .sorted(Comparator.comparing(ExcelFieldConfig::getColumnIndex))
                .collect(Collectors.toList());
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }
    
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    private static void addDataValidation(Sheet sheet, ExcelFieldConfig field, int columnIndex) {
        if (field.getDropdownConfig() != null) {
            DropdownConfig dropdownConfig = field.getDropdownConfigObject();
            if (dropdownConfig != null && "STATIC".equals(dropdownConfig.getType())) {
                List<DropdownOption> options = dropdownConfig.getStaticOptions();
                if (options != null && !options.isEmpty()) {
                    String[] optionArray = options.stream()
                            .map(DropdownOption::getLabel)
                            .toArray(String[]::new);
                    
                    DataValidationHelper helper = sheet.getDataValidationHelper();
                    DataValidationConstraint constraint = helper.createExplicitListConstraint(optionArray);
                    CellRangeAddressList range = new CellRangeAddressList(1, 1000, columnIndex, columnIndex);
                    DataValidation validation = helper.createValidation(constraint, range);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                }
            }
        }
    }
    
    private static Map<String, Object> parseRow(Row row, List<ExcelFieldConfig> visibleFields, int rowIndex) {
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("_rowIndex", rowIndex);
        
        for (int i = 0; i < visibleFields.size(); i++) {
            ExcelFieldConfig field = visibleFields.get(i);
            Cell cell = row.getCell(i);
            Object value = getCellValue(cell, field);
            rowData.put(field.getFieldName(), value);
        }
        
        return rowData;
    }
    
    private static Object getCellValue(Cell cell, ExcelFieldConfig field) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    
    private static void setCellValue(Cell cell, Object value, ExcelFieldConfig field) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            
            // 设置日期格式
            if (field.getDateFormat() != null && !field.getDateFormat().isEmpty()) {
                Workbook workbook = cell.getSheet().getWorkbook();
                CellStyle dateStyle = workbook.createCellStyle();
                CreationHelper createHelper = workbook.getCreationHelper();
                dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(field.getDateFormat()));
                cell.setCellStyle(dateStyle);
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    private static boolean isEmptyRow(Map<String, Object> rowData) {
        if (rowData == null || rowData.isEmpty()) {
            return true;
        }
        
        // 检查除了_rowIndex之外的所有值是否都为空
        for (Map.Entry<String, Object> entry : rowData.entrySet()) {
            if (!"_rowIndex".equals(entry.getKey()) && entry.getValue() != null 
                    && !entry.getValue().toString().trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
}
