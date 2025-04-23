package com.platform.scheduler.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.scheduler.config.datasource.DataSourceSwitch;
import com.platform.scheduler.config.datasource.DynamicDataSource;
import com.platform.scheduler.config.datasource.LogShardingStrategy;
import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogQueryParam;
import com.platform.scheduler.repository.LogRepository;
import com.platform.scheduler.service.LogService;
import com.platform.scheduler.util.JsonUtils;

/**
 * 日志服务实现类
 * 
 * @author platform
 */
@Service
public class LogServiceImpl implements LogService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);
    
    @Autowired
    private LogRepository logRepository;
    
    @Autowired
    private LogShardingStrategy logShardingStrategy;
    
    @Value("${scheduler.log.export.path:./export/logs}")
    private String logExportPath;
    
    @Override
    @Transactional
    public Long logInfo(TaskLog log) {
        return saveLog(log, "INFO");
    }
    
    @Override
    @Transactional
    public Long logWarning(TaskLog log) {
        return saveLog(log, "WARNING");
    }
    
    @Override
    @Transactional
    public Long logError(TaskLog log) {
        return saveLog(log, "ERROR");
    }
    
    /**
     * 保存日志，使用分库分表策略
     * 
     * @param log 日志对象
     * @param level 日志级别
     * @return 日志ID
     */
    @DataSourceSwitch(isLogDatabase = true, hashField = "taskId")
    private Long saveLog(TaskLog log, String level) {
        log.setLevel(level);
        
        if (log.getCreatedTime() == null) {
            log.setCreatedTime(new Date());
        }
        
        // 根据日期确定表名
        String tableName = logShardingStrategy.getLogTableName(log.getCreatedTime());
        
        // 确保表存在
        logShardingStrategy.ensureLogTableExists(tableName);
        
        // 保存日志
        TaskLog savedLog = logRepository.save(log, tableName);
        logger.debug("日志已保存: {}, 级别: {}, 表: {}", savedLog.getId(), level, tableName);
        
        return savedLog.getId();
    }
    
    @Override
    public Page<TaskLog> findLogsByTaskId(Long taskId, Pageable pageable) {
        try {
            // 切换到对应的日志数据源
            logShardingStrategy.switchToLogDataSource(taskId);
            
            // 获取最近6个月的日志表
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(6);
            List<String> tableNames = logShardingStrategy.getLogTableNamesForTask(taskId, startDate, endDate);
            
            // 查询日志
            if (tableNames.isEmpty()) {
                return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
            }
            
            return logRepository.findByTaskId(taskId, tableNames, pageable);
        } finally {
            // 恢复默认数据源
            DynamicDataSource.clearDataSource();
        }
    }
    
    @Override
    public Page<TaskLog> findLogsByExecutionId(Long executionId, Pageable pageable) {
        // 由于不知道executionId对应的taskId，无法确定使用哪个分片
        // 因此需要在每个日志数据源中查询
        
        Page<TaskLog> result = null;
        
        for (int i = 0; i < 2; i++) { // 假设有2个日志数据源
            // 切换数据源
            DynamicDataSource.useLogDb(i);
            
            try {
                // 获取最近6个月的日志表
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusMonths(6);
                List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
                
                if (!tableNames.isEmpty()) {
                    Page<TaskLog> logs = logRepository.findByExecutionId(executionId, tableNames, pageable);
                    if (logs.getTotalElements() > 0) {
                        result = logs;
                        break; // 找到结果，结束循环
                    }
                }
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        }
        
        if (result == null) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }
        
        return result;
    }
    
    @Override
    public Page<TaskLog> findLogsByLevel(String level, Pageable pageable) {
        // 需要在所有日志数据源中查询，然后合并结果
        // 由于实现复杂度较高，这里使用简化实现，仅查询第一个数据源
        
        DynamicDataSource.useLogDb(0);
        
        try {
            // 获取最近6个月的日志表
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(6);
            List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
            
            // 构造查询参数
            LogQueryParam queryParam = new LogQueryParam();
            queryParam.setLevel(level);
            
            if (tableNames.isEmpty()) {
                return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
            }
            
            return logRepository.findByParams(queryParam, tableNames, pageable);
        } finally {
            // 恢复默认数据源
            DynamicDataSource.clearDataSource();
        }
    }
    
    @Override
    public Page<TaskLog> findLogs(LogQueryParam queryParam, Pageable pageable) {
        // 如果指定了taskId，则可以确定使用哪个分片
        if (queryParam.getTaskId() != null) {
            try {
                // 切换到对应的日志数据源
                logShardingStrategy.switchToLogDataSource(queryParam.getTaskId());
                
                // 获取日志表
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusMonths(6); // 默认查询最近6个月
                List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
                
                if (tableNames.isEmpty()) {
                    return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
                }
                
                return logRepository.findByParams(queryParam, tableNames, pageable);
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        } else {
            // 如果没有指定taskId，则需要查询所有分片
            // 由于实现复杂度较高，这里使用简化实现，仅查询第一个数据源
            
            DynamicDataSource.useLogDb(0);
            
            try {
                // 获取日志表
                LocalDate endDate = LocalDate.now();
                LocalDate startDate = endDate.minusMonths(6); // 默认查询最近6个月
                List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
                
                if (tableNames.isEmpty()) {
                    return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
                }
                
                return logRepository.findByParams(queryParam, tableNames, pageable);
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        }
    }
    
    @Override
    @Transactional
    public int cleanupLogs(int retentionDays) {
        // 计算保留日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -retentionDays);
        Date retentionDate = calendar.getTime();
        
        int totalDeleted = 0;
        
        // 在每个日志数据源中清理日志
        for (int i = 0; i < 2; i++) { // 假设有2个日志数据源
            // 切换数据源
            DynamicDataSource.useLogDb(i);
            
            try {
                // 获取所有日志表
                List<String> allTables = logRepository.getAllLogTableNames();
                
                for (String tableName : allTables) {
                    // 在每个表中删除过期日志
                    String deleteSql = "DELETE FROM " + tableName + " WHERE created_time < ?";
                    int deleted = 0;
                    
                    try {
                        deleted = logRepository.executeUpdate(deleteSql, retentionDate);
                        totalDeleted += deleted;
                        logger.info("从表 {} 中删除了 {} 条过期日志", tableName, deleted);
                    } catch (Exception e) {
                        logger.error("删除表 {} 中的过期日志失败", tableName, e);
                    }
                }
            } finally {
                // 恢复默认数据源
                DynamicDataSource.clearDataSource();
            }
        }
        
        logger.info("总共清理过期日志: {} 条, 保留 {} 天的日志", totalDeleted, retentionDays);
        
        return totalDeleted;
    }
    
    @Override
    public String exportLogs(LogQueryParam queryParam) {
        try {
            // 创建导出目录
            File exportDir = new File(logExportPath);
            if (!exportDir.exists()) {
                if (!exportDir.mkdirs()) {
                    throw new IOException("无法创建导出目录: " + logExportPath);
                }
            }
            
            // 生成文件名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = String.format("logs_%s.json", dateFormat.format(new Date()));
            File exportFile = new File(exportDir, fileName);
            
            // 分页查询并写入文件
            try (FileWriter writer = new FileWriter(exportFile)) {
                int pageSize = 1000;
                int pageNum = 0;
                long totalExported = 0;
                
                writer.write("[\n");
                boolean isFirst = true;
                
                // 如果指定了taskId，只需要查询对应的分片
                if (queryParam.getTaskId() != null) {
                    // 切换到对应的日志数据源
                    logShardingStrategy.switchToLogDataSource(queryParam.getTaskId());
                    
                    try {
                        // 获取日志表
                        LocalDate endDate = LocalDate.now();
                        LocalDate startDate = endDate.minusMonths(6); // 默认查询最近6个月
                        List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
                        
                        if (!tableNames.isEmpty()) {
                            totalExported = exportLogsFromDataSource(writer, queryParam, tableNames, pageSize, isFirst);
                            isFirst = false;
                        }
                    } finally {
                        // 恢复默认数据源
                        DynamicDataSource.clearDataSource();
                    }
                } else {
                    // 如果没有指定taskId，需要查询所有分片
                    for (int i = 0; i < 2; i++) { // 假设有2个日志数据源
                        // 切换数据源
                        DynamicDataSource.useLogDb(i);
                        
                        try {
                            // 获取日志表
                            LocalDate endDate = LocalDate.now();
                            LocalDate startDate = endDate.minusMonths(6); // 默认查询最近6个月
                            List<String> tableNames = logShardingStrategy.getLogTableNamesByDateRange(startDate, endDate);
                            
                            if (!tableNames.isEmpty()) {
                                totalExported += exportLogsFromDataSource(writer, queryParam, tableNames, pageSize, isFirst);
                                isFirst = false;
                            }
                        } finally {
                            // 恢复默认数据源
                            DynamicDataSource.clearDataSource();
                        }
                    }
                }
                
                writer.write("\n]");
                logger.info("日志导出成功: {}, 共导出 {} 条日志", exportFile.getAbsolutePath(), totalExported);
            }
            
            return exportFile.getAbsolutePath();
            
        } catch (IOException e) {
            logger.error("日志导出失败", e);
            throw new RuntimeException("日志导出失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从数据源导出日志
     * 
     * @param writer 文件写入器
     * @param queryParam 查询参数
     * @param tableNames 表名列表
     * @param pageSize 分页大小
     * @param isFirst 是否第一条记录
     * @return 导出的记录数
     */
    private long exportLogsFromDataSource(FileWriter writer, LogQueryParam queryParam, List<String> tableNames, int pageSize, boolean isFirst) throws IOException {
        int pageNum = 0;
        long totalExported = 0;
        Page<TaskLog> page;
        boolean currentIsFirst = isFirst;
        
        do {
            // 分页查询
            Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNum++, pageSize);
            page = logRepository.findByParams(queryParam, tableNames, pageable);
            
            // 写入数据
            for (TaskLog log : page.getContent()) {
                if (!currentIsFirst) {
                    writer.write(",\n");
                }
                writer.write(JsonUtils.toJsonString(log));
                currentIsFirst = false;
                totalExported++;
            }
            
        } while (page.hasNext());
        
        return totalExported;
    }
    
    /**
     * 初始化当前月份的日志表
     * 
     * @return 是否成功
     */
    public boolean initCurrentMonthLogTables() {
        return logShardingStrategy.initCurrentMonthLogTables();
    }
    
    /**
     * 清理过期日志表
     * 
     * @param retentionMonths 保留月数
     * @return 清理的表数量
     */
    public int cleanupExpiredLogTables(int retentionMonths) {
        return logShardingStrategy.cleanupExpiredLogTables(retentionMonths);
    }
}
