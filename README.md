# 数据库监控系统

Spring Boot 3.2 + MariaDB 数据库监控应用，实时监控数据库性能和连接状态，支持智能报警和系统健康检查。

## 功能特性

### 核心监控功能

- ✅ 实时监控数据库连接状态（当前配置：192.168.226.136）
- ✅ 检测长时间运行的SQL查询
- ✅ 数据库连接断开/重连自动检测
- ✅ 系统资源监控（CPU、内存、磁盘）
- ✅ 支持自定义监控规则和阈值
- ✅ 动态调整监控频率
- ✅ 兼容性优化：移除SESSION_STATUS依赖，使用SHOW STATUS

### 智能报警系统 🚨

- ✅ **三级报警机制**：支持 info、warn、error 三个级别
- ✅ **三账号分级分发**：每个级别对应一个独立账号和token
- ✅ **每日限额控制**：每个账号每天最多300次报警
- ✅ **标准三参数格式**：receiver, auth, content
- ✅ **状态变化检测**：数据库断开/重连时自动发送通知
- ✅ **统一Webhook接口**：所有报警通过同一个地址发送
- ✅ **自动账号选择**：根据严重程度自动选择对应账号

### API接口

- ✅ REST API接口查询监控状态
- ✅ **测试接口**：支持JSON参数（senduser、token、content）
- ✅ 综合健康检查（数据库+系统）
- ✅ **定时报告系统**：每半小时自动健康检查和报告

## 技术栈

- Java 21 (兼容较早版本)
- Spring Boot 3.2
- MySQL 5.7+ (推荐) / MariaDB
- HikariCP 连接池
- Lombok
- Jackson (时间模块)

## 项目结构

```
src/main/java/com/example/dbmonitor/
├── config/          # 配置类（数据源管理、监控属性）
├── controller/      # REST控制器
│   ├── MonitorController.java      # 主要监控接口
│   └── TestController.java         # 测试接口（JSON参数支持）
├── entity/          # 实体类
│   ├── AlertMessage.java           # 警告消息实体
│   ├── MonitorResult.java          # 监控结果实体
│   └── ProcessInfo.java            # 进程信息实体
├── exception/       # 异常处理
│   ├── AlertSendException.java     # 警告发送异常
│   ├── DatabaseConnectionException.java # 数据库连接异常
│   └── GlobalExceptionHandler.java # 全局异常处理
├── scheduler/       # 定时任务
│   ├── DynamicMonitorScheduler.java # 动态监控调度器
│   └── ScheduledReportService.java  # 定时报告服务（每半小时）
├── service/         # 业务服务
│   ├── AlertService.java           # 智能分级报警服务
│   ├── DatabaseMonitorService.java # 数据库监控服务
│   └── SystemHealthService.java   # 系统健康检查服务
└── util/           # 工具类
    ├── HttpUtil.java               # HTTP请求工具
    └── ProcessListMapper.java      # 统一的ProcessList映射器
```

## 新版本三账号报警系统 🆕

### 报警级别与账号映射

- **info**: 正常状态通知 → `info-alerts` 账号
- **warn**: 性能问题、警告 → `warn-alerts` 账号  
- **error**: 严重故障、连接断开 → `error-alerts` 账号

### 三参数标准格式

所有报警统一使用以下参数格式：
```json
{
  "receiver": "info-alerts",     // 接收方标识
  "auth": "info_token_12345",    // 认证token
  "content": "报警内容文本"        // 报警消息内容
}
```

### 账号配置

每个账号独立配置，支持：
- 独立的 `receiver` 标识
- 独立的 `auth` token  
- 每日 300 次报警限额
- 自动使用次数跟踪
- 每天自动重置计数

## 快速开始

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd springboot3M18
   ```

2. **配置数据库连接**
   编辑 `src/main/resources/application.yml`：
   ```yaml
   monitor:
     datasources:
       - name: "main-db"
         url: "jdbc:mysql://192.168.226.136:3306/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8"
         username: "root"
         password: "rootpass"
   ```

3. **配置三账号报警系统**
   ```yaml
   monitor:
     alert:
       enabled: true
       webhook-url: "http://localhost:8080/api/test/webhook"  # 可改为您的报警接口
       accounts:
         info:
           receiver: "info-alerts"
           auth: "info_token_12345"           # 替换为您的token
           daily-limit: 300
         warn:
           receiver: "warn-alerts"
           auth: "warn_token_67890"           # 替换为您的token
           daily-limit: 300
         error:
           receiver: "error-alerts"
           auth: "error_token_abcde"          # 替换为您的token
           daily-limit: 300
   ```

4. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

5. **访问监控面板**
   ```
   http://localhost:8080/api/monitor/status/comprehensive
   ```

## API接口文档

### 核心监控接口

- `GET /api/monitor/health` - 应用健康检查
- `GET /api/monitor/status` - 数据库监控状态概览
- `GET /api/monitor/status/comprehensive` - 综合状态（数据库+系统）
- `GET /api/monitor/check/all` - 检查所有数据源
- `GET /api/monitor/system-health` - 系统健康检查
- `PUT /api/monitor/schedule/{dataSource}` - 动态调整监控间隔（例如：main-db）

### 新版本报警测试接口

- `POST /api/test/alert` - 测试三参数报警接收（receiver, auth, content）
- `POST /api/test/webhook` - 标准Webhook接收端点 
- `POST /api/test/test-alert/{level}` - 测试指定级别报警（info/warn/error）
- `GET /api/test/alert-config` - 获取报警配置信息
- `GET /api/test/alert-usage` - 获取账号使用统计
- `POST /api/test/reset-daily-usage` - 重置每日使用次数（测试用）
- `POST /api/test/test-db-disconnect` - 测试数据库断开警告
- `POST /api/test/test-db-reconnect` - 测试数据库重连通知
- `POST /api/test/trigger-scheduled-report` - 手动触发定时健康报告

### 新版本三参数报警接口示例

```bash
# 测试三参数报警接收
curl -X POST http://localhost:8080/api/test/alert \
  -H "Content-Type: application/json" \
  -d '{
    "receiver": "test-alerts",
    "auth": "test_token_12345", 
    "content": "🔴 数据库连接断开警告\n数据库: main-db\n状态: 连接断开\n时间: 2024-01-20 15:30:00"
  }'

# 测试不同级别报警
curl -X POST "http://localhost:8080/api/test/test-alert/info?message=测试INFO级别报警"
curl -X POST "http://localhost:8080/api/test/test-alert/warn?message=测试WARN级别报警"  
curl -X POST "http://localhost:8080/api/test/test-alert/error?message=测试ERROR级别报警"

# 查看使用统计
curl http://localhost:8080/api/test/alert-usage
```

## 监控功能验证

### 数据库连接状态监控

- ✅ 程序能够检测数据库断开并发送critical级别警告
- ✅ 程序能够检测数据库重连并发送info级别通知
- ✅ 支持单一数据源监控（192.168.226.136）
- ✅ 优化连接统计收集，移除了不兼容的SESSION_STATUS依赖
- ✅ 使用SHOW STATUS替代SESSION_STATUS，提高兼容性

### 系统状态监控

- ✅ JVM内存使用率监控
- ✅ 系统CPU和内存监控
- ✅ 磁盘空间检查
- ✅ 网络连通性测试
- ✅ 应用运行时间统计

### 智能报警验证

- ✅ 不同级别报告自动分发给对应接收方
- ✅ 警告URL自动确保使用HTTP协议
- ✅ 状态变化时自动发送通知

### 定时报告系统

- ✅ **定时执行**：每小时5分和35分自动执行健康检查
- ✅ **Java程序状态**：定期报告程序本身正常运行（INFO级别）
- ✅ **MySQL连接检查**：验证所有数据库连接状态
- ✅ **长时间SQL分析**：检查超过30分钟的SQL查询，发现异常时发送具体SQL语句
- ✅ **分级报告**：不同检查项目按严重程度发送给不同警告对象
- ✅ **顺序执行**：按设定顺序依次执行各项检查

## 配置文件说明

参考 `application.yml` 中的详细配置说明和示例。

## 定时报告系统详细说明

### 执行时间

- **调度规则**：每小时的第5分钟和第35分钟执行
- **Cron表达式**：`0 5,35 * * * *`
- **执行频率**：每半小时一次

### 报告顺序和内容

1. **Java程序状态检查** (INFO级别)
    - 报告程序本身正常运行
    - 包含运行时间、内存使用率、线程数等信息

2. **MySQL连接状态检查** (INFO/CRITICAL级别)
    - 验证所有配置的数据库连接
    - 连接正常发送INFO级别，异常发送CRITICAL级别

3. **长时间SQL分析** (INFO/WARNING/CRITICAL级别)
    - 使用`SHOW FULL PROCESSLIST`检查超过30分钟的SQL
    - 无长时间SQL：发送INFO级别正常报告
    - 有长时间SQL：发送WARNING/CRITICAL级别异常报告，包含具体SQL语句

4. **系统资源检查** (INFO/WARNING/CRITICAL级别)
    - CPU使用率、内存使用情况
    - 根据资源紧张程度确定警告级别

5. **磁盘空间检查** (INFO/WARNING/CRITICAL级别)
    - 各分区使用率检查
    - 根据磁盘使用率确定警告级别

6. **综合健康总结** (INFO级别)
    - 整体状态汇总报告

### 分级发送策略

- **INFO级别**：发送给监控系统、状态看板
- **WARNING级别**：发送给运维团队
- **CRITICAL级别**：发送给管理员、值班人员

## 测试

使用提供的 `test-api.http` 文件在IntelliJ IDEA中测试所有API接口，包括手动触发定时报告功能。

## 配置变更记录

### v1.3 (最新) - 三账号报警系统重构 🚨
- **全新报警架构**: 重构为三账号分级报警系统
- **标准三参数**: 统一使用 receiver, auth, content 参数格式
- **三级映射**: info/warn/error → 三个独立账号
- **每日限额**: 每个账号每天300次报警限制，自动跟踪使用次数
- **配置简化**: 移除复杂的端点配置，使用统一webhook地址
- **测试接口**: 提供完整的报警测试和统计查看接口
- **自动重置**: 每日使用次数自动重置
- **账号管理**: 支持使用统计查看、手动重置等管理功能

### v1.2 - MySQL 5.7 兼容性优化
- **MySQL 5.7 兼容**: 全面适配 MySQL 5.7，移除不兼容语法
- **数据库驱动**: 添加原生 MySQL 驱动支持，保留 MariaDB 驱动作为备选
- **锁等待检测**: 使用 `SHOW STATUS` 替代 `information_schema.innodb_lock_waits`
- **启动类优化**: 排除默认数据源自动配置，避免冲突
- **连接URL优化**: 添加 MySQL 5.7 必要的连接参数

### v1.1 
- **数据源配置**: 简化为单一数据源 `main-db` (192.168.226.136:3306)
- **兼容性改进**: 移除 `information_schema.SESSION_STATUS` 依赖，改用 `SHOW STATUS`
- **连接统计**: 优化统计收集逻辑，提高数据库兼容性
- **测试更新**: 更新所有测试用例以匹配新的数据源配置

### 当前配置 (v1.3 三账号报警系统)
```yaml
monitor:
  datasources:
    - name: "main-db"
      url: "jdbc:mysql://192.168.226.136:3306/mysql?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8"
      username: "root"
      password: "rootpass"
      enabled: true
      check-interval: 30
      tags: ["production", "main"]
      
  # 新版本三账号报警配置
  alert:
    enabled: true
    webhook-url: "http://localhost:8080/api/test/webhook"  # 改为您的报警接口
    accounts:
      info:
        receiver: "info-alerts"      # INFO级别接收方
        auth: "info_token_12345"     # INFO级别认证token
        daily-limit: 300
      warn:
        receiver: "warn-alerts"      # WARN级别接收方
        auth: "warn_token_67890"     # WARN级别认证token
        daily-limit: 300
      error:
        receiver: "error-alerts"     # ERROR级别接收方
        auth: "error_token_abcde"    # ERROR级别认证token
        daily-limit: 300
```

### 报警工作流程

1. **监控检测** → 发现问题，确定级别（info/warn/error）
2. **账号选择** → 自动选择对应级别的账号配置
3. **限额检查** → 验证当日使用次数是否超限
4. **构建请求** → 使用三参数格式：receiver, auth, content
5. **发送报警** → POST到配置的webhook-url
6. **统计更新** → 增加对应账号的使用次数

### 报警内容格式

系统自动生成的报警内容格式：
```
🔴 数据库连接断开警告
数据库: main-db
状态: 连接断开
时间: 2024-01-20 15:30:00
检查耗时: 120ms
```

## 故障排除

### MySQL 5.7 兼容性问题
如果遇到数据库兼容性问题：
- ✅ **已修复**：全面适配 MySQL 5.7
- ✅ **锁等待检测**：使用 `SHOW STATUS` 替代 `information_schema.innodb_lock_waits`
- ✅ **SESSION_STATUS问题**：系统现在使用 `SHOW STATUS` 替代
- ✅ **权限问题**：优雅降级，统计收集失败时不影响核心监控功能
- ✅ **连接参数**：针对 MySQL 5.7 优化连接 URL 参数

### MySQL 5.7 必需权限
确保数据库用户具有以下权限：
```sql
-- 基本监控权限
GRANT SELECT ON *.* TO 'root'@'%';
GRANT PROCESS ON *.* TO 'root'@'%';

-- 查看进程列表权限
GRANT SHOW DATABASES ON *.* TO 'root'@'%';
```

### 连接测试
```bash
# 测试数据库连接
curl http://localhost:8080/api/monitor/check/main-db

# 测试连接统计收集
curl http://localhost:8080/api/monitor/status/detailed

# 测试 MySQL 5.7 兼容性
curl http://localhost:8080/api/monitor/status/comprehensive
```

### 常见问题解决

1. **SSL连接问题**
   ```
   连接URL已包含 useSSL=false 参数
   ```

2. **时区问题**
   ```
   连接URL已包含 serverTimezone=Asia/Shanghai 参数
   ```

3. **字符编码问题**
   ```
   连接URL已包含 useUnicode=true&characterEncoding=utf8 参数
   ```

## 许可证

MIT License