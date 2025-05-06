package com.example.platform.collect.core.domain.port;

import com.example.platform.collect.core.domain.model.ExecutionContext;

import java.util.Map;

/**
 * 加载器接口
 * 定义将处理后的数据持久化到目标存储的标准接口
 */
public interface Loader {
    
    /**
     * 获取加载器类型
     *
     * @return 加载器类型
     */
    String getType();
    
    /**
     * 初始化加载器
     *
     * @param config 加载器配置
     * @param context 执行上下文
     * @return 初始化结果
     */
    LoaderInitResult initialize(Map<String, Object> config, ExecutionContext context);
    
    /**
     * 加载单条数据
     *
     * @param data 数据
     * @param context 执行上下文
     * @return 加载结果
     */
    LoadResult load(Object data, ExecutionContext context);
    
    /**
     * 批量加载数据
     *
     * @param batch 数据批次
     * @param context 执行上下文
     * @return 加载结果
     */
    BatchLoadResult loadBatch(Object[] batch, ExecutionContext context);
    
    /**
     * 刷新缓冲区，确保数据持久化
     *
     * @param context 执行上下文
     * @return 刷新结果
     */
    FlushResult flush(ExecutionContext context);
    
    /**
     * 关闭加载器，释放资源
     *
     * @param context 执行上下文
     */
    void close(ExecutionContext context);
    
    /**
     * 验证配置是否有效
     *
     * @param config 加载器配置
     * @return 验证结果
     */
    ValidationResult validate(Map<String, Object> config);
    
    /**
     * 加载器初始化结果
     */
    class LoaderInitResult {
        private final boolean success;
        private final String message;
        
        public LoaderInitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static LoaderInitResult success() {
            return new LoaderInitResult(true, "Loader initialized successfully");
        }
        
        public static LoaderInitResult success(String message) {
            return new LoaderInitResult(true, message);
        }
        
        public static LoaderInitResult failure(String message) {
            return new LoaderInitResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * 单条数据加载结果
     */
    class LoadResult {
        private final boolean success;
        private final String message;
        private final String identifier;
        
        public LoadResult(boolean success, String message, String identifier) {
            this.success = success;
            this.message = message;
            this.identifier = identifier;
        }
        
        public static LoadResult success(String identifier) {
            return new LoadResult(true, "Data loaded successfully", identifier);
        }
        
        public static LoadResult success(String message, String identifier) {
            return new LoadResult(true, message, identifier);
        }
        
        public static LoadResult failure(String message) {
            return new LoadResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getIdentifier() {
            return identifier;
        }
    }
    
    /**
     * 批量数据加载结果
     */
    class BatchLoadResult {
        private final boolean success;
        private final String message;
        private final String[] identifiers;
        private final int successCount;
        private final int failedCount;
        
        public BatchLoadResult(boolean success, String message, String[] identifiers, 
                              int successCount, int failedCount) {
            this.success = success;
            this.message = message;
            this.identifiers = identifiers;
            this.successCount = successCount;
            this.failedCount = failedCount;
        }
        
        public static BatchLoadResult success(String[] identifiers, int successCount, int failedCount) {
            return new BatchLoadResult(true, "Batch loaded successfully", 
                                     identifiers, successCount, failedCount);
        }
        
        public static BatchLoadResult failure(String message) {
            return new BatchLoadResult(false, message, null, 0, 0);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String[] getIdentifiers() {
            return identifiers;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public int getTotalCount() {
            return successCount + failedCount;
        }
    }
    
    /**
     * 缓冲区刷新结果
     */
    class FlushResult {
        private final boolean success;
        private final String message;
        private final int flushedCount;
        
        public FlushResult(boolean success, String message, int flushedCount) {
            this.success = success;
            this.message = message;
            this.flushedCount = flushedCount;
        }
        
        public static FlushResult success(int flushedCount) {
            return new FlushResult(true, "Flush completed successfully", flushedCount);
        }
        
        public static FlushResult failure(String message) {
            return new FlushResult(false, message, 0);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getFlushedCount() {
            return flushedCount;
        }
    }
    
    /**
     * 验证结果
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, "Configuration is valid");
        }
        
        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
