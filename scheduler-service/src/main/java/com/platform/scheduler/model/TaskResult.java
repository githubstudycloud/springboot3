package com.platform.scheduler.model;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务执行结果实体类
 * 
 * @author platform
 */
@Data
@NoArgsConstructor
public class TaskResult {
    
    /**
     * 状态码
     */
    private Integer statusCode;
    
    /**
     * 响应体
     */
    private String body;
    
    /**
     * 执行时长（毫秒）
     */
    private Long duration;
    
    /**
     * 响应头
     */
    private Map<String, String> headers;
    
    /**
     * 构造函数
     * 
     * @param statusCode 状态码
     * @param body 响应体
     */
    public TaskResult(Integer statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }
}
