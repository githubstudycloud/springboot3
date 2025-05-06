package com.example.platform.governance.core.domain.model.rule.cleansing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 清洗上下文领域模型
 * 
 * 包含清洗规则执行时的上下文信息
 */
public class CleansingContext {
    
    private final String dataAssetId;
    private final Map<String, Object> record;
    private final Map<String, Object> parameters;
    private final Map<String, Object> variables;
    
    /**
     * 创建清洗上下文
     * 
     * @param dataAssetId 数据资产ID
     * @param record 当前处理的记录
     * @return 新创建的清洗上下文实例
     */
    public static CleansingContext create(String dataAssetId, Map<String, Object> record) {
        if (dataAssetId == null || dataAssetId.trim().isEmpty()) {
            throw new IllegalArgumentException("Data asset ID cannot be null or empty");
        }
        
        return new CleansingContext(dataAssetId, record);
    }
    
    /**
     * 获取数据资产ID
     * 
     * @return 数据资产ID
     */
    public String getDataAssetId() {
        return dataAssetId;
    }
    
    /**
     * 获取当前记录中的字段值
     * 
     * @param fieldName 字段名
     * @return 字段值，如果不存在则返回null
     */
    public Object getFieldValue(String fieldName) {
        return record.get(fieldName);
    }
    
    /**
     * 获取当前记录的所有字段
     * 
     * @return 不可修改的字段Map
     */
    public Map<String, Object> getRecord() {
        return Collections.unmodifiableMap(record);
    }
    
    /**
     * 设置上下文参数
     * 
     * @param key 参数键
     * @param value 参数值
     */
    public void setParameter(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter key cannot be null or empty");
        }
        parameters.put(key, value);
    }
    
    /**
     * 获取上下文参数
     * 
     * @param key 参数键
     * @return 参数值，如果不存在则返回null
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * 获取所有上下文参数
     * 
     * @return 不可修改的参数Map
     */
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }
    
    /**
     * 设置上下文变量
     * 
     * @param key 变量键
     * @param value 变量值
     */
    public void setVariable(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Variable key cannot be null or empty");
        }
        variables.put(key, value);
    }
    
    /**
     * 获取上下文变量
     * 
     * @param key 变量键
     * @return 变量值，如果不存在则返回null
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }
    
    /**
     * 获取所有上下文变量
     * 
     * @return 不可修改的变量Map
     */
    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
    
    /**
     * 检查字段是否存在
     * 
     * @param fieldName 字段名
     * @return 是否存在
     */
    public boolean hasField(String fieldName) {
        return record.containsKey(fieldName);
    }
    
    // 私有构造函数，强制使用工厂方法
    private CleansingContext(String dataAssetId, Map<String, Object> record) {
        this.dataAssetId = dataAssetId;
        this.record = record != null ? new HashMap<>(record) : new HashMap<>();
        this.parameters = new HashMap<>();
        this.variables = new HashMap<>();
    }
}
