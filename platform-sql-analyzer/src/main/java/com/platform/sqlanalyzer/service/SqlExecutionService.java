package com.platform.sqlanalyzer.service;

import com.platform.sqlanalyzer.config.DataSourceConfig.DynamicDataSourceManager;
import com.platform.sqlanalyzer.config.SqlScriptConfig;
import com.platform.sqlanalyzer.config.SqlScriptConfig.SqlScript;
import com.platform.sqlanalyzer.model.QueryRequest;
import com.platform.sqlanalyzer.model.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * SQL执行服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SqlExecutionService {
    
    private final Map<String, Map<String, SqlScript>> sqlScripts;
    private final DynamicDataSourceManager dataSourceManager;
    
    @Autowired
    @Qualifier("defaultJdbcTemplate")
    private JdbcTemplate defaultJdbcTemplate;
    
    /**
     * 执行SQL查询
     */
    @Cacheable(value = "sqlQueryCache", key = "#request.toString()", 
            condition = "@environment.getProperty('sql-analyzer.cache-enabled', Boolean.class, false)")
    public QueryResult executeQuery(QueryRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(request.getDataSource());
            if (jdbcTemplate == null) {
                return QueryResult.fail("数据源[" + request.getDataSource() + "]不存在或未配置");
            }
            
            // 确定执行的SQL和参数
            String sql;
            Map<String, Object> params;
            int timeout;
            
            // 使用预定义脚本或自定义SQL
            if (StringUtils.hasText(request.getScriptName())) {
                SqlScript script = getScript(request.getCategory(), request.getScriptName());
                if (script == null) {
                    return QueryResult.fail("未找到脚本: " + request.getCategory() + "/" + request.getScriptName());
                }
                sql = script.getSql();
                params = request.getParameters();
                timeout = request.getTimeout() > 0 ? request.getTimeout() : script.getTimeout();
            } else if (StringUtils.hasText(request.getCustomSql())) {
                sql = request.getCustomSql();
                params = request.getParameters();
                timeout = request.getTimeout();
            } else {
                return QueryResult.fail("必须指定脚本名称或自定义SQL");
            }
            
            // 执行查询
            NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<Map<String, Object>> results = namedJdbcTemplate.queryForList(sql, params);
            
            // 提取列名
            List<String> columns = results.isEmpty() ? new ArrayList<>() : 
                    new ArrayList<>(results.get(0).keySet());
            
            long endTime = System.currentTimeMillis();
            return QueryResult.success(columns, results, endTime - startTime);
        } catch (Exception e) {
            log.error("执行SQL查询失败", e);
            long endTime = System.currentTimeMillis();
            return QueryResult.fail("执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有脚本分类
     */
    public List<String> getScriptCategories() {
        return new ArrayList<>(sqlScripts.keySet());
    }
    
    /**
     * 获取指定分类下的所有脚本
     */
    public List<Map<String, Object>> getScriptsByCategory(String category) {
        Map<String, SqlScript> scripts = sqlScripts.getOrDefault(category, Collections.emptyMap());
        
        return scripts.values().stream()
                .map(script -> {
                    Map<String, Object> scriptInfo = new HashMap<>();
                    scriptInfo.put("name", script.getName());
                    scriptInfo.put("description", script.getDescription());
                    scriptInfo.put("parameters", script.getParameters());
                    return scriptInfo;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取脚本详情
     */
    public SqlScript getScriptDetails(String category, String name) {
        return getScript(category, name);
    }
    
    /**
     * 添加数据源
     */
    public boolean addDataSource(String name, String url, String username, String password) {
        try {
            dataSourceManager.addDataSource(name, url, username, password);
            return true;
        } catch (Exception e) {
            log.error("添加数据源失败: {}", name, e);
            return false;
        }
    }
    
    /**
     * 获取所有可用数据源名称
     */
    public Set<String> getDataSourceNames() {
        return dataSourceManager.getDataSourceNames();
    }
    
    /**
     * 移除数据源
     */
    public boolean removeDataSource(String name) {
        if ("default".equals(name)) {
            return false; // 不允许移除默认数据源
        }
        
        try {
            dataSourceManager.removeDataSource(name);
            return true;
        } catch (Exception e) {
            log.error("移除数据源失败: {}", name, e);
            return false;
        }
    }
    
    /**
     * 获取JdbcTemplate
     */
    private JdbcTemplate getJdbcTemplate(String dataSourceName) {
        if ("default".equals(dataSourceName)) {
            return defaultJdbcTemplate;
        }
        return dataSourceManager.getJdbcTemplate(dataSourceName);
    }
    
    /**
     * 获取数据源
     */
    public javax.sql.DataSource getDataSource(String dataSourceName) {
        if ("default".equals(dataSourceName)) {
            return defaultJdbcTemplate.getDataSource();
        }
        return dataSourceManager.getDataSource(dataSourceName);
    }
    
    /**
     * 获取SQL脚本
     */
    private SqlScript getScript(String category, String name) {
        Map<String, SqlScript> categoryScripts = sqlScripts.get(category);
        if (categoryScripts == null) {
            return null;
        }
        return categoryScripts.get(name);
    }
}
