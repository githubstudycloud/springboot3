package com.platform.sqlanalyzer.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * SQL查询请求
 */
@Data
public class QueryRequest {
    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空")
    private String dataSource;
    
    /**
     * 脚本名称，优先于自定义SQL
     */
    private String scriptName;
    
    /**
     * 脚本分类
     */
    private String category = "default";
    
    /**
     * 自定义SQL，当scriptName为空时生效
     */
    private String customSql;
    
    /**
     * 查询参数
     */
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 超时时间（毫秒），0表示使用默认超时
     */
    private int timeout = 0;
}
