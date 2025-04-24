-- @name: table_optimization
-- @description: 分析表结构并提供优化建议，包括数据类型、字符集、存储引擎等
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    t.ENGINE AS '存储引擎',
    t.TABLE_ROWS AS '行数',
    ROUND((t.DATA_LENGTH + t.INDEX_LENGTH) / 1024 / 1024, 2) AS '大小(MB)',
    t.ROW_FORMAT AS '行格式',
    t.TABLE_COLLATION AS '排序规则',
    CASE 
        WHEN t.ENGINE != 'InnoDB' THEN '建议更换为InnoDB引擎'
        WHEN t.ROW_FORMAT != 'COMPRESSED' AND t.DATA_LENGTH > 100 * 1024 * 1024 THEN '考虑使用COMPRESSED行格式减少存储空间'
        WHEN t.TABLE_COLLATION NOT LIKE '%utf8%' AND t.TABLE_COLLATION NOT LIKE '%utf8mb4%' THEN '建议使用UTF8字符集'
        ELSE '结构良好'
    END AS '表结构建议',
    (
        SELECT GROUP_CONCAT(
            CONCAT(c.COLUMN_NAME, ' (', c.COLUMN_TYPE, ')') 
            ORDER BY c.ORDINAL_POSITION 
            SEPARATOR ', '
        )
        FROM information_schema.COLUMNS c
        WHERE c.TABLE_SCHEMA = t.TABLE_SCHEMA
        AND c.TABLE_NAME = t.TABLE_NAME
        AND c.DATA_TYPE IN ('varchar', 'char', 'text')
        AND c.CHARACTER_SET_NAME != 'utf8mb4'
    ) AS '非UTF8MB4字符列',
    (
        SELECT GROUP_CONCAT(
            CONCAT(c.COLUMN_NAME, ' (', c.COLUMN_TYPE, ')') 
            ORDER BY c.ORDINAL_POSITION 
            SEPARATOR ', '
        )
        FROM information_schema.COLUMNS c
        WHERE c.TABLE_SCHEMA = t.TABLE_SCHEMA
        AND c.TABLE_NAME = t.TABLE_NAME
        AND (
            (c.DATA_TYPE = 'varchar' AND c.CHARACTER_MAXIMUM_LENGTH > 255) OR
            (c.DATA_TYPE = 'int' AND c.COLUMN_TYPE LIKE '%unsigned%' AND c.COLUMN_NAME LIKE '%id%') OR
            (c.DATA_TYPE = 'datetime' AND c.COLUMN_DEFAULT IS NULL AND c.IS_NULLABLE = 'YES' AND 
             (c.COLUMN_NAME LIKE '%time%' OR c.COLUMN_NAME LIKE '%date%'))
        )
    ) AS '可优化的列',
    CASE 
        WHEN t.ENGINE = 'InnoDB' AND t.TABLE_ROWS > 1000000 THEN '大表，考虑分区或分表'
        WHEN t.ENGINE = 'InnoDB' AND t.DATA_FREE > 100 * 1024 * 1024 THEN '需要执行OPTIMIZE TABLE'
        WHEN NOT EXISTS (
            SELECT 1 FROM information_schema.STATISTICS s 
            WHERE s.TABLE_SCHEMA = t.TABLE_SCHEMA 
            AND s.TABLE_NAME = t.TABLE_NAME
        ) THEN '无索引，建议添加适当索引'
        ELSE '无特殊建议'
    END AS '其他建议',
    t.CREATE_TIME AS '创建时间',
    t.UPDATE_TIME AS '最后更新时间'
FROM 
    information_schema.TABLES t
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    t.DATA_LENGTH + t.INDEX_LENGTH DESC;
