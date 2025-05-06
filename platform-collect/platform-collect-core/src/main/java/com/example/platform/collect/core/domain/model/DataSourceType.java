package com.example.platform.collect.core.domain.model;

/**
 * 数据源类型枚举
 * 定义系统支持的所有数据源类型
 */
public enum DataSourceType {
    /**
     * REST API数据源
     */
    REST_API("REST API", "基于HTTP的REST API数据源"),
    
    /**
     * SOAP API数据源
     */
    SOAP_API("SOAP API", "基于SOAP协议的Web服务数据源"),
    
    /**
     * 关系型数据库数据源
     */
    RDBMS("Relational Database", "关系型数据库数据源，如MySQL、Oracle等"),
    
    /**
     * MongoDB数据源
     */
    MONGODB("MongoDB", "MongoDB NoSQL数据库数据源"),
    
    /**
     * XML文件数据源
     */
    XML_FILE("XML File", "基于XML格式的文件数据源"),
    
    /**
     * JSON文件数据源
     */
    JSON_FILE("JSON File", "基于JSON格式的文件数据源"),
    
    /**
     * CSV文件数据源
     */
    CSV_FILE("CSV File", "基于CSV格式的文件数据源"),
    
    /**
     * FTP服务器数据源
     */
    FTP("FTP Server", "FTP服务器数据源"),
    
    /**
     * SFTP服务器数据源
     */
    SFTP("SFTP Server", "SFTP安全文件传输协议服务器数据源"),
    
    /**
     * Kafka消息队列数据源
     */
    KAFKA("Kafka", "Kafka消息队列数据源"),
    
    /**
     * RabbitMQ消息队列数据源
     */
    RABBITMQ("RabbitMQ", "RabbitMQ消息队列数据源"),
    
    /**
     * Redis数据源
     */
    REDIS("Redis", "Redis内存数据库数据源"),
    
    /**
     * Elasticsearch数据源
     */
    ELASTICSEARCH("Elasticsearch", "Elasticsearch搜索引擎数据源"),
    
    /**
     * 自定义数据源，用于扩展
     */
    CUSTOM("Custom", "自定义数据源类型");
    
    private final String displayName;
    private final String description;
    
    DataSourceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 获取数据源类型显示名称
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取数据源类型描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
}
