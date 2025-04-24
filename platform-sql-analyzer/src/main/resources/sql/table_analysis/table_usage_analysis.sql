-- @name: table_usage_analysis
-- @description: 分析数据库中表的使用情况，检测长时间未使用的表
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    ps.COUNT_STAR AS '总访问次数',
    ps.COUNT_FETCH AS '读取次数',
    ps.COUNT_INSERT AS '插入次数',
    ps.COUNT_UPDATE AS '更新次数',
    ps.COUNT_DELETE AS '删除次数',
    t.CREATE_TIME AS '创建时间',
    t.UPDATE_TIME AS '最后更新时间',
    CASE 
        WHEN ps.COUNT_STAR IS NULL OR ps.COUNT_STAR = 0 THEN '从未使用'
        WHEN ps.COUNT_FETCH = 0 AND ps.COUNT_INSERT = 0 AND ps.COUNT_UPDATE = 0 AND ps.COUNT_DELETE = 0 THEN '从未使用'
        WHEN ps.COUNT_FETCH > 0 AND ps.COUNT_INSERT = 0 AND ps.COUNT_UPDATE = 0 AND ps.COUNT_DELETE = 0 THEN '只读表'
        WHEN ps.COUNT_FETCH = 0 AND (ps.COUNT_INSERT > 0 OR ps.COUNT_UPDATE > 0 OR ps.COUNT_DELETE > 0) THEN '只写表'
        ELSE '正常使用'
    END AS '使用情况',
    t.TABLE_ROWS AS '行数',
    ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024, 2) AS '总大小(MB)'
FROM 
    information_schema.TABLES t
LEFT JOIN 
    performance_schema.table_io_waits_summary_by_table ps
    ON t.TABLE_SCHEMA = ps.OBJECT_SCHEMA AND t.TABLE_NAME = ps.OBJECT_NAME
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    ps.COUNT_STAR IS NULL DESC,
    ps.COUNT_STAR ASC,
    t.DATA_LENGTH + t.INDEX_LENGTH DESC;
