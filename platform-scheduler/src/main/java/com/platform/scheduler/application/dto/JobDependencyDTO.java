package com.platform.scheduler.application.dto;

import lombok.Data;

/**
 * 作业依赖关系数据传输对象
 * 
 * @author platform
 */
@Data
public class JobDependencyDTO {
    
    /**
     * 依赖的作业ID
     */
    private String dependencyJobId;
    
    /**
     * 依赖的作业名称
     */
    private String dependencyJobName;
    
    /**
     * 依赖类型
     */
    private String type;
    
    /**
     * 依赖条件
     */
    private String condition;
    
    /**
     * 是否强制依赖（如果为true，则前置作业失败时本作业不执行）
     */
    private boolean required;
}
