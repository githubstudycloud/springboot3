package com.example.common.utils;

import com.example.common.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 忽略未知属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许序列化空对象
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 使用Java8新的日期时间API
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 格式化日期输出
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 对象转JSON字符串
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
            log.error("将对象转为JSON字符串时发生错误", e);
            throw new SystemException("JSON序列化失败", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串
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
            log.error("将对象转为格式化JSON字符串时发生错误", e);
            throw new SystemException("JSON序列化失败", e);
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类
     * @param <T>   对象类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("将JSON字符串转为对象时发生错误", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON字符串转复杂对象
     *
     * @param json         JSON字符串
     * @param valueTypeRef 类型引用
     * @param <T>          对象类型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (IOException e) {
            log.error("将JSON字符串转为复杂对象时发生错误", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON字符串转Map
     *
     * @param json JSON字符串
     * @return Map
     */
    public static Map<String, Object> toMap(String json) {
        if (StringUtil.isBlank(json)) {
            return new HashMap<>(0);
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("将JSON字符串转为Map时发生错误", e);
            throw new SystemException("JSON反序列化失败", e);
        }
    }

    /**
     * 将对象转换为Map
     *
     * @param obj 对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> objectToMap(Object obj) {
        return fromJson(toJson(obj), Map.class);
    }

    /**
     * JSON字符串转List
     *
     * @param json  JSON字符串
     * @param clazz 对象类
     * @param <T>   对象类型
     * @return List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json)) {
            return new ArrayList<>(0);
        }
        try {
            return OBJECT_MAPPER.readValue(json, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            log.error("将JSON字符串转为List时发生错误", e);
            throw new SystemException("JSON反序列化失败", e);
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
}