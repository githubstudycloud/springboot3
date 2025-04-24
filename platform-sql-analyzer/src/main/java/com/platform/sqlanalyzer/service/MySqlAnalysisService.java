package com.platform.sqlanalyzer.service;

import com.platform.sqlanalyzer.model.DatabaseInfo;
import com.platform.sqlanalyzer.model.DatabaseInfo.TableInfo;
import com.platform.sqlanalyzer.model.QueryRequest;
import com.platform.sqlanalyzer.model.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MySQL数据库分析服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlAnalysisService {
    
    private final SqlExecutionService sqlExecutionService;
    
    @Value("${sql-analyzer.default-timeout:30000}")
    private int defaultTimeout;
    
    /**
     * 获取数据库基本信息
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @return 数据库信息
     */
    public DatabaseInfo getDatabaseInfo(String dataSourceName, String databaseName) {
        // 1. 获取数据库基本信息
        QueryRequest dbInfoRequest = new QueryRequest();
        dbInfoRequest.setDataSource(dataSourceName);
        dbInfoRequest.setCustomSql(
                "SELECT DEFAULT_CHARACTER_SET_NAME as charset, " +
                "DEFAULT_COLLATION_NAME as collation " +
                "FROM information_schema.SCHEMATA " +
                "WHERE SCHEMA_NAME = '" + databaseName + "'");
        
        QueryResult dbInfoResult = sqlExecutionService.executeQuery(dbInfoRequest);
        if (!dbInfoResult.isSuccess() || dbInfoResult.getData().isEmpty()) {
            throw new RuntimeException("获取数据库信息失败: " + dbInfoResult.getMessage());
        }
        
        Map<String, Object> dbInfoRow = dbInfoResult.getData().get(0);
        
        // 2. 获取表列表及数据大小
        QueryRequest tablesRequest = new QueryRequest();
        tablesRequest.setDataSource(dataSourceName);
        tablesRequest.setCustomSql(
                "SELECT t.TABLE_NAME as name, " +
                "t.ENGINE as engine, " +
                "t.TABLE_ROWS as rowCount, " +
                "t.AVG_ROW_LENGTH as avgRowLength, " +
                "t.DATA_LENGTH as size, " +
                "t.INDEX_LENGTH as indexSize, " +
                "t.DATA_FREE as dataFree, " +
                "t.AUTO_INCREMENT as autoIncrement, " +
                "t.CREATE_TIME as createTime, " +
                "t.UPDATE_TIME as lastUpdate, " +
                "t.TABLE_COMMENT as comment " +
                "FROM information_schema.TABLES t " +
                "WHERE t.TABLE_SCHEMA = '" + databaseName + "' " +
                "ORDER BY t.DATA_LENGTH DESC");
        
        QueryResult tablesResult = sqlExecutionService.executeQuery(tablesRequest);
        if (!tablesResult.isSuccess()) {
            throw new RuntimeException("获取表信息失败: " + tablesResult.getMessage());
        }
        
        List<TableInfo> tables = new ArrayList<>();
        long totalSize = 0;
        
        for (Map<String, Object> row : tablesResult.getData()) {
            TableInfo tableInfo = mapRowToTableInfo(row);
            tables.add(tableInfo);
            totalSize += tableInfo.getSize() + tableInfo.getIndexSize();
            
            // 获取表的列数和索引数
            enrichTableWithColumnsAndIndexes(dataSourceName, databaseName, tableInfo);
            
            // 估算表的增长率和最近使用情况
            estimateTableGrowthAndUsage(dataSourceName, databaseName, tableInfo);
        }
        
        return DatabaseInfo.builder()
                .name(databaseName)
                .size(totalSize)
                .tableCount(tables.size())
                .charset(String.valueOf(dbInfoRow.get("charset")))
                .collation(String.valueOf(dbInfoRow.get("collation")))
                .tables(tables)
                .build();
    }
    
    /**
     * 获取表最近是否被使用
     *
     * @param dataSourceName 数据源名称
     * @param tableName 表名
     * @return 是否最近被使用
     */
    public boolean isTableRecentlyUsed(String dataSourceName, String databaseName, String tableName) {
        // MySQL 5.7中查询表使用情况，可使用performance_schema
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT COUNT(*) as count FROM performance_schema.table_io_waits_summary_by_table " +
                "WHERE OBJECT_SCHEMA = '" + databaseName + "' " +
                "AND OBJECT_NAME = '" + tableName + "' " +
                "AND (COUNT_FETCH > 0 OR COUNT_INSERT > 0 OR COUNT_UPDATE > 0 OR COUNT_DELETE > 0)");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess() && !result.getData().isEmpty()) {
            Long count = (Long) result.getData().get(0).get("count");
            return count != null && count > 0;
        }
        return false; // 如果查询失败或无法确定，则假设未使用
    }
    
    /**
     * 获取表碎片率
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 碎片率（百分比）
     */
    public double getTableFragmentationRatio(String dataSourceName, String databaseName, String tableName) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT DATA_FREE, DATA_LENGTH FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = '" + databaseName + "' " +
                "AND TABLE_NAME = '" + tableName + "'");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess() && !result.getData().isEmpty()) {
            Map<String, Object> row = result.getData().get(0);
            Long dataFree = convertToLong(row.get("DATA_FREE"));
            Long dataLength = convertToLong(row.get("DATA_LENGTH"));
            
            if (dataLength > 0) {
                return (double) dataFree / dataLength * 100;
            }
        }
        return 0.0;
    }
    
    /**
     * 获取表增长率
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 每日增长率（百分比）
     */
    public double estimateTableGrowthRate(String dataSourceName, String databaseName, String tableName) {
        // 注意：这是一个近似计算，实际增长率需要历史数据
        // 这里仅作演示，实际项目中可以通过binlog或统计信息更准确计算
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT TABLE_ROWS, CREATE_TIME, UPDATE_TIME " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = '" + databaseName + "' " +
                "AND TABLE_NAME = '" + tableName + "'");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess() && !result.getData().isEmpty()) {
            Map<String, Object> row = result.getData().get(0);
            
            // 实际实现中，这里应该根据历史记录计算真实增长率
            // 这里只是根据创建时间和当前行数做一个简单估计
            return 0.5; // 假设每天增长0.5%
        }
        return 0.0;
    }
    
    /**
     * 分析表字段使用情况
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 字段使用情况
     */
    public List<Map<String, Object>> analyzeTableColumns(String dataSourceName, String databaseName, String tableName) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT c.COLUMN_NAME, c.COLUMN_TYPE, c.IS_NULLABLE, " +
                "c.COLUMN_KEY, c.COLUMN_DEFAULT, c.EXTRA, c.COLUMN_COMMENT " +
                "FROM information_schema.COLUMNS c " +
                "WHERE c.TABLE_SCHEMA = '" + databaseName + "' " +
                "AND c.TABLE_NAME = '" + tableName + "' " +
                "ORDER BY c.ORDINAL_POSITION");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess()) {
            return result.getData();
        }
        return Collections.emptyList();
    }
    
    /**
     * 分析表索引使用情况
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @param tableName 表名
     * @return 索引使用情况
     */
    public List<Map<String, Object>> analyzeTableIndexes(String dataSourceName, String databaseName, String tableName) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT s.INDEX_NAME, s.COLUMN_NAME, s.SEQ_IN_INDEX, " +
                "s.NON_UNIQUE, s.INDEX_TYPE " +
                "FROM information_schema.STATISTICS s " +
                "WHERE s.TABLE_SCHEMA = '" + databaseName + "' " +
                "AND s.TABLE_NAME = '" + tableName + "' " +
                "ORDER BY s.INDEX_NAME, s.SEQ_IN_INDEX");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess()) {
            // 按索引名称分组
            Map<String, List<Map<String, Object>>> indexMap = new HashMap<>();
            
            for (Map<String, Object> row : result.getData()) {
                String indexName = String.valueOf(row.get("INDEX_NAME"));
                indexMap.computeIfAbsent(indexName, k -> new ArrayList<>()).add(row);
            }
            
            // 组合结果
            List<Map<String, Object>> indexList = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : indexMap.entrySet()) {
                String indexName = entry.getKey();
                List<Map<String, Object>> columns = entry.getValue();
                
                Map<String, Object> indexInfo = new HashMap<>();
                indexInfo.put("name", indexName);
                indexInfo.put("type", columns.get(0).get("INDEX_TYPE"));
                indexInfo.put("unique", !((Long) columns.get(0).get("NON_UNIQUE") == 1));
                
                List<String> columnNames = columns.stream()
                        .map(col -> String.valueOf(col.get("COLUMN_NAME")))
                        .collect(Collectors.toList());
                
                indexInfo.put("columns", columnNames);
                indexList.add(indexInfo);
            }
            
            return indexList;
        }
        return Collections.emptyList();
    }
    
    /**
     * 获取未使用索引列表
     *
     * @param dataSourceName 数据源名称
     * @param databaseName 数据库名称
     * @return 未使用索引列表
     */
    public List<Map<String, Object>> findUnusedIndexes(String dataSourceName, String databaseName) {
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setDataSource(dataSourceName);
        queryRequest.setCustomSql(
                "SELECT object_schema as 'database', " +
                "object_name as 'table', " +
                "index_name as 'index', " +
                "count_star as 'total_operations', " +
                "count_read as 'read_operations', " +
                "count_fetch as 'fetch_operations', " +
                "count_insert as 'insert_operations', " +
                "count_update as 'update_operations', " +
                "count_delete as 'delete_operations' " +
                "FROM performance_schema.table_io_waits_summary_by_index_usage " +
                "WHERE object_schema = '" + databaseName + "' " +
                "AND index_name IS NOT NULL " +
                "AND count_star = 0 " +
                "ORDER BY object_name, index_name");
        
        QueryResult result = sqlExecutionService.executeQuery(queryRequest);
        if (result.isSuccess()) {
            return result.getData();
        }
        return Collections.emptyList();
    }
    
    /**
     * 将行数据映射为TableInfo对象
     */
    private TableInfo mapRowToTableInfo(Map<String, Object> row) {
        return TableInfo.builder()
                .name(String.valueOf(row.get("name")))
                .engine(String.valueOf(row.get("engine")))
                .rowCount(convertToLong(row.get("rowCount")))
                .avgRowLength(convertToDouble(row.get("avgRowLength")))
                .size(convertToLong(row.get("size")))
                .indexSize(convertToLong(row.get("indexSize")))
                .dataFree(convertToLong(row.get("dataFree")))
                .autoIncrement(convertToLong(row.get("autoIncrement")))
                .createTime(String.valueOf(row.get("createTime")))
                .lastUpdate(row.get("lastUpdate") != null ? String.valueOf(row.get("lastUpdate")) : null)
                .comment(String.valueOf(row.get("comment")))
                .fragmentationRatio(0.0) // 将在后续步骤中填充
                .dailyGrowthRate(0.0) // 将在后续步骤中填充
                .recentlyUsed(false) // 将在后续步骤中填充
                .columnCount(0) // 将在后续步骤中填充
                .indexCount(0) // 将在后续步骤中填充
                .rowsAdded7Days(0) // 将在后续步骤中填充
                .rowsAdded30Days(0) // 将在后续步骤中填充
                .build();
    }
    
    /**
     * 使用列和索引信息丰富表信息
     */
    private void enrichTableWithColumnsAndIndexes(String dataSourceName, String databaseName, TableInfo tableInfo) {
        // 获取列数
        QueryRequest columnsRequest = new QueryRequest();
        columnsRequest.setDataSource(dataSourceName);
        columnsRequest.setCustomSql(
                "SELECT COUNT(*) as count FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = '" + databaseName + "' " +
                "AND TABLE_NAME = '" + tableInfo.getName() + "'");
        
        QueryResult columnsResult = sqlExecutionService.executeQuery(columnsRequest);
        if (columnsResult.isSuccess() && !columnsResult.getData().isEmpty()) {
            tableInfo.setColumnCount(((Number) columnsResult.getData().get(0).get("count")).intValue());
        }
        
        // 获取索引数
        QueryRequest indexesRequest = new QueryRequest();
        indexesRequest.setDataSource(dataSourceName);
        indexesRequest.setCustomSql(
                "SELECT COUNT(DISTINCT INDEX_NAME) as count FROM information_schema.STATISTICS " +
                "WHERE TABLE_SCHEMA = '" + databaseName + "' " +
                "AND TABLE_NAME = '" + tableInfo.getName() + "'");
        
        QueryResult indexesResult = sqlExecutionService.executeQuery(indexesRequest);
        if (indexesResult.isSuccess() && !indexesResult.getData().isEmpty()) {
            tableInfo.setIndexCount(((Number) indexesResult.getData().get(0).get("count")).intValue());
        }
        
        // 计算碎片率
        double fragmentationRatio = getTableFragmentationRatio(dataSourceName, databaseName, tableInfo.getName());
        tableInfo.setFragmentationRatio(fragmentationRatio);
    }
    
    /**
     * 估算表的增长率和最近使用情况
     */
    private void estimateTableGrowthAndUsage(String dataSourceName, String databaseName, TableInfo tableInfo) {
        // 估算增长率
        double growthRate = estimateTableGrowthRate(dataSourceName, databaseName, tableInfo.getName());
        tableInfo.setDailyGrowthRate(growthRate);
        
        // 检查最近是否使用
        boolean recentlyUsed = isTableRecentlyUsed(dataSourceName, databaseName, tableInfo.getName());
        tableInfo.setRecentlyUsed(recentlyUsed);
        
        // 在实际项目中，这里应该添加最近7天和30天的行数增长统计
        // 这里简单估算
        tableInfo.setRowsAdded7Days((long) (tableInfo.getRowCount() * growthRate * 7 / 100));
        tableInfo.setRowsAdded30Days((long) (tableInfo.getRowCount() * growthRate * 30 / 100));
    }
    
    /**
     * 将对象转换为Long
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    /**
     * 将对象转换为Double
     */
    private Double convertToDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
