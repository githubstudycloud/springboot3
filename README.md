# 数据库监控系统 (DB Monitor)

一个基于Spring Boot 3和JDK 21的MySQL/MariaDB数据库监控系统，专注于检测长时间运行的SQL查询并发送报警。

## 特性

- 支持多个数据库同时监控
- 检测长时间运行的SQL（通过 SHOW FULL PROCESSLIST）
- 灵活的报警机制
- RESTful API接口
- 使用MariaDB驱动（兼容MySQL）
- JDK 21虚拟线程支持

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MariaDB 10.x 或 MySQL 5.7+

### 配置

当前配置的数据库连接信息：
- 地址：192.168.226.136:3306
- 用户名：root
- 密码：rootpass

如需修改，请编辑 `src/main/resources/application.yml`

### 运行

```bash
# 编译项目
mvn clean package

# 运行应用
mvn spring-boot:run

# 或者直接运行JAR
java --enable-preview -jar target/db-monitor-1.0.0.jar
```

### API端点

- 健康检查：`GET http://localhost:8080/api/monitor/health`
- 检查所有数据源：`GET http://localhost:8080/api/monitor/check/all`
- 检查单个数据源：`GET http://localhost:8080/api/monitor/check/main-db`
- 监控状态：`GET http://localhost:8080/api/monitor/status`
- 详细状态：`GET http://localhost:8080/api/monitor/status/detailed`

## 测试长时间SQL

在MySQL/MariaDB中执行：
```sql
-- 创建一个35秒的测试查询
SELECT SLEEP(35), 'Test slow query';
```

然后查看监控日志或调用API查看结果。

## 授权说明

确保数据库用户有以下权限：
```sql
GRANT PROCESS ON *.* TO 'root'@'%';
GRANT SELECT ON information_schema.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

## 报警配置

默认报警接口为 `http://localhost:8080/webhook`，请根据实际情况修改。

## License

MIT