package com.platform.scheduler.core.log;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogRepository;

/**
 * 日志管理器
 * 
 * @author platform
 */
@Component
public class LogManager {
    
    private static final Logger logger = LoggerFactory.getLogger(LogManager.class);
    
    @Autowired
    private LogRepository logRepository;
    
    @Value("${scheduler.node.id}")
    private String nodeId;
    
    /**
     * 记录INFO级别日志
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 日志消息
     */
    public void info(Long taskId, Long executionId, String message) {
        log(taskId, executionId, "INFO", message);
    }
    
    /**
     * 记录WARN级别日志
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 日志消息
     */
    public void warn(Long taskId, Long executionId, String message) {
        log(taskId, executionId, "WARN", message);
    }
    
    /**
     * 记录ERROR级别日志
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 日志消息
     */
    public void error(Long taskId, Long executionId, String message) {
        log(taskId, executionId, "ERROR", message);
    }
    
    /**
     * 记录ERROR级别日志（带异常）
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param message 日志消息
     * @param e 异常
     */
    public void error(Long taskId, Long executionId, String message, Throwable e) {
        error(taskId, executionId, message + ": " + e.getMessage());
    }
    
    /**
     * 记录日志
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param level 日志级别
     * @param message 日志消息
     */
    private void log(Long taskId, Long executionId, String level, String message) {
        try {
            // 创建日志记录
            TaskLog log = new TaskLog();
            log.setTaskId(taskId);
            log.setExecutionId(executionId);
            log.setNodeId(nodeId);
            log.setLevel(level);
            log.setMessage(message);
            log.setCreatedTime(new Date());
            
            // 获取当前日志表名
            String tableName = getCurrentLogTableName();
            
            // 确保日志表存在
            ensureLogTableExists(tableName);
            
            // 保存日志
            logRepository.save(log, tableName);
            
            // 同时输出到应用日志
            if ("INFO".equals(level)) {
                logger.info("[Task-{}][Execution-{}] {}", taskId, executionId, message);
            } else if ("WARN".equals(level)) {
                logger.warn("[Task-{}][Execution-{}] {}", taskId, executionId, message);
            } else if ("ERROR".equals(level)) {
                logger.error("[Task-{}][Execution-{}] {}", taskId, executionId, message);
            }
        } catch (Exception e) {
            logger.error("记录任务日志异常: " + taskId + ", 执行ID: " + executionId, e);
        }
    }
    
    /**
     * 获取当前日志表名
     * 
     * @return 日志表名
     */
    private String getCurrentLogTableName() {
        LocalDate now = LocalDate.now();
        return logRepository.getLogTableNameByDate(now);
    }
    
    /**
     * 确保日志表存在
     * 
     * @param tableName 表名
     */
    private void ensureLogTableExists(String tableName) {
        try {
            if (!logRepository.existsLogTable(tableName)) {
                logger.info("创建日志表: {}", tableName);
                logRepository.createLogTable(tableName);
            }
        } catch (Exception e) {
            logger.error("确保日志表存在异常: " + tableName, e);
        }
    }
    
    /**
     * 清理过期日志表（超过12个月）
     */
    public void cleanupLogTables() {
        try {
            // 获取所有日志表
            List<String> allTables = logRepository.getAllLogTableNames();
            
            // 获取12个月前的表名
            LocalDate thresholdDate = LocalDate.now().minusMonths(12);
            String thresholdTableName = logRepository.getLogTableNameByDate(thresholdDate);
            
            // 删除过期表
            for (String tableName : allTables) {
                if (tableName.compareTo(thresholdTableName) < 0) {
                    logger.info("删除过期日志表: {}", tableName);
                    logRepository.dropLogTable(tableName);
                }
            }
        } catch (Exception e) {
            logger.error("清理过期日志表异常", e);
        }
    }
    
    /**
     * 确保后续3个月的日志表存在（提前创建）
     */
    public void ensureFutureLogTables() {
        try {
            LocalDate now = LocalDate.now();
            
            // 确保当前月及后续2个月的日志表存在
            for (int i = 0; i < 3; i++) {
                LocalDate futureDate = now.plusMonths(i);
                String tableName = logRepository.getLogTableNameByDate(futureDate);
                
                ensureLogTableExists(tableName);
            }
        } catch (Exception e) {
            logger.error("确保未来日志表存在异常", e);
        }
    }
}
