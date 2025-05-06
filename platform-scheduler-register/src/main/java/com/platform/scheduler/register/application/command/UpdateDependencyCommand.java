package com.platform.scheduler.register.application.command;

import com.platform.scheduler.register.domain.model.dependency.DependencyType;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新依赖关系命令
 * 
 * @author platform
 */
@Data
@Builder
public class UpdateDependencyCommand {
    
    /**
     * 依赖类型
     */
    private DependencyType type;
    
    /**
     * 条件表达式（适用于条件依赖）
     */
    @Size(max = 500, message = "条件表达式不能超过500个字符")
    private String condition;
    
    /**
     * 描述
     */
    @Size(max = 200, message = "描述不能超过200个字符")
    private String description;
    
    /**
     * 操作者
     */
    @NotBlank(message = "操作者不能为空")
    private String operator;
}
