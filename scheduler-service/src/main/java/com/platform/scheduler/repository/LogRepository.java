package com.platform.scheduler.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskLog;

/**
 * 任务日志数据访问层接口
 * 
 * @author platform
 */
public interface LogRepository {
    
    /**
     * 保存日志
     * 
     * @param log 日志实体
     * @param tableName 表名
     * @return 保存后的日志实体
     */
    TaskLog save(TaskLog log, String tableName);
    
    /**
     * 批量保存日志
     * 
     * @param logs 日志实体列表
     * @param tableName 表名
     * @return 保存后的日志实体列表
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
     * 根据执行ID分页查询日志
     * 
     * @param executionId 执行ID
     * @param tableNames 表名列表
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskLog> findByExecutionId(Long executionId, List<String> tableNames, Pageable pageable);
    
    /**
     * 根据条件查询日志
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
     * 查询日志表是否存在
     * 
     * @param tableName 表名
     * @return 是否存在
     */
    boolean existsLogTable(String tableName);
    
    /**
     * 获取所有日志表名
     * 
     * @return 日志表名列表
     */
    List<String> getAllLogTableNames();
    
    /**
     * 获取日期范围内的日志表名
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日志表名列表
     */
    List<String> getLogTableNamesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据日期获取日志表名
     * 
     * @param date 日期
     * @return 日志表名
     */
    String getLogTableNameByDate(LocalDate date);
}

/**
 * 日志查询参数
 */
class LogQueryParam {
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 执行ID
     */
    private Long executionId;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 日志级别
     */
    private String level;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 关键字
     */
    private String keyword;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    /**
     * 构建查询条件
     * 
     * @return SQL条件
     */
    public String buildWhereSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE 1=1");
        
        if (taskId != null) {
            sb.append(" AND task_id = #{taskId}");
        }
        
        if (executionId != null) {
            sb.append(" AND execution_id = #{executionId}");
        }
        
        if (nodeId != null) {
            sb.append(" AND node_id = #{nodeId}");
        }
        
        if (level != null) {
            sb.append(" AND level = #{level}");
        }
        
        if (keyword != null) {
            sb.append(" AND message LIKE #{keyword}");
        }
        
        return sb.toString();
    }
    
    /**
     * 构建参数Map
     * 
     * @return 参数Map
     */
    public Map<String, Object> buildParamMap() {
        Map<String, Object> params = new java.util.HashMap<>();
        
        if (taskId != null) {
            params.put("taskId", taskId);
        }
        
        if (executionId != null) {
            params.put("executionId", executionId);
        }
        
        if (nodeId != null) {
            params.put("nodeId", nodeId);
        }
        
        if (level != null) {
            params.put("level", level);
        }
        
        if (keyword != null) {
            params.put("keyword", "%" + keyword + "%");
        }
        
        return params;
    }
}
