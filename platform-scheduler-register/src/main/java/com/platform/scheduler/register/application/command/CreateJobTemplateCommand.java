package com.platform.scheduler.register.application.command;

import com.platform.scheduler.domain.model.job.JobParameterType;
import com.platform.scheduler.domain.model.job.JobType;
import com.platform.scheduler.register.domain.model.template.TemplateCategory;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建作业模板命令
 * 
 * @author platform
 */
@Data
@Builder
public class CreateJobTemplateCommand {
    
    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称不能超过100个字符")
    private String name;
    
    /**
     * 模板描述
     */
    @Size(max = 500, message = "模板描述不能超过500个字符")
    private String description;
    
    /**
     * 作业类型
     */
    @NotNull(message = "作业类型不能为空")
    private JobType type;
    
    /**
     * 处理器名称
     */
    @NotBlank(message = "处理器名称不能为空")
    @Size(max = 200, message = "处理器名称不能超过200个字符")
    private String handlerName;
    
    /**
     * 模板分类
     */
    @NotNull(message = "模板分类不能为空")
    private TemplateCategory category;
    
    /**
     * 标签列表
     */
    @Size(max = 20, message = "标签数量不能超过20个")
    private List<String> labels = new ArrayList<>();
    
    /**
     * 参数模板列表
     */
    @Valid
    private List<ParameterTemplateDto> parameterTemplates = new ArrayList<>();
    
    /**
     * 操作者
     */
    @NotBlank(message = "操作者不能为空")
    private String operator;
    
    /**
     * 参数模板DTO
     */
    @Data
    @Builder
    public static class ParameterTemplateDto {
        
        /**
         * 参数名称
         */
        @NotBlank(message = "参数名称不能为空")
        @Size(max = 50, message = "参数名称不能超过50个字符")
        private String name;
        
        /**
         * 参数显示名称
         */
        @NotBlank(message = "参数显示名称不能为空")
        @Size(max = 100, message = "参数显示名称不能超过100个字符")
        private String displayName;
        
        /**
         * 参数描述
         */
        @Size(max = 200, message = "参数描述不能超过200个字符")
        private String description;
        
        /**
         * 参数类型
         */
        @NotNull(message = "参数类型不能为空")
        private JobParameterType type;
        
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
        @Size(max = 200, message = "验证正则表达式不能超过200个字符")
        private String validationPattern;
        
        /**
         * 验证失败消息
         */
        @Size(max = 200, message = "验证失败消息不能超过200个字符")
        private String validationMessage;
        
        /**
         * 允许的取值列表
         */
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
}
