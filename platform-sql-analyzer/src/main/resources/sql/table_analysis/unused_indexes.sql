-- @name: unused_indexes
-- @description: 分析未使用的索引，找出可能可以删除的冗余索引
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    ps.OBJECT_SCHEMA AS '数据库名',
    ps.OBJECT_NAME AS '表名',
    ps.INDEX_NAME AS '索引名',
    t.ENGINE AS '存储引擎',
    ROUND(i.INDEX_LENGTH / 1024 / 1024, 2) AS '索引大小(MB)',
    ps.COUNT_STAR AS '总访问次数',
    ps.COUNT_FETCH AS '读取次数',
    t.TABLE_ROWS AS '表行数',
    CASE 
        WHEN i.INDEX_NAME = 'PRIMARY' THEN '主键索引'
        WHEN i.INDEX_NAME LIKE 'fk_%' THEN '外键索引'
        WHEN i.NON_UNIQUE = 0 THEN '唯一索引'
        ELSE '普通索引'
    END AS '索引类型',
    GROUP_CONCAT(DISTINCT i.COLUMN_NAME ORDER BY i.SEQ_IN_INDEX SEPARATOR ', ') AS '索引字段',
    CASE 
        WHEN i.INDEX_NAME = 'PRIMARY' THEN '系统必须'
        WHEN i.INDEX_NAME LIKE 'fk_%' THEN '外键约束'
        WHEN ps.COUNT_STAR = 0 THEN '建议删除'
        WHEN ps.COUNT_FETCH = 0 THEN '只用于写入操作'
        ELSE '正常使用'
    END AS '使用状态'
FROM 
    performance_schema.table_io_waits_summary_by_index_usage ps
JOIN 
    information_schema.STATISTICS i 
    ON ps.OBJECT_SCHEMA = i.TABLE_SCHEMA 
    AND ps.OBJECT_NAME = i.TABLE_NAME 
    AND ps.INDEX_NAME = i.INDEX_NAME
JOIN 
    information_schema.TABLES t 
    ON ps.OBJECT_SCHEMA = t.TABLE_SCHEMA 
    AND ps.OBJECT_NAME = t.TABLE_NAME
WHERE 
    ps.OBJECT_SCHEMA = :dbName
    AND ps.INDEX_NAME IS NOT NULL
GROUP BY 
    ps.OBJECT_SCHEMA, ps.OBJECT_NAME, ps.INDEX_NAME, t.ENGINE, i.INDEX_LENGTH, ps.COUNT_STAR, 
    ps.COUNT_FETCH, t.TABLE_ROWS
ORDER BY 
    ps.COUNT_STAR ASC, ps.COUNT_FETCH ASC, i.INDEX_LENGTH DESC;
