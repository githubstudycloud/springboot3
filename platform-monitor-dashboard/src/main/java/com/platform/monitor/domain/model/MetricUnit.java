package com.platform.monitor.domain.model;

/**
 * 指标单位枚举
 */
public enum MetricUnit {
    
    /**
     * 百分比
     */
    PERCENTAGE("percentage", "%"),
    
    /**
     * 字节
     */
    BYTES("bytes", "B"),
    
    /**
     * 千字节
     */
    KILOBYTES("kilobytes", "KB"),
    
    /**
     * 兆字节
     */
    MEGABYTES("megabytes", "MB"),
    
    /**
     * 吉字节
     */
    GIGABYTES("gigabytes", "GB"),
    
    /**
     * 太字节
     */
    TERABYTES("terabytes", "TB"),
    
    /**
     * 秒
     */
    SECONDS("seconds", "s"),
    
    /**
     * 毫秒
     */
    MILLISECONDS("milliseconds", "ms"),
    
    /**
     * 微秒
     */
    MICROSECONDS("microseconds", "μs"),
    
    /**
     * 纳秒
     */
    NANOSECONDS("nanoseconds", "ns"),
    
    /**
     * 次数
     */
    COUNT("count", ""),
    
    /**
     * 每秒请求数
     */
    REQUESTS_PER_SECOND("requests_per_second", "req/s"),
    
    /**
     * 每分钟请求数
     */
    REQUESTS_PER_MINUTE("requests_per_minute", "req/min"),
    
    /**
     * 比特每秒
     */
    BITS_PER_SECOND("bits_per_second", "bps"),
    
    /**
     * 千比特每秒
     */
    KILOBITS_PER_SECOND("kilobits_per_second", "Kbps"),
    
    /**
     * 兆比特每秒
     */
    MEGABITS_PER_SECOND("megabits_per_second", "Mbps"),
    
    /**
     * 吉比特每秒
     */
    GIGABITS_PER_SECOND("gigabits_per_second", "Gbps"),
    
    /**
     * 自定义单位
     */
    CUSTOM("custom", "");
    
    private final String code;
    private final String symbol;
    
    MetricUnit(String code, String symbol) {
        this.code = code;
        this.symbol = symbol;
    }
    
    /**
     * 获取指标单位的代码
     * 
     * @return 指标单位代码
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 获取指标单位的符号
     * 
     * @return 指标单位符号
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * 根据代码获取指标单位
     * 
     * @param code 指标单位代码
     * @return 指标单位枚举值，如果找不到则返回CUSTOM
     */
    public static MetricUnit fromCode(String code) {
        for (MetricUnit unit : values()) {
            if (unit.getCode().equals(code)) {
                return unit;
            }
        }
        return CUSTOM;
    }
}
