package com.example.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * JSON工具类
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 注册Java8时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 禁止将日期序列化为时间戳
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Object to JSON failed: ", e);
            throw new RuntimeException("Object to JSON failed", e);
        }
    }

    /**
     * 对象转格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Object to pretty JSON failed: ", e);
            throw new RuntimeException("Object to pretty JSON failed", e);
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("JSON to Object failed: ", e);
            throw new RuntimeException("JSON to Object failed", e);
        }
    }

    /**
     * JSON字符串转复杂对象
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 复杂对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error("JSON to Object with TypeReference failed: ", e);
            throw new RuntimeException("JSON to Object with TypeReference failed", e);
        }
    }

    /**
     * 将对象转换为Map
     *
     * @param obj 对象
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        return fromJson(toJson(obj), Map.class);
    }
} 