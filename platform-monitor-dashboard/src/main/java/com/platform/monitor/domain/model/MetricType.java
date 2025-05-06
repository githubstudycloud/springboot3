package com.platform.monitor.domain.model;

/**
 * 指标类型枚举
 */
public enum MetricType {
    
    /**
     * 系统CPU使用率
     */
    SYSTEM_CPU_USAGE("system.cpu.usage", "系统CPU使用率", MetricUnit.PERCENTAGE),
    
    /**
     * 进程CPU使用率
     */
    PROCESS_CPU_USAGE("process.cpu.usage", "进程CPU使用率", MetricUnit.PERCENTAGE),
    
    /**
     * 系统内存总量
     */
    SYSTEM_MEMORY_TOTAL("system.memory.total", "系统内存总量", MetricUnit.BYTES),
    
    /**
     * 系统内存已用
     */
    SYSTEM_MEMORY_USED("system.memory.used", "系统内存已用", MetricUnit.BYTES),
    
    /**
     * 系统内存可用
     */
    SYSTEM_MEMORY_FREE("system.memory.free", "系统内存可用", MetricUnit.BYTES),
    
    /**
     * 进程内存最大值
     */
    JVM_MEMORY_MAX("jvm.memory.max", "JVM内存最大值", MetricUnit.BYTES),
    
    /**
     * 进程内存已提交
     */
    JVM_MEMORY_COMMITTED("jvm.memory.committed", "JVM内存已提交", MetricUnit.BYTES),
    
    /**
     * 进程内存已用
     */
    JVM_MEMORY_USED("jvm.memory.used", "JVM内存已用", MetricUnit.BYTES),
    
    /**
     * 磁盘总空间
     */
    DISK_TOTAL("disk.total", "磁盘总空间", MetricUnit.BYTES),
    
    /**
     * 磁盘已用空间
     */
    DISK_USED("disk.used", "磁盘已用空间", MetricUnit.BYTES),
    
    /**
     * 磁盘可用空间
     */
    DISK_FREE("disk.free", "磁盘可用空间", MetricUnit.BYTES),
    
    /**
     * HTTP请求总数
     */
    HTTP_SERVER_REQUESTS_COUNT("http.server.requests.count", "HTTP请求总数", MetricUnit.COUNT),
    
    /**
     * HTTP错误请求数
     */
    HTTP_SERVER_ERRORS_COUNT("http.server.errors.count", "HTTP错误请求数", MetricUnit.COUNT),
    
    /**
     * HTTP请求平均响应时间
     */
    HTTP_SERVER_REQUESTS_AVG_TIME("http.server.requests.avg.time", "HTTP请求平均响应时间", MetricUnit.MILLISECONDS),
    
    /**
     * HTTP请求最大响应时间
     */
    HTTP_SERVER_REQUESTS_MAX_TIME("http.server.requests.max.time", "HTTP请求最大响应时间", MetricUnit.MILLISECONDS),
    
    /**
     * 数据库连接池活跃连接数
     */
    JDBC_CONNECTIONS_ACTIVE("jdbc.connections.active", "数据库活跃连接数", MetricUnit.COUNT),
    
    /**
     * 数据库连接池最大连接数
     */
    JDBC_CONNECTIONS_MAX("jdbc.connections.max", "数据库最大连接数", MetricUnit.COUNT),
    
    /**
     * 数据库连接池最小连接数
     */
    JDBC_CONNECTIONS_MIN("jdbc.connections.min", "数据库最小连接数", MetricUnit.COUNT),
    
    /**
     * 缓存命中率
     */
    CACHE_HIT_RATIO("cache.hit.ratio", "缓存命中率", MetricUnit.PERCENTAGE),
    
    /**
     * 缓存大小
     */
    CACHE_SIZE("cache.size", "缓存大小", MetricUnit.COUNT),
    
    /**
     * 线程池活跃线程数
     */
    THREAD_POOL_ACTIVE_COUNT("thread.pool.active.count", "线程池活跃线程数", MetricUnit.COUNT),
    
    /**
     * 线程池最大线程数
     */
    THREAD_POOL_MAX_COUNT("thread.pool.max.count", "线程池最大线程数", MetricUnit.COUNT),
    
    /**
     * 线程池队列大小
     */
    THREAD_POOL_QUEUE_SIZE("thread.pool.queue.size", "线程池队列大小", MetricUnit.COUNT),
    
    /**
     * 消息队列消费者数量
     */
    QUEUE_CONSUMER_COUNT("queue.consumer.count", "消息队列消费者数量", MetricUnit.COUNT),
    
    /**
     * 消息队列生产者数量
     */
    QUEUE_PRODUCER_COUNT("queue.producer.count", "消息队列生产者数量", MetricUnit.COUNT),
    
    /**
     * 消息队列未消费消息数
     */
    QUEUE_PENDING_MESSAGE_COUNT("queue.pending.message.count", "消息队列未消费消息数", MetricUnit.COUNT),
    
    /**
     * 系统负载(1分钟)
     */
    SYSTEM_LOAD_AVERAGE_1M("system.load.average.1m", "系统负载(1分钟)", MetricUnit.COUNT),
    
    /**
     * 系统负载(5分钟)
     */
    SYSTEM_LOAD_AVERAGE_5M("system.load.average.5m", "系统负载(5分钟)", MetricUnit.COUNT),
    
    /**
     * 系统负载(15分钟)
     */
    SYSTEM_LOAD_AVERAGE_15M("system.load.average.15m", "系统负载(15分钟)", MetricUnit.COUNT),
    
    /**
     * 服务实例数量
     */
    SERVICE_INSTANCE_COUNT("service.instance.count", "服务实例数量", MetricUnit.COUNT),
    
    /**
     * 自定义指标类型
     */
    CUSTOM("custom", "自定义指标", MetricUnit.CUSTOM);
    
    private final String code;
    private final String displayName;
    private final MetricUnit defaultUnit;
    
    MetricType(String code, String displayName, MetricUnit defaultUnit) {
        this.code = code;
        this.displayName = displayName;
        this.defaultUnit = defaultUnit;
    }
    
    /**
     * 获取指标类型代码
     * 
     * @return 指标类型代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取指标类型显示名称
     * 
     * @return 指标类型显示名称
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 获取指标类型默认单位
     * 
     * @return 指标类型默认单位
     */
    public MetricUnit getDefaultUnit() {
        return defaultUnit;
    }
    
    /**
     * 根据代码获取指标类型
     * 
     * @param code 指标类型代码
     * @return 指标类型枚举值，如果找不到则返回CUSTOM
     */
    public static MetricType fromCode(String code) {
        for (MetricType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return CUSTOM;
    }
}
