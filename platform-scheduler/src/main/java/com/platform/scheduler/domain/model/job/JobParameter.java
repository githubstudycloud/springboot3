package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.model.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 作业参数值对象
 * 定义作业执行所需的参数及其类型
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public class JobParameter implements ValueObject {
    
    /**
     * 参数名称
     */
    private final String name;
    
    /**
     * 参数值（字符串形式）
     */
    private final String value;
    
    /**
     * 参数数据类型
     */
    private final ParameterType type;
    
    /**
     * 是否必需
     */
    private final boolean required;
    
    /**
     * 参数描述
     */
    private final String description;
    
    public JobParameter(String name, String value, ParameterType type, boolean required, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter name cannot be null or empty");
        }
        this.name = name;
        this.value = value;
        this.type = type != null ? type : ParameterType.STRING;
        this.required = required;
        this.description = description;
    }
    
    /**
     * 创建简单参数，默认为字符串类型、非必需，无描述
     *
     * @param name 参数名称
     * @param value 参数值
     * @return 新的作业参数
     */
    public static JobParameter of(String name, String value) {
        return new JobParameter(name, value, ParameterType.STRING, false, null);
    }
    
    /**
     * 创建必需参数，默认为字符串类型，无描述
     *
     * @param name 参数名称
     * @param value 参数值
     * @return 新的作业参数
     */
    public static JobParameter required(String name, String value) {
        return new JobParameter(name, value, ParameterType.STRING, true, null);
    }
    
    /**
     * 参数类型枚举
     */
    public enum ParameterType {
        STRING("string", "字符串"),
        NUMBER("number", "数值"),
        BOOLEAN("boolean", "布尔值"),
        DATE("date", "日期"),
        DATETIME("datetime", "日期时间"),
        JSON("json", "JSON对象"),
        LIST("list", "列表");
        
        private final String code;
        private final String description;
        
        ParameterType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * 根据代码查找参数类型
         *
         * @param code 类型代码
         * @return 对应的参数类型，如果未找到则返回STRING类型
         */
        public static ParameterType fromCode(String code) {
            for (ParameterType type : ParameterType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return STRING;
        }
    }
}
