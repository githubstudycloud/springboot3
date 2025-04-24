-- @name: table_size_analysis
-- @description: 分析数据库中所有表的大小情况，按照数据大小降序排列
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    t.ENGINE AS '存储引擎',
    t.TABLE_ROWS AS '行数',
    t.AVG_ROW_LENGTH AS '平均行长度(Bytes)',
    ROUND(t.DATA_LENGTH / 1024 / 1024, 2) AS '数据大小(MB)',
    ROUND(t.INDEX_LENGTH / 1024 / 1024, 2) AS '索引大小(MB)',
    ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024, 2) AS '总大小(MB)',
    ROUND(t.DATA_FREE / 1024 / 1024, 2) AS '碎片大小(MB)',
    CASE 
        WHEN t.DATA_LENGTH > 0 THEN ROUND(t.DATA_FREE * 100 / t.DATA_LENGTH, 2)
        ELSE 0 
    END AS '碎片率(%)',
    t.CREATE_TIME AS '创建时间',
    t.UPDATE_TIME AS '最后更新时间',
    t.TABLE_COMMENT AS '表注释'
FROM 
    information_schema.TABLES t
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    t.DATA_LENGTH + t.INDEX_LENGTH DESC;
