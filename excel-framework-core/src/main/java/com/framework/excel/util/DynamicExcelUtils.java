package com.framework.excel.util;

import com.framework.excel.config.DropdownConfig;
import com.framework.excel.config.ExcelFieldConfig;
import com.framework.excel.config.ExcelTemplateConfig;
import com.framework.excel.dto.DropdownOption;
import com.framework.excel.dto.ImportError;
import com.framework.excel.dto.ImportResult;
import com.framework.excel.provider.DropdownProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态Excel工具类
 *
 * @author framework
 * @since 1.0.0
 */
@Slf4j
public class DynamicExcelUtils {

    private static final int MAX_DROPDOWN_OPTIONS = 100;
    private static final String DROPDOWN_SHEET_NAME = "DropdownData";

    /**
     * 导出Excel模板
     *
     * @param config   模板配置
     * @param response HTTP响应
     * @param providers 下拉数据提供者映射
     */
    public static void exportTemplate(ExcelTemplateConfig config, HttpServletResponse response,
                                    Map<String, DropdownProvider> providers) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // 创建主工作表
            Sheet mainSheet = workbook.createSheet(config.getSheetName());
            
            // 创建下拉数据工作表（隐藏）
            Sheet dropdownSheet = workbook.createSheet(DROPDOWN_SHEET_NAME);
            workbook.setSheetHidden(workbook.getSheetIndex(DROPDOWN_SHEET_NAME), true);

            // 获取可见字段
            List<ExcelFieldConfig> visibleFields = config.getVisibleFields();
            if (CollectionUtils.isEmpty(visibleFields)) {
                throw new IllegalArgumentException("No visible fields configured for template: " + config.getTemplateKey());
            }

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 创建表头
            Row headerRow = mainSheet.createRow(0);
            for (int i = 0; i < visibleFields.size(); i++) {
                ExcelFieldConfig fieldConfig = visibleFields.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fieldConfig.getColumnName());
                cell.setCellStyle(headerStyle);
                
                // 设置列宽
                if (fieldConfig.getWidth() != null && fieldConfig.getWidth() > 0) {
                    mainSheet.setColumnWidth(i, fieldConfig.getWidth() * 256);
                }
            }

            // 创建数据验证（下拉框）
            createDataValidations(mainSheet, dropdownSheet, visibleFields, providers);

            // 设置响应头
            String fileName = generateFileName(config.getTemplateKey(), "template");
            setResponseHeaders(response, fileName);

            // 写入响应流
            workbook.write(response.getOutputStream());
            
            log.info("Exported template for: {}", config.getTemplateKey());
            
        } catch (Exception e) {
            log.error("Failed to export template for: {}", config.getTemplateKey(), e);
            throw new RuntimeException("Failed to export template", e);
        }
    }

    /**
     * 导入Excel数据
     *
     * @param inputStream 输入流
     * @param config      模板配置
     * @param providers   下拉数据提供者映射
     * @return 导入结果
     */
    public static ImportResult importData(InputStream inputStream, ExcelTemplateConfig config,
                                        Map<String, DropdownProvider> providers) {
        ImportResult result = new ImportResult();
        long startTime = System.currentTimeMillis();

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 获取可见字段
            List<ExcelFieldConfig> visibleFields = config.getVisibleFields();
            if (CollectionUtils.isEmpty(visibleFields)) {
                result.addError(0, null, null, "No visible fields configured");
                return result;
            }

            // 验证表头
            if (!validateHeaders(sheet, visibleFields, result)) {
                return result;
            }

            // 构建下拉选项映射
            Map<String, Map<String, Object>> dropdownMaps = buildDropdownMaps(visibleFields, providers);

            // 读取数据行
            List<Map<String, Object>> dataList = new ArrayList<>();
            int lastRowNum = sheet.getLastRowNum();
            result.setTotalRows(lastRowNum);

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isEmptyRow(row)) {
                    result.addSkip();
                    continue;
                }

                Map<String, Object> rowData = parseRowData(row, visibleFields, dropdownMaps, result, rowIndex);
                if (rowData != null) {
                    dataList.add(rowData);
                }
            }

            result.setProcessTime(System.currentTimeMillis() - startTime);
            log.info("Imported {} rows for template: {}", dataList.size(), config.getTemplateKey());
            
            return result;
            
        } catch (Exception e) {
            log.error("Failed to import data for template: {}", config.getTemplateKey(), e);
            result.addError(0, null, null, "Import failed: " + e.getMessage());
            return result;
        }
    }

    /**
     * 导出Excel数据
     *
     * @param dataList      数据列表
     * @param config        模板配置
     * @param visibleFields 可见字段列表
     * @param response      HTTP响应
     */
    public static void exportData(List<?> dataList, ExcelTemplateConfig config,
                                List<String> visibleFields, HttpServletResponse response) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(config.getSheetName());

            // 获取字段配置
            List<ExcelFieldConfig> fieldConfigs = getFieldConfigs(config, visibleFields);
            if (CollectionUtils.isEmpty(fieldConfigs)) {
                throw new IllegalArgumentException("No field configurations found");
            }

            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fieldConfigs.size(); i++) {
                ExcelFieldConfig fieldConfig = fieldConfigs.get(i);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fieldConfig.getColumnName());
                cell.setCellStyle(headerStyle);
                
                // 设置列宽
                if (fieldConfig.getWidth() != null && fieldConfig.getWidth() > 0) {
                    sheet.setColumnWidth(i, fieldConfig.getWidth() * 256);
                }
            }

            // 填充数据
            for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
                Object dataItem = dataList.get(rowIndex);
                Row dataRow = sheet.createRow(rowIndex + 1);
                
                for (int colIndex = 0; colIndex < fieldConfigs.size(); colIndex++) {
                    ExcelFieldConfig fieldConfig = fieldConfigs.get(colIndex);
                    Cell cell = dataRow.createCell(colIndex);
                    cell.setCellStyle(dataStyle);
                    
                    Object value = getFieldValue(dataItem, fieldConfig.getFieldName());
                    String displayValue = DataConverter.convertToExcelValue(value, fieldConfig);
                    cell.setCellValue(displayValue);
                }
            }

            // 设置响应头
            String fileName = generateFileName(config.getTemplateKey(), "data");
            setResponseHeaders(response, fileName);

            // 写入响应流
            workbook.write(response.getOutputStream());
            
            log.info("Exported {} rows for template: {}", dataList.size(), config.getTemplateKey());
            
        } catch (Exception e) {
            log.error("Failed to export data for template: {}", config.getTemplateKey(), e);
            throw new RuntimeException("Failed to export data", e);
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    /**
     * 创建数据验证（下拉框）
     */
    private static void createDataValidations(Sheet mainSheet, Sheet dropdownSheet,
                                            List<ExcelFieldConfig> visibleFields,
                                            Map<String, DropdownProvider> providers) {
        XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper((org.apache.poi.xssf.usermodel.XSSFSheet) mainSheet);
        int dropdownRowIndex = 0;

        for (int colIndex = 0; colIndex < visibleFields.size(); colIndex++) {
            ExcelFieldConfig fieldConfig = visibleFields.get(colIndex);
            DropdownConfig dropdownConfig = fieldConfig.getDropdownProvider();
            
            if (dropdownConfig == null) {
                continue;
            }

            // 获取下拉选项
            List<DropdownOption> options = getDropdownOptions(dropdownConfig, providers);
            if (CollectionUtils.isEmpty(options) || options.size() > MAX_DROPDOWN_OPTIONS) {
                log.warn("Skip dropdown for field {} due to empty or too many options: {}", 
                        fieldConfig.getFieldName(), options.size());
                continue;
            }

            // 在隐藏工作表中创建下拉数据
            String[] optionArray = options.stream()
                    .map(DropdownOption::getLabel)
                    .toArray(String[]::new);

            for (int i = 0; i < optionArray.length; i++) {
                Row row = dropdownSheet.getRow(dropdownRowIndex + i);
                if (row == null) {
                    row = dropdownSheet.createRow(dropdownRowIndex + i);
                }
                Cell cell = row.createCell(colIndex);
                cell.setCellValue(optionArray[i]);
            }

            // 创建数据验证
            String formula = String.format("%s!$%s$%d:$%s$%d",
                    DROPDOWN_SHEET_NAME,
                    getColumnName(colIndex),
                    dropdownRowIndex + 1,
                    getColumnName(colIndex),
                    dropdownRowIndex + optionArray.length);

            XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) 
                    validationHelper.createFormulaListConstraint(formula);
            
            CellRangeAddressList addressList = new CellRangeAddressList(1, 65535, colIndex, colIndex);
            XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
            
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("输入错误", "请从下拉列表中选择有效值");
            
            mainSheet.addValidationData(validation);
            
            dropdownRowIndex = Math.max(dropdownRowIndex, optionArray.length);
        }
    }

    /**
     * 获取下拉选项
     */
    private static List<DropdownOption> getDropdownOptions(DropdownConfig dropdownConfig,
                                                         Map<String, DropdownProvider> providers) {
        if (dropdownConfig == null || !StringUtils.hasText(dropdownConfig.getType())) {
            return new ArrayList<>();
        }

        DropdownProvider provider = providers.get(dropdownConfig.getType());
        if (provider == null) {
            log.warn("Dropdown provider not found for type: {}", dropdownConfig.getType());
            return new ArrayList<>();
        }

        return provider.getOptions(dropdownConfig);
    }

    /**
     * 验证表头
     */
    private static boolean validateHeaders(Sheet sheet, List<ExcelFieldConfig> visibleFields, ImportResult result) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            result.addError(0, null, null, "Header row not found");
            return false;
        }

        for (int i = 0; i < visibleFields.size(); i++) {
            ExcelFieldConfig fieldConfig = visibleFields.get(i);
            Cell cell = headerRow.getCell(i);
            
            if (cell == null) {
                result.addError(0, fieldConfig.getFieldName(), null, 
                        "Header cell not found for column: " + fieldConfig.getColumnName());
                return false;
            }

            String headerValue = cell.getStringCellValue();
            if (!fieldConfig.getColumnName().equals(headerValue)) {
                result.addError(0, fieldConfig.getFieldName(), headerValue,
                        "Header mismatch. Expected: " + fieldConfig.getColumnName() + ", Found: " + headerValue);
                return false;
            }
        }

        return true;
    }

    /**
     * 构建下拉选项映射
     */
    private static Map<String, Map<String, Object>> buildDropdownMaps(List<ExcelFieldConfig> visibleFields,
                                                                     Map<String, DropdownProvider> providers) {
        Map<String, Map<String, Object>> dropdownMaps = new HashMap<>();

        for (ExcelFieldConfig fieldConfig : visibleFields) {
            DropdownConfig dropdownConfig = fieldConfig.getDropdownProvider();
            if (dropdownConfig == null) {
                continue;
            }

            List<DropdownOption> options = getDropdownOptions(dropdownConfig, providers);
            if (!CollectionUtils.isEmpty(options)) {
                Map<String, Object> optionMap = options.stream()
                        .collect(Collectors.toMap(
                                DropdownOption::getLabel,
                                DropdownOption::getValue,
                                (existing, replacement) -> existing
                        ));
                dropdownMaps.put(fieldConfig.getFieldName(), optionMap);
            }
        }

        return dropdownMaps;
    }

    /**
     * 解析行数据
     */
    private static Map<String, Object> parseRowData(Row row, List<ExcelFieldConfig> visibleFields,
                                                   Map<String, Map<String, Object>> dropdownMaps,
                                                   ImportResult result, int rowIndex) {
        Map<String, Object> rowData = new HashMap<>();
        boolean hasError = false;

        for (int colIndex = 0; colIndex < visibleFields.size(); colIndex++) {
            ExcelFieldConfig fieldConfig = visibleFields.get(colIndex);
            Cell cell = row.getCell(colIndex);
            
            try {
                Object cellValue = getCellValue(cell);
                
                // 处理下拉框值转换
                if (dropdownMaps.containsKey(fieldConfig.getFieldName()) && cellValue != null) {
                    Map<String, Object> optionMap = dropdownMaps.get(fieldConfig.getFieldName());
                    Object mappedValue = optionMap.get(String.valueOf(cellValue));
                    if (mappedValue != null) {
                        cellValue = mappedValue;
                    }
                }

                // 数据类型转换
                Object convertedValue = DataConverter.convertCellValue(cellValue, fieldConfig);
                
                // 必填验证
                if (Boolean.TRUE.equals(fieldConfig.getRequired()) && 
                    (convertedValue == null || !StringUtils.hasText(String.valueOf(convertedValue)))) {
                    result.addError(rowIndex, fieldConfig.getFieldName(), cellValue,
                            "Required field is empty: " + fieldConfig.getColumnName());
                    hasError = true;
                    continue;
                }

                rowData.put(fieldConfig.getFieldName(), convertedValue);
                
            } catch (Exception e) {
                result.addError(rowIndex, fieldConfig.getFieldName(), getCellValue(cell),
                        "Data conversion failed: " + e.getMessage());
                hasError = true;
            }
        }

        return hasError ? null : rowData;
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 判断是否为空行
     */
    private static boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String cellValue = String.valueOf(getCellValue(cell));
                if (StringUtils.hasText(cellValue)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 获取字段配置列表
     */
    private static List<ExcelFieldConfig> getFieldConfigs(ExcelTemplateConfig config, List<String> visibleFields) {
        if (CollectionUtils.isEmpty(visibleFields)) {
            return config.getVisibleFields();
        }

        return config.getFields().stream()
                .filter(field -> visibleFields.contains(field.getFieldName()))
                .sorted((f1, f2) -> Integer.compare(f1.getColumnIndex(), f2.getColumnIndex()))
                .collect(Collectors.toList());
    }

    /**
     * 获取字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            log.warn("Failed to get field value: {}.{}", obj.getClass().getSimpleName(), fieldName);
            return null;
        }
    }

    /**
     * 获取列名（A, B, C, ...）
     */
    private static String getColumnName(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        while (columnIndex >= 0) {
            columnName.insert(0, (char) ('A' + columnIndex % 26));
            columnIndex = columnIndex / 26 - 1;
        }
        return columnName.toString();
    }

    /**
     * 生成文件名
     */
    private static String generateFileName(String templateKey, String suffix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s_%s.xlsx", templateKey, suffix, timestamp);
    }

    /**
     * 设置响应头
     */
    private static void setResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}