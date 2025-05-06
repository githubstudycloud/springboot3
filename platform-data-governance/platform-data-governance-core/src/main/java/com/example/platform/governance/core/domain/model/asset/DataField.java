package com.example.platform.governance.core.domain.model.asset;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据字段领域模型
 * 
 * 表示数据资产中的字段信息
 */
public class DataField {
    
    private final String code;
    private String name;
    private String description;
    private DataFieldType type;
    private boolean primaryKey;
    private boolean nullable;
    private String defaultValue;
    private int maxLength;
    private int precision;
    private int scale;
    private String format;
    private Map<String, String> properties;
    
    /**
     * 创建新的数据字段
     * 
     * @param code 字段编码
     * @param name 字段名称
     * @param type 字段类型
     * @return 新创建的数据字段实例
     */
    public static DataField create(String code, String name, DataFieldType type) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Field code cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Field type cannot be null");
        }
        
        DataField field = new DataField(code);
        field.name = name;
        field.type = type;
        field.nullable = true;
        field.primaryKey = false;
        field.properties = new HashMap<>();
        
        return field;
    }
    
    /**
     * 设置字段描述
     * 
     * @param description 描述信息
     * @return 当前字段实例，支持链式调用
     */
    public DataField withDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * 设置字段是否为主键
     * 
     * @param primaryKey 是否为主键
     * @return 当前字段实例，支持链式调用
     */
    public DataField withPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
        if (primaryKey) {
            this.nullable = false; // 主键不能为空
        }
        return this;
    }
    
    /**
     * 设置字段是否可为空
     * 
     * @param nullable 是否可为空
     * @return 当前字段实例，支持链式调用
     */
    public DataField withNullable(boolean nullable) {
        if (this.primaryKey && nullable) {
            throw new IllegalArgumentException("Primary key field cannot be nullable");
        }
        this.nullable = nullable;
        return this;
    }
    
    /**
     * 设置字段默认值
     * 
     * @param defaultValue 默认值
     * @return 当前字段实例，支持链式调用
     */
    public DataField withDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    /**
     * 设置字段最大长度
     * 
     * @param maxLength 最大长度
     * @return 当前字段实例，支持链式调用
     */
    public DataField withMaxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Max length cannot be negative");
        }
        this.maxLength = maxLength;
        return this;
    }
    
    /**
     * 设置数值精度
     * 
     * @param precision 精度（总位数）
     * @param scale 小数位数
     * @return 当前字段实例，支持链式调用
     */
    public DataField withPrecision(int precision, int scale) {
        if (precision <= 0) {
            throw new IllegalArgumentException("Precision must be positive");
        }
        if (scale < 0 || scale > precision) {
            throw new IllegalArgumentException("Scale must be non-negative and not greater than precision");
        }
        this.precision = precision;
        this.scale = scale;
        return this;
    }
    
    /**
     * 设置字段格式
     * 
     * @param format 格式（如日期格式、正则表达式等）
     * @return 当前字段实例，支持链式调用
     */
    public DataField withFormat(String format) {
        this.format = format;
        return this;
    }
    
    /**
     * 添加自定义属性
     * 
     * @param key 属性键
     * @param value 属性值
     * @return 当前字段实例，支持链式调用
     */
    public DataField withProperty(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }
        this.properties.put(key, value);
        return this;
    }
    
    // Getters
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public DataFieldType getType() {
        return type;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public boolean isNullable() {
        return nullable;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public int getMaxLength() {
        return maxLength;
    }
    
    public int getPrecision() {
        return precision;
    }
    
    public int getScale() {
        return scale;
    }
    
    public String getFormat() {
        return format;
    }
    
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    // equals, hashCode and toString
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataField dataField = (DataField) o;
        return Objects.equals(code, dataField.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return "DataField{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
    
    // 私有构造函数，强制使用工厂方法
    private DataField(String code) {
        this.code = code;
    }
}
