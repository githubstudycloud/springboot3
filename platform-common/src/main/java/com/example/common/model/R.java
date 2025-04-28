package com.example.common.model;

import com.example.common.constant.SystemConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 统一响应对象
 *
 * @author platform
 * @since 1.0.0
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 时间戳
     */
    private Long timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

    /**
     * 无参构造函数
     */
    public R() {
    }

    /**
     * 带参构造函数
     *
     * @param code      响应码
     * @param message   响应消息
     * @param data      响应数据
     * @param traceId   追踪ID
     * @param timestamp 时间戳
     */
    public R(Integer code, String message, T data, String traceId, Long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    /**
     * 获取响应码
     *
     * @return 响应码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置响应码
     *
     * @param code 响应码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取追踪ID
     *
     * @return 追踪ID
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * 设置追踪ID
     *
     * @param traceId 追踪ID
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     *
     * @param timestamp 时间戳
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 成功返回
     *
     * @param <E> 数据类型
     * @return 响应对象
     */
    public static <E> R<E> success() {
        R<E> r = new R<>();
        r.setCode(SystemConstants.SUCCESS_CODE);
        r.setMessage("操作成功");
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 数据
     * @param <E>  数据类型
     * @return 响应对象
     */
    public static <E> R<E> success(final E data) {
        R<E> r = new R<>();
        r.setCode(SystemConstants.SUCCESS_CODE);
        r.setMessage("操作成功");
        r.setData(data);
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 成功返回（带消息和数据）
     *
     * @param message 消息
     * @param data    数据
     * @param <E>     数据类型
     * @return 响应对象
     */
    public static <E> R<E> success(final String message, final E data) {
        R<E> r = new R<>();
        r.setCode(SystemConstants.SUCCESS_CODE);
        r.setMessage(message);
        r.setData(data);
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 成功返回（带数据）- 别名方法
     *
     * @param data 数据
     * @param <E>  数据类型
     * @return 响应对象
     */
    public static <E> R<E> ok(final E data) {
        return success(data);
    }

    /**
     * 成功返回 - 别名方法
     *
     * @param <E> 数据类型
     * @return 响应对象
     */
    public static <E> R<E> ok() {
        return success();
    }

    /**
     * 成功返回（带消息和数据）- 别名方法
     *
     * @param message 消息
     * @param data    数据
     * @param <E>     数据类型
     * @return 响应对象
     */
    public static <E> R<E> ok(final String message, final E data) {
        return success(message, data);
    }

    /**
     * 失败返回
     *
     * @param <E> 数据类型
     * @return 响应对象
     */
    public static <E> R<E> error() {
        R<E> r = new R<>();
        r.setCode(SystemConstants.ERROR_CODE);
        r.setMessage("操作失败");
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 失败返回（带消息）
     *
     * @param message 消息
     * @param <E>     数据类型
     * @return 响应对象
     */
    public static <E> R<E> error(final String message) {
        R<E> r = new R<>();
        r.setCode(SystemConstants.ERROR_CODE);
        r.setMessage(message);
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 失败返回（带错误码和消息）
     *
     * @param code    错误码
     * @param message 消息
     * @param <E>     数据类型
     * @return 响应对象
     */
    public static <E> R<E> error(final Integer code, final String message) {
        R<E> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return r;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return SystemConstants.SUCCESS_CODE.equals(this.code);
    }

    /**
     * 设置追踪ID
     *
     * @param traceId 追踪ID
     * @return 响应对象
     */
    public R<T> withTraceId(final String traceId) {
        this.traceId = traceId;
        return this;
    }
}