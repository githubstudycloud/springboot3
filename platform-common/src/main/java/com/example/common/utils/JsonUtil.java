package com.example.common.utils;

import com.example.common.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonInclude;
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
public final class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * 创建默认配置的ObjectMapper实例
     *
     * @return 配置好的ObjectMapper实例
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许序列化空对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 使用Java8新的日期时间API
        objectMapper.registerModule(new JavaTimeModule());
        // 格式化日期输出
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 忽略null值
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * 私有构造函数，防止实例化
     * 修复构造函数异常问题
     */
    private JsonUtil() {
        // 空构造函数，不抛出异常
    }

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(final Object obj) {
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
    public static String toPrettyJson(final Object obj) {
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
    public static <T> T fromJson(final String json, final Class<T> clazz) {
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
    public static <T> T fromJson(final String json, final TypeReference<T> valueTypeRef) {
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
    public static Map<String, Object> toMap(final String json) {
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
    public static Map<String, Object> objectToMap(final Object obj) {
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
    public static <T> List<T> toList(final String json, final Class<T> clazz) {
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
     * 获取新的ObjectMapper实例（解决暴露内部表示问题）
     *
     * @return 新的ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        // 返回一个新的配置好的ObjectMapper实例，而不是内部静态实例
        return createObjectMapper();
    }
}