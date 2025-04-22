package com.platform.scheduler.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.platform.scheduler.model.TaskLog;
import com.platform.scheduler.repository.LogRepository;
import com.platform.scheduler.repository.LogQueryParam;

/**
 * 任务日志数据访问层实现类
 * 
 * @author platform
 */
@Repository
public class LogRepositoryImpl implements LogRepository {

    private static final DateTimeFormatter TABLE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM");
    private static final String LOG_TABLE_PREFIX = "task_log_";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final RowMapper<TaskLog> rowMapper = new RowMapper<TaskLog>() {
        @Override
        public TaskLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            TaskLog log = new TaskLog();
            log.setId(rs.getLong("id"));
            log.setTaskId(rs.getLong("task_id"));
            log.setExecutionId(rs.getLong("execution_id"));
            log.setNodeId(rs.getString("node_id"));
            log.setLevel(rs.getString("level"));
            log.setMessage(rs.getString("message"));
            log.setCreatedTime(rs.getTimestamp("created_time"));
            return log;
        }
    };
    
    @Override
    public TaskLog save(TaskLog log, String tableName) {
        String sql = "INSERT INTO " + tableName + " (task_id, execution_id, node_id, level, message, created_time) " +
                "VALUES (:taskId, :executionId, :nodeId, :level, :message, :createdTime)";
        
        if (log.getCreatedTime() == null) {
            log.setCreatedTime(new Date());
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", log.getTaskId());
        params.addValue("executionId", log.getExecutionId());
        params.addValue("nodeId", log.getNodeId());
        params.addValue("level", log.getLevel());
        params.addValue("message", log.getMessage());
        params.addValue("createdTime", log.getCreatedTime());
        
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        log.setId(keyHolder.getKey().longValue());
        
        return log;
    }
    
    @Override
    public List<TaskLog> saveAll(List<TaskLog> logs, String tableName) {
        List<TaskLog> result = new ArrayList<>();
        for (TaskLog log : logs) {
            result.add(save(log, tableName));
        }
        return result;
    }
    
    @Override
    public Page<TaskLog> findByTaskId(Long taskId, List<String> tableNames, Pageable pageable) {
        if (tableNames == null || tableNames.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT * FROM ").append(tableNames.get(i))
               .append(" WHERE task_id = :taskId");
        }
        
        sql.append(" ORDER BY created_time DESC")
           .append(" LIMIT ").append(pageable.getPageSize())
           .append(" OFFSET ").append(pageable.getOffset());
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", taskId);
        
        List<TaskLog> content = namedParameterJdbcTemplate.query(sql.toString(), params, rowMapper);
        
        StringBuilder countSql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                countSql.append(" + ");
            } else {
                countSql.append("SELECT (");
            }
            countSql.append("SELECT COUNT(1) FROM ").append(tableNames.get(i))
                    .append(" WHERE task_id = :taskId");
        }
        countSql.append(") AS total");
        
        Integer total = namedParameterJdbcTemplate.queryForObject(countSql.toString(), params, Integer.class);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public List<TaskLog> findByExecutionId(Long executionId, List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            return new ArrayList<>();
        }
        
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT * FROM ").append(tableNames.get(i))
               .append(" WHERE execution_id = :executionId");
        }
        
        sql.append(" ORDER BY created_time");
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("executionId", executionId);
        
        return namedParameterJdbcTemplate.query(sql.toString(), params, rowMapper);
    }
    
    @Override
    public Page<TaskLog> findByExecutionId(Long executionId, List<String> tableNames, Pageable pageable) {
        if (tableNames == null || tableNames.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT * FROM ").append(tableNames.get(i))
               .append(" WHERE execution_id = :executionId");
        }
        
        // 处理排序
        sql.append(" ORDER BY created_time");
        if (pageable.getSort().isSorted()) {
            sql.append(", ");
            pageable.getSort().forEach(order -> 
                sql.append(camelToUnderscore(order.getProperty()))
                   .append(" ")
                   .append(order.getDirection().name())
            );
        }
        
        sql.append(" LIMIT ").append(pageable.getPageSize())
           .append(" OFFSET ").append(pageable.getOffset());
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("executionId", executionId);
        
        List<TaskLog> content = namedParameterJdbcTemplate.query(sql.toString(), params, rowMapper);
        
        StringBuilder countSql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                countSql.append(" + ");
            } else {
                countSql.append("SELECT (");
            }
            countSql.append("SELECT COUNT(1) FROM ").append(tableNames.get(i))
                    .append(" WHERE execution_id = :executionId");
        }
        countSql.append(") AS total");
        
        Integer total = namedParameterJdbcTemplate.queryForObject(countSql.toString(), params, Integer.class);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Page<TaskLog> findByParams(LogQueryParam params, List<String> tableNames, Pageable pageable) {
        if (tableNames == null || tableNames.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT * FROM ").append(tableNames.get(i))
               .append(buildWhereSql(params));
        }
        
        // 处理排序
        sql.append(" ORDER BY created_time DESC");
        if (pageable.getSort().isSorted()) {
            sql.append(", ");
            pageable.getSort().forEach(order -> 
                sql.append(camelToUnderscore(order.getProperty()))
                   .append(" ")
                   .append(order.getDirection().name())
            );
        }
        
        sql.append(" LIMIT ").append(pageable.getPageSize())
           .append(" OFFSET ").append(pageable.getOffset());
        
        Map<String, Object> paramMap = buildParamMap(params);
        
        List<TaskLog> content = namedParameterJdbcTemplate.query(sql.toString(), paramMap, rowMapper);
        
        StringBuilder countSql = new StringBuilder();
        for (int i = 0; i < tableNames.size(); i++) {
            if (i > 0) {
                countSql.append(" + ");
            } else {
                countSql.append("SELECT (");
            }
            countSql.append("SELECT COUNT(1) FROM ").append(tableNames.get(i))
                    .append(buildWhereSql(params));
        }
        countSql.append(") AS total");
        
        Integer total = namedParameterJdbcTemplate.queryForObject(countSql.toString(), paramMap, Integer.class);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    /**
     * 构建查询条件
     * 
     * @param params 查询参数
     * @return SQL条件
     */
    private String buildWhereSql(LogQueryParam params) {
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE 1=1");
        
        if (params.getTaskId() != null) {
            sb.append(" AND task_id = :taskId");
        }
        
        if (params.getExecutionId() != null) {
            sb.append(" AND execution_id = :executionId");
        }
        
        if (params.getNodeId() != null && !params.getNodeId().isEmpty()) {
            sb.append(" AND node_id = :nodeId");
        }
        
        if (params.getLevel() != null && !params.getLevel().isEmpty()) {
            sb.append(" AND level = :level");
        }
        
        if (params.getKeyword() != null && !params.getKeyword().isEmpty()) {
            sb.append(" AND message LIKE :keyword");
        }
        
        return sb.toString();
    }
    
    /**
     * 构建参数Map
     * 
     * @param params 查询参数
     * @return 参数Map
     */
    private Map<String, Object> buildParamMap(LogQueryParam params) {
        Map<String, Object> paramMap = new HashMap<>();
        
        if (params.getTaskId() != null) {
            paramMap.put("taskId", params.getTaskId());
        }
        
        if (params.getExecutionId() != null) {
            paramMap.put("executionId", params.getExecutionId());
        }
        
        if (params.getNodeId() != null && !params.getNodeId().isEmpty()) {
            paramMap.put("nodeId", params.getNodeId());
        }
        
        if (params.getLevel() != null && !params.getLevel().isEmpty()) {
            paramMap.put("level", params.getLevel());
        }
        
        if (params.getKeyword() != null && !params.getKeyword().isEmpty()) {
            paramMap.put("keyword", "%" + params.getKeyword() + "%");
        }
        
        return paramMap;
    }
    
    /**
     * 驼峰命名转下划线命名
     * 
     * @param camel 驼峰命名
     * @return 下划线命名
     */
    private String camelToUnderscore(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
    
    @Override
    public boolean createLogTable(String tableName) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID', " +
                    "task_id bigint(20) NOT NULL COMMENT '任务ID', " +
                    "execution_id bigint(20) NOT NULL COMMENT '执行ID', " +
                    "node_id varchar(100) NOT NULL COMMENT '节点ID', " +
                    "level varchar(10) NOT NULL COMMENT '日志级别(INFO/WARN/ERROR)', " +
                    "message text NOT NULL COMMENT '日志内容', " +
                    "created_time datetime NOT NULL COMMENT '创建时间', " +
                    "PRIMARY KEY (id), " +
                    "INDEX idx_task_id (task_id), " +
                    "INDEX idx_execution_id (execution_id), " +
                    "INDEX idx_created_time (created_time), " +
                    "INDEX idx_level (level) " +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表(按月分表)'";
            
            jdbcTemplate.execute(sql);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
    
    @Override
    public boolean dropLogTable(String tableName) {
        try {
            String sql = "DROP TABLE IF EXISTS " + tableName;
            jdbcTemplate.execute(sql);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
    
    @Override
    public boolean existsLogTable(String tableName) {
        try {
            String sql = "SELECT COUNT(1) FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }
    
    @Override
    public List<String> getAllLogTableNames() {
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name LIKE 'task_log_%' " +
                "ORDER BY table_name";
        
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    @Override
    public List<String> getLogTableNamesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<String> tableNames = new ArrayList<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String tableName = getLogTableNameByDate(current);
            tableNames.add(tableName);
            current = current.plusMonths(1);
        }
        
        return tableNames;
    }
    
    @Override
    public String getLogTableNameByDate(LocalDate date) {
        return LOG_TABLE_PREFIX + date.format(TABLE_NAME_FORMATTER);
    }
}
