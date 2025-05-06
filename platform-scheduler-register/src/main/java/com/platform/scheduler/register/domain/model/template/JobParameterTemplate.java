package com.platform.scheduler.register.domain.model.template;

import com.platform.scheduler.domain.model.job.JobParameterType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作业参数模板值对象
 * 定义了作业参数的属性和约束条件
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class JobParameterTemplate {
    
    private final String name;
    private final String displayName;
    private final String description;
    private final JobParameterType type;
    private final String defaultValue;
    private final boolean required;
    private final String validationPattern;
    private final String validationMessage;
    private final List<String> allowedValues;
    private final String minValue;
    private final String maxValue;
    
    /**
     * 创建新的参数模板构建器
     */
    public static class JobParameterTemplateBuilder {
        private List<String> allowedValues = new ArrayList<>();
        
        /**
         * 添加允许的取值
         * 
         * @param value 允许的取值
         * @return 构建器实例
         */
        public JobParameterTemplateBuilder addAllowedValue(String value) {
            if (value != null) {
                if (this.allowedValues == null) {
                    this.allowedValues = new ArrayList<>();
                }
                this.allowedValues.add(value);
            }
            return this;
        }
        
        /**
         * 设置允许的取值列表
         * 
         * @param values 允许的取值列表
         * @return 构建器实例
         */
        public JobParameterTemplateBuilder allowedValues(List<String> values) {
            if (values != null) {
                if (this.allowedValues == null) {
                    this.allowedValues = new ArrayList<>();
                } else {
                    this.allowedValues.clear();
                }
                this.allowedValues.addAll(values);
            }
            return this;
        }
    }
    
    /**
     * 校验参数值是否符合约束条件
     * 
     * @param value 待校验的参数值
     * @return 校验结果
     */
    public ValidationResult validate(String value) {
        // 必填检查
        if (required && (value == null || value.trim().isEmpty())) {
            return ValidationResult.error("参数「" + displayName + "」不能为空");
        }
        
        // 空值但非必填，直接返回成功
        if ((value == null || value.trim().isEmpty()) && !required) {
            return ValidationResult.success();
        }
        
        // 正则表达式校验
        if (validationPattern != null && !value.matches(validationPattern)) {
            String message = validationMessage != null ? 
                    validationMessage : "参数「" + displayName + "」格式不正确";
            return ValidationResult.error(message);
        }
        
        // 允许值列表校验
        if (allowedValues != null && !allowedValues.isEmpty() && !allowedValues.contains(value)) {
            return ValidationResult.error("参数「" + displayName + "」必须是以下值之一: " + 
                    String.join(", ", allowedValues));
        }
        
        // 根据类型进行校验
        switch (type) {
            case NUMBER:
                try {
                    double numValue = Double.parseDouble(value);
                    
                    // 最小值校验
                    if (minValue != null && !minValue.isEmpty()) {
                        double min = Double.parseDouble(minValue);
                        if (numValue < min) {
                            return ValidationResult.error("参数「" + displayName + "」不能小于 " + minValue);
                        }
                    }
                    
                    // 最大值校验
                    if (maxValue != null && !maxValue.isEmpty()) {
                        double max = Double.parseDouble(maxValue);
                        if (numValue > max) {
                            return ValidationResult.error("参数「" + displayName + "」不能大于 " + maxValue);
                        }
                    }
                } catch (NumberFormatException e) {
                    return ValidationResult.error("参数「" + displayName + "」必须是有效的数字");
                }
                break;
                
            case DATE:
                // 日期格式校验在正则表达式中处理
                break;
                
            case BOOLEAN:
                if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    return ValidationResult.error("参数「" + displayName + "」必须是布尔值(true/false)");
                }
                break;
                
            default:
                // 对于STRING和JSON类型，没有额外的校验
                break;
        }
        
        return ValidationResult.success();
    }
    
    /**
     * 获取允许的取值列表
     * 
     * @return 不可修改的允许取值列表
     */
    public List<String> getAllowedValues() {
        return allowedValues != null ? Collections.unmodifiableList(allowedValues) : Collections.emptyList();
    }
    
    /**
     * 参数校验结果值对象
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
    }
}
