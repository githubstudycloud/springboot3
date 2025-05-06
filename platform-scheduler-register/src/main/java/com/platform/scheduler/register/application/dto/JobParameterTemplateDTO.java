package com.platform.scheduler.register.application.dto;

import com.platform.scheduler.domain.model.job.JobParameterType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业参数模板DTO
 * 
 * @author platform
 */
@Data
@Builder
public class JobParameterTemplateDTO {
    
    /**
     * 参数名称
     */
    private String name;
    
    /**
     * 参数显示名称
     */
    private String displayName;
    
    /**
     * 参数描述
     */
    private String description;
    
    /**
     * 参数类型
     */
    private JobParameterType type;
    
    /**
     * 参数类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 是否必填
     */
    private boolean required;
    
    /**
     * 验证正则表达式
     */
    private String validationPattern;
    
    /**
     * 验证失败消息
     */
    private String validationMessage;
    
    /**
     * 允许的取值列表
     */
    @Builder.Default
    private List<String> allowedValues = new ArrayList<>();
    
    /**
     * 最小值（适用于数值类型）
     */
    private String minValue;
    
    /**
     * 最大值（适用于数值类型）
     */
    private String maxValue;
}
