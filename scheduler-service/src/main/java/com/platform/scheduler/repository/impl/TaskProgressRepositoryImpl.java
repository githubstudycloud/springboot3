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

import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.repository.TaskProgressRepository;

/**
 * 任务进度数据访问层实现类
 * 
 * @author platform
 */
@Repository
public class TaskProgressRepositoryImpl implements TaskProgressRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final BeanPropertyRowMapper<TaskProgress> rowMapper = new BeanPropertyRowMapper<>(TaskProgress.class);
    
    @Override
    public TaskProgress save(TaskProgress progress) {
        if (progress.getId() == null) {
            return insert(progress);
        } else {
            return update(progress);
        }
    }
    
    /**
     * 插入进度
     * 
     * @param progress 进度实体
     * @return 插入后的进度实体
     */
    private TaskProgress insert(TaskProgress progress) {
        String sql = "INSERT INTO task_progress (task_id, execution_id, progress, status_desc, updated_time) " +
                "VALUES (:taskId, :executionId, :progress, :statusDesc, :updatedTime)";
        
        if (progress.getUpdatedTime() == null) {
            progress.setUpdatedTime(new Date());
        }
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new BeanPropertySqlParameterSource(progress);
        
        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        progress.setId(keyHolder.getKey().longValue());
        
        return progress;
    }
    
    /**
     * 更新进度
     * 
     * @param progress 进度实体
     * @return 更新后的进度实体
     */
    private TaskProgress update(TaskProgress progress) {
        String sql = "UPDATE task_progress SET task_id = :taskId, execution_id = :executionId, " +
                "progress = :progress, status_desc = :statusDesc, updated_time = :updatedTime " +
                "WHERE id = :id";
        
        progress.setUpdatedTime(new Date());
        
        SqlParameterSource params = new BeanPropertySqlParameterSource(progress);
        namedParameterJdbcTemplate.update(sql, params);
        
        return progress;
    }
    
    @Override
    public List<TaskProgress> saveAll(Iterable<TaskProgress> entities) {
        List<TaskProgress> result = new ArrayList<>();
        for (TaskProgress entity : entities) {
            result.add(save(entity));
        }
        return result;
    }
    
    @Override
    public Optional<TaskProgress> findById(Long id) {
        try {
            String sql = "SELECT * FROM task_progress WHERE id = ?";
            TaskProgress progress = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(progress);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(1) FROM task_progress WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    @Override
    public List<TaskProgress> findAll() {
        String sql = "SELECT * FROM task_progress";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public Page<TaskProgress> findAll(Pageable pageable) {
        String sql = "SELECT * FROM task_progress ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskProgress> content = jdbcTemplate.query(sql, rowMapper);
        
        String countSql = "SELECT COUNT(1) FROM task_progress";
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
            return "updated_time DESC";
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
    public List<TaskProgress> findAllById(Iterable<Long> ids) {
        List<Long> idList = new ArrayList<>();
        ids.forEach(idList::add);
        
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM task_progress WHERE id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", idList);
        
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(1) FROM task_progress";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
    
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM task_progress WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    @Override
    public void delete(TaskProgress entity) {
        deleteById(entity.getId());
    }
    
    @Override
    public void deleteAll(Iterable<TaskProgress> entities) {
        for (TaskProgress entity : entities) {
            delete(entity);
        }
    }
    
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM task_progress";
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<TaskProgress> findByTaskId(Long taskId) {
        String sql = "SELECT * FROM task_progress WHERE task_id = ? ORDER BY updated_time DESC";
        return jdbcTemplate.query(sql, rowMapper, taskId);
    }
    
    @Override
    public Page<TaskProgress> findByTaskId(Long taskId, Pageable pageable) {
        String sql = "SELECT * FROM task_progress WHERE task_id = ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<TaskProgress> content = jdbcTemplate.query(sql, rowMapper, taskId);
        
        String countSql = "SELECT COUNT(1) FROM task_progress WHERE task_id = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, taskId);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public Optional<TaskProgress> findByExecutionId(Long executionId) {
        try {
            String sql = "SELECT * FROM task_progress WHERE execution_id = ?";
            TaskProgress progress = jdbcTemplate.queryForObject(sql, rowMapper, executionId);
            return Optional.ofNullable(progress);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<TaskProgress> findByTaskIdAndExecutionId(Long taskId, Long executionId) {
        try {
            String sql = "SELECT * FROM task_progress WHERE task_id = ? AND execution_id = ?";
            TaskProgress progress = jdbcTemplate.queryForObject(sql, rowMapper, taskId, executionId);
            return Optional.ofNullable(progress);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public TaskProgress updateProgress(TaskProgress progress) {
        // 先检查是否存在
        Optional<TaskProgress> existingOpt = findByTaskIdAndExecutionId(progress.getTaskId(), progress.getExecutionId());
        
        if (existingOpt.isPresent()) {
            // 存在则更新
            TaskProgress existing = existingOpt.get();
            existing.setProgress(progress.getProgress());
            existing.setStatusDesc(progress.getStatusDesc());
            existing.setUpdatedTime(new Date());
            return update(existing);
        } else {
            // 不存在则插入
            return insert(progress);
        }
    }
    
    @Override
    public void deleteByTaskId(Long taskId) {
        String sql = "DELETE FROM task_progress WHERE task_id = ?";
        jdbcTemplate.update(sql, taskId);
    }
    
    @Override
    public void deleteByExecutionId(Long executionId) {
        String sql = "DELETE FROM task_progress WHERE execution_id = ?";
        jdbcTemplate.update(sql, executionId);
    }
}
