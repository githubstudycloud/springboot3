package com.platform.scheduler.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.platform.scheduler.model.Task;
import com.platform.scheduler.repository.TaskRepository;

/**
 * 任务数据访问层实现类
 * 
 * @author platform
 */
@Repository
public class TaskRepositoryImpl implements TaskRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final BeanPropertyRowMapper<Task> rowMapper = new BeanPropertyRowMapper<>(Task.class);
    
    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            return insert(task);
        } else {
            return update(task);
        }
    }
    
    /**
     * 插入任务
     * 
     * @param task 任务实体
     * @return 插入后的任务实体
     */
    private Task insert(Task task) {
        String sql = "INSERT INTO task (name, description, type, target, parameters, " +
                "schedule_type, cron_expression, fixed_rate, fixed_delay, initial_delay, " +
                "timeout, retry_count, retry_interval, status, current_retry_count, " +
                "last_execution_time, next_execution_time, created_time, updated_time) " +
                "VALUES (:name, :description, :type, :target, :parameters, " +
                ":scheduleType, :cronExpression, :fixedRate, :fixedDelay, :initialDelay, " +
                ":timeout, :retryCount, :retryInterval, :status, :currentRetryCount, " +
                ":lastExecutionTime, :nextExecutionTime, :createdTime, :updatedTime)";
        
        if (task.getCreatedTime() == null) {
            task.setCreatedTime(new Date());
        }
        if (task.getUpdatedTime() == null) {
            task.setUpdatedTime(new Date());
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(task);
        
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        task.setId(keyHolder.getKey().longValue());
        
        return task;
    }
    
    /**
     * 更新任务
     * 
     * @param task 任务实体
     * @return 更新后的任务实体
     */
    private Task update(Task task) {
        String sql = "UPDATE task SET name = :name, description = :description, type = :type, " +
                "target = :target, parameters = :parameters, schedule_type = :scheduleType, " +
                "cron_expression = :cronExpression, fixed_rate = :fixedRate, fixed_delay = :fixedDelay, " +
                "initial_delay = :initialDelay, timeout = :timeout, retry_count = :retryCount, " +
                "retry_interval = :retryInterval, status = :status, current_retry_count = :currentRetryCount, " +
                "last_execution_time = :lastExecutionTime, next_execution_time = :nextExecutionTime, " +
                "updated_time = :updatedTime WHERE id = :id";
        
        task.setUpdatedTime(new Date());
        
        SqlParameterSource params = new BeanPropertySqlParameterSource(task);
        namedParameterJdbcTemplate.update(sql, params);
        
        return task;
    }
    
    @Override
    public List<Task> saveAll(Iterable<Task> tasks) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            result.add(save(task));
        }
        return result;
    }
    
    @Override
    public Optional<Task> findById(Long id) {
        try {
            String sql = "SELECT * FROM task WHERE id = ?";
            Task task = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(task);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(1) FROM task WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    @Override
    public List<Task> findAll() {
        String sql = "SELECT * FROM task";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public Page<Task> findAll(Pageable pageable) {
        String sql = "SELECT * FROM task ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper);
        
        String countSql = "SELECT COUNT(1) FROM task";
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
            return "id DESC";
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
    public List<Task> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<>();
        ids.forEach(idList::add);
        
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM task WHERE id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", idList);
        
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(1) FROM task";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
    
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM task WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    @Override
    public void delete(Task entity) {
        deleteById(entity.getId());
    }
    
    @Override
    public void deleteAll(Iterable<Task> entities) {
        for (Task entity : entities) {
            delete(entity);
        }
    }
    
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM task";
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Task> findByStatus(String status) {
        String sql = "SELECT * FROM task WHERE status = ?";
        return jdbcTemplate.query(sql, rowMapper, status);
    }
    
    @Override
    public Page<Task> findByStatus(String status, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE status = ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, status);
        
        String countSql = "SELECT COUNT(1) FROM task WHERE status = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Page<Task> findByNameContaining(String name, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE name LIKE ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, "%" + name + "%");
        
        String countSql = "SELECT COUNT(1) FROM task WHERE name LIKE ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, "%" + name + "%");
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Page<Task> findByStatusAndNameContaining(String status, String name, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE status = ? AND name LIKE ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, status, "%" + name + "%");
        
        String countSql = "SELECT COUNT(1) FROM task WHERE status = ? AND name LIKE ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status, "%" + name + "%");
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public List<Task> findTasksDueForExecution(Date currentTime) {
        String sql = "SELECT * FROM task WHERE status IN ('ENABLED', 'RETRY_PENDING') " +
                "AND next_execution_time <= ? ORDER BY next_execution_time";
        
        return jdbcTemplate.query(sql, rowMapper, currentTime);
    }
    
    @Override
    public int updateStatus(Long taskId, String status) {
        String sql = "UPDATE task SET status = ?, updated_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, new Date(), taskId);
    }
    
    @Override
    public int updateNextExecutionTime(Long taskId, Date nextExecutionTime) {
        String sql = "UPDATE task SET next_execution_time = ?, updated_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, nextExecutionTime, new Date(), taskId);
    }
    
    @Override
    public Page<Task> findByType(String type, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE type = ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, type);
        
        String countSql = "SELECT COUNT(1) FROM task WHERE type = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, type);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Page<Task> findByStatusAndType(String status, String type, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE status = ? AND type = ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, status, type);
        
        String countSql = "SELECT COUNT(1) FROM task WHERE status = ? AND type = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status, type);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Page<Task> findByStatusAndTypeAndNameContaining(String status, String type, String name, Pageable pageable) {
        String sql = "SELECT * FROM task WHERE status = ? AND type = ? AND name LIKE ? ORDER BY " + 
                getOrderBy(pageable) + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<Task> content = jdbcTemplate.query(sql, rowMapper, status, type, "%" + name + "%");
        
        String countSql = "SELECT COUNT(1) FROM task WHERE status = ? AND type = ? AND name LIKE ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status, type, "%" + name + "%");
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
