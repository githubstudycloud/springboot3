# 三账号报警系统使用指南

## 概述

本系统实现了全新的三账号分级报警机制，每个报警级别对应一个独立的账号，支持标准的三参数格式（receiver, auth, content），并具备每日使用次数限制功能。

## 核心特性

### 🎯 三级分类
- **INFO**: 正常状态通知，连接恢复等
- **WARN**: 性能问题，警告级别事件
- **ERROR**: 严重故障，数据库连接断开等

### 🔐 独立账号
- 每个级别使用独立的 receiver 和 auth token
- 避免账号混用，便于权限管理和追踪

### 📊 使用限制
- 每个账号每天最多 300 次报警
- 自动跟踪使用次数，防止过度报警
- 每日自动重置计数器

### 🔄 标准格式
- 统一的三参数格式：receiver, auth, content
- 便于对接各种报警系统

## 配置方法

### 1. 基础配置 (application.yml)

```yaml
monitor:
  alert:
    enabled: true
    webhook-url: "http://your-alert-system.com/webhook"  # 替换为您的报警接口
    accounts:
      info:
        receiver: "info-alerts"
        auth: "your_info_token_here"      # 替换为您的INFO token
        daily-limit: 300
      warn:
        receiver: "warn-alerts"
        auth: "your_warn_token_here"      # 替换为您的WARN token
        daily-limit: 300
      error:
        receiver: "error-alerts"
        auth: "your_error_token_here"     # 替换为您的ERROR token
        daily-limit: 300
```

### 2. 使用内置测试接口

如果您想先使用内置测试接口进行调试：

```yaml
monitor:
  alert:
    enabled: true
    webhook-url: "http://localhost:8080/api/test/webhook"  # 使用内置测试接口
    accounts:
      info:
        receiver: "info-alerts"
        auth: "info_token_12345"
        daily-limit: 300
      warn:
        receiver: "warn-alerts"
        auth: "warn_token_67890"
        daily-limit: 300
      error:
        receiver: "error-alerts"
        auth: "error_token_abcde"
        daily-limit: 300
```

## 工作流程

### 自动报警流程
1. **监控检测** → 系统发现问题并确定严重程度
2. **级别映射** → 将严重程度映射到对应级别（info/warn/error）
3. **账号选择** → 自动选择对应级别的账号配置
4. **限额检查** → 验证该账号今日使用次数是否超限
5. **构建请求** → 使用三参数格式构建报警请求
6. **发送报警** → POST 到配置的 webhook-url
7. **更新统计** → 增加该账号的使用次数

### 手动测试流程
1. **配置验证** → 使用 `/api/test/alert-config` 检查配置
2. **统计查看** → 使用 `/api/test/alert-usage` 查看使用情况
3. **分级测试** → 使用 `/api/test/test-alert/{level}` 测试各级别
4. **结果验证** → 检查报警接口是否收到正确格式的请求

## API 使用指南

### 查看配置信息
```bash
curl http://localhost:8080/api/test/alert-config
```

### 查看使用统计
```bash
curl http://localhost:8080/api/test/alert-usage
```

### 测试不同级别报警
```bash
# INFO级别
curl -X POST "http://localhost:8080/api/test/test-alert/info?message=测试INFO级别"

# WARN级别  
curl -X POST "http://localhost:8080/api/test/test-alert/warn?message=测试WARN级别"

# ERROR级别
curl -X POST "http://localhost:8080/api/test/test-alert/error?message=测试ERROR级别"
```

### 模拟接收报警
```bash
curl -X POST http://localhost:8080/api/test/alert \
  -H "Content-Type: application/json" \
  -d '{
    "receiver": "test-alerts",
    "auth": "test_token_12345",
    "content": "这是一条测试报警消息"
  }'
```

## 接口规范

### 报警请求格式

您的报警接口应该接收以下格式的 POST 请求：

```json
{
  "receiver": "info-alerts",           // 接收方标识
  "auth": "info_token_12345",          // 认证token
  "content": "报警内容文本",            // 格式化的报警消息
  "timestamp": "2024-01-20 15:30:00",  // 时间戳（可选）
  "severity": "info",                  // 严重程度（可选）
  "dataSource": "main-db"              // 数据源（可选）
}
```

### 报警内容示例

系统生成的报警内容格式：

**数据库连接断开 (ERROR级别)**:
```
🔴 数据库连接断开警告
数据库: main-db
状态: 连接断开
时间: 2024-01-20 15:30:00
检查耗时: 120ms
```

**数据库连接恢复 (INFO级别)**:
```
🟢 数据库连接恢复通知
数据库: main-db
状态: 连接已恢复
时间: 2024-01-20 15:30:00
检查耗时: 85ms
```

**长时间SQL检测 (WARN级别)**:
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

## 监控与管理

### 使用统计监控

定期检查各账号使用情况：
```bash
curl http://localhost:8080/api/test/alert-usage
```

响应示例：
```json
{
  "date": "2024-01-20",
  "info": {
    "receiver": "info-alerts",
    "used": 45,
    "limit": 300,
    "remaining": 255
  },
  "warn": {
    "receiver": "warn-alerts", 
    "used": 12,
    "limit": 300,
    "remaining": 288
  },
  "error": {
    "receiver": "error-alerts",
    "used": 3,
    "limit": 300,
    "remaining": 297
  }
}
```

### 测试环境重置

仅在测试环境中，可以手动重置每日计数：
```bash
curl -X POST http://localhost:8080/api/test/reset-daily-usage
```

**⚠️ 警告**: 生产环境请谨慎使用此功能！

## 故障排除

### 常见问题

**1. 报警不发送**
- 检查 `alert.enabled` 是否为 `true`
- 验证 `webhook-url` 是否正确
- 查看日志中的错误信息

**2. 超过每日限制**
- 检查使用统计：`GET /api/test/alert-usage`
- 考虑增加 `daily-limit` 或优化报警频率
- 验证是否有报警风暴

**3. 报警格式问题**
- 确保接收端支持 JSON 格式
- 验证三参数格式：receiver, auth, content
- 检查字符编码是否正确

**4. 网络连接问题**
- 验证 webhook-url 的网络可达性
- 检查防火墙和代理设置
- 查看 HTTP 响应状态码

### 调试步骤

1. **配置验证**
   ```bash
   curl http://localhost:8080/api/test/alert-config
   ```

2. **连通性测试**
   ```bash
   curl -X POST http://localhost:8080/api/test/test-alert/info?message=连通性测试
   ```

3. **查看日志**
   检查应用日志中的报警发送记录和错误信息

4. **使用统计**
   ```bash
   curl http://localhost:8080/api/test/alert-usage
   ```

## 最佳实践

### 1. Token 安全
- 使用强随机 token
- 定期更换 token
- 不要在日志中输出完整 token

### 2. 报警频率控制
- 合理设置监控间隔
- 避免重复报警
- 使用状态变化检测

### 3. 接收端设计
- 实现幂等性处理
- 支持快速响应（< 5秒）
- 记录接收日志用于审计

### 4. 监控管理
- 定期检查使用统计
- 监控报警系统本身的健康状况
- 建立报警系统的备用方案

## 升级指南

### 从旧版本升级

如果您从旧版本的多端点配置升级到新的三账号系统：

1. **备份原配置**
   ```bash
   cp src/main/resources/application.yml src/main/resources/application.yml.backup
   ```

2. **更新配置格式**
   将原来的 `endpoints` 配置替换为新的 `accounts` 配置

3. **测试新配置**
   使用测试接口验证新配置是否工作正常

4. **更新对接系统**
   如需要，更新下游报警系统以支持新的三参数格式

### 配置迁移示例

**旧配置** (不再支持):
```yaml
alert:
  endpoints:
    - name: "admin"
      url: "http://admin.alert.com"
      type: "critical-only"
```

**新配置**:
```yaml
alert:
  webhook-url: "http://admin.alert.com"
  accounts:
    error:
      receiver: "admin-alerts"
      auth: "admin_token_xyz"
      daily-limit: 300
```

## 支持

如遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查应用日志
3. 使用测试接口进行调试
4. 联系系统管理员

---

**版本**: v1.3  
**更新日期**: 2024-01-20  
**适用系统**: Spring Boot 3.2 + 数据库监控系统