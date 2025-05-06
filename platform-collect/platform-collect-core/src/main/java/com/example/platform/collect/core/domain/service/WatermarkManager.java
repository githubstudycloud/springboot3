package com.example.platform.collect.core.domain.service;

import com.example.platform.collect.core.domain.model.WatermarkConfig;

import java.util.List;
import java.util.Map;

/**
 * 水印管理服务接口
 * 定义增量采集时水印的管理操作
 */
public interface WatermarkManager {
    
    /**
     * 获取任务的当前水印
     *
     * @param taskId 任务ID
     * @param config 水印配置
     * @return 水印值映射
     */
    Map<String, Object> getWatermarks(String taskId, WatermarkConfig config);
    
    /**
     * 更新任务的水印
     *
     * @param taskId 任务ID
     * @param fieldName 水印字段名
     * @param value 水印值
     */
    void updateWatermark(String taskId, String fieldName, Object value);
    
    /**
     * 批量更新任务的水印
     *
     * @param taskId 任务ID
     * @param watermarks 水印值映射
     */
    void updateWatermarks(String taskId, Map<String, Object> watermarks);
    
    /**
     * 保存任务的水印状态
     *
     * @param taskId 任务ID
     * @param watermarks 水印值映射
     */
    void saveWatermarks(String taskId, Map<String, Object> watermarks);
    
    /**
     * 清除任务的水印
     *
     * @param taskId 任务ID
     */
    void clearWatermarks(String taskId);
    
    /**
     * 检查是否应根据水印处理数据项
     *
     * @param taskId 任务ID
     * @param config 水印配置
     * @param dataItem 数据项
     * @return 是否应处理
     */
    boolean shouldProcess(String taskId, WatermarkConfig config, Object dataItem);
    
    /**
     * 获取增量查询条件
     *
     * @param taskId 任务ID
     * @param config 水印配置
     * @return 查询条件映射
     */
    Map<String, Object> getIncrementalQueryConditions(String taskId, WatermarkConfig config);
    
    /**
     * 从数据项中提取水印值
     *
     * @param config 水印配置
     * @param dataItem 数据项
     * @return 提取的水印值映射
     */
    Map<String, Object> extractWatermarkValues(WatermarkConfig config, Object dataItem);
    
    /**
     * 比较两个水印值
     *
     * @param config 水印配置字段
     * @param currentValue 当前值
     * @param storedValue 存储的值
     * @return 比较结果，大于0表示当前值更新，小于0表示存储值更新，等于0表示相等
     */
    int compareWatermarkValues(WatermarkConfig.WatermarkField config, Object currentValue, Object storedValue);
    
    /**
     * 获取水印历史
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @return 水印历史记录
     */
    List<WatermarkHistory> getWatermarkHistory(String taskId, int limit);
    
    /**
     * 回滚任务水印到指定版本
     *
     * @param taskId 任务ID
     * @param version 版本
     * @return 回滚结果
     */
    RollbackResult rollbackToVersion(String taskId, String version);
    
    /**
     * 水印历史记录类
     */
    class WatermarkHistory {
        private final String version;
        private final long timestamp;
        private final Map<String, Object> watermarks;
        private final String executionId;
        
        public WatermarkHistory(String version, long timestamp, Map<String, Object> watermarks, String executionId) {
            this.version = version;
            this.timestamp = timestamp;
            this.watermarks = watermarks;
            this.executionId = executionId;
        }
        
        public String getVersion() {
            return version;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public Map<String, Object> getWatermarks() {
            return watermarks;
        }
        
        public String getExecutionId() {
            return executionId;
        }
    }
    
    /**
     * 回滚结果类
     */
    class RollbackResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> previousWatermarks;
        private final Map<String, Object> currentWatermarks;
        
        public RollbackResult(boolean success, String message, 
                            Map<String, Object> previousWatermarks, 
                            Map<String, Object> currentWatermarks) {
            this.success = success;
            this.message = message;
            this.previousWatermarks = previousWatermarks;
            this.currentWatermarks = currentWatermarks;
        }
        
        public static RollbackResult success(String message, 
                                          Map<String, Object> previousWatermarks, 
                                          Map<String, Object> currentWatermarks) {
            return new RollbackResult(true, message, previousWatermarks, currentWatermarks);
        }
        
        public static RollbackResult failure(String message) {
            return new RollbackResult(false, message, null, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Map<String, Object> getPreviousWatermarks() {
            return previousWatermarks;
        }
        
        public Map<String, Object> getCurrentWatermarks() {
            return currentWatermarks;
        }
    }
}
