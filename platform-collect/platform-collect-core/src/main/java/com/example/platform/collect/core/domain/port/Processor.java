package com.example.platform.collect.core.domain.port;

import com.example.platform.collect.core.domain.model.ExecutionContext;
import com.example.platform.collect.core.domain.model.PipelineStage;

/**
 * 处理器接口
 * 定义数据处理的标准接口
 */
public interface Processor {
    
    /**
     * 获取处理器类型
     *
     * @return 处理器类型
     */
    String getType();
    
    /**
     * 初始化处理器
     *
     * @param stage 流水线阶段配置
     * @param context 执行上下文
     * @return 初始化结果
     */
    ProcessorInitResult initialize(PipelineStage stage, ExecutionContext context);
    
    /**
     * 处理单条数据
     *
     * @param data 输入数据
     * @param context 执行上下文
     * @return 处理结果
     */
    ProcessResult process(Object data, ExecutionContext context);
    
    /**
     * 批量处理数据
     *
     * @param batch 输入数据批次
     * @param context 执行上下文
     * @return 处理结果
     */
    BatchProcessResult processBatch(Object[] batch, ExecutionContext context);
    
    /**
     * 关闭处理器，释放资源
     *
     * @param context 执行上下文
     */
    void close(ExecutionContext context);
    
    /**
     * 验证配置是否有效
     *
     * @param stage 流水线阶段配置
     * @return 验证结果
     */
    ValidationResult validate(PipelineStage stage);
    
    /**
     * 处理器初始化结果
     */
    class ProcessorInitResult {
        private final boolean success;
        private final String message;
        
        public ProcessorInitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static ProcessorInitResult success() {
            return new ProcessorInitResult(true, "Processor initialized successfully");
        }
        
        public static ProcessorInitResult success(String message) {
            return new ProcessorInitResult(true, message);
        }
        
        public static ProcessorInitResult failure(String message) {
            return new ProcessorInitResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * 单条数据处理结果
     */
    class ProcessResult {
        private final boolean success;
        private final String message;
        private final Object result;
        private final boolean filtered;
        
        public ProcessResult(boolean success, String message, Object result, boolean filtered) {
            this.success = success;
            this.message = message;
            this.result = result;
            this.filtered = filtered;
        }
        
        public static ProcessResult success(Object result) {
            return new ProcessResult(true, "Processing successful", result, false);
        }
        
        public static ProcessResult filtered(String reason) {
            return new ProcessResult(true, reason, null, true);
        }
        
        public static ProcessResult failure(String message) {
            return new ProcessResult(false, message, null, false);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getResult() {
            return result;
        }
        
        public boolean isFiltered() {
            return filtered;
        }
    }
    
    /**
     * 批量数据处理结果
     */
    class BatchProcessResult {
        private final boolean success;
        private final String message;
        private final Object[] results;
        private final int successCount;
        private final int filteredCount;
        private final int failedCount;
        
        public BatchProcessResult(boolean success, String message, Object[] results, 
                                 int successCount, int filteredCount, int failedCount) {
            this.success = success;
            this.message = message;
            this.results = results;
            this.successCount = successCount;
            this.filteredCount = filteredCount;
            this.failedCount = failedCount;
        }
        
        public static BatchProcessResult success(Object[] results, int successCount, 
                                               int filteredCount, int failedCount) {
            return new BatchProcessResult(true, "Batch processing successful", 
                                        results, successCount, filteredCount, failedCount);
        }
        
        public static BatchProcessResult failure(String message) {
            return new BatchProcessResult(false, message, null, 0, 0, 0);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object[] getResults() {
            return results;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFilteredCount() {
            return filteredCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public int getTotalCount() {
            return successCount + filteredCount + failedCount;
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
