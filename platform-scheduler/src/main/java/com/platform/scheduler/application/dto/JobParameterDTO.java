package com.platform.scheduler.application.dto;

import lombok.Data;

/**
 * 作业参数数据传输对象
 * 
 * @author platform
 */
@Data
public class JobParameterDTO {
    
    /**
     * 参数名称
     */
    private String name;
    
    /**
     * 参数值
     */
    private String value;
    
    /**
     * 参数类型
     */
    private String type;
    
    /**
     * 是否必需
     */
    private boolean required;
    
    /**
     * 参数描述
     */
    private String description;
}
