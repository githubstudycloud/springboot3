package com.example.platform.collect.core.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 执行上下文模型
 * 在任务执行过程中维护状态和传递数据
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExecutionContext {
    
    /**
     * 上下文唯一标识
     */
    private String id;
    
    /**
     * 关联的任务ID
     */
    private String taskId;
    
    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 当前执行状态
     */
    private TaskStatus status;
    
    /**
     * 当前执行阶段
     */
    private String currentStage;
    
    /**
     * 执行参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 当前水印状态
     */
    private Map<String, Object> watermarks;
    
    /**
     * 执行过程中的临时数据
     */
    private Map<String, Object> temporaryData;
    
    /**
     * 执行统计信息
     */
    private Map<String, Object> statistics;
    
    /**
     * 错误记录
     */
    private List<ErrorRecord> errors;
    
    /**
     * 执行结果摘要
     */
    private Map<String, Object> resultSummary;
    
    /**
     * 构造函数
     */
    @Builder
    public ExecutionContext(String id, String taskId, Map<String, Object> parameters, 
                          Map<String, Object> watermarks) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.taskId = taskId;
        this.startTime = LocalDateTime.now();
        this.status = TaskStatus.RUNNING;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.watermarks = watermarks != null ? watermarks : new HashMap<>();
        this.temporaryData = new HashMap<>();
        this.statistics = new HashMap<>();
        this.errors = new ArrayList<>();
        this.resultSummary = new HashMap<>();
    }
    
    /**
     * 设置当前执行阶段
     *
     * @param stageName 阶段名称
     */
    public void setCurrentStage(String stageName) {
        this.currentStage = stageName;
    }
    
    /**
     * 更新执行状态
     *
     * @param status 新状态
     */
    public void updateStatus(TaskStatus status) {
        this.status = status;
        
        // 如果是终态，记录结束时间
        if (status.isTerminal()) {
            this.endTime = LocalDateTime.now();
        }
    }
    
    /**
     * 添加错误记录
     *
     * @param stage 发生错误的阶段
     * @param message 错误信息
     * @param exception 异常
     */
    public void addError(String stage, String message, Throwable exception) {
        ErrorRecord error = new ErrorRecord(stage, message, exception);
        this.errors.add(error);
    }
    
    /**
     * 更新水印
     *
     * @param fieldName 字段名
     * @param value 新值
     */
    public void updateWatermark(String fieldName, Object value) {
        this.watermarks.put(fieldName, value);
    }
    
    /**
     * 存储临时数据
     *
     * @param key 键
     * @param value 值
     */
    public void putTemporaryData(String key, Object value) {
        this.temporaryData.put(key, value);
    }
    
    /**
     * 获取临时数据
     *
     * @param key 键
     * @param <T> 数据类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getTemporaryData(String key) {
        return (T) this.temporaryData.get(key);
    }
    
    /**
     * 更新统计信息
     *
     * @param key 统计项
     * @param value 值
     */
    public void updateStatistics(String key, Object value) {
        this.statistics.put(key, value);
    }
    
    /**
     * 递增统计计数
     *
     * @param key 统计项
     * @param increment 增量
     */
    public void incrementStatistics(String key, long increment) {
        Object currentValue = this.statistics.getOrDefault(key, 0L);
        if (currentValue instanceof Number) {
            long newValue = ((Number) currentValue).longValue() + increment;
            this.statistics.put(key, newValue);
        } else {
            this.statistics.put(key, increment);
        }
    }
    
    /**
     * 获取执行持续时间（毫秒）
     *
     * @return 持续时间
     */
    public long getDurationMillis() {
        LocalDateTime end = this.endTime != null ? this.endTime : LocalDateTime.now();
        return java.time.Duration.between(this.startTime, end).toMillis();
    }
    
    /**
     * 设置结果摘要信息
     *
     * @param key 摘要项
     * @param value 值
     */
    public void setResultSummary(String key, Object value) {
        this.resultSummary.put(key, value);
    }
    
    /**
     * 完成执行
     */
    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * 标记执行失败
     *
     * @param errorMessage 错误信息
     */
    public void fail(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.endTime = LocalDateTime.now();
        addError(this.currentStage, errorMessage, null);
    }
    
    /**
     * 错误记录内部类
     * 记录执行过程中的错误信息
     */
    @Getter
    public static class ErrorRecord {
        /**
         * 发生错误的阶段
         */
        private final String stage;
        
        /**
         * 错误消息
         */
        private final String message;
        
        /**
         * 错误发生时间
         */
        private final LocalDateTime timestamp;
        
        /**
         * 异常堆栈
         */
        private final String stackTrace;
        
        /**
         * 构造函数
         */
        public ErrorRecord(String stage, String message, Throwable exception) {
            this.stage = stage;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.stackTrace = exception != null ? getStackTraceAsString(exception) : null;
        }
        
        /**
         * 将异常堆栈转换为字符串
         */
        private String getStackTraceAsString(Throwable throwable) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
