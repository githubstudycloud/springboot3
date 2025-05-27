package com.framework.excel.util.excel;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.entity.config.DropdownConfig;
import com.framework.excel.entity.dto.DropdownOption;
import com.framework.excel.mapper.DynamicMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下拉解析器 - 可独立使用
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class DropdownResolver {
    
    /**
     * 解析所有字段的下拉框数据
     */
    public void resolveDropdowns(ExcelTemplateConfig config, Map<String, Object> dropdownParams, DynamicMapper dynamicMapper) {
        if (config.getFields() == null) {
            return;
        }
        
        for (ExcelFieldConfig field : config.getFields()) {
            DropdownConfig dropdownConfig = field.getDropdownConfigObject();
            if (dropdownConfig != null) {
                resolveDropdown(dropdownConfig, dropdownParams, dynamicMapper);
            }
        }
    }
    
    /**
     * 解析单个下拉框数据
     */
    public void resolveDropdown(DropdownConfig dropdownConfig, Map<String, Object> dropdownParams, DynamicMapper dynamicMapper) {
        if ("RELATED_TABLE".equals(dropdownConfig.getType())) {
            resolveRelatedTableDropdown(dropdownConfig, dropdownParams, dynamicMapper);
        }
        // STATIC类型的下拉框不需要解析，直接使用配置中的静态选项
    }
    
    /**
     * 解析关联表下拉框
     */
    private void resolveRelatedTableDropdown(DropdownConfig dropdownConfig, Map<String, Object> dropdownParams, DynamicMapper dynamicMapper) {
        String tableName = dropdownConfig.getTableName();
        String valueField = dropdownConfig.getValueField();
        String displayField = dropdownConfig.getDisplayField();
        String whereClause = dropdownConfig.getWhereClause();
        
        if (tableName == null || valueField == null || displayField == null) {
            return;
        }
        
        try {
            // 构建查询条件
            Map<String, Object> conditions = buildQueryConditions(whereClause, dropdownParams);
            
            // 查询数据
            List<String> selectFields = List.of(valueField, displayField);
            List<Map<String, Object>> dataList = dynamicMapper.selectByConditions(
                tableName, conditions, selectFields, null, null);
            
            // 转换为下拉选项
            List<DropdownOption> options = new ArrayList<>();
            
            // 如果允许为空，添加空选项
            if (dropdownConfig.getAllowEmpty() != null && dropdownConfig.getAllowEmpty()) {
                options.add(new DropdownOption("", ""));
            }
            
            for (Map<String, Object> data : dataList) {
                Object value = data.get(valueField);
                Object label = data.get(displayField);
                if (value != null && label != null) {
                    options.add(new DropdownOption(String.valueOf(value), label.toString()));
                }
            }
            
            // 设置解析后的选项
            dropdownConfig.setStaticOptions(options);
            
        } catch (Exception e) {
            // 查询失败时，设置为空列表
            dropdownConfig.setStaticOptions(new ArrayList<>());
        }
    }
    
    /**
     * 构建查询条件
     */
    private Map<String, Object> buildQueryConditions(String whereClause, Map<String, Object> dropdownParams) {
        Map<String, Object> conditions = new HashMap<>();
        
        if (whereClause == null || whereClause.trim().isEmpty() || "1=1".equals(whereClause.trim())) {
            // 没有默认条件，只使用参数条件
        } else {
            // 解析简单的where条件（例如：enabled = 1）
            parseSimpleWhereClause(whereClause, conditions);
        }
        
        // 合并参数条件
        if (dropdownParams != null) {
            conditions.putAll(dropdownParams);
        }
        
        return conditions;
    }
    
    /**
     * 解析简单的where条件
     */
    private void parseSimpleWhereClause(String whereClause, Map<String, Object> conditions) {
        // 简单解析 "field = value" 格式的条件
        String[] parts = whereClause.split("=");
        if (parts.length == 2) {
            String field = parts[0].trim();
            String value = parts[1].trim();
            
            // 去除引号
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            
            // 尝试转换为数字
            try {
                if (value.contains(".")) {
                    conditions.put(field, Double.parseDouble(value));
                } else {
                    conditions.put(field, Long.parseLong(value));
                }
            } catch (NumberFormatException e) {
                conditions.put(field, value);
            }
        }
    }
} 