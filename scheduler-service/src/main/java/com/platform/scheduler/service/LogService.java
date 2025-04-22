package com.platform.scheduler.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogQueryParam;

/**
 * 日志服务接口
 * 
 * @author platform
 */
public interface LogService {
    
    /**
     * 记录日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    Long logInfo(TaskLog log);
    
    /**
     * 记录警告日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    Long logWarning(TaskLog log);
    
    /**
     * 记录错误日志
     * 
     * @param log 日志对象
     * @return 日志ID
     */
    Long logError(TaskLog log);
    
    /**
     * 根据任务ID查询日志
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findLogsByTaskId(Long taskId, Pageable pageable);
    
    /**
     * 根据执行ID查询日志
     * 
     * @param executionId 执行ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findLogsByExecutionId(Long executionId, Pageable pageable);
    
    /**
     * 根据日志级别查询日志
     * 
     * @param level 日志级别
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findLogsByLevel(String level, Pageable pageable);
    
    /**
     * 根据多条件查询日志
     * 
     * @param queryParam 查询参数
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findLogs(LogQueryParam queryParam, Pageable pageable);
    
    /**
     * 清理过期日志
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    int cleanupLogs(int retentionDays);
    
    /**
     * 导出日志
     * 
     * @param queryParam 查询参数
     * @return 导出文件路径
     */
    String exportLogs(LogQueryParam queryParam);
}
