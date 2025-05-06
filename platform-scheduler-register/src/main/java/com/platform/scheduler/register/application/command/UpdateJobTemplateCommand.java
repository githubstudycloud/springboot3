package com.platform.scheduler.register.application.command;

import com.platform.scheduler.register.domain.model.template.TemplateCategory;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 更新作业模板命令
 * 
 * @author platform
 */
@Data
@Builder
public class UpdateJobTemplateCommand {
    
    /**
     * 模板名称
     */
    @Size(max = 100, message = "模板名称不能超过100个字符")
    private String name;
    
    /**
     * 模板描述
     */
    @Size(max = 500, message = "模板描述不能超过500个字符")
    private String description;
    
    /**
     * 模板分类
     */
    private TemplateCategory category;
    
    /**
     * 标签列表
     */
    @Size(max = 20, message = "标签数量不能超过20个")
    private List<String> labels = new ArrayList<>();
    
    /**
     * 要添加的参数模板列表
     */
    @Valid
    private List<CreateJobTemplateCommand.ParameterTemplateDto> parametersToAdd = new ArrayList<>();
    
    /**
     * 要删除的参数名称列表
     */
    private List<String> parametersToRemove = new ArrayList<>();
    
    /**
     * 操作者
     */
    @NotBlank(message = "操作者不能为空")
    private String operator;
}
