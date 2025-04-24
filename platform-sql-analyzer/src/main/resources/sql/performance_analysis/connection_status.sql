-- @name: connection_status
-- @description: 分析MySQL连接状态，检查连接池使用情况
-- @params: 
-- @timeout: 30000
-- @dbType: mysql

SELECT 
    pl.USER AS '用户名',
    pl.HOST AS '主机',
    pl.DB AS '数据库',
    pl.COMMAND AS '命令',
    pl.TIME AS '连接时间(秒)',
    pl.STATE AS '状态',
    CONVERT(pl.INFO USING utf8mb4) AS '正在执行的查询',
    CASE 
        WHEN pl.TIME > 3600 THEN '长时间连接(>1小时)'
        WHEN pl.TIME > 600 THEN '中等时间连接(>10分钟)'
        ELSE '正常连接'
    END AS '连接时长分类',
    CASE 
        WHEN pl.COMMAND = 'Sleep' AND pl.TIME > 3600 THEN '建议关闭'
        WHEN pl.COMMAND = 'Sleep' AND pl.TIME > 600 THEN '需要关注'
        ELSE '正常'
    END AS '连接状态建议'
FROM 
    information_schema.PROCESSLIST pl
ORDER BY 
    pl.TIME DESC;
