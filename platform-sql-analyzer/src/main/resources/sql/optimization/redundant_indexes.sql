-- @name: redundant_indexes
-- @description: 分析冗余索引，找出可能可以合并或删除的冗余索引
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

-- 查找同一表上的冗余索引
WITH index_columns AS (
    SELECT 
        TABLE_SCHEMA,
        TABLE_NAME,
        INDEX_NAME,
        GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX SEPARATOR ',') AS columns_in_index,
        COUNT(*) AS column_count
    FROM 
        information_schema.STATISTICS
    WHERE 
        TABLE_SCHEMA = :dbName
    GROUP BY 
        TABLE_SCHEMA, TABLE_NAME, INDEX_NAME
)
SELECT 
    a.TABLE_SCHEMA AS '数据库名',
    a.TABLE_NAME AS '表名',
    a.INDEX_NAME AS '索引A',
    a.columns_in_index AS 'A包含的列',
    b.INDEX_NAME AS '索引B',
    b.columns_in_index AS 'B包含的列',
    CASE 
        WHEN a.INDEX_NAME = 'PRIMARY' OR b.INDEX_NAME = 'PRIMARY' THEN '主键不能删除'
        WHEN a.column_count > b.column_count THEN CONCAT('索引', b.INDEX_NAME, '可能冗余')
        WHEN a.column_count < b.column_count THEN CONCAT('索引', a.INDEX_NAME, '可能冗余')
        ELSE '需要进一步分析'
    END AS '建议',
    CASE 
        WHEN a.INDEX_NAME != 'PRIMARY' AND b.INDEX_NAME != 'PRIMARY' THEN 
            CASE 
                WHEN a.column_count > b.column_count THEN CONCAT('ALTER TABLE `', a.TABLE_SCHEMA, '`.`', a.TABLE_NAME, '` DROP INDEX `', b.INDEX_NAME, '`;')
                WHEN a.column_count < b.column_count THEN CONCAT('ALTER TABLE `', a.TABLE_SCHEMA, '`.`', a.TABLE_NAME, '` DROP INDEX `', a.INDEX_NAME, '`;')
                ELSE '-- 需要根据查询模式决定要删除哪个索引'
            END
        ELSE '-- 无需操作'
    END AS 'SQL语句'
FROM 
    index_columns a
JOIN 
    index_columns b
    ON a.TABLE_SCHEMA = b.TABLE_SCHEMA
    AND a.TABLE_NAME = b.TABLE_NAME
    AND a.INDEX_NAME < b.INDEX_NAME
    AND (
        (a.columns_in_index = b.columns_in_index) OR
        (a.columns_in_index LIKE CONCAT(b.columns_in_index, ',%')) OR
        (b.columns_in_index LIKE CONCAT(a.columns_in_index, ',%'))
    )
ORDER BY 
    a.TABLE_SCHEMA,
    a.TABLE_NAME,
    CASE 
        WHEN a.column_count > b.column_count THEN b.column_count
        ELSE a.column_count
    END;
