package com.platform.scheduler.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.platform.scheduler.config.datasource.DynamicDataSource;

/**
 * SQL执行工具
 * 
 * @author platform
 */
@Component
public class SqlExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 在指定数据源中执行查询
     * 
     * @param <T> 结果类型
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数
     * @return 查询结果
     */
    public <T> List<T> queryForList(String dataSourceKey, String sql, RowMapper<T> rowMapper, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.query(sql, rowMapper, args);
        });
    }
    
    /**
     * 在指定数据源中执行查询
     * 
     * @param <T> 结果类型
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param requiredType 结果类型
     * @param args 参数
     * @return 查询结果
     */
    public <T> List<T> queryForList(String dataSourceKey, String sql, Class<T> requiredType, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.queryForList(sql, requiredType, args);
        });
    }
    
    /**
     * 在指定数据源中执行查询
     * 
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param args 参数
     * @return 查询结果
     */
    public List<Map<String, Object>> queryForList(String dataSourceKey, String sql, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.queryForList(sql, args);
        });
    }
    
    /**
     * 在指定数据源中执行查询
     * 
     * @param <T> 结果类型
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param requiredType 结果类型
     * @param args 参数
     * @return 查询结果
     */
    public <T> T queryForObject(String dataSourceKey, String sql, Class<T> requiredType, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.queryForObject(sql, requiredType, args);
        });
    }
    
    /**
     * 在指定数据源中执行查询
     * 
     * @param <T> 结果类型
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param rowMapper 行映射器
     * @param args 参数
     * @return 查询结果
     */
    public <T> T queryForObject(String dataSourceKey, String sql, RowMapper<T> rowMapper, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        });
    }
    
    /**
     * 在指定数据源中执行更新
     * 
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param args 参数
     * @return 影响行数
     */
    public int update(String dataSourceKey, String sql, Object... args) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.update(sql, args);
        });
    }
    
    /**
     * 在指定数据源中执行批量更新
     * 
     * @param dataSourceKey 数据源键
     * @param sql SQL语句
     * @param batchArgs 批量参数
     * @return 影响行数数组
     */
    public int[] batchUpdate(String dataSourceKey, String sql, List<Object[]> batchArgs) {
        return executeInDataSource(dataSourceKey, () -> {
            return jdbcTemplate.batchUpdate(sql, batchArgs);
        });
    }
    
    /**
     * 在指定数据源中执行操作
     * 
     * @param <T> 结果类型
     * @param dataSourceKey 数据源键
     * @param action 操作
     * @return 操作结果
     */
    public <T> T executeInDataSource(String dataSourceKey, Function<Void, T> action) {
        // 记录原始数据源
        String originalDataSource = DynamicDataSource.getDataSource();
        
        try {
            // 切换到指定数据源
            DynamicDataSource.setDataSource(dataSourceKey);
            
            // 执行操作
            return action.apply(null);
            
        } catch (Exception e) {
            logger.error("在数据源 " + dataSourceKey + " 中执行操作失败", e);
            throw e;
        } finally {
            // 恢复原始数据源
            if (originalDataSource != null) {
                DynamicDataSource.setDataSource(originalDataSource);
            } else {
                DynamicDataSource.clearDataSource();
            }
        }
    }
}
