package com.example.platform.governance.core.domain.model.asset;

/**
 * 数据资产类型枚举
 * 
 * 定义系统支持的各种数据资产类型
 */
public enum DataAssetType {
    
    /**
     * 数据表
     */
    TABLE("数据表"),
    
    /**
     * API接口
     */
    API("API接口"),
    
    /**
     * 结构化文件
     */
    STRUCTURED_FILE("结构化文件"),
    
    /**
     * 非结构化文件
     */
    UNSTRUCTURED_FILE("非结构化文件"),
    
    /**
     * 数据流
     */
    DATA_STREAM("数据流"),
    
    /**
     * 数据模型
     */
    DATA_MODEL("数据模型"),
    
    /**
     * 数据服务
     */
    DATA_SERVICE("数据服务"),
    
    /**
     * 消息队列
     */
    MESSAGE_QUEUE("消息队列"),
    
    /**
     * 其他类型
     */
    OTHER("其他");
    
    private final String displayName;
    
    DataAssetType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 获取资产类型的显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据显示名称获取资产类型枚举
     * 
     * @param displayName 显示名称
     * @return 对应的资产类型枚举，如果不存在则返回OTHER
     */
    public static DataAssetType fromDisplayName(String displayName) {
        for (DataAssetType type : values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return OTHER;
    }
}
