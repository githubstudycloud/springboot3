package com.example.platform.governance.core.domain.model.rule.cleansing.impl;

import com.example.platform.governance.core.domain.model.rule.cleansing.BaseCleansingRule;
import com.example.platform.governance.core.domain.model.rule.cleansing.CleansingContext;
import com.example.platform.governance.core.domain.model.rule.cleansing.CleansingResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 正则表达式清洗规则
 * 
 * 使用正则表达式模式匹配和替换实现数据清洗
 */
public class RegexCleansingRule extends BaseCleansingRule {
    
    private final String patternString;
    private final String replacement;
    private Pattern pattern;
    
    /**
     * 创建新的正则表达式清洗规则
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param patternString 正则表达式模式
     * @param replacement 替换字符串
     * @return 新创建的规则实例
     */
    public static RegexCleansingRule create(String name, String targetField, String patternString, String replacement) {
        return new RegexCleansingRule(name, targetField, patternString, replacement);
    }
    
    /**
     * 构造函数
     * 
     * @param name 规则名称
     * @param targetField 目标字段
     * @param patternString 正则表达式模式
     * @param replacement 替换字符串
     */
    private RegexCleansingRule(String name, String targetField, String patternString, String replacement) {
        super(name, targetField, String.class);
        
        if (patternString == null || patternString.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        
        this.patternString = patternString;
        this.replacement = replacement != null ? replacement : "";
        
        try {
            this.pattern = Pattern.compile(patternString);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression pattern: " + e.getMessage());
        }
    }
    
    /**
     * 构造函数
     * 
     * @param id 规则ID
     * @param name 规则名称
     * @param targetField 目标字段
     * @param patternString 正则表达式模式
     * @param replacement 替换字符串
     */
    private RegexCleansingRule(String id, String name, String targetField, String patternString, String replacement) {
        super(id, name, targetField, String.class);
        
        if (patternString == null || patternString.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }
        
        this.patternString = patternString;
        this.replacement = replacement != null ? replacement : "";
        
        try {
            this.pattern = Pattern.compile(patternString);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression pattern: " + e.getMessage());
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
            // 应用正则表达式替换
            Matcher matcher = pattern.matcher(stringValue);
            String cleanedValue = matcher.replaceAll(replacement);
            
            // 判断是否有变化
            if (cleanedValue.equals(stringValue)) {
                return CleansingResult.skipped(stringValue, "No changes needed for field " + getTargetField(), getId());
            }
            
            return CleansingResult.success(stringValue, cleanedValue, getId());
        } catch (Exception e) {
            return CleansingResult.failure(
                stringValue,
                "Failed to apply regex pattern: " + e.getMessage(),
                getId()
            );
        }
    }
    
    /**
     * 获取正则表达式模式字符串
     * 
     * @return 模式字符串
     */
    public String getPatternString() {
        return patternString;
    }
    
    /**
     * 获取替换字符串
     * 
     * @return 替换字符串
     */
    public String getReplacement() {
        return replacement;
    }
}
