package com.platform.scheduler.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源路由
 * 
 * @author platform
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
    
    /**
     * 本地线程变量，存储当前使用的数据源键
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    /**
     * 默认数据源键
     */
    private static final String DEFAULT_DATASOURCE = "master";
    
    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey = getDataSource();
        logger.debug("当前使用数据源: {}", dataSourceKey);
        return dataSourceKey;
    }
    
    /**
     * 获取当前数据源键
     * 
     * @return 数据源键
     */
    public static String getDataSource() {
        String dataSource = CONTEXT_HOLDER.get();
        return dataSource == null ? DEFAULT_DATASOURCE : dataSource;
    }
    
    /**
     * 设置当前数据源键
     * 
     * @param dataSourceKey 数据源键
     */
    public static void setDataSource(String dataSourceKey) {
        CONTEXT_HOLDER.set(dataSourceKey);
    }
    
    /**
     * 清除当前数据源键
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
    
    /**
     * 使用主数据源
     */
    public static void useMaster() {
        setDataSource(DEFAULT_DATASOURCE);
    }
    
    /**
     * 使用从数据源
     * 
     * @param index 从数据源索引，从0开始
     */
    public static void useSlave(int index) {
        setDataSource("slave" + index);
    }
    
    /**
     * 使用日志数据源
     * 
     * @param index 日志数据源索引，从0开始
     */
    public static void useLogDb(int index) {
        setDataSource("log" + index);
    }
}
