package com.example.platform.governance.metadata.domain.model;

/**
 * 元数据类型枚举
 * 
 * 定义系统支持的元数据类型
 */
public enum MetadataType {
    
    /**
     * 数据源元数据
     */
    DATA_SOURCE("数据源"),
    
    /**
     * 数据集元数据
     */
    DATASET("数据集"),
    
    /**
     * 数据表元数据
     */
    TABLE("数据表"),
    
    /**
     * 数据字段元数据
     */
    FIELD("数据字段"),
    
    /**
     * API元数据
     */
    API("API接口"),
    
    /**
     * 文件元数据
     */
    FILE("文件"),
    
    /**
     * 报表元数据
     */
    REPORT("报表"),
    
    /**
     * 业务对象元数据
     */
    BUSINESS_OBJECT("业务对象"),
    
    /**
     * 系统元数据
     */
    SYSTEM("系统"),
    
    /**
     * 其他元数据
     */
    OTHER("其他");
    
    private final String displayName;
    
    MetadataType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取类型显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据显示名称获取元数据类型枚举
     * 
     * @param displayName 显示名称
     * @return 对应的元数据类型枚举，如果不存在则返回OTHER
     */
    public static MetadataType fromDisplayName(String displayName) {
        for (MetadataType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return OTHER;
    }
}
