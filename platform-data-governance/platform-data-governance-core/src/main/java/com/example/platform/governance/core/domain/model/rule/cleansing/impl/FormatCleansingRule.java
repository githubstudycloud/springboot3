package com.example.platform.governance.core.domain.model.rule.cleansing.impl;

import com.example.platform.governance.core.domain.model.rule.cleansing.BaseCleansingRule;
import com.example.platform.governance.core.domain.model.rule.cleansing.CleansingContext;
import com.example.platform.governance.core.domain.model.rule.cleansing.CleansingResult;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式化清洗规则
 * 
 * 对字符串进行格式化处理，支持模式匹配和格式化输出
 */
public class FormatCleansingRule extends BaseCleansingRule {
    
    private final String formatPattern;
    private final String extractPattern;
    private Pattern extractRegex;
    
    /**
     * 创建新的格式化清洗规则
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param formatPattern 格式化模式
     * @return 新创建的规则实例
     */
    public static FormatCleansingRule create(String name, String targetField, String formatPattern) {
        return new FormatCleansingRule(name, targetField, formatPattern, null);
    }
    
    /**
     * 创建带正则提取的格式化清洗规则
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param formatPattern 格式化模式
     * @param extractPattern 提取模式（正则表达式）
     * @return 新创建的规则实例
     */
    public static FormatCleansingRule createWithExtract(
            String name, String targetField, String formatPattern, String extractPattern) {
        return new FormatCleansingRule(name, targetField, formatPattern, extractPattern);
    }
    
    /**
     * 构造函数
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param formatPattern 格式化模式
     * @param extractPattern 提取模式（正则表达式）
     */
    private FormatCleansingRule(String name, String targetField, String formatPattern, String extractPattern) {
        super(name, targetField, String.class);
        
        if (formatPattern == null || formatPattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Format pattern cannot be null or empty");
        }
        
        this.formatPattern = formatPattern;
        this.extractPattern = extractPattern;
        
        if (extractPattern != null && !extractPattern.trim().isEmpty()) {
            try {
                this.extractRegex = Pattern.compile(extractPattern);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid extract pattern: " + e.getMessage());
            }
        }
    }
    
    @Override
    public CleansingResult apply(Object fieldValue, CleansingContext context) {
        // 验证字段
        CleansingResult validationResult = validateField(fieldValue, context);
        if (validationResult != null) {
            return validationResult;
        }
        
        String stringValue = (String) fieldValue;
        
        try {
            String cleanedValue;
            
            if (extractRegex != null) {
                // 如果有提取模式，先提取分组
                Matcher matcher = extractRegex.matcher(stringValue);
                if (!matcher.find()) {
                    return CleansingResult.warning(
                        stringValue,
                        stringValue,
                        "Pattern does not match for field " + getTargetField(),
                        getId()
                    );
                }
                
                // 收集分组值作为格式化参数
                Object[] groups = new Object[matcher.groupCount()];
                for (int i = 0; i < matcher.groupCount(); i++) {
                    groups[i] = matcher.group(i + 1);
                }
                
                // 应用格式化
                cleanedValue = MessageFormat.format(formatPattern, groups);
            } else {
                // 如果没有提取模式，直接将整个值作为唯一参数进行格式化
                cleanedValue = MessageFormat.format(formatPattern, stringValue);
            }
            
            // 判断是否有变化
            if (cleanedValue.equals(stringValue)) {
                return CleansingResult.skipped(
                    stringValue,
                    "No changes needed for field " + getTargetField(),
                    getId()
                );
            }
            
            return CleansingResult.success(stringValue, cleanedValue, getId());
        } catch (Exception e) {
            return CleansingResult.failure(
                stringValue,
                "Failed to format value: " + e.getMessage(),
                getId()
            );
        }
    }
    
    /**
     * 获取格式化模式
     * 
     * @return 格式化模式
     */
    public String getFormatPattern() {
        return formatPattern;
    }
    
    /**
     * 获取提取模式
     * 
     * @return 提取模式
     */
    public String getExtractPattern() {
        return extractPattern;
    }
}
