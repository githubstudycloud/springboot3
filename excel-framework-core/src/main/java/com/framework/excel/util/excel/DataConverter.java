package com.framework.excel.util.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.enums.DataType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 数据转换器
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class DataConverter {
    
    /**
     * 根据字段配置转换数据
     */
    public static Object convertValue(Object value, ExcelFieldConfig fieldConfig) {
        if (value == null) {
            return null;
        }
        
        String dataType = fieldConfig.getDataType();
        
        // 处理扩展配置中的转换规则
        JSONObject extraConfig = fieldConfig.getExtraConfigJson();
        if (extraConfig != null && extraConfig.containsKey("converter")) {
            JSONObject converterConfig = extraConfig.getJSONObject("converter");
            return applyCustomConverter(value, converterConfig);
        }
        
        // 标准类型转换
        DataType type = DataType.getByCode(dataType.toUpperCase());
        switch (type) {
            case STRING:
                return convertToString(value);
            case INTEGER:
                return convertToInteger(value);
            case LONG:
                return convertToLong(value);
            case DOUBLE:
                return convertToDouble(value);
            case DATE:
                return convertToDate(value, fieldConfig.getDateFormat());
            case DATETIME:
                return convertToDateTime(value, fieldConfig.getDateFormat());
            case BOOLEAN:
                return convertToBoolean(value);
            case JSON:
                return convertToJson(value);
            default:
                return value;
        }
    }
    
    /**
     * 自定义转换器
     */
    private static Object applyCustomConverter(Object value, JSONObject converterConfig) {
        String type = converterConfig.getString("type");
        
        switch (type) {
            case "enum_mapping":
                return convertEnumMapping(value, converterConfig.getJSONObject("mapping"));
            case "scale":
                return convertScale(value, converterConfig.getDouble("factor"));
            case "prefix":
                return addPrefix(value, converterConfig.getString("prefix"));
            case "suffix":
                return addSuffix(value, converterConfig.getString("suffix"));
            case "json_extract":
                return extractJsonField(value, converterConfig.getString("field"));
            default:
                return value;
        }
    }
    
    // 具体转换方法实现
    private static String convertToString(Object value) {
        if (value instanceof String) {
            return ((String) value).trim();
        }
        return value.toString().trim();
    }
    
    private static Integer convertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        try {
            // 处理小数点情况
            if (str.contains(".")) {
                return Double.valueOf(str).intValue();
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法转换为整数: " + str);
        }
    }
    
    private static Long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        try {
            // 处理小数点情况
            if (str.contains(".")) {
                return Double.valueOf(str).longValue();
            }
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法转换为长整数: " + str);
        }
    }
    
    private static Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法转换为小数: " + str);
        }
    }
    
    private static Date convertToDate(Object value, String dateFormat) {
        if (value instanceof Date) {
            return (Date) value;
        }
        
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        // 默认日期格式
        if (dateFormat == null || dateFormat.isEmpty()) {
            dateFormat = "yyyy-MM-dd";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("无法转换为日期: " + str + ", 格式: " + dateFormat);
        }
    }
    
    private static Date convertToDateTime(Object value, String dateFormat) {
        if (value instanceof Date) {
            return (Date) value;
        }
        
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        // 默认日期时间格式
        if (dateFormat == null || dateFormat.isEmpty()) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("无法转换为日期时间: " + str + ", 格式: " + dateFormat);
        }
    }
    
    private static Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        
        String str = value.toString().trim().toLowerCase();
        if (str.isEmpty()) {
            return null;
        }
        
        return "true".equals(str) || "yes".equals(str) || "1".equals(str) || "是".equals(str);
    }
    
    private static Object convertToJson(Object value) {
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (str.isEmpty()) {
                return null;
            }
            return JSON.parse(str);
        }
        return value;
    }
    
    // 自定义转换方法
    private static Object convertEnumMapping(Object value, JSONObject mapping) {
        if (mapping == null) {
            return value;
        }
        
        String key = value.toString();
        if (mapping.containsKey(key)) {
            return mapping.getString(key);
        }
        
        return value;
    }
    
    private static Object convertScale(Object value, Double factor) {
        if (factor == null || factor == 0) {
            return value;
        }
        
        try {
            Double numValue = convertToDouble(value);
            if (numValue != null) {
                return numValue * factor;
            }
        } catch (Exception e) {
            // 转换失败，返回原值
        }
        
        return value;
    }
    
    private static String addPrefix(Object value, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return value.toString();
        }
        
        return prefix + value.toString();
    }
    
    private static String addSuffix(Object value, String suffix) {
        if (suffix == null || suffix.isEmpty()) {
            return value.toString();
        }
        
        return value.toString() + suffix;
    }
    
    private static Object extractJsonField(Object value, String field) {
        if (field == null || field.isEmpty()) {
            return value;
        }
        
        try {
            JSONObject json;
            if (value instanceof String) {
                json = JSON.parseObject((String) value);
            } else if (value instanceof Map) {
                json = new JSONObject((Map<String, Object>) value);
            } else {
                return value;
            }
            
            return json.get(field);
        } catch (Exception e) {
            return value;
        }
    }
}
