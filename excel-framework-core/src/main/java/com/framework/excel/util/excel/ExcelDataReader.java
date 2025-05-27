package com.framework.excel.util.excel;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.dto.BusinessConditions;
import com.framework.excel.entity.dto.ImportResult;
import com.framework.excel.mapper.DynamicMapper;
import com.framework.excel.enums.UpdateMode;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel数据读取器 - 可独立使用
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ExcelDataReader {
    
    /**
     * 读取Excel文件数据
     */
    public List<Map<String, Object>> readExcel(MultipartFile file, ExcelTemplateConfig config, BusinessConditions conditions) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<ExcelFieldConfig> visibleFields = getVisibleFields(config.getFields(), conditions);
            
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
            throw new RuntimeException("解析Excel文件失败", e);
        }
        
        return dataList;
    }
    
    /**
     * 导入数据到数据库
     */
    public ImportResult importData(List<Map<String, Object>> dataList, ExcelTemplateConfig config, 
                                 BusinessConditions conditions, DynamicMapper dynamicMapper) {
        ImportResult result = new ImportResult();
        result.setTotalRows(dataList.size());
        
        if (conditions != null && conditions.getValidateOnly()) {
            // 仅验证模式，不实际导入
            result.setSuccessCount(dataList.size());
            return result;
        }
        
        for (Map<String, Object> rowData : dataList) {
            try {
                // 应用默认值
                applyDefaultValues(rowData, conditions);
                
                // 数据转换和验证
                convertAndValidateData(rowData, config);
                
                // 检查重复
                if (conditions != null && conditions.getSkipDuplicates() && isDuplicate(rowData, config, dynamicMapper)) {
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    continue;
                }
                
                // 执行数据库操作
                executeDataOperation(rowData, config, dynamicMapper);
                result.setSuccessCount(result.getSuccessCount() + 1);
                
            } catch (Exception e) {
                result.setErrorCount(result.getErrorCount() + 1);
                Integer rowIndex = (Integer) rowData.get("_rowIndex");
                result.getErrors().add(new ImportResult.ErrorInfo(rowIndex, e.getMessage()));
            }
        }
        
        return result;
    }
    
    /**
     * 获取可见字段
     */
    private List<ExcelFieldConfig> getVisibleFields(List<ExcelFieldConfig> fields, BusinessConditions conditions) {
        if (fields == null) {
            return new ArrayList<>();
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
                .sorted(Comparator.comparing(ExcelFieldConfig::getColumnIndex))
                .collect(Collectors.toList());
    }
    
    /**
     * 解析行数据
     */
    private Map<String, Object> parseRow(Row row, List<ExcelFieldConfig> visibleFields, int rowIndex) {
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
    
    /**
     * 获取单元格值
     */
    private Object getCellValue(Cell cell, ExcelFieldConfig field) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
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
    private boolean isEmptyRow(Map<String, Object> rowData) {
        return rowData.entrySet().stream()
                .filter(entry -> !"_rowIndex".equals(entry.getKey()))
                .allMatch(entry -> entry.getValue() == null || 
                         (entry.getValue() instanceof String && ((String) entry.getValue()).trim().isEmpty()));
    }
    
    /**
     * 应用默认值
     */
    private void applyDefaultValues(Map<String, Object> rowData, BusinessConditions conditions) {
        if (conditions == null || conditions.getDefaultValues() == null) {
            return;
        }
        
        for (Map.Entry<String, Object> entry : conditions.getDefaultValues().entrySet()) {
            String fieldName = entry.getKey();
            Object defaultValue = entry.getValue();
            
            // 如果字段值为空，则应用默认值
            if (rowData.get(fieldName) == null || 
                (rowData.get(fieldName) instanceof String && ((String) rowData.get(fieldName)).trim().isEmpty())) {
                rowData.put(fieldName, defaultValue);
            }
        }
    }
    
    /**
     * 数据转换和验证
     */
    private void convertAndValidateData(Map<String, Object> rowData, ExcelTemplateConfig config) {
        for (ExcelFieldConfig field : config.getFields()) {
            Object value = rowData.get(field.getFieldName());
            
            // 数据转换
            if (value != null) {
                Object convertedValue = DataConverter.convertValue(value, field);
                rowData.put(field.getFieldName(), convertedValue);
            }
            
            // 必填验证
            if (field.getRequired() && (value == null || 
                (value instanceof String && ((String) value).trim().isEmpty()))) {
                throw new RuntimeException("字段 " + field.getColumnName() + " 不能为空");
            }
            
            // 其他验证规则
            ValidationUtils.validateField(value, field, rowData.get("_rowIndex"));
        }
    }
    
    /**
     * 检查重复
     */
    private boolean isDuplicate(Map<String, Object> rowData, ExcelTemplateConfig config, DynamicMapper dynamicMapper) {
        Map<String, Object> conditions = new HashMap<>();
        
        // 使用主键字段构建查询条件
        for (String pkField : config.getPrimaryKeyFieldList()) {
            Object value = rowData.get(pkField);
            if (value != null) {
                conditions.put(pkField, value);
            }
        }
        
        if (conditions.isEmpty()) {
            return false;
        }
        
        try {
            int count = dynamicMapper.countByConditions(config.getTableName(), conditions);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 执行数据库操作
     */
    private void executeDataOperation(Map<String, Object> rowData, ExcelTemplateConfig config, DynamicMapper dynamicMapper) {
        // 移除辅助字段
        rowData.remove("_rowIndex");
        
        String updateMode = config.getUpdateMode();
        if (updateMode == null) {
            updateMode = UpdateMode.INSERT_OR_UPDATE.getCode();
        }
        
        switch (UpdateMode.getByCode(updateMode)) {
            case INSERT_ONLY:
                dynamicMapper.insert(config.getTableName(), rowData);
                break;
            case UPDATE_ONLY:
                updateExistingRecord(rowData, config, dynamicMapper);
                break;
            case INSERT_OR_UPDATE:
                insertOrUpdateRecord(rowData, config, dynamicMapper);
                break;
            default:
                throw new RuntimeException("不支持的更新模式: " + updateMode);
        }
    }
    
    /**
     * 更新现有记录
     */
    private void updateExistingRecord(Map<String, Object> rowData, ExcelTemplateConfig config, DynamicMapper dynamicMapper) {
        Map<String, Object> conditions = new HashMap<>();
        Map<String, Object> updateData = new HashMap<>(rowData);
        
        // 使用主键字段构建查询条件
        for (String pkField : config.getPrimaryKeyFieldList()) {
            Object value = rowData.get(pkField);
            if (value != null) {
                conditions.put(pkField, value);
                updateData.remove(pkField); // 主键字段不参与更新
            }
        }
        
        if (conditions.isEmpty()) {
            throw new RuntimeException("更新模式下主键字段不能为空");
        }
        
        int affected = dynamicMapper.update(config.getTableName(), updateData, conditions);
        if (affected == 0) {
            throw new RuntimeException("未找到要更新的记录");
        }
    }
    
    /**
     * 插入或更新记录
     */
    private void insertOrUpdateRecord(Map<String, Object> rowData, ExcelTemplateConfig config, DynamicMapper dynamicMapper) {
        Map<String, Object> conditions = new HashMap<>();
        
        // 使用主键字段构建查询条件
        for (String pkField : config.getPrimaryKeyFieldList()) {
            Object value = rowData.get(pkField);
            if (value != null) {
                conditions.put(pkField, value);
            }
        }
        
        if (conditions.isEmpty()) {
            // 没有主键，直接插入
            dynamicMapper.insert(config.getTableName(), rowData);
        } else {
            // 检查记录是否存在
            int count = dynamicMapper.countByConditions(config.getTableName(), conditions);
            if (count > 0) {
                // 记录存在，执行更新
                Map<String, Object> updateData = new HashMap<>(rowData);
                for (String pkField : config.getPrimaryKeyFieldList()) {
                    updateData.remove(pkField); // 主键字段不参与更新
                }
                dynamicMapper.update(config.getTableName(), updateData, conditions);
            } else {
                // 记录不存在，执行插入
                dynamicMapper.insert(config.getTableName(), rowData);
            }
        }
    }
} 