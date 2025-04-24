-- @name: table_locks
-- @description: 分析表锁定情况，找出可能存在锁竞争的表
-- @params: dbName:string
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    t.TABLE_SCHEMA AS '数据库名',
    t.TABLE_NAME AS '表名',
    CASE 
        WHEN t.ENGINE = 'MyISAM' THEN '表级锁'
        WHEN t.ENGINE = 'InnoDB' THEN '行级锁'
        ELSE '其他'
    END AS '锁类型',
    io.count_read AS '读取次数',
    io.count_write AS '写入次数',
    io.count_fetch AS '获取次数',
    io.count_insert AS '插入次数',
    io.count_update AS '更新次数',
    io.count_delete AS '删除次数',
    CASE 
        WHEN io.count_read > 0 AND io.count_write > 0 THEN '读写混合'
        WHEN io.count_read > 0 THEN '只读'
        WHEN io.count_write > 0 THEN '只写'
        ELSE '未使用'
    END AS '访问模式',
    CASE 
        WHEN t.ENGINE = 'MyISAM' AND io.count_read > 0 AND io.count_write > 0 THEN '潜在锁竞争'
        WHEN t.ENGINE = 'InnoDB' AND io.count_update > 100 AND io.count_write / io.count_read > 0.5 THEN '潜在锁竞争'
        ELSE '正常'
    END AS '锁竞争状态'
FROM 
    information_schema.TABLES t
JOIN 
    performance_schema.table_io_waits_summary_by_table io
    ON t.TABLE_SCHEMA = io.OBJECT_SCHEMA AND t.TABLE_NAME = io.OBJECT_NAME
WHERE 
    t.TABLE_SCHEMA = :dbName
    AND t.TABLE_TYPE = 'BASE TABLE'
    AND (io.count_read > 0 OR io.count_write > 0)
ORDER BY 
    (io.count_read + io.count_write) DESC;
