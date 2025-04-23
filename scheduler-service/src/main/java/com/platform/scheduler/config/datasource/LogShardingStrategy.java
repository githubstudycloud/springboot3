package com.platform.scheduler.config.datasource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.platform.scheduler.repository.LogRepository;

/**
 * 日志分片策略
 * 
 * @author platform
 */
@Component
public class LogShardingStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(LogShardingStrategy.class);
    
    /**
     * 日志表前缀
     */
    private static final String LOG_TABLE_PREFIX = "task_log_";
    
    /**
     * 表名日期格式化器
     */
    private static final DateTimeFormatter TABLE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM");
    
    /**
     * 日志数据源数量
     */
    @Value("${scheduler.log.datasource.count:2}")
    private int logDataSourceCount;
    
    @Autowired
    private LogRepository logRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取日志表名
     * 
     * @param date 日期
     * @return 表名
     */
    public String getLogTableName(LocalDate date) {
        return LOG_TABLE_PREFIX + date.format(TABLE_NAME_FORMATTER);
    }
    
    /**
     * 获取日志表名
     * 
     * @param date 日期
     * @return 表名
     */
    public String getLogTableName(Date date) {
        return getLogTableName(LocalDate.of(
                date.getYear() + 1900, 
                date.getMonth() + 1, 
                date.getDate()));
    }
    
    /**
     * 确保日志表存在
     * 
     * @param tableName 表名
     * @return 是否成功
     */
    public boolean ensureLogTableExists(String tableName) {
        // 判断表是否存在
        if (logRepository.existsLogTable(tableName)) {
            return true;
        }
        
        // 创建表
        return logRepository.createLogTable(tableName);
    }
    
    /**
     * 获取任务日志数据源索引
     * 
     * @param taskId 任务ID
     * @return 数据源索引
     */
    public int getLogDataSourceIndex(Long taskId) {
        if (logDataSourceCount <= 1) {
            return 0;
        }
        return Math.abs(taskId.hashCode() % logDataSourceCount);
    }
    
    /**
     * 切换到对应的日志数据源
     * 
     * @param taskId 任务ID
     */
    public void switchToLogDataSource(Long taskId) {
        int dataSourceIndex = getLogDataSourceIndex(taskId);
        DynamicDataSource.useLogDb(dataSourceIndex);
    }
    
    /**
     * 获取日期范围内的所有日志表名
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 表名列表
     */
    public List<String> getLogTableNamesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<String> tableNames = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String tableName = getLogTableName(current);
            // 如果表存在，才添加到列表中
            if (logRepository.existsLogTable(tableName)) {
                tableNames.add(tableName);
            }
            current = current.plusMonths(1);
        }
        
        return tableNames;
    }
    
    /**
     * 根据任务ID和日期范围获取日志表名
     * 
     * @param taskId 任务ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 对应数据源的表名列表
     */
    public List<String> getLogTableNamesForTask(Long taskId, LocalDate startDate, LocalDate endDate) {
        // 切换到对应的数据源
        switchToLogDataSource(taskId);
        
        try {
            // 获取数据源中的表名
            return getLogTableNamesByDateRange(startDate, endDate);
        } finally {
            // 恢复默认数据源
            DynamicDataSource.clearDataSource();
        }
    }
    
    /**
     * 初始化当前月份的日志表
     * 
     * @return 是否成功
     */
    public boolean initCurrentMonthLogTables() {
        LocalDate currentDate = LocalDate.now();
        String tableName = getLogTableName(currentDate);
        
        boolean allSuccess = true;
        
        // 在每个日志数据源中创建表
        for (int i = 0; i < logDataSourceCount; i++) {
            // 切换数据源
            DynamicDataSource.useLogDb(i);
            
            try {
                boolean success = ensureLogTableExists(tableName);
                if (success) {
                    logger.info("成功创建日志表: {} 在数据源 log{}", tableName, i);
                } else {
                    logger.error("创建日志表失败: {} 在数据源 log{}", tableName, i);
                    allSuccess = false;
                }
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        }
        
        return allSuccess;
    }
    
    /**
     * 清理过期日志表
     * 
     * @param retentionMonths 保留月数
     * @return 清理的表数量
     */
    public int cleanupExpiredLogTables(int retentionMonths) {
        // 计算截止日期
        LocalDate cutoffDate = LocalDate.now().minusMonths(retentionMonths);
        
        int totalDropped = 0;
        
        // 在每个日志数据源中清理表
        for (int i = 0; i < logDataSourceCount; i++) {
            // 切换数据源
            DynamicDataSource.useLogDb(i);
            
            try {
                // 获取所有日志表
                List<String> allTables = logRepository.getAllLogTableNames();
                
                for (String tableName : allTables) {
                    // 提取表名中的日期部分
                    if (tableName.startsWith(LOG_TABLE_PREFIX) && tableName.length() > LOG_TABLE_PREFIX.length()) {
                        String datePart = tableName.substring(LOG_TABLE_PREFIX.length());
                        try {
                            // 解析表名中的日期
                            LocalDate tableDate = LocalDate.parse("01_" + datePart, DateTimeFormatter.ofPattern("dd_yyyy_MM"));
                            
                            // 如果表日期早于截止日期，则删除表
                            if (tableDate.isBefore(cutoffDate)) {
                                boolean dropped = logRepository.dropLogTable(tableName);
                                if (dropped) {
                                    logger.info("已删除过期日志表: {} 在数据源 log{}", tableName, i);
                                    totalDropped++;
                                } else {
                                    logger.error("删除过期日志表失败: {} 在数据源 log{}", tableName, i);
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("无法解析日志表名中的日期: {}", tableName, e);
                        }
                    }
                }
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        }
        
        return totalDropped;
    }
}
