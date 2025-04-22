package com.platform.scheduler.core.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务执行器工厂
 * 
 * @author platform
 */
@Component
public class TaskExecutorFactory {
    
    private final Map<String, TaskExecutor> executorMap = new HashMap<>();
    
    /**
     * 构造函数，注入所有任务执行器
     * 
     * @param executors 任务执行器列表
     */
    @Autowired
    public TaskExecutorFactory(List<TaskExecutor> executors) {
        for (TaskExecutor executor : executors) {
            executorMap.put(executor.getType(), executor);
        }
    }
    
    /**
     * 获取任务执行器
     * 
     * @param type 任务类型
     * @return 任务执行器
     * @throws IllegalArgumentException 如果找不到对应类型的执行器
     */
    public TaskExecutor getExecutor(String type) {
        TaskExecutor executor = executorMap.get(type);
        if (executor == null) {
            throw new IllegalArgumentException("找不到任务类型对应的执行器: " + type);
        }
        return executor;
    }
    
    /**
     * 判断是否支持指定类型的任务
     * 
     * @param type 任务类型
     * @return 是否支持
     */
    public boolean supportsType(String type) {
        return executorMap.containsKey(type);
    }
    
    /**
     * 获取所有支持的任务类型
     * 
     * @return 任务类型列表
     */
    public List<String> getSupportedTypes() {
        return new java.util.ArrayList<>(executorMap.keySet());
    }
}
