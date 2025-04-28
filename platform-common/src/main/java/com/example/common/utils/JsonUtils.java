package com.example.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * JSON工具类（过渡用）
 *
 * @deprecated 请使用 {@link JsonUtil} 替代，此类将在下一个版本中移除
 */
@Deprecated
public class JsonUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private JsonUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 对象转JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     * @deprecated 请使用 {@link JsonUtil#toJson(Object)} 替代
     */
    @Deprecated
    public static String toJson(Object obj) {
        return JsonUtil.toJson(obj);
    }

    /**
     * 对象转格式化的JSON字符串
     *
     * @param obj 对象
     * @return 格式化的JSON字符串
     * @deprecated 请使用 {@link JsonUtil#toPrettyJson(Object)} 替代
     */
    @Deprecated
    public static String toPrettyJson(Object obj) {
        return JsonUtil.toPrettyJson(obj);
    }

    /**
     * JSON字符串转对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return 对象
     * @deprecated 请使用 {@link JsonUtil#fromJson(String, Class)} 替代
     */
    @Deprecated
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JsonUtil.fromJson(json, clazz);
    }

    /**
     * JSON字符串转复杂对象
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 复杂对象
     * @deprecated 请使用 {@link JsonUtil#fromJson(String, TypeReference)} 替代
     */
    @Deprecated
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return JsonUtil.fromJson(json, typeReference);
    }

    /**
     * 将对象转换为Map
     *
     * @param obj 对象
     * @return Map
     * @deprecated 请使用 {@link JsonUtil#objectToMap(Object)} 替代
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        return JsonUtil.objectToMap(obj);
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return ObjectMapper实例
     * @deprecated 请使用 {@link JsonUtil#getObjectMapper()} 替代
     */
    @Deprecated
    public static ObjectMapper getObjectMapper() {
        return JsonUtil.getObjectMapper();
    }
}