-- @name: slow_queries
-- @description: 分析MySQL慢查询日志，找出执行时间过长的查询
-- @params: minExecutionTime:number
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    sl.start_time AS '开始时间',
    sl.user_host AS '用户主机',
    sl.query_time AS '查询时间(秒)',
    sl.lock_time AS '锁定时间(秒)',
    sl.rows_sent AS '发送行数',
    sl.rows_examined AS '扫描行数',
    ROUND(sl.rows_examined / IF(sl.rows_sent = 0, 1, sl.rows_sent)) AS '扫描/结果比例',
    sl.db AS '数据库',
    CONVERT(sl.sql_text USING utf8mb4) AS 'SQL语句'
FROM 
    mysql.slow_log sl
WHERE 
    sl.query_time > :minExecutionTime
ORDER BY 
    sl.query_time DESC
LIMIT 100;
