package com.example.platform.collect.core.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 采集框架配置属性类
 * 定义框架的各种配置参数
 */
@ConfigurationProperties(prefix = "platform.collect")
public class CollectProperties {

    /**
     * 并发设置
     */
    private Concurrency concurrency = new Concurrency();

    /**
     * 水印配置
     */
    @NestedConfigurationProperty
    private Watermark watermark = new Watermark();

    /**
     * 调度系统配置
     */
    @NestedConfigurationProperty
    private Scheduler scheduler = new Scheduler();

    /**
     * 数据治理配置
     */
    @NestedConfigurationProperty
    private DataGovernance dataGovernance = new DataGovernance();

    /**
     * 连接器配置
     */
    @NestedConfigurationProperty
    private Connectors connectors = new Connectors();

    /**
     * 处理器配置
     */
    @NestedConfigurationProperty
    private Processors processors = new Processors();

    /**
     * 加载器配置
     */
    @NestedConfigurationProperty
    private Loaders loaders = new Loaders();

    /**
     * 缓存配置
     */
    @NestedConfigurationProperty
    private Cache cache = new Cache();

    /**
     * 监控配置
     */
    @NestedConfigurationProperty
    private Monitoring monitoring = new Monitoring();

    /**
     * 错误处理配置
     */
    @NestedConfigurationProperty
    private ErrorHandling errorHandling = new ErrorHandling();

    // Getters and Setters

    public Concurrency getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(Concurrency concurrency) {
        this.concurrency = concurrency;
    }

    public Watermark getWatermark() {
        return watermark;
    }

    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public DataGovernance getDataGovernance() {
        return dataGovernance;
    }

    public void setDataGovernance(DataGovernance dataGovernance) {
        this.dataGovernance = dataGovernance;
    }

    public Connectors getConnectors() {
        return connectors;
    }

    public void setConnectors(Connectors connectors) {
        this.connectors = connectors;
    }

    public Processors getProcessors() {
        return processors;
    }

    public void setProcessors(Processors processors) {
        this.processors = processors;
    }

    public Loaders getLoaders() {
        return loaders;
    }

    public void setLoaders(Loaders loaders) {
        this.loaders = loaders;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    public ErrorHandling getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(ErrorHandling errorHandling) {
        this.errorHandling = errorHandling;
    }

    /**
     * 并发配置
     */
    public static class Concurrency {
        /**
         * 默认核心线程数
         */
        private int corePoolSize = 5;
        
        /**
         * 最大线程数
         */
        private int maxPoolSize = 20;
        
        /**
         * 线程空闲时间（秒）
         */
        private int keepAliveSeconds = 60;
        
        /**
         * 队列容量
         */
        private int queueCapacity = 100;
        
        /**
         * 是否允许核心线程超时
         */
        private boolean allowCoreThreadTimeout = false;
        
        /**
         * 线程名称前缀
         */
        private String threadNamePrefix = "collect-thread-";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public boolean isAllowCoreThreadTimeout() {
            return allowCoreThreadTimeout;
        }

        public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
            this.allowCoreThreadTimeout = allowCoreThreadTimeout;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    /**
     * 水印配置
     */
    public static class Watermark {
        /**
         * 存储类型 (redis, database, memory)
         */
        private String storageType = "redis";
        
        /**
         * Redis键前缀
         */
        private String redisKeyPrefix = "collect:watermark:";
        
        /**
         * 水印历史记录数量
         */
        private int historySize = 10;
        
        /**
         * 水印更新策略
         */
        private String updateStrategy = "AFTER_BATCH";
        
        /**
         * 是否启用版本控制
         */
        private boolean versioningEnabled = true;
        
        /**
         * 时间戳格式
         */
        private String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

        public String getStorageType() {
            return storageType;
        }

        public void setStorageType(String storageType) {
            this.storageType = storageType;
        }

        public String getRedisKeyPrefix() {
            return redisKeyPrefix;
        }

        public void setRedisKeyPrefix(String redisKeyPrefix) {
            this.redisKeyPrefix = redisKeyPrefix;
        }

        public int getHistorySize() {
            return historySize;
        }

        public void setHistorySize(int historySize) {
            this.historySize = historySize;
        }

        public String getUpdateStrategy() {
            return updateStrategy;
        }

        public void setUpdateStrategy(String updateStrategy) {
            this.updateStrategy = updateStrategy;
        }

        public boolean isVersioningEnabled() {
            return versioningEnabled;
        }

        public void setVersioningEnabled(boolean versioningEnabled) {
            this.versioningEnabled = versioningEnabled;
        }

        public String getTimestampFormat() {
            return timestampFormat;
        }

        public void setTimestampFormat(String timestampFormat) {
            this.timestampFormat = timestampFormat;
        }
    }

    /**
     * 调度系统配置
     */
    public static class Scheduler {
        /**
         * 调度系统基础URL
         */
        private String baseUrl = "http://localhost:8080/scheduler";
        
        /**
         * 是否启用调度集成
         */
        private boolean enabled = true;
        
        /**
         * 调度任务组名
         */
        private String taskGroup = "COLLECT";
        
        /**
         * 任务超时时间（秒）
         */
        private int defaultTimeoutSeconds = 3600;
        
        /**
         * 默认重试次数
         */
        private int defaultRetryCount = 3;
        
        /**
         * 默认重试间隔（毫秒）
         */
        private long defaultRetryInterval = 5000;
        
        /**
         * 默认时区
         */
        private String defaultTimezone = "Asia/Shanghai";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTaskGroup() {
            return taskGroup;
        }

        public void setTaskGroup(String taskGroup) {
            this.taskGroup = taskGroup;
        }

        public int getDefaultTimeoutSeconds() {
            return defaultTimeoutSeconds;
        }

        public void setDefaultTimeoutSeconds(int defaultTimeoutSeconds) {
            this.defaultTimeoutSeconds = defaultTimeoutSeconds;
        }

        public int getDefaultRetryCount() {
            return defaultRetryCount;
        }

        public void setDefaultRetryCount(int defaultRetryCount) {
            this.defaultRetryCount = defaultRetryCount;
        }

        public long getDefaultRetryInterval() {
            return defaultRetryInterval;
        }

        public void setDefaultRetryInterval(long defaultRetryInterval) {
            this.defaultRetryInterval = defaultRetryInterval;
        }

        public String getDefaultTimezone() {
            return defaultTimezone;
        }

        public void setDefaultTimezone(String defaultTimezone) {
            this.defaultTimezone = defaultTimezone;
        }
    }

    /**
     * 数据治理配置
     */
    public static class DataGovernance {
        /**
         * 数据治理系统基础URL
         */
        private String baseUrl = "http://localhost:8080/governance";
        
        /**
         * 是否启用数据治理集成
         */
        private boolean enabled = true;
        
        /**
         * 是否自动注册元数据
         */
        private boolean autoRegisterMetadata = true;
        
        /**
         * 是否记录数据血缘
         */
        private boolean recordLineage = true;
        
        /**
         * 默认清洗规则集ID
         */
        private String defaultCleansingRuleSetId = "";
        
        /**
         * 默认质量规则集ID
         */
        private String defaultQualityRuleSetId = "";
        
        /**
         * 质量检查失败是否中断处理
         */
        private boolean failOnQualityIssues = false;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAutoRegisterMetadata() {
            return autoRegisterMetadata;
        }

        public void setAutoRegisterMetadata(boolean autoRegisterMetadata) {
            this.autoRegisterMetadata = autoRegisterMetadata;
        }

        public boolean isRecordLineage() {
            return recordLineage;
        }

        public void setRecordLineage(boolean recordLineage) {
            this.recordLineage = recordLineage;
        }

        public String getDefaultCleansingRuleSetId() {
            return defaultCleansingRuleSetId;
        }

        public void setDefaultCleansingRuleSetId(String defaultCleansingRuleSetId) {
            this.defaultCleansingRuleSetId = defaultCleansingRuleSetId;
        }

        public String getDefaultQualityRuleSetId() {
            return defaultQualityRuleSetId;
        }

        public void setDefaultQualityRuleSetId(String defaultQualityRuleSetId) {
            this.defaultQualityRuleSetId = defaultQualityRuleSetId;
        }

        public boolean isFailOnQualityIssues() {
            return failOnQualityIssues;
        }

        public void setFailOnQualityIssues(boolean failOnQualityIssues) {
            this.failOnQualityIssues = failOnQualityIssues;
        }
    }

    /**
     * 连接器配置
     */
    public static class Connectors {
        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeout = 30000;
        
        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 30000;
        
        /**
         * 最大连接数
         */
        private int maxConnections = 100;
        
        /**
         * 每个主机最大连接数
         */
        private int maxConnectionsPerHost = 20;
        
        /**
         * 空闲连接超时时间（毫秒）
         */
        private int idleTimeout = 60000;
        
        /**
         * HTTP请求重试次数
         */
        private int httpRetries = 3;
        
        /**
         * 数据库连接池大小
         */
        private int dbPoolSize = 10;
        
        /**
         * 是否使用连接池
         */
        private boolean useConnectionPool = true;

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getMaxConnectionsPerHost() {
            return maxConnectionsPerHost;
        }

        public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
            this.maxConnectionsPerHost = maxConnectionsPerHost;
        }

        public int getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public int getHttpRetries() {
            return httpRetries;
        }

        public void setHttpRetries(int httpRetries) {
            this.httpRetries = httpRetries;
        }

        public int getDbPoolSize() {
            return dbPoolSize;
        }

        public void setDbPoolSize(int dbPoolSize) {
            this.dbPoolSize = dbPoolSize;
        }

        public boolean isUseConnectionPool() {
            return useConnectionPool;
        }

        public void setUseConnectionPool(boolean useConnectionPool) {
            this.useConnectionPool = useConnectionPool;
        }
    }

    /**
     * 处理器配置
     */
    public static class Processors {
        /**
         * 批处理大小
         */
        private int batchSize = 200;
        
        /**
         * 默认处理器超时时间（秒）
         */
        private int defaultTimeoutSeconds = 300;
        
        /**
         * 是否并行处理
         */
        private boolean parallelProcessing = true;
        
        /**
         * 插件扫描包路径
         */
        private String pluginScanPackages = "com.example.platform.collect.processors";

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getDefaultTimeoutSeconds() {
            return defaultTimeoutSeconds;
        }

        public void setDefaultTimeoutSeconds(int defaultTimeoutSeconds) {
            this.defaultTimeoutSeconds = defaultTimeoutSeconds;
        }

        public boolean isParallelProcessing() {
            return parallelProcessing;
        }

        public void setParallelProcessing(boolean parallelProcessing) {
            this.parallelProcessing = parallelProcessing;
        }

        public String getPluginScanPackages() {
            return pluginScanPackages;
        }

        public void setPluginScanPackages(String pluginScanPackages) {
            this.pluginScanPackages = pluginScanPackages;
        }
    }

    /**
     * 加载器配置
     */
    public static class Loaders {
        /**
         * 批处理大小
         */
        private int batchSize = 100;
        
        /**
         * 提交间隔（毫秒）
         */
        private int commitInterval = 5000;
        
        /**
         * 是否并行加载
         */
        private boolean parallelLoading = false;
        
        /**
         * 加载超时时间（秒）
         */
        private int loadTimeoutSeconds = 600;
        
        /**
         * 插件扫描包路径
         */
        private String pluginScanPackages = "com.example.platform.collect.loaders";

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getCommitInterval() {
            return commitInterval;
        }

        public void setCommitInterval(int commitInterval) {
            this.commitInterval = commitInterval;
        }

        public boolean isParallelLoading() {
            return parallelLoading;
        }

        public void setParallelLoading(boolean parallelLoading) {
            this.parallelLoading = parallelLoading;
        }

        public int getLoadTimeoutSeconds() {
            return loadTimeoutSeconds;
        }

        public void setLoadTimeoutSeconds(int loadTimeoutSeconds) {
            this.loadTimeoutSeconds = loadTimeoutSeconds;
        }

        public String getPluginScanPackages() {
            return pluginScanPackages;
        }

        public void setPluginScanPackages(String pluginScanPackages) {
            this.pluginScanPackages = pluginScanPackages;
        }
    }

    /**
     * 缓存配置
     */
    public static class Cache {
        /**
         * 是否启用缓存
         */
        private boolean enabled = true;
        
        /**
         * 缓存类型 (redis, caffeine, none)
         */
        private String type = "caffeine";
        
        /**
         * 缓存过期时间（秒）
         */
        private int expireAfterSeconds = 3600;
        
        /**
         * 最大缓存条目数
         */
        private int maximumSize = 10000;
        
        /**
         * Redis键前缀
         */
        private String redisKeyPrefix = "collect:cache:";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getExpireAfterSeconds() {
            return expireAfterSeconds;
        }

        public void setExpireAfterSeconds(int expireAfterSeconds) {
            this.expireAfterSeconds = expireAfterSeconds;
        }

        public int getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
        }

        public String getRedisKeyPrefix() {
            return redisKeyPrefix;
        }

        public void setRedisKeyPrefix(String redisKeyPrefix) {
            this.redisKeyPrefix = redisKeyPrefix;
        }
    }

    /**
     * 监控配置
     */
    public static class Monitoring {
        /**
         * 是否启用指标收集
         */
        private boolean metricsEnabled = true;
        
        /**
         * 是否启用跟踪
         */
        private boolean tracingEnabled = true;
        
        /**
         * 是否启用健康检查
         */
        private boolean healthCheckEnabled = true;
        
        /**
         * 指标前缀
         */
        private String metricsPrefix = "platform.collect";
        
        /**
         * 是否暴露Prometheus端点
         */
        private boolean exposePrometheusEndpoint = true;

        public boolean isMetricsEnabled() {
            return metricsEnabled;
        }

        public void setMetricsEnabled(boolean metricsEnabled) {
            this.metricsEnabled = metricsEnabled;
        }

        public boolean isTracingEnabled() {
            return tracingEnabled;
        }

        public void setTracingEnabled(boolean tracingEnabled) {
            this.tracingEnabled = tracingEnabled;
        }

        public boolean isHealthCheckEnabled() {
            return healthCheckEnabled;
        }

        public void setHealthCheckEnabled(boolean healthCheckEnabled) {
            this.healthCheckEnabled = healthCheckEnabled;
        }

        public String getMetricsPrefix() {
            return metricsPrefix;
        }

        public void setMetricsPrefix(String metricsPrefix) {
            this.metricsPrefix = metricsPrefix;
        }

        public boolean isExposePrometheusEndpoint() {
            return exposePrometheusEndpoint;
        }

        public void setExposePrometheusEndpoint(boolean exposePrometheusEndpoint) {
            this.exposePrometheusEndpoint = exposePrometheusEndpoint;
        }
    }

    /**
     * 错误处理配置
     */
    public static class ErrorHandling {
        /**
         * 默认错误处理策略
         */
        private String defaultStrategy = "RETRY_THEN_CONTINUE";
        
        /**
         * 默认最大重试次数
         */
        private int defaultMaxRetries = 3;
        
        /**
         * 默认重试间隔（毫秒）
         */
        private long defaultRetryInterval = 1000;
        
        /**
         * 是否使用指数退避策略
         */
        private boolean useExponentialBackoff = true;
        
        /**
         * 最大退避间隔（毫秒）
         */
        private long maxBackoffInterval = 30000;
        
        /**
         * 是否保留失败记录
         */
        private boolean keepFailedRecords = true;
        
        /**
         * 失败记录存储路径
         */
        private String failedRecordsPath = "./failed_records";

        public String getDefaultStrategy() {
            return defaultStrategy;
        }

        public void setDefaultStrategy(String defaultStrategy) {
            this.defaultStrategy = defaultStrategy;
        }

        public int getDefaultMaxRetries() {
            return defaultMaxRetries;
        }

        public void setDefaultMaxRetries(int defaultMaxRetries) {
            this.defaultMaxRetries = defaultMaxRetries;
        }

        public long getDefaultRetryInterval() {
            return defaultRetryInterval;
        }

        public void setDefaultRetryInterval(long defaultRetryInterval) {
            this.defaultRetryInterval = defaultRetryInterval;
        }

        public boolean isUseExponentialBackoff() {
            return useExponentialBackoff;
        }

        public void setUseExponentialBackoff(boolean useExponentialBackoff) {
            this.useExponentialBackoff = useExponentialBackoff;
        }

        public long getMaxBackoffInterval() {
            return maxBackoffInterval;
        }

        public void setMaxBackoffInterval(long maxBackoffInterval) {
            this.maxBackoffInterval = maxBackoffInterval;
        }

        public boolean isKeepFailedRecords() {
            return keepFailedRecords;
        }

        public void setKeepFailedRecords(boolean keepFailedRecords) {
            this.keepFailedRecords = keepFailedRecords;
        }

        public String getFailedRecordsPath() {
            return failedRecordsPath;
        }

        public void setFailedRecordsPath(String failedRecordsPath) {
            this.failedRecordsPath = failedRecordsPath;
        }
    }
}
