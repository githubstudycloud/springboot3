# 数据库监控系统使用说明

## 快速开始

### 1. 配置数据源

在 `application.yml` 中配置您的MySQL数据源。可以配置单个或多个数据源：

#### 单数据源配置示例
```yaml
monitor:
  datasources:
    - name: main-db
      url: jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
      username: root
      password: password
      enabled: true
```

#### 多数据源配置示例（使用 # 注释不需要的数据源）
```yaml
monitor:
  datasources:
    - name: main-db
      url: jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
      username: root
      password: password
      enabled: true
    
#    - name: secondary-db
#      url: jdbc:mysql://secondary.example.com:3306/db2?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true
#      username: monitor_user
#      password: password
#      enabled: true
```

### 2. 重要的连接参数

为防止连接超时和中断，建议在JDBC URL中添加以下参数：

```
?useSSL=false&serverTimezone=UTC&connectTimeout=20000&socketTimeout=60000&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8
```

参数说明：
- `connectTimeout=20000` - 连接超时20秒
- `socketTimeout=60000` - Socket超时60秒（防止SHOW FULL PROCESSLIST执行时断开）
- `autoReconnect=true` - 自动重连
- `useUnicode=true&characterEncoding=UTF-8` - 支持中文

### 3. 配置监控参数

```yaml
monitor:
  defaults:
    check-interval: 60  # 检测间隔（秒）
    sql-timeout-threshold: 30  # SQL执行时间阈值（秒）
    connection-timeout: 10000  # 连接超时（毫秒）
    pool-size: 5  # 连接池大小
    keep-alive-time: 600000  # 连接保活时间（10分钟）
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

### 2. SHOW FULL PROCESSLIST执行时连接断开

这是常见问题，通常由以下原因造成：

#### 原因分析
1. MySQL的 `wait_timeout` 或 `interactive_timeout` 设置太短
2. 网络不稳定
3. PROCESSLIST返回数据量太大
4. 连接池配置不当

#### 解决方案

**1. 调整MySQL服务器参数**
```sql
-- 查看当前超时设置
SHOW VARIABLES LIKE '%timeout%';

-- 临时调整（重启失效）
SET GLOBAL wait_timeout = 28800;  -- 8小时
SET GLOBAL interactive_timeout = 28800;

-- 永久调整（my.cnf）
[mysqld]
wait_timeout = 28800
interactive_timeout = 28800
max_allowed_packet = 64M
```

**2. 优化JDBC连接参数**
```yaml
url: jdbc:mysql://host:3306/db?socketTimeout=60000&connectTimeout=20000&autoReconnect=true
```

**3. 调整连接池配置**
```yaml
monitor:
  defaults:
    pool-size: 5  # 增加连接池大小
    keep-alive-time: 300000  # 5分钟发送一次心跳
```

**4. 如果仍有问题，可以使用备选方案**
```sql
-- 使用information_schema代替SHOW PROCESSLIST
SELECT id, user, host, db, command, time, state, info
FROM information_schema.processlist
WHERE command != 'Sleep' AND time > 30;
```

### 3. PROCESSLIST权限不足

确保MySQL用户有以下权限：
```sql
-- 创建监控专用用户
CREATE USER 'monitor_user'@'%' IDENTIFIED BY 'password';

-- 授予必要权限
GRANT PROCESS ON *.* TO 'monitor_user'@'%';
GRANT SELECT ON performance_schema.* TO 'monitor_user'@'%';
GRANT SELECT ON information_schema.* TO 'monitor_user'@'%';
FLUSH PRIVILEGES;

-- 验证权限
SHOW GRANTS FOR 'monitor_user'@'%';
```

### 4. 测试SHOW FULL PROCESSLIST

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

### 5. 报警发送失败

检查：
- 报警接口URL是否可访问
- 网络连接是否正常
- 查看日志中的具体错误信息

## 常见问题FAQ

### Q1: 为什么执行SHOW FULL PROCESSLIST时连接会断开？

**A:** 这通常是因为：
1. MySQL的`wait_timeout`设置太短（默认8小时，但某些环境可能更短）
2. 查询返回数据量太大，超过了socket超时时间
3. 网络不稳定

**解决方案：**
- 在JDBC URL中添加`socketTimeout=60000`参数
- 调整MySQL的`wait_timeout`参数
- 使用连接池的keepAlive机制

### Q2: 如何只监控一个数据库？

**A:** 在配置文件中只保留一个数据源，其他的用`#`注释掉：
```yaml
monitor:
  datasources:
    - name: my-db
      url: jdbc:mysql://localhost:3306/mydb
      username: root
      password: password
      enabled: true
    
#    - name: other-db  # 这个被注释掉了
#      url: jdbc:mysql://other:3306/db
#      enabled: false
```

### Q3: 报警发送失败怎么办？

**A:** 检查以下几点：
1. 报警URL是否可访问：`curl -X POST http://your-alert-url`
2. 检查防火墙设置
3. 查看详细错误日志
4. 可以先用本地测试URL：`http://localhost:8080/test`

### Q4: 如何调试SQL检测功能？

**A:** 可以通过以下步骤：
1. 降低SQL超时阈值到10秒方便测试
2. 在MySQL中执行`SELECT SLEEP(15)`创建慢查询
3. 查看应用日志中的WARNING信息
4. 通过API `/api/monitor/check/your-db`手动触发检查

### Q5: 支持哪些MySQL版本？

**A:** 支持MySQL 5.7及以上版本。不同版本的区别：
- MySQL 5.7+：完整支持所有功能
- MySQL 5.6：不支持performance_schema的某些表
- MySQL 8.0+：最佳支持，包括所有高级特性

### Q6: 如何处理大量的processlist结果？

**A:** 系统已经做了以下优化：
1. 限制返回最多100个长时间查询
2. SQL语句超过1000字符会被截断
3. 日志中只记录前10个查询的详情
4. 可以使用`information_schema.processlist`替代方案

### Q7: 监控对数据库性能有影响吗？

**A:** 影响很小：
- `SHOW PROCESSLIST`是轻量级操作
- 默认60秒执行一次
- 使用独立的连接池
- 可以针对不同数据库调整检查间隔

### Q8: 如何添加自定义监控指标？

**A:** 在`DatabaseMonitorService`中添加新方法：
```java
private void checkCustomMetrics(String name, JdbcTemplate template) {
    // 你的自定义逻辑
}
```

然后在`checkDataSource`方法中调用它。