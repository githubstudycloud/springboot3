package com.platform.scheduler.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskLog;

/**
 * 日志数据访问接口
 * 
 * @author platform
 */
public interface LogRepository {
    
    /**
     * 保存日志到指定表
     * 
     * @param log 日志对象
     * @param tableName 表名
     * @return 保存后的日志对象
     */
    TaskLog save(TaskLog log, String tableName);
    
    /**
     * 批量保存日志到指定表
     * 
     * @param logs 日志对象列表
     * @param tableName 表名
     * @return 保存后的日志对象列表
     */
    List<TaskLog> saveAll(List<TaskLog> logs, String tableName);
    
    /**
     * 根据任务ID查询日志
     * 
     * @param taskId 任务ID
     * @param tableNames 表名列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findByTaskId(Long taskId, List<String> tableNames, Pageable pageable);
    
    /**
     * 根据执行ID查询日志
     * 
     * @param executionId 执行ID
     * @param tableNames 表名列表
     * @return 日志列表
     */
    List<TaskLog> findByExecutionId(Long executionId, List<String> tableNames);
    
    /**
     * 根据执行ID查询日志（分页）
     * 
     * @param executionId 执行ID
     * @param tableNames 表名列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findByExecutionId(Long executionId, List<String> tableNames, Pageable pageable);
    
    /**
     * 根据查询参数查询日志
     * 
     * @param params 查询参数
     * @param tableNames 表名列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findByParams(LogQueryParam params, List<String> tableNames, Pageable pageable);
    
    /**
     * 创建日志表
     * 
     * @param tableName 表名
     * @return 是否成功
     */
    boolean createLogTable(String tableName);
    
    /**
     * 删除日志表
     * 
     * @param tableName 表名
     * @return 是否成功
     */
    boolean dropLogTable(String tableName);
    
    /**
     * 检查日志表是否存在
     * 
     * @param tableName 表名
     * @return 是否存在
     */
    boolean existsLogTable(String tableName);
    
    /**
     * 获取所有日志表名
     * 
     * @return 表名列表
     */
    List<String> getAllLogTableNames();
    
    /**
     * 获取日期范围内的日志表名
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 表名列表
     */
    List<String> getLogTableNamesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取日期对应的日志表名
     * 
     * @param date 日期
     * @return 表名
     */
    String getLogTableNameByDate(LocalDate date);
    
    /**
     * 执行更新SQL
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return 影响行数
     */
    int executeUpdate(String sql, Object... args);
}
