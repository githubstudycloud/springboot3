package com.platform.scheduler.config.datasource.multi;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 数据源定义
 * 
 * @author platform
 */
@Data
public class DataSourceDefinition {
    
    /**
     * 数据源ID
     */
    private String id;
    
    /**
     * 数据源名称
     */
    private String name;
    
    /**
     * 数据源类型
     */
    private String type;
    
    /**
     * 驱动类名
     */
    private String driverClassName;
    
    /**
     * 连接URL
     */
    private String url;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 是否默认数据源
     */
    private boolean defaultDataSource = false;
    
    /**
     * 连接池配置
     */
    private Map<String, Object> poolConfig = new HashMap<>();
    
    /**
     * 连接属性
     */
    private Map<String, String> properties = new HashMap<>();
    
    /**
     * 状态
     */
    private DataSourceStatus status = DataSourceStatus.INACTIVE;
    
    /**
     * 创建时间
     */
    private long createdTime;
    
    /**
     * 最后更新时间
     */
    private long updatedTime;
    
    /**
     * 最后检查时间
     */
    private long lastCheckTime;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 数据源状态枚举
     */
    public enum DataSourceStatus {
        /**
         * 未激活
         */
        INACTIVE,
        
        /**
         * 激活
         */
        ACTIVE,
        
        /**
         * 失败
         */
        FAILED,
        
        /**
         * 暂停
         */
        PAUSED
    }
}
