package com.example.common.constant;

/**
 * 通用常量
 */
public interface CommonConstants {
    /**
     * 成功标记
     */
    String SUCCESS = "200";

    /**
     * 失败标记
     */
    String FAIL = "500";

    /**
     * 未授权
     */
    String UNAUTHORIZED = "401";

    /**
     * 禁止访问
     */
    String FORBIDDEN = "403";

    /**
     * 未找到
     */
    String NOT_FOUND = "404";

    /**
     * 请求方法不支持
     */
    String METHOD_NOT_ALLOWED = "405";

    /**
     * 服务器内部错误
     */
    String INTERNAL_SERVER_ERROR = "500";

    /**
     * 默认页码
     */
    int DEFAULT_PAGE_INDEX = 1;

    /**
     * 默认页大小
     */
    int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大页大小
     */
    int MAX_PAGE_SIZE = 100;
} 