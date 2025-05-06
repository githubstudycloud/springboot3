package com.example.platform.collect.core.domain.port;

import com.example.platform.collect.core.domain.model.DataSource;

import java.util.Map;

/**
 * 数据源连接器接口
 * 定义与各类数据源交互的标准接口
 */
public interface DataSourceConnector {
    
    /**
     * 获取连接器支持的数据源类型
     *
     * @return 数据源类型
     */
    String getType();
    
    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源配置
     * @return 连接结果
     */
    ConnectionResult testConnection(DataSource dataSource);
    
    /**
     * 连接数据源
     *
     * @param dataSource 数据源配置
     * @return 连接结果
     */
    ConnectionResult connect(DataSource dataSource);
    
    /**
     * 关闭连接
     */
    void disconnect();
    
    /**
     * 执行查询并返回结果
     *
     * @param query 查询条件
     * @param parameters 查询参数
     * @return 查询结果
     */
    FetchResult fetch(String query, Map<String, Object> parameters);
    
    /**
     * 流式获取数据
     *
     * @param query 查询条件
     * @param parameters 查询参数
     * @return 数据流迭代器
     */
    DataIterator stream(String query, Map<String, Object> parameters);
    
    /**
     * 获取数据源元数据
     *
     * @param dataSource 数据源配置
     * @return 元数据
     */
    MetadataResult getMetadata(DataSource dataSource);
    
    /**
     * 验证数据源配置是否有效
     *
     * @param dataSource 数据源配置
     * @return 验证结果
     */
    ValidationResult validateConfig(DataSource dataSource);
    
    /**
     * 连接结果类
     */
    class ConnectionResult {
        private final boolean success;
        private final String message;
        private final Object connectionHandle;
        
        public ConnectionResult(boolean success, String message, Object connectionHandle) {
            this.success = success;
            this.message = message;
            this.connectionHandle = connectionHandle;
        }
        
        public static ConnectionResult success(String message, Object connectionHandle) {
            return new ConnectionResult(true, message, connectionHandle);
        }
        
        public static ConnectionResult failure(String message) {
            return new ConnectionResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getConnectionHandle() {
            return connectionHandle;
        }
    }
    
    /**
     * 获取结果类
     */
    class FetchResult {
        private final boolean success;
        private final String message;
        private final Object data;
        private final int count;
        
        public FetchResult(boolean success, String message, Object data, int count) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.count = count;
        }
        
        public static FetchResult success(String message, Object data, int count) {
            return new FetchResult(true, message, data, count);
        }
        
        public static FetchResult failure(String message) {
            return new FetchResult(false, message, null, 0);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getData() {
            return data;
        }
        
        public int getCount() {
            return count;
        }
    }
    
    /**
     * 元数据结果类
     */
    class MetadataResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> metadata;
        
        public MetadataResult(boolean success, String message, Map<String, Object> metadata) {
            this.success = success;
            this.message = message;
            this.metadata = metadata;
        }
        
        public static MetadataResult success(String message, Map<String, Object> metadata) {
            return new MetadataResult(true, message, metadata);
        }
        
        public static MetadataResult failure(String message) {
            return new MetadataResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
    
    /**
     * 验证结果类
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        private final Map<String, String> validationErrors;
        
        public ValidationResult(boolean valid, String message, Map<String, String> validationErrors) {
            this.valid = valid;
            this.message = message;
            this.validationErrors = validationErrors;
        }
        
        public static ValidationResult valid(String message) {
            return new ValidationResult(true, message, null);
        }
        
        public static ValidationResult invalid(String message, Map<String, String> validationErrors) {
            return new ValidationResult(false, message, validationErrors);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Map<String, String> getValidationErrors() {
            return validationErrors;
        }
    }
    
    /**
     * 数据迭代器接口
     * 用于流式处理大量数据
     */
    interface DataIterator extends AutoCloseable {
        /**
         * 是否有下一条数据
         *
         * @return 是否有下一条
         */
        boolean hasNext();
        
        /**
         * 获取下一条数据
         *
         * @return 数据项
         */
        Object next();
        
        /**
         * 获取已处理的数据项数量
         *
         * @return 数据项数量
         */
        int getProcessedCount();
        
        /**
         * 关闭迭代器并释放资源
         */
        @Override
        void close();
    }
}
