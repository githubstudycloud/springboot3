package com.platform.scheduler.core.executor;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.scheduler.model.HttpTaskParams;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskResult;

/**
 * HTTP任务执行器
 * 
 * @author platform
 */
@Component
public class HttpTaskExecutor extends AbstractTaskExecutor {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    
    @Override
    public String getType() {
        return "HTTP";
    }
    
    @Override
    protected TaskResult doExecute(Task task, Long executionId) throws Exception {
        logger.info("执行HTTP任务: {}, 执行ID: {}", task.getId(), executionId);
        
        // 解析任务参数
        HttpTaskParams params = parseParams(task.getParameters());
        
        // 创建RestTemplate，配置超时
        RestTemplate restTemplate = createRestTemplate(params);
        
        // 构建请求URL
        URI uri = new URI(params.getUrl());
        
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        if (params.getHeaders() != null) {
            params.getHeaders().forEach(headers::add);
        }
        
        // 构建请求体
        HttpEntity<String> requestEntity = new HttpEntity<>(params.getBody(), headers);
        
        // 记录日志
        logger.info("HTTP请求: URL={}, Method={}", params.getUrl(), params.getMethod());
        updateProgress(task.getId(), executionId, 30, "正在发送HTTP请求");
        
        // 执行HTTP请求
        long startTime = System.currentTimeMillis();
        
        // 异步执行并注册运行任务
        Future<ResponseEntity<String>> future = taskExecutor.submit(() -> {
            return restTemplate.exchange(uri, HttpMethod.valueOf(params.getMethod()), requestEntity, String.class);
        });
        
        // 注册运行任务，用于中断
        registerRunningTask(executionId, future);
        
        // 等待结果（带超时）
        ResponseEntity<String> response;
        try {
            if (task.getTimeout() != null && task.getTimeout() > 0) {
                response = future.get(task.getTimeout(), TimeUnit.MILLISECONDS);
            } else {
                response = future.get();
            }
        } catch (Exception e) {
            updateProgress(task.getId(), executionId, 60, "HTTP请求异常: " + e.getMessage());
            throw e;
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 记录日志
        logger.info("HTTP响应: Status={}, Time={}ms", response.getStatusCodeValue(), duration);
        updateProgress(task.getId(), executionId, 90, "收到HTTP响应: " + response.getStatusCodeValue());
        
        // 构建结果
        TaskResult result = new TaskResult();
        result.setStatusCode(response.getStatusCodeValue());
        result.setBody(response.getBody());
        result.setDuration(duration);
        result.setHeaders(toStringMap(response.getHeaders().toSingleValueMap()));
        
        return result;
    }
    
    /**
     * 解析HTTP任务参数
     * 
     * @param parametersJson 参数JSON
     * @return HTTP任务参数
     * @throws Exception 解析异常
     */
    private HttpTaskParams parseParams(String parametersJson) throws Exception {
        try {
            return objectMapper.readValue(parametersJson, HttpTaskParams.class);
        } catch (Exception e) {
            logger.error("解析HTTP任务参数异常", e);
            throw new Exception("解析HTTP任务参数异常: " + e.getMessage());
        }
    }
    
    /**
     * 创建RestTemplate
     * 
     * @param params HTTP任务参数
     * @return RestTemplate
     */
    private RestTemplate createRestTemplate(HttpTaskParams params) {
        RestTemplate restTemplate = new RestTemplate();
        
        // 配置超时
        ClientHttpRequestFactory factory;
        if (params.getConnectTimeout() != null || params.getReadTimeout() != null) {
            int connectTimeout = params.getConnectTimeout() != null ? params.getConnectTimeout() : 5000;
            int readTimeout = params.getReadTimeout() != null ? params.getReadTimeout() : 30000;
            
            try {
                // 优先使用HttpComponentsClientHttpRequestFactory
                HttpComponentsClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory();
                httpFactory.setConnectTimeout(connectTimeout);
                httpFactory.setReadTimeout(readTimeout);
                factory = httpFactory;
            } catch (Error e) {
                // 降级使用SimpleClientHttpRequestFactory
                SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
                simpleFactory.setConnectTimeout(connectTimeout);
                simpleFactory.setReadTimeout(readTimeout);
                factory = simpleFactory;
            }
            
            restTemplate.setRequestFactory(factory);
        }
        
        return restTemplate;
    }
    
    /**
     * 转换Map值为字符串
     * 
     * @param map Map对象
     * @return 字符串Map
     */
    private Map<String, String> toStringMap(Map<String, ?> map) {
        Map<String, String> result = new java.util.HashMap<>();
        if (map != null) {
            map.forEach((key, value) -> result.put(key, value != null ? value.toString() : null));
        }
        return result;
    }
}
