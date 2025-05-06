package com.platform.common.model;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统错误"),
    
    /**
     * 参数校验错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 业务错误
     */
    BUSINESS_ERROR(501, "业务处理失败"),
    
    /**
     * 未找到资源
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    /**
     * 请求超时
     */
    TIMEOUT(504, "请求超时"),
    
    /**
     * 请求频率超限
     */
    TOO_MANY_REQUESTS(429, "请求频率超限"),
    
    /**
     * 数据库操作失败
     */
    DATABASE_ERROR(502, "数据库操作失败"),
    
    /**
     * 远程服务调用失败
     */
    REMOTE_SERVICE_ERROR(503, "远程服务调用失败"),
    
    /**
     * 数据不一致
     */
    DATA_INCONSISTENCY(506, "数据不一致"),
    
    /**
     * 重复提交
     */
    DUPLICATE_SUBMISSION(507, "重复提交"),
    
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(508, "文件上传失败"),
    
    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_ERROR(509, "文件下载失败"),
    
    /**
     * 参数校验失败
     */
    VALIDATION_ERROR(400, "参数校验失败"),
    
    /**
     * 操作被拒绝
     */
    OPERATION_DENIED(403, "操作被拒绝"),
    
    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(409, "数据已存在"),
    
    /**
     * 会话过期
     */
    SESSION_EXPIRED(401, "会话已过期，请重新登录"),
    
    /**
     * 账号或密码错误
     */
    INVALID_CREDENTIALS(401, "账号或密码错误"),
    
    /**
     * 无效的token
     */
    INVALID_TOKEN(401, "无效的令牌"),
    
    /**
     * 缺少必要参数
     */
    MISSING_PARAMETER(400, "缺少必要参数");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态消息
     */
    private final String message;
    
    /**
     * 构造方法
     * 
     * @param code 状态码
     * @param message 状态消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
