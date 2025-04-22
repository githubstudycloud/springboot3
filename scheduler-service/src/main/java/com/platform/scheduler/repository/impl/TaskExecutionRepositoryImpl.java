package com.platform.scheduler.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.repository.TaskExecutionRepository;

/**
 * 任务执行记录数据访问层实现类
 * 
 * @author platform
 */
@Repository
public class TaskExecutionRepositoryImpl implements TaskExecutionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final BeanPropertyRowMapper<TaskExecution> rowMapper = new BeanPropertyRowMapper<>(TaskExecution.class);
    
    @Override
    public TaskExecution save(TaskExecution execution) {
        if (execution.getId() == null) {
            return insert(execution);
        } else {
            return update(execution);
        }
    }
    
    /**
     * 插入执行记录
     * 
     * @param execution 执行记录实体
     * @return 插入后的执行记录实体
     */
    private TaskExecution insert(TaskExecution execution) {
        String sql = "INSERT INTO task_execution (task_id, node_id, trigger_type, start_time, " +
                "end_time, status, result, error_message) " +
                "VALUES (:taskId, :nodeId, :triggerType, :startTime, " +
                ":endTime, :status, :result, :errorMessage)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(execution);
        
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        execution.setId(keyHolder.getKey().longValue());
        
        return execution;
    }
    
    /**
     * 更新执行记录
     * 
     * @param execution 执行记录实体
     * @return 更新后的执行记录实体
     */
    private TaskExecution update(TaskExecution execution) {
        String sql = "UPDATE task_execution SET task_id = :taskId, node_id = :nodeId, " +
                "trigger_type = :triggerType, start_time = :startTime, end_time = :endTime, " +
                "status = :status, result = :result, error_message = :errorMessage " +
                "WHERE id = :id";
        
        SqlParameterSource params = new BeanPropertySqlParameterSource(execution);
        namedParameterJdbcTemplate.update(sql, params);
        
        return execution;
    }

    @Override
    public List<TaskExecution> saveAll(Iterable<TaskExecution> entities) {
        List<TaskExecution> result = new ArrayList<>();
        for (TaskExecution entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public Optional<TaskExecution> findById(Long id) {
        try {
            String sql = "SELECT * FROM task_execution WHERE id = ?";
            TaskExecution execution = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(execution);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(1) FROM task_execution WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public List<TaskExecution> findAll() {
        String sql = "SELECT * FROM task_execution";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Page<TaskExecution> findAll(Pageable pageable) {
        String sql = "SELECT * FROM task_execution ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper);
        
        String countSql = "SELECT COUNT(1) FROM task_execution";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    /**
     * 获取排序SQL
     * 
     * @param pageable 分页参数
     * @return 排序SQL
     */
    private String getOrderBy(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return "start_time DESC";
        }
        
        StringBuilder orderBy = new StringBuilder();
        pageable.getSort().forEach(order -> {
            if (orderBy.length() > 0) {
                orderBy.append(", ");
            }
            orderBy.append(camelToUnderscore(order.getProperty()))
                  .append(" ")
                  .append(order.getDirection().name());
        });
        
        return orderBy.toString();
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
    public List<TaskExecution> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<>();
        ids.forEach(idList::add);
        
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM task_execution WHERE id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", idList);
        
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(1) FROM task_execution";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM task_execution WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(TaskExecution entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<TaskExecution> entities) {
        for (TaskExecution entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM task_execution";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<TaskExecution> findByTaskId(Long taskId) {
        String sql = "SELECT * FROM task_execution WHERE task_id = ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, rowMapper, taskId);
    }

    @Override
    public Page<TaskExecution> findByTaskId(Long taskId, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE task_id = ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, taskId);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE task_id = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, taskId);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<TaskExecution> findByTaskIdAndStatus(Long taskId, String status) {
        String sql = "SELECT * FROM task_execution WHERE task_id = ? AND status = ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, rowMapper, taskId, status);
    }

    @Override
    public Page<TaskExecution> findByTaskIdAndStatus(Long taskId, String status, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE task_id = ? AND status = ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, taskId, status);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE task_id = ? AND status = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, taskId, status);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<TaskExecution> findByStatus(String status) {
        String sql = "SELECT * FROM task_execution WHERE status = ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    @Override
    public Page<TaskExecution> findByStatus(String status, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE status = ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, status);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE status = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<TaskExecution> findByNodeId(String nodeId) {
        String sql = "SELECT * FROM task_execution WHERE node_id = ? ORDER BY start_time DESC";
        return jdbcTemplate.query(sql, rowMapper, nodeId);
    }

    @Override
    public Page<TaskExecution> findByNodeId(String nodeId, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE node_id = ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, nodeId);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE node_id = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, nodeId);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public List<TaskExecution> findRunning() {
        String sql = "SELECT * FROM task_execution WHERE status = 'RUNNING' ORDER BY start_time";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public TaskExecution findLatestByTaskId(Long taskId) {
        try {
            String sql = "SELECT * FROM task_execution WHERE task_id = ? ORDER BY start_time DESC LIMIT 1";
            return jdbcTemplate.queryForObject(sql, rowMapper, taskId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TaskExecution> findTimeout(long timeout) {
        String sql = "SELECT * FROM task_execution WHERE status = 'RUNNING' AND " +
                "start_time < ? AND end_time IS NULL";
        Date timeoutDate = new Date(System.currentTimeMillis() - timeout);
        return jdbcTemplate.query(sql, rowMapper, timeoutDate);
    }

    @Override
    public int updateStatus(Long executionId, String status) {
        String sql = "UPDATE task_execution SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, executionId);
    }

    @Override
    public int updateEndTimeAndStatus(Long executionId, Date endTime, String status) {
        String sql = "UPDATE task_execution SET end_time = ?, status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, endTime, status, executionId);
    }

    @Override
    public int updateResult(Long executionId, String result) {
        String sql = "UPDATE task_execution SET result = ? WHERE id = ?";
        return jdbcTemplate.update(sql, result, executionId);
    }

    @Override
    public int updateErrorMessage(Long executionId, String errorMessage) {
        String sql = "UPDATE task_execution SET error_message = ? WHERE id = ?";
        return jdbcTemplate.update(sql, errorMessage, executionId);
    }

    @Override
    public Page<TaskExecution> findByStartTimeBetween(Date startTime, Date endTime, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE start_time BETWEEN ? AND ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, startTime, endTime);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE start_time BETWEEN ? AND ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, startTime, endTime);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<TaskExecution> findByTaskIdAndStartTimeBetween(Long taskId, Date startTime, Date endTime, Pageable pageable) {
        String sql = "SELECT * FROM task_execution WHERE task_id = ? AND start_time BETWEEN ? AND ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskExecution> content = jdbcTemplate.query(sql, rowMapper, taskId, startTime, endTime);
        
        String countSql = "SELECT COUNT(1) FROM task_execution WHERE task_id = ? AND start_time BETWEEN ? AND ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, taskId, startTime, endTime);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
