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
 * 采集任务领域模型
 * 定义需要执行的数据采集任务、处理流程和执行参数
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectTask {
    
    /**
     * 任务唯一标识
     */
    private String id;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 关联的数据源ID
     */
    private String dataSourceId;
    
    /**
     * 采集模式
     */
    private CollectMode collectMode;
    
    /**
     * 采集流水线定义，包含一系列处理阶段
     */
    private List<PipelineStage> pipeline;
    
    /**
     * 任务参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 水印配置，用于增量采集
     */
    private WatermarkConfig watermarkConfig;
    
    /**
     * 错误处理策略
     */
    private ErrorHandlingStrategy errorHandlingStrategy;
    
    /**
     * 重试配置
     */
    private RetryConfig retryConfig;
    
    /**
     * 任务状态
     */
    private TaskStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人
     */
    private String createdBy;
    
    /**
     * 标签，用于分类和组织
     */
    private Map<String, String> tags;
    
    /**
     * 最大执行时间（秒），超过则超时
     */
    private int timeoutSeconds;
    
    /**
     * 构造函数
     */
    @Builder
    public CollectTask(String id, String name, String description, String dataSourceId,
                      CollectMode collectMode, List<PipelineStage> pipeline,
                      Map<String, Object> parameters, WatermarkConfig watermarkConfig,
                      ErrorHandlingStrategy errorHandlingStrategy, RetryConfig retryConfig,
                      String createdBy, Map<String, String> tags, Integer timeoutSeconds) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.dataSourceId = dataSourceId;
        this.collectMode = collectMode != null ? collectMode : CollectMode.FULL;
        this.pipeline = pipeline != null ? pipeline : new ArrayList<>();
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.watermarkConfig = watermarkConfig;
        this.errorHandlingStrategy = errorHandlingStrategy != null ? 
                                    errorHandlingStrategy : ErrorHandlingStrategy.FAIL_FAST;
        this.retryConfig = retryConfig != null ? retryConfig : new RetryConfig();
        this.status = TaskStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.createdBy = createdBy;
        this.tags = tags != null ? tags : new HashMap<>();
        this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : 3600; // 默认1小时
    }
    
    /**
     * 启用任务
     */
    public void enable() {
        if (this.status == TaskStatus.DISABLED) {
            this.status = TaskStatus.CREATED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 禁用任务
     */
    public void disable() {
        if (this.status != TaskStatus.DISABLED) {
            this.status = TaskStatus.DISABLED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 添加流水线阶段
     * 
     * @param stage 流水线阶段
     */
    public void addPipelineStage(PipelineStage stage) {
        if (this.pipeline == null) {
            this.pipeline = new ArrayList<>();
        }
        this.pipeline.add(stage);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新任务信息
     * 
     * @param name 名称
     * @param description 描述
     * @param collectMode 采集模式
     * @param parameters 参数
     * @param watermarkConfig 水印配置
     * @param errorHandlingStrategy 错误处理策略
     * @param retryConfig 重试配置
     * @param tags 标签
     * @param timeoutSeconds 超时时间
     */
    public void update(String name, String description, CollectMode collectMode,
                      Map<String, Object> parameters, WatermarkConfig watermarkConfig,
                      ErrorHandlingStrategy errorHandlingStrategy, RetryConfig retryConfig,
                      Map<String, String> tags, Integer timeoutSeconds) {
        this.name = name != null ? name : this.name;
        this.description = description != null ? description : this.description;
        this.collectMode = collectMode != null ? collectMode : this.collectMode;
        
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        
        this.watermarkConfig = watermarkConfig != null ? watermarkConfig : this.watermarkConfig;
        this.errorHandlingStrategy = errorHandlingStrategy != null ? 
                                    errorHandlingStrategy : this.errorHandlingStrategy;
        this.retryConfig = retryConfig != null ? retryConfig : this.retryConfig;
        
        if (tags != null) {
            this.tags.putAll(tags);
        }
        
        this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : this.timeoutSeconds;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 验证任务配置是否有效
     * 
     * @return 是否有效
     */
    public boolean validate() {
        // 基本验证：必填字段非空
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        if (dataSourceId == null || dataSourceId.trim().isEmpty()) {
            return false;
        }
        
        // 流水线不能为空
        if (pipeline == null || pipeline.isEmpty()) {
            return false;
        }
        
        // 流水线阶段验证
        for (PipelineStage stage : pipeline) {
            if (!stage.validate()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 获取参数值
     * 
     * @param key 参数键
     * @param <T> 参数类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) parameters.get(key);
    }
    
    /**
     * 设置参数值
     * 
     * @param key 参数键
     * @param value 参数值
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
        updatedAt = LocalDateTime.now();
    }
}
