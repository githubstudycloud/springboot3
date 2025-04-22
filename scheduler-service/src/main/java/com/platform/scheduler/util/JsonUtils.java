package com.platform.scheduler.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON工具类
 * 
 * @author platform
 */
public class JsonUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        // 设置序列化属性
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // 设置反序列化属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 对象转JSON字符串
     * 
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("对象转JSON字符串异常", e);
            return null;
        }
    }
    
    /**
     * 对象转美化的JSON字符串
     * 
     * @param object 对象
     * @return 美化的JSON字符串
     */
    public static String toPrettyJson(Object object) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("对象转美化的JSON字符串异常", e);
            return null;
        }
    }
    
    /**
     * JSON字符串转对象
     * 
     * @param <T> 对象类型
     * @param json JSON字符串
     * @param clazz 对象类
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("JSON字符串转对象异常", e);
            return null;
        }
    }
    
    /**
     * JSON字符串转对象
     * 
     * @param <T> 对象类型
     * @param json JSON字符串
     * @param typeReference 类型引用
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error("JSON字符串转对象异常", e);
            return null;
        }
    }
    
    /**
     * JSON字符串转List
     * 
     * @param <T> 对象类型
     * @param json JSON字符串
     * @param elementClass 元素类
     * @return List
     */
    public static <T> List<T> parseList(String json, Class<T> elementClass) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, elementClass);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            logger.error("JSON字符串转List异常", e);
            return null;
        }
    }
    
    /**
     * JSON字符串转Map
     * 
     * @param <K> 键类型
     * @param <V> 值类型
     * @param json JSON字符串
     * @param keyClass 键类
     * @param valueClass 值类
     * @return Map
     */
    public static <K, V> Map<K, V> parseMap(String json, Class<K> keyClass, Class<V> valueClass) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            logger.error("JSON字符串转Map异常", e);
            return null;
        }
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
     * 私有构造函数，防止实例化
     */
    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }
}
