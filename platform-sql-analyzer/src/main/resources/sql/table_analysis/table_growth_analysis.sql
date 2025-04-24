-- @name: table_growth_analysis
-- @description: 估算表的增长速度，根据行数和创建时间计算每日平均增长行数
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    t.TABLE_ROWS AS '当前行数',
    t.CREATE_TIME AS '创建时间',
    DATEDIFF(NOW(), t.CREATE_TIME) AS '存在天数',
    CASE 
        WHEN DATEDIFF(NOW(), t.CREATE_TIME) > 0 
        THEN ROUND(t.TABLE_ROWS / DATEDIFF(NOW(), t.CREATE_TIME), 2)
        ELSE t.TABLE_ROWS
    END AS '平均每日增长行数',
    CASE 
        WHEN t.TABLE_ROWS > 0 AND DATEDIFF(NOW(), t.CREATE_TIME) > 0 
        THEN ROUND(t.TABLE_ROWS / DATEDIFF(NOW(), t.CREATE_TIME) * 100 / t.TABLE_ROWS, 4)
        ELSE 0
    END AS '每日增长率(%)',
    t.AVG_ROW_LENGTH AS '平均行长度(Bytes)',
    CASE 
        WHEN DATEDIFF(NOW(), t.CREATE_TIME) > 0 
        THEN ROUND(t.TABLE_ROWS / DATEDIFF(NOW(), t.CREATE_TIME) * t.AVG_ROW_LENGTH / 1024 / 1024, 4)
        ELSE 0
    END AS '每日增长数据量(MB)',
    ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024, 2) AS '当前总大小(MB)',
    CASE 
        WHEN DATEDIFF(NOW(), t.CREATE_TIME) > 0 
        THEN ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024 / DATEDIFF(NOW(), t.CREATE_TIME), 4)
        ELSE 0
    END AS '每日增长大小(MB)'
FROM 
    information_schema.TABLES t
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    (CASE 
        WHEN DATEDIFF(NOW(), t.CREATE_TIME) > 0 
        THEN ROUND(t.TABLE_ROWS / DATEDIFF(NOW(), t.CREATE_TIME) * t.AVG_ROW_LENGTH / 1024 / 1024, 4)
        ELSE 0
    END) DESC;
