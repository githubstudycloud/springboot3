# 数据库监控系统

Spring Boot 3.2 + MariaDB 数据库监控应用，实时监控数据库性能和连接状态，支持智能报警和系统健康检查。

## 功能特性

### 核心监控功能

- ✅ 实时监控多个数据库连接状态
- ✅ 检测长时间运行的SQL查询
- ✅ 数据库连接断开/重连自动检测
- ✅ 系统资源监控（CPU、内存、磁盘）
- ✅ 支持自定义监控规则和阈值
- ✅ 动态调整监控频率

### 智能报警系统

- ✅ **分级报警机制**：支持 critical、warning、info 三个级别
- ✅ **按级别分发**：不同等级报告发送给不同接收对象
- ✅ **状态变化检测**：数据库断开/重连时自动发送通知
- ✅ **HTTP协议支持**：确保所有警告URL使用HTTP方式
- ✅ **多端点配置**：支持管理员、运维、开发等不同团队接收不同级别警告

### API接口

- ✅ REST API接口查询监控状态
- ✅ **测试接口**：支持JSON参数（senduser、token、content）
- ✅ 综合健康检查（数据库+系统）
- ✅ **定时报告系统**：每半小时自动健康检查和报告

## 技术栈

- Java 21 (虚拟线程支持)
- Spring Boot 3.2
- MariaDB
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

## 报警级别与分发策略

### 报警级别

- **Critical**: 数据库完全不可用、系统严重故障
- **Warning**: 性能问题、长时间运行SQL
- **Info**: 连接恢复、状态正常通知

### 接收端点类型

- `critical-only`: 只接收critical级别（管理员、值班人员）
- `warning-and-critical`: 接收warning和critical（运维团队）
- `info-and-above`: 接收所有级别（监控系统、日志收集）
- `non-critical`: 排除critical级别（开发团队）
- `info-only`: 只接收info级别（状态看板）

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
       - name: "primary-db"
         url: "jdbc:mariadb://localhost:3306/test"
         username: "your_user"
         password: "your_password"
   ```

3. **配置报警端点**
   ```yaml
   monitor:
     alert:
       endpoints:
         - name: "admin-critical"
           url: "http://your-webhook-url/critical"
           type: "critical-only"
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
- `PUT /api/monitor/schedule/{dataSource}` - 动态调整监控间隔

### 测试接口

- `POST /api/test/alert` - 测试警告发送（JSON参数：senduser、token、content）
- `POST /api/test/test-db-disconnect` - 测试数据库断开警告
- `POST /api/test/test-db-reconnect` - 测试数据库重连通知
- `POST /api/test/test-severity/{level}` - 测试不同级别警告
- `POST /api/test/webhook` - Webhook接收测试端点
- `POST /api/test/trigger-scheduled-report` - 手动触发定时健康报告
- `GET /api/test/scheduled-report-info` - 获取定时报告配置信息

### 测试警告接口示例

```bash
curl -X POST http://localhost:8080/api/test/alert \
  -H "Content-Type: application/json" \
  -d '{
    "senduser": "admin",
    "token": "test-token-123", 
    "content": "测试警告消息",
    "webhookUrl": "http://localhost:8080/api/test/webhook"
  }'
```

## 监控功能验证

### 数据库连接状态监控

- ✅ 程序能够检测数据库断开并发送critical级别警告
- ✅ 程序能够检测数据库重连并发送info级别通知
- ✅ 支持多数据源并行监控

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

## 许可证

MIT License