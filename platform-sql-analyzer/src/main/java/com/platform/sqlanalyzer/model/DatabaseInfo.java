package com.platform.sqlanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据库信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseInfo {
    /**
     * 数据库名称
     */
    private String name;
    
    /**
     * 数据库大小（字节）
     */
    private long size;
    
    /**
     * 表数量
     */
    private int tableCount;
    
    /**
     * 数据库字符集
     */
    private String charset;
    
    /**
     * 数据库排序规则
     */
    private String collation;
    
    /**
     * 表信息列表
     */
    private List<TableInfo> tables;
    
    /**
     * 表信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableInfo {
        /**
         * 表名
         */
        private String name;
        
        /**
         * 表大小（字节）
         */
        private long size;
        
        /**
         * 行数
         */
        private long rowCount;
        
        /**
         * 平均行长度（字节）
         */
        private double avgRowLength;
        
        /**
         * 表引擎
         */
        private String engine;
        
        /**
         * 最后更新时间
         */
        private String lastUpdate;
        
        /**
         * 创建时间
         */
        private String createTime;
        
        /**
         * 表备注
         */
        private String comment;
        
        /**
         * 自动增长值（若有）
         */
        private Long autoIncrement;
        
        /**
         * 索引大小（字节）
         */
        private long indexSize;
        
        /**
         * 数据空闲空间（字节）
         */
        private long dataFree;
        
        /**
         * 最近7天新增行数
         */
        private long rowsAdded7Days;
        
        /**
         * 最近30天新增行数
         */
        private long rowsAdded30Days;
        
        /**
         * 是否最近有被使用
         */
        private boolean recentlyUsed;
        
        /**
         * 表每日增长率（百分比）
         */
        private double dailyGrowthRate;
        
        /**
         * 字段数量
         */
        private int columnCount;
        
        /**
         * 索引数量
         */
        private int indexCount;
        
        /**
         * 表的碎片率（百分比）
         */
        private double fragmentationRatio;
    }
}
