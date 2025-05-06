package com.platform.scheduler.infrastructure.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * JSON工具类
 * 提供对象与JSON字符串之间的转换功能
 *
 * @author platform
 */
public class JsonUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    
    private JsonUtils() {
        // 私有构造方法，防止实例化
    }
    
    /**
     * 将对象转换为JSON字符串
     *
     * @param object 待转换的对象
     * @return JSON字符串，转换失败则返回null
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to JSON failed", e);
            return null;
        }
    }
    
    /**
     * 将对象转换为格式化的JSON字符串
     *
     * @param object 待转换的对象
     * @return 格式化的JSON字符串，转换失败则返回null
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to pretty JSON failed", e);
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @return 转换后的对象，转换失败则返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Parse JSON to object failed", e);
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为Map
     *
     * @param json JSON字符串
     * @return 转换后的Map，转换失败则返回null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonToMap(String json) {
        return fromJson(json, Map.class);
    }
    
    /**
     * 将对象转换为Map
     *
     * @param object 待转换的对象
     * @return 转换后的Map，转换失败则返回null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> objectToMap(Object object) {
        return objectMapper.convertValue(object, Map.class);
    }
    
    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
