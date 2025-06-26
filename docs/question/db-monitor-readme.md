# 数据库监控系统使用说明

## 快速开始

### 1. 配置数据源

在 `application.yml` 中配置您的MySQL数据源：

```yaml
spring:
  datasource:
    primary:
      jdbc-url: jdbc:mysql://你的主机:3306/数据库名?useSSL=false&serverTimezone=UTC
      username: 用户名
      password: 密码
    
    secondary:
      jdbc-url: jdbc:mysql://你的主机:3306/数据库名?useSSL=false&serverTimezone=UTC
      username: 用户名
      password: 密码
```

### 2. 配置监控参数

```yaml
monitor:
  # 检测间隔（秒）
  check-interval: 60
  # SQL执行时间阈值（秒）
  sql-timeout-threshold: 30
  # 报警接口
  alert:
    url: http://你的报警接口地址/alert
    enabled: true
```

### 3. 报警消息格式

系统会向配置的报警接口发送JSON格式的消息：

```json
{
  "alertType": "LONG_RUNNING_QUERIES",
  "dataSource": "production-db1",
  "message": "数据库: production-db1\n检查耗时: 235ms\n长时间运行SQL: 3 个\n  - ID:12345, 时间:150s, 严重程度:critical, SQL:SELECT COUNT(*) FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.created_at > '2024-01-01'...\n  - ID:12346, 时间:85s, 严重程度:warning, SQL:UPDATE inventory SET quantity = quantity - 1 WHERE product_id IN (SELECT product_id FROM temp_products)...\n  - ID:12347, 时间:45s, 严重程度:warning, SQL:DELETE FROM logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)...",
  "timestamp": "2024-01-20T10:30:00",
  "severity": "critical",
  "tags": ["production", "primary"],
  "longRunningQueries": [
    {
      "id": 12345,
      "user": "app_user",
      "host": "192.168.1.100:45678",
      "db": "ecommerce",
      "command": "Query",
      "time": 150,
      "state": "Sending data",
      "info": "SELECT COUNT(*) FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.created_at > '2024-01-01' AND o.status = 'completed' GROUP BY c.region ORDER BY COUNT(*) DESC",
      "rowsSent": null,
      "rowsExamined": null
    },
    {
      "id": 12346,
      "user": "batch_user",
      "host": "192.168.1.101:45679",
      "db": "ecommerce",
      "command": "Query",
      "time": 85,
      "state": "Updating",
      "info": "UPDATE inventory SET quantity = quantity - 1 WHERE product_id IN (SELECT product_id FROM temp_products WHERE status = 'active')",
      "rowsSent": null,
      "rowsExamined": null
    },
    {
      "id": 12347,
      "user": "maintenance_user",
      "host": "192.168.1.102:45680",
      "db": "ecommerce",
      "command": "Query",
      "time": 45,
      "state": "Deleting",
      "info": "DELETE FROM logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)",
      "rowsSent": null,
      "rowsExamined": null
    }
  ],
  "metrics": {
    "currentConnections": 156,
    "maxConnections": 200,
    "slowQueries": 23,
    "lockWaits": 2,
    "connectionUsagePercent": 78.0,
    "totalQueries": 1234567
  },
  "additionalInfo": {
    "connections": [
      {"VARIABLE_NAME": "Threads_connected", "VARIABLE_VALUE": "156"},
      {"VARIABLE_NAME": "Max_used_connections", "VARIABLE_VALUE": "189"},
      {"VARIABLE_NAME": "Threads_running", "VARIABLE_VALUE": "12"}
    ],
    "queries": [
      {"VARIABLE_NAME": "Questions", "VARIABLE_VALUE": "1234567"},
      {"VARIABLE_NAME": "Slow_queries", "VARIABLE_VALUE": "23"},
      {"VARIABLE_NAME": "Com_select", "VARIABLE_VALUE": "890123"},
      {"VARIABLE_NAME": "Com_insert", "VARIABLE_VALUE": "234567"},
      {"VARIABLE_NAME": "Com_update", "VARIABLE_VALUE": "89012"},
      {"VARIABLE_NAME": "Com_delete", "VARIABLE_VALUE": "20865"}
    ],
    "lockWaits": 2
  }
}
```

## API接口

### 手动触发检测

```bash
# 检查主数据源
GET http://localhost:8080/api/monitor/check/primary

# 检查副数据源
GET http://localhost:8080/api/monitor/check/secondary

# 健康检查
GET http://localhost:8080/api/monitor/health
```

## 扩展功能

### 1. 添加更多数据源

在 `application.yml` 中添加新的数据源：

```yaml
monitor:
  datasources:
    # 现有的数据源...
    
    # 添加新的数据源
    - name: new-analytics-db
      url: jdbc:mysql://analytics-new.example.com:3306/analytics
      username: monitor_user
      password: password
      enabled: true
      check-interval: 120  # 2分钟检查一次
      sql-timeout-threshold: 600  # 分析库允许10分钟的查询
      tags: [analytics, data-warehouse]
```

### 2. 针对不同类型数据库的配置示例

```yaml
# OLTP数据库（事务型）- 严格的超时限制
- name: transaction-db
  sql-timeout-threshold: 10  # 10秒超时
  check-interval: 30  # 频繁检查
  tags: [oltp, critical]

# OLAP数据库（分析型）- 宽松的超时限制  
- name: analytics-db
  sql-timeout-threshold: 300  # 5分钟超时
  check-interval: 300  # 5分钟检查一次
  tags: [olap, analytics]

# 报表数据库 - 非常宽松的限制
- name: reporting-db
  sql-timeout-threshold: 1800  # 30分钟超时
  check-interval: 600  # 10分钟检查一次
  tags: [reporting]
```

### 3. 自定义监控指标

在 `DatabaseMonitorService` 中添加新的检查方法：

```java
private void checkCustomMetrics(String dataSourceName, JdbcTemplate jdbcTemplate) {
    // 检查特定表的行数
    Long rowCount = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM your_critical_table", Long.class);
    
    // 检查复制延迟（如果是从库）
    Map<String, Object> slaveStatus = jdbcTemplate.queryForMap(
        "SHOW SLAVE STATUS");
}
```

### 4. 使用HTTP工具类

```java
// 发送POST请求
httpUtil.sendAlert("http://api.example.com/alert", alertData);

// 发送GET请求（异步）
CompletableFuture<Void> future = httpUtil.sendAlertAsync(
    "http://api.example.com/alert", alertData);
```

## 监控指标说明

### 1. 长时间运行SQL检测

- 通过 `SHOW FULL PROCESSLIST` 获取所有进程
- 筛选条件：
  - Command = "Query"
  - Time > 配置的阈值（默认30秒）
  - Info 不为空（有实际SQL语句）

### 2. 性能指标

- **Threads_connected**: 当前连接数
- **Max_used_connections**: 历史最大连接数
- **Slow_queries**: 慢查询总数
- **Table_locks_waited**: 表锁等待次数

### 3. 增强功能（EnhancedMonitorService）

- **死锁检测**: 查询 `information_schema.innodb_lock_waits`
- **表锁监控**: 查询 `performance_schema.data_locks`
- **数据库大小**: 统计各数据库占用空间
- **慢查询统计**: 获取慢查询详细信息

## 故障排查

### 1. 连接失败

检查：
- 数据库URL是否正确
- 用户名密码是否正确
- 防火墙是否开放端口
- MySQL用户权限是否足够

### 2. PROCESSLIST权限不足

确保MySQL用户有以下权限：
```sql
GRANT PROCESS ON *.* TO 'monitor_user'@'%';
GRANT SELECT ON performance_schema.* TO 'monitor_user'@'%';
GRANT SELECT ON information_schema.* TO 'monitor_user'@'%';
FLUSH PRIVILEGES;
```

### 3. 测试SHOW FULL PROCESSLIST

可以手动测试SQL检测功能：
```sql
-- 查看当前所有进程
SHOW FULL PROCESSLIST;

-- 模拟长时间运行的查询（测试用）
SELECT SLEEP(35), 'This is a test query' as message;

-- 在另一个会话中查看
SHOW FULL PROCESSLIST;
-- 应该能看到上面的SLEEP查询，Time字段会显示运行时间
```

### 4. 报警发送失败

检查：
- 报警接口URL是否可访问
- 网络连接是否正常
- 查看日志中的具体错误信息

## 性能优化建议

1. **调整检测间隔**: 根据实际需求调整 `check-interval`，避免过于频繁
2. **连接池优化**: 合理设置HikariCP的连接池参数
3. **异步处理**: 可以将报警发送改为异步，避免阻塞监控主流程
4. **日志级别**: 生产环境建议将日志级别调整为 `INFO` 或 `WARN`

## 部署建议

1. 使用独立的监控账号，只赋予必要的权限
2. 将监控系统部署在与数据库网络互通的环境
3. 配置合理的JVM参数，如：
   ```bash
   java -Xms512m -Xmx1024m -jar db-monitor.jar
   ```
4. 使用外部配置文件覆盖默认配置：
   ```bash
   java -jar db-monitor.jar --spring.config.location=file:./config/
   ```