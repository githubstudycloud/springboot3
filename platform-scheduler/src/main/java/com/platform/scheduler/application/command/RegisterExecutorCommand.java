package com.platform.scheduler.application.command;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 注册执行器命令
 * 
 * @author platform
 */
@Data
public class RegisterExecutorCommand {
    
    /**
     * 执行器名称
     */
    private String name;
    
    /**
     * 执行器描述
     */
    private String description;
    
    /**
     * 执行器类型
     */
    private String type;
    
    /**
     * 主机地址
     */
    private String host;
    
    /**
     * 端口号
     */
    private Integer port;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 标签列表
     */
    private Set<String> tags = new HashSet<>();
    
    /**
     * 最大并发数
     */
    private Integer maxConcurrency;
    
    /**
     * CPU核心数
     */
    private Integer cpuCores;
    
    /**
     * 总内存（字节）
     */
    private Long totalMemory;
    
    /**
     * 注册者
     */
    private String registeredBy;
}
