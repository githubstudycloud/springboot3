package com.platform.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.platform.common.exception.BusinessException;
import com.platform.common.model.ResultCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类，基于Jackson
 */
public class JsonUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    
    /**
     * ObjectMapper实例
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtil() {
        throw new AssertionError("No JsonUtil instances for you!");
    }
    
    static {
        // 配置ObjectMapper
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }
    
    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
    
    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Convert object to JSON string error", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "转换对象为JSON字符串失败", e);
        }
    }
    
    /**
     * 将对象转换为格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Convert object to pretty JSON string error", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "转换对象为格式化JSON字符串失败", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json      JSON字符串
     * @param valueType 目标类型
     * @param <T>       目标类型
     * @return 目标类型的对象
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, valueType);
        } catch (Exception e) {
            log.error("Parse JSON string to object error, JSON: {}, type: {}", json, valueType, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "解析JSON字符串失败", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json        JSON字符串
     * @param valueTypeRef 目标类型引用
     * @param <T>         目标类型
     * @return 目标类型的对象
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (Exception e) {
            log.error("Parse JSON string to object error, JSON: {}, type reference: {}", json, valueTypeRef, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "解析JSON字符串失败", e);
        }
    }
    
    /**
     * 将JSON字符串转换为JsonNode对象
     *
     * @param json JSON字符串
     * @return JsonNode对象
     */
    public static JsonNode parseNode(String json) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            log.error("Parse JSON string to JsonNode error, JSON: {}", json, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "解析JSON字符串为JsonNode失败", e);
        }
    }
    
    /**
     * 将JSON字符串转换为Map对象
     *
     * @param json JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> toMap(String json) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Parse JSON string to Map error, JSON: {}", json, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "解析JSON字符串为Map失败", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的List
     *
     * @param json      JSON字符串
     * @param valueType 目标元素类型
     * @param <T>       目标元素类型
     * @return 目标类型的List
     */
    public static <T> List<T> toList(String json, Class<T> valueType) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, valueType);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            log.error("Parse JSON string to List error, JSON: {}, element type: {}", json, valueType, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "解析JSON字符串为List失败", e);
        }
    }
    
    /**
     * 将对象转换为指定类型的对象
     *
     * @param fromObj   源对象
     * @param valueType 目标类型
     * @param <T>       目标类型
     * @return 目标类型的对象
     */
    public static <T> T convert(Object fromObj, Class<T> valueType) {
        if (fromObj == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.convertValue(fromObj, valueType);
        } catch (Exception e) {
            log.error("Convert object to different type error, from: {}, to type: {}", 
                    fromObj.getClass().getName(), valueType, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "转换对象类型失败", e);
        }
    }
    
    /**
     * 将对象转换为指定类型的对象
     *
     * @param fromObj     源对象
     * @param valueTypeRef 目标类型引用
     * @param <T>         目标类型
     * @return 目标类型的对象
     */
    public static <T> T convert(Object fromObj, TypeReference<T> valueTypeRef) {
        if (fromObj == null) {
            return null;
        }
        
        try {
            return OBJECT_MAPPER.convertValue(fromObj, valueTypeRef);
        } catch (Exception e) {
            log.error("Convert object to different type error, from: {}, to type reference: {}", 
                    fromObj.getClass().getName(), valueTypeRef, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "转换对象类型失败", e);
        }
    }
    
    /**
     * 克隆对象（通过序列化和反序列化实现深拷贝）
     *
     * @param obj  源对象
     * @param type 对象类型
     * @param <T>  对象类型
     * @return 克隆后的对象
     */
    public static <T> T clone(T obj, Class<T> type) {
        if (obj == null) {
            return null;
        }
        
        return fromJson(toJson(obj), type);
    }
}
