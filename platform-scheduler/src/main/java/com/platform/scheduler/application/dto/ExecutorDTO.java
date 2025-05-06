package com.platform.scheduler.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 执行器数据传输对象
 * 
 * @author platform
 */
@Data
public class ExecutorDTO {
    
    /**
     * 执行器ID
     */
    private String id;
    
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
     * 执行器状态
     */
    private String status;
    
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
     * 当前负载
     */
    private Integer currentLoad;
    
    /**
     * 负载百分比
     */
    private Integer loadPercentage;
    
    /**
     * 总内存（字节）
     */
    private Long totalMemory;
    
    /**
     * 空闲内存（字节）
     */
    private Long freeMemory;
    
    /**
     * 内存使用百分比
     */
    private Integer memoryUsagePercentage;
    
    /**
     * CPU核心数
     */
    private Integer cpuCores;
    
    /**
     * CPU使用率
     */
    private Double cpuUsage;
    
    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeatTime;
    
    /**
     * 注册时间
     */
    private LocalDateTime registrationTime;
    
    /**
     * 创建者
     */
    private String createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后修改者
     */
    private String lastModifiedBy;
    
    /**
     * 最后修改时间
     */
    private LocalDateTime lastModifiedAt;
    
    /**
     * 运行中的任务数
     */
    private Integer runningTaskCount;
    
    /**
     * 是否在线
     */
    private boolean online;
    
    /**
     * 是否可用
     */
    private boolean available;
}
