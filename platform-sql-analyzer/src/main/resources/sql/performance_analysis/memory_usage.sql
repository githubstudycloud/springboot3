-- @name: memory_usage
-- @description: 分析MySQL内存使用情况，包括缓冲池、连接缓存等
-- @params: 
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    'InnoDB缓冲池大小' AS '指标名称',
    CONCAT(ROUND(@@innodb_buffer_pool_size / 1024 / 1024 / 1024, 2), ' GB') AS '当前值',
    '缓冲池越大，可以缓存更多数据，减少磁盘I/O' AS '说明'
UNION ALL
SELECT
    'InnoDB缓冲池使用率',
    CONCAT(
        ROUND(
            (1 - (
                SELECT VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME = 'INNODB_BUFFER_POOL_PAGES_FREE'
            ) / (
                SELECT VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME = 'INNODB_BUFFER_POOL_PAGES_TOTAL'
            )
        ) * 100, 2), '%'
    ),
    '使用率高表示缓冲池被充分利用，但接近100%可能导致频繁置换'
UNION ALL
SELECT 
    '查询缓存大小',
    CONCAT(ROUND(@@query_cache_size / 1024 / 1024, 2), ' MB'),
    '缓存查询结果，避免重复执行相同查询'
UNION ALL
SELECT 
    '查询缓存命中率',
    CONCAT(
        ROUND(
            (
                SELECT VARIABLE_VALUE 
                FROM performance_schema.global_status 
                WHERE VARIABLE_NAME = 'Qcache_hits'
            ) / (
                (
                    SELECT VARIABLE_VALUE 
                    FROM performance_schema.global_status 
                    WHERE VARIABLE_NAME = 'Qcache_hits'
                ) + (
                    SELECT VARIABLE_VALUE 
                    FROM performance_schema.global_status 
                    WHERE VARIABLE_NAME = 'Com_select'
                )
            ) * 100, 2
        ), '%'
    ),
    '命中率高表示查询缓存工作良好，但MySQL 5.7.20以后版本已废弃查询缓存'
UNION ALL
SELECT 
    '排序缓冲区大小',
    CONCAT(ROUND(@@sort_buffer_size / 1024 / 1024, 2), ' MB'),
    '影响ORDER BY或GROUP BY操作的性能'
UNION ALL
SELECT 
    '最大连接数',
    CONCAT(@@max_connections),
    '允许的最大客户端连接数'
UNION ALL
SELECT 
    '当前连接数',
    CONCAT(
        (
            SELECT COUNT(*) 
            FROM information_schema.PROCESSLIST
        ), ' (', 
        ROUND(
            (
                SELECT COUNT(*) 
                FROM information_schema.PROCESSLIST
            ) / @@max_connections * 100, 2
        ), '%)'
    ),
    '当前活动连接数及占最大连接数的百分比';
