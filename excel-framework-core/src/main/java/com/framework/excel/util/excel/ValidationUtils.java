package com.framework.excel.util.excel;

import com.alibaba.fastjson.JSONObject;
import com.framework.excel.entity.config.ExcelFieldConfig;
import com.framework.excel.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * 
 * @author Framework Team
 * @since 1.0.0
 */
public class ValidationUtils {
    
    /**
     * 验证字段值
     * @param value 字段值
     * @param fieldConfig 字段配置
     * @param rowIndex 行号（用于错误提示）
     */
    public static void validateField(Object value, ExcelFieldConfig fieldConfig, int rowIndex) {
        // 必填验证
        if (Boolean.TRUE.equals(fieldConfig.getRequired())) {
            if (value == null || value.toString().trim().isEmpty()) {
                throw new ValidationException(
                    String.format("第%d行: %s不能为空", rowIndex, fieldConfig.getColumnName())
                );
            }
        }
        
        // 如果值为空且非必填，跳过后续验证
        if (value == null || value.toString().trim().isEmpty()) {
            return;
        }
        
        // 扩展配置中的验证规则
        JSONObject extraConfig = fieldConfig.getExtraConfigJson();
        if (extraConfig != null && extraConfig.containsKey("validator")) {
            JSONObject validator = extraConfig.getJSONObject("validator");
            applyCustomValidation(value, validator, fieldConfig, rowIndex);
        }
        
        // 验证规则JSON
        JSONObject validationRules = fieldConfig.getValidationRulesJson();
        if (validationRules != null && !validationRules.isEmpty()) {
            applyValidationRules(value, validationRules, fieldConfig, rowIndex);
        }
    }
    
    /**
     * 应用自定义验证
     */
    private static void applyCustomValidation(Object value, JSONObject validator, 
                                            ExcelFieldConfig fieldConfig, int rowIndex) {
        // 正则表达式验证
        if (validator.containsKey("pattern")) {
            String pattern = validator.getString("pattern");
            String message = validator.getString("message");
            
            if (!Pattern.matches(pattern, value.toString())) {
                throw new ValidationException(
                    String.format("第%d行: %s格式不正确%s", 
                        rowIndex, 
                        fieldConfig.getColumnName(),
                        message != null ? "，" + message : "")
                );
            }
        }
        
        // 长度验证
        if (validator.containsKey("minLength") || validator.containsKey("maxLength")) {
            int length = value.toString().length();
            
            Integer minLength = validator.getInteger("minLength");
            if (minLength != null && length < minLength) {
                throw new ValidationException(
                    String.format("第%d行: %s长度不能小于%d", 
                        rowIndex, fieldConfig.getColumnName(), minLength)
                );
            }
            
            Integer maxLength = validator.getInteger("maxLength");
            if (maxLength != null && length > maxLength) {
                throw new ValidationException(
                    String.format("第%d行: %s长度不能大于%d", 
                        rowIndex, fieldConfig.getColumnName(), maxLength)
                );
            }
        }
        
        // 数值范围验证
        if (validator.containsKey("min") || validator.containsKey("max")) {
            try {
                double numValue = Double.parseDouble(value.toString());
                
                Double min = validator.getDouble("min");
                if (min != null && numValue < min) {
                    throw new ValidationException(
                        String.format("第%d行: %s的值不能小于%s", 
                            rowIndex, fieldConfig.getColumnName(), min)
                    );
                }
                
                Double max = validator.getDouble("max");
                if (max != null && numValue > max) {
                    throw new ValidationException(
                        String.format("第%d行: %s的值不能大于%s", 
                            rowIndex, fieldConfig.getColumnName(), max)
                    );
                }
            } catch (NumberFormatException e) {
                // 不是数值类型，跳过数值验证
            }
        }
        
        // 枚举值验证
        if (validator.containsKey("enum")) {
            String enumValues = validator.getString("enum");
            String[] validValues = enumValues.split(",");
            boolean valid = false;
            
            for (String validValue : validValues) {
                if (validValue.trim().equals(value.toString())) {
                    valid = true;
                    break;
                }
            }
            
            if (!valid) {
                throw new ValidationException(
                    String.format("第%d行: %s的值必须是以下之一: %s", 
                        rowIndex, fieldConfig.getColumnName(), enumValues)
                );
            }
        }
    }
    
    /**
     * 应用验证规则
     */
    private static void applyValidationRules(Object value, JSONObject rules, 
                                           ExcelFieldConfig fieldConfig, int rowIndex) {
        // 唯一性验证（需要在服务层实现）
        if (rules.containsKey("unique") && rules.getBoolean("unique")) {
            // 标记需要唯一性验证，实际验证在服务层进行
        }
        
        // 自定义规则
        if (rules.containsKey("custom")) {
            String customRule = rules.getString("custom");
            // 可以根据customRule实现特定的验证逻辑
        }
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailPattern, email);
    }
    
    /**
     * 验证手机号格式（中国大陆）
     */
    public static boolean isValidPhoneNumber(String phone) {
        String phonePattern = "^1[3-9]\\d{9}$";
        return Pattern.matches(phonePattern, phone);
    }
    
    /**
     * 验证身份证号格式（中国大陆）
     */
    public static boolean isValidIdCard(String idCard) {
        String idCardPattern = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";
        return Pattern.matches(idCardPattern, idCard);
    }
}
