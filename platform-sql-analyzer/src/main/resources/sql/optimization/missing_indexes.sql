-- @name: missing_indexes
-- @description: 分析可能需要添加索引的表和列，基于查询模式和表访问情况
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

-- 获取没有索引的大表（超过10000行）
SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    t.TABLE_ROWS AS '行数',
    ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024, 2) AS '大小(MB)',
    COUNT(s.INDEX_NAME) AS '索引数量',
    c.COLUMN_NAME AS '主键',
    CASE 
        WHEN COUNT(s.INDEX_NAME) = 0 THEN '无索引，建议检查'
        WHEN COUNT(s.INDEX_NAME) = 1 AND c.COLUMN_NAME IS NOT NULL THEN '仅有主键索引，可能需要额外索引'
        ELSE '索引状态正常'
    END AS '索引状态',
    (
        SELECT GROUP_CONCAT(DISTINCT st.COLUMN_NAME ORDER BY st.SEQ_IN_INDEX SEPARATOR ', ')
        FROM information_schema.STATISTICS st
        WHERE st.TABLE_SCHEMA = t.TABLE_SCHEMA
        AND st.TABLE_NAME = t.TABLE_NAME
    ) AS '已有索引字段'
FROM 
    information_schema.TABLES t
LEFT JOIN 
    information_schema.STATISTICS s
    ON t.TABLE_SCHEMA = s.TABLE_SCHEMA
    AND t.TABLE_NAME = s.TABLE_NAME
LEFT JOIN
    information_schema.COLUMNS c
    ON t.TABLE_SCHEMA = c.TABLE_SCHEMA
    AND t.TABLE_NAME = c.TABLE_NAME
    AND c.COLUMN_KEY = 'PRI'
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
    AND t.TABLE_ROWS > 10000
GROUP BY 
    t.TABLE_SCHEMA, t.TABLE_NAME, t.TABLE_ROWS, t.DATA_LENGTH, t.INDEX_LENGTH, c.COLUMN_NAME
HAVING 
    COUNT(s.INDEX_NAME) <= 1
ORDER BY 
    t.TABLE_ROWS DESC;
