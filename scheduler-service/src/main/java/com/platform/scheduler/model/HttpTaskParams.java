package com.platform.scheduler.model;

import java.util.Map;

import lombok.Data;

/**
 * HTTP任务参数类
 * 
 * @author platform
 */
@Data
public class HttpTaskParams {
    
    /**
     * 请求URL
     */
    private String url;
    
    /**
     * 请求方法(GET/POST/PUT/DELETE等)
     */
    private String method;
    
    /**
     * 请求头
     */
    private Map<String, String> headers;
    
    /**
     * 请求体
     */
    private String body;
    
    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout;
    
    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout;
    
    /**
     * 是否跟随重定向
     */
    private Boolean followRedirects;
    
    /**
     * 是否忽略SSL证书错误
     */
    private Boolean ignoreSSLErrors;
    
    /**
     * 代理服务器地址
     */
    private String proxyHost;
    
    /**
     * 代理服务器端口
     */
    private Integer proxyPort;
}
