# 数据库监控系统 (Database Monitor)

## 概述

这是一个基于Spring Boot 3的数据库监控系统，用于实时监控MySQL数据库的连接状态、长时间运行的SQL查询和系统性能指标。

## 最新功能更新 (v2.0)

### 🔄 智能锁等待监控
- **增长差值计算**: 不再依赖累计值，而是计算锁等待的增长差值，避免误报
- **MySQL重启检测**: 自动处理MySQL重启导致的累计值重置情况
- **半小时专项报备**: 每30分钟检查锁等待增长情况，及时发现性能问题

### ⏰ 优化的定时报告
- **精准时间控制**: 定时报告改为每日0,5,7,9,12,14,16,18,20,22,23小时的第5分钟执行
- **双重监控机制**: 常规健康报告 + 锁等待专项报备
- **频率优化**: 避免监控过于频繁，减少系统负载

### 🎯 精准进程监控
- **有效SQL过滤**: 只推送info字段有实际SQL内容的进程
- **智能空值过滤**: 自动过滤NULL、空字符串和纯空白内容
- **性能提升**: 减少无效数据传输，提升监控效率

## 核心功能

### 数据库监控
- 实时连接状态检查
- 长时间运行SQL检测
- 锁等待增长监控
- 性能指标收集

### 三级报警系统
- **INFO级别**: 正常状态通知、连接恢复
- **WARN级别**: 性能警告、锁等待增长
- **ERROR级别**: 严重故障、连接断开

### 定时报告
- **常规健康报告**: 多个时间点的全面检查
- **锁等待专项报备**: 每30分钟的专项监控

## 快速开始

### 环境要求
- Java 17+
- MySQL 5.7+
- Spring Boot 3.0+

### 配置示例

```yaml
monitor:
  defaults:
    check-interval: 60
    sql-timeout-threshold: 30
  alert:
    enabled: true
    webhook-url: "http://your-alert-system.com/webhook"
    accounts:
      info:
        receiver: "info-alerts"
        auth: "your_info_token"
        daily-limit: 300
      warn:
        receiver: "warn-alerts"
        auth: "your_warn_token"
        daily-limit: 300
      error:
        receiver: "error-alerts"
        auth: "your_error_token"
        daily-limit: 300
  datasources:
    - name: "main-db"
      enabled: true
      url: "jdbc:mysql://localhost:3306/database"
      username: "monitor_user"
      password: "monitor_password"
```

### 启动应用

```bash
mvn spring-boot:run
```

## API接口

### 监控接口
- `GET /api/monitor/status` - 获取所有数据源状态
- `GET /api/monitor/datasource/{name}` - 获取指定数据源状态
- `GET /api/monitor/health` - 系统健康检查

### 测试接口
- `GET /api/test/alert-config` - 查看报警配置
- `GET /api/test/alert-usage` - 查看报警使用统计
- `POST /api/test/test-alert/{level}` - 测试指定级别报警

## 监控时间表

### 常规健康报告
执行时间: 0:05, 5:05, 7:05, 9:05, 12:05, 14:05, 16:05, 18:05, 20:05, 22:05, 23:05

### 锁等待专项报备
执行时间: 每小时的0分和30分 (如 10:00, 10:30, 11:00, 11:30...)

## 报警示例

### 锁等待增长警告
```
⚠️ 锁等待增长警告
时间: 2024-01-20 15:30:00
检查周期: 30分钟
检查结果:
  - main-db: 新增锁等待 5 个 (累计: 1245)
  - backup-db: 无新增锁等待 (累计: 89)

建议检查相关SQL语句和数据库性能。
```

### 长时间SQL检测
```
数据库监控报告
数据库: main-db
健康状态: 健康
时间: 2024-01-20 15:30:00
检查耗时: 200ms
长时间运行SQL: 2 个
  - ID:123, 时间:45s, SQL:SELECT * FROM large_table WHERE...
  - ID:124, 时间:38s, SQL:UPDATE user_table SET status...
```

## 技术特性

### 高性能
- 并行监控多个数据源
- 智能连接池管理
- 异步报警发送

### 容错性
- 连接失败自动重试
- 优雅降级处理
- 详细的错误日志

### 扩展性
- 支持动态添加数据源
- 可配置的监控阈值
- 灵活的报警策略

## 更多信息

- 详细配置说明: [ALERT_SYSTEM_GUIDE.md](ALERT_SYSTEM_GUIDE.md)
- API文档: 启动应用后访问 `/swagger-ui.html`
- 测试接口: 使用内置的 `/api/test/*` 接口进行功能测试

## 许可证

本项目基于 MIT 许可证开源。