package com.example.common.model;

import com.example.common.constant.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 统一响应对象
 *
 * @author platform
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private Long timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    
    /**
     * 成功返回
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> success() {
        return R.<T>builder()
                .code(SystemConstants.SUCCESS_CODE)
                .message("操作成功")
                .build();
    }
    
    /**
     * 成功返回（带数据）
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> R<T> success(T data) {
        return R.<T>builder()
                .code(SystemConstants.SUCCESS_CODE)
                .message("操作成功")
                .data(data)
                .build();
    }
    
    /**
     * 成功返回（带消息和数据）
     *
     * @param message 消息
     * @param data    数据
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> R<T> success(String message, T data) {
        return R.<T>builder()
                .code(SystemConstants.SUCCESS_CODE)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * 失败返回
     *
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> R<T> error() {
        return R.<T>builder()
                .code(SystemConstants.ERROR_CODE)
                .message("操作失败")
                .build();
    }
    
    /**
     * 失败返回（带消息）
     *
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> R<T> error(String message) {
        return R.<T>builder()
                .code(SystemConstants.ERROR_CODE)
                .message(message)
                .build();
    }
    
    /**
     * 失败返回（带错误码和消息）
     *
     * @param code    错误码
     * @param message 消息
     * @param <T>     数据类型
     * @return 响应对象
     */
    public static <T> R<T> error(Integer code, String message) {
        return R.<T>builder()
                .code(code)
                .message(message)
                .build();
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
    public R<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}