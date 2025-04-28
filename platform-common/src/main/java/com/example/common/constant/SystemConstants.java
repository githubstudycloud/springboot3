package com.example.common.constant;

/**
 * 系统常量
 *
 * @author platform
 * @since 1.0.0
 */
public interface SystemConstants {
    /**
     * 系统默认字符集
     */
    String DEFAULT_CHARSET = "UTF-8";

    /**
     * 成功响应码
     */
    Integer SUCCESS_CODE = 200;

    /**
     * 失败响应码
     */
    Integer ERROR_CODE = 500;

    /**
     * 未授权响应码
     */
    Integer UNAUTHORIZED_CODE = 401;

    /**
     * 禁止访问响应码
     */
    Integer FORBIDDEN_CODE = 403;

    /**
     * 资源不存在响应码
     */
    Integer NOT_FOUND_CODE = 404;

    /**
     * 服务不可用响应码
     */
    Integer SERVICE_UNAVAILABLE_CODE = 503;

    /**
     * 请求参数错误响应码
     */
    Integer BAD_REQUEST_CODE = 400;

    /**
     * 分页默认页码
     */
    Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 分页默认每页条数
     */
    Integer DEFAULT_PAGE_SIZE = 20;

    /**
     * 分页最大每页条数
     */
    Integer MAX_PAGE_SIZE = 100;

    /**
     * 默认排序字段
     */
    String DEFAULT_SORT_FIELD = "createTime";

    /**
     * 默认排序方向
     */
    String DEFAULT_SORT_DIRECTION = "desc";

    /**
     * 请求跟踪ID KEY
     */
    String TRACE_ID_KEY = "X-Trace-Id";
}