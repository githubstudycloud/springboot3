package com.platform.scheduler.repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志查询参数
 * 
 * @author platform
 */
public class LogQueryParam {
    
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
            sb.append(" AND task_id = :taskId");
        }
        
        if (executionId != null) {
            sb.append(" AND execution_id = :executionId");
        }
        
        if (nodeId != null && !nodeId.isEmpty()) {
            sb.append(" AND node_id = :nodeId");
        }
        
        if (level != null && !level.isEmpty()) {
            sb.append(" AND level = :level");
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            sb.append(" AND message LIKE :keyword");
        }
        
        return sb.toString();
    }
    
    /**
     * 构建参数Map
     * 
     * @return 参数Map
     */
    public Map<String, Object> buildParamMap() {
        Map<String, Object> params = new HashMap<>();
        
        if (taskId != null) {
            params.put("taskId", taskId);
        }
        
        if (executionId != null) {
            params.put("executionId", executionId);
        }
        
        if (nodeId != null && !nodeId.isEmpty()) {
            params.put("nodeId", nodeId);
        }
        
        if (level != null && !level.isEmpty()) {
            params.put("level", level);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", "%" + keyword + "%");
        }
        
        return params;
    }
}
