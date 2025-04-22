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
import org.springframework.stereotype.Repository;

import com.platform.scheduler.model.SchedulerNode;
import com.platform.scheduler.repository.SchedulerNodeRepository;

/**
 * 调度节点数据访问层实现类
 * 
 * @author platform
 */
@Repository
public class SchedulerNodeRepositoryImpl implements SchedulerNodeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    private final BeanPropertyRowMapper<SchedulerNode> rowMapper = new BeanPropertyRowMapper<>(SchedulerNode.class);
    
    @Override
    public SchedulerNode save(SchedulerNode node) {
        if (existsById(node.getId())) {
            return update(node);
        } else {
            return insert(node);
        }
    }
    
    /**
     * 插入节点
     * 
     * @param node 节点信息
     * @return 节点信息
     */
    private SchedulerNode insert(SchedulerNode node) {
        String sql = "INSERT INTO scheduler_node (id, host, ip, port, status, last_heartbeat, created_time, updated_time) " +
                "VALUES (:id, :host, :ip, :port, :status, :lastHeartbeat, :createdTime, :updatedTime)";
        
        if (node.getCreatedTime() == null) {
            node.setCreatedTime(new Date());
        }
        if (node.getUpdatedTime() == null) {
            node.setUpdatedTime(new Date());
        }
        if (node.getLastHeartbeat() == null) {
            node.setLastHeartbeat(new Date());
        }
        
        SqlParameterSource params = new BeanPropertySqlParameterSource(node);
        namedParameterJdbcTemplate.update(sql, params);
        
        return node;
    }
    
    /**
     * 更新节点
     * 
     * @param node 节点信息
     * @return 节点信息
     */
    private SchedulerNode update(SchedulerNode node) {
        String sql = "UPDATE scheduler_node SET host = :host, ip = :ip, port = :port, " +
                "status = :status, last_heartbeat = :lastHeartbeat, updated_time = :updatedTime " +
                "WHERE id = :id";
        
        node.setUpdatedTime(new Date());
        
        SqlParameterSource params = new BeanPropertySqlParameterSource(node);
        namedParameterJdbcTemplate.update(sql, params);
        
        return node;
    }
    
    @Override
    public List<SchedulerNode> saveAll(Iterable<SchedulerNode> entities) {
        List<SchedulerNode> result = new ArrayList<>();
        for (SchedulerNode entity : entities) {
            result.add(save(entity));
        }
        return result;
    }
    
    @Override
    public Optional<SchedulerNode> findById(String id) {
        try {
            String sql = "SELECT * FROM scheduler_node WHERE id = ?";
            SchedulerNode node = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(node);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(1) FROM scheduler_node WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    
    @Override
    public List<SchedulerNode> findAll() {
        String sql = "SELECT * FROM scheduler_node";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public Page<SchedulerNode> findAll(Pageable pageable) {
        String sql = "SELECT * FROM scheduler_node ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<SchedulerNode> content = jdbcTemplate.query(sql, rowMapper);
        
        String countSql = "SELECT COUNT(1) FROM scheduler_node";
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
            return "last_heartbeat DESC";
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
    public List<SchedulerNode> findAllById(Iterable<String> ids) {
        List<String> idList = new ArrayList<>();
        ids.forEach(idList::add);
        
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM scheduler_node WHERE id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", idList);
        
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(1) FROM scheduler_node";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
    
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM scheduler_node WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    @Override
    public void delete(SchedulerNode entity) {
        deleteById(entity.getId());
    }
    
    @Override
    public void deleteAll(Iterable<SchedulerNode> entities) {
        for (SchedulerNode entity : entities) {
            delete(entity);
        }
    }
    
    @Override
    public void deleteAll() {
        String sql = "DELETE FROM scheduler_node";
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<SchedulerNode> findByStatus(String status) {
        String sql = "SELECT * FROM scheduler_node WHERE status = ? ORDER BY last_heartbeat DESC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }
    
    @Override
    public Page<SchedulerNode> findByStatus(String status, Pageable pageable) {
        String sql = "SELECT * FROM scheduler_node WHERE status = ? ORDER BY " + getOrderBy(pageable) + 
                " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        
        List<SchedulerNode> content = jdbcTemplate.query(sql, rowMapper, status);
        
        String countSql = "SELECT COUNT(1) FROM scheduler_node WHERE status = ?";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, status);
        
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
    
    @Override
    public int updateStatus(String nodeId, String status) {
        String sql = "UPDATE scheduler_node SET status = ?, updated_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, new Date(), nodeId);
    }
    
    @Override
    public int updateHeartbeat(String nodeId, Date heartbeatTime) {
        String sql = "UPDATE scheduler_node SET last_heartbeat = ?, updated_time = ? WHERE id = ?";
        return jdbcTemplate.update(sql, heartbeatTime, new Date(), nodeId);
    }
    
    @Override
    public List<SchedulerNode> findTimeout(long timeout) {
        String sql = "SELECT * FROM scheduler_node WHERE status = 'ONLINE' AND last_heartbeat < ?";
        Date timeoutDate = new Date(System.currentTimeMillis() - timeout);
        return jdbcTemplate.query(sql, rowMapper, timeoutDate);
    }
    
    @Override
    public List<SchedulerNode> findOnline() {
        return findByStatus("ONLINE");
    }
    
    @Override
    public SchedulerNode register(SchedulerNode node) {
        node.setStatus("ONLINE");
        node.setLastHeartbeat(new Date());
        return save(node);
    }
    
    @Override
    public boolean unregister(String nodeId) {
        int rows = updateStatus(nodeId, "OFFLINE");
        return rows > 0;
    }
}
