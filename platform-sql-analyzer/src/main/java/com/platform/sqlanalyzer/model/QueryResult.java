package com.platform.sqlanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQL查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {
    /**
     * 查询成功标志
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 列名列表
     */
    private List<String> columns;
    
    /**
     * 数据行列表
     */
    private List<Map<String, Object>> data;
    
    /**
     * 执行耗时（毫秒）
     */
    private long executionTime;
    
    /**
     * 影响的行数（适用于更新操作）
     */
    private int affectedRows;
    
    /**
     * 创建失败结果
     */
    public static QueryResult fail(String message) {
        return QueryResult.builder()
                .success(false)
                .message(message)
                .columns(new ArrayList<>())
                .data(new ArrayList<>())
                .build();
    }
    
    /**
     * 创建成功结果
     */
    public static QueryResult success(List<String> columns, List<Map<String, Object>> data, long executionTime) {
        return QueryResult.builder()
                .success(true)
                .message("查询成功")
                .columns(columns)
                .data(data)
                .executionTime(executionTime)
                .build();
    }
}
