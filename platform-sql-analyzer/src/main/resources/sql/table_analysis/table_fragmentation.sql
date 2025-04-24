-- @name: table_fragmentation
-- @description: 分析表碎片情况，找出需要优化的表
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    TABLE_SCHEMA AS '数据库名',
    TABLE_NAME AS '表名',
    ENGINE AS '存储引擎',
    TABLE_ROWS AS '行数',
    ROUND(DATA_LENGTH / 1024 / 1024, 2) AS '数据大小(MB)',
    ROUND(INDEX_LENGTH / 1024 / 1024, 2) AS '索引大小(MB)',
    ROUND(DATA_FREE / 1024 / 1024, 2) AS '碎片大小(MB)',
    CASE 
        WHEN DATA_LENGTH > 0 THEN ROUND(DATA_FREE * 100 / DATA_LENGTH, 2)
        ELSE 0 
    END AS '碎片率(%)',
    CASE 
        WHEN DATA_LENGTH > 0 AND DATA_FREE * 100 / DATA_LENGTH >= 20 THEN '建议优化'
        WHEN DATA_LENGTH > 0 AND DATA_FREE * 100 / DATA_LENGTH >= 10 THEN '需要关注'
        ELSE '状态良好' 
    END AS '优化建议',
    CREATE_TIME AS '创建时间',
    UPDATE_TIME AS '最后更新时间',
    CASE 
        WHEN ENGINE = 'InnoDB' THEN 'OPTIMIZE TABLE `' + TABLE_SCHEMA + '`.`' + TABLE_NAME + '`'
        ELSE NULL 
    END AS '优化SQL'
FROM 
    information_schema.TABLES
WHERE 
    TABLE_SCHEMA = :dbName
    AND TABLE_TYPE = 'BASE TABLE'
    AND ENGINE = 'InnoDB'
HAVING 
    '碎片率(%)' > 0
ORDER BY 
    '碎片率(%)' DESC, 
    DATA_FREE DESC;
