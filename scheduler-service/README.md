# 分布式定时任务调度系统

基于Spring Boot 3.2.9构建的分布式定时任务调度系统，支持跨多个节点协调任务执行，确保任务不会重复执行，并提供统一的任务监控和管理能力。

## 核心特性

- **分布式调度**：支持集群部署，任务在多节点间自动协调，避免重复执行
- **多种任务类型**：支持HTTP任务，并可扩展支持脚本任务、类执行任务等
- **多种调度策略**：支持Cron表达式、固定频率、固定延迟等多种调度方式
- **可靠性保障**：
  - 分布式锁确保任务不重复执行
  - 任务失败自动重试（指数退避策略）
  - 节点故障自动恢复
- **实时监控**：任务执行状态和进度实时更新和查询
- **统一日志**：集中式日志管理，按月分表存储，支持多维度查询
- **高性能设计**：
  - 异步执行和并行处理
  - Redis缓存加速状态查询
  - 连接池和线程池优化

## 技术栈

- **核心框架**：Spring Boot 3.2.9
- **数据存储**：MySQL 8.0+
- **缓存与分布式锁**：Redis 6.0+
- **ORM框架**：MyBatis Plus 3.5.2
- **分库分表**：ShardingSphere 5.3.1
- **分布式锁**：Redisson 3.17.6
- **API文档**：SpringDoc OpenAPI 2.0.2

## 系统架构

```
+---------------------------+
|      API层 (Controller)    |
+---------------------------+
              |
+---------------------------+
|      业务层 (Service)       |
+---------------------------+
              |
+---------+----------+------+
| 调度器   | 执行器    | 监控器 |
+---------+----------+------+
              |
+---------------------------+
|      持久层 (Repository)   |
+---------------------------+
              |
+---------------------------+
| 存储层 (MySQL, Redis)      |
+---------------------------+
```

### 核心组件

1. **任务调度器(TaskScheduler)**
   - 扫描待执行任务
   - 获取分布式锁防止重复执行
   - 计算下次执行时间
   - 支持手动触发和终止任务

2. **任务执行器(TaskExecutor)**
   - 插件式设计，易于扩展
   - 内置HTTP任务执行器
   - 支持任务进度更新
   - 执行超时控制

3. **分布式锁管理器(DistributedLockManager)**
   - 基于Redis实现，MySQL作为备选
   - 支持锁超时和异常处理
   - 安全的锁释放机制

4. **状态管理器(TaskStatusManager)**
   - 任务状态实时同步
   - Redis缓存加速查询
   - 支持进度实时更新

5. **日志管理器(LogManager)**
   - 统一日志收集
   - 按月分表存储
   - 多维度查询支持

## 数据模型

- **Task**: 任务定义，包含调度配置和执行参数
- **TaskExecution**: 任务执行记录，记录每次执行的详情
- **TaskLog**: 任务执行日志，记录执行过程中的详细日志
- **SchedulerNode**: 调度节点信息，用于集群管理
- **TaskProgress**: 任务执行进度，用于长时间运行任务的进度跟踪

## 接口设计

系统提供RESTful API接口，支持以下功能：

- 任务管理：创建、更新、删除、查询任务
- 任务控制：启动、暂停、立即执行、终止任务
- 状态查询：查询任务状态、执行历史、执行详情
- 日志管理：查询任务日志、执行日志、导出日志

## 目录结构

```
scheduler-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── platform/
│   │   │           └── scheduler/
│   │   │               ├── api/           # REST API控制器
│   │   │               ├── config/        # 配置类
│   │   │               ├── core/          # 核心组件
│   │   │               │   ├── scheduler/ # 任务调度器
│   │   │               │   ├── executor/  # 任务执行器
│   │   │               │   ├── lock/      # 分布式锁
│   │   │               │   ├── status/    # 状态管理
│   │   │               │   └── log/       # 日志管理
│   │   │               ├── model/         # 数据模型
│   │   │               ├── repository/    # 数据访问层
│   │   │               ├── service/       # 业务服务层
│   │   │               └── util/          # 工具类
│   │   └── resources/
│   │       ├── application.yml            # 应用配置
│   │       ├── application-dev.yml        # 开发环境配置
│   │       └── application-prod.yml       # 生产环境配置
│   └── test/                              # 测试代码
└── docs/                                  # 文档
```

## 开发进度

目前项目处于开发中，已完成的部分包括：

- [x] 基础文档编写
- [x] 核心框架搭建
- [x] 数据模型实现
- [x] 基础设施组件实现
- [x] 数据访问层实现
- [x] 核心组件实现
- [ ] 业务服务层实现
- [ ] API层实现
- [ ] 高级功能实现
- [ ] 测试与优化
- [ ] 文档完善和部署

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 准备数据库

执行`docs/database-design.md`中的SQL脚本创建数据库和表结构。

### 2. 配置应用

修改`application.yml`、`application-dev.yml`或`application-prod.yml`中的数据库和Redis连接配置。

### 3. 构建项目

```bash
mvn clean package
```

### 4. 运行应用

```bash
java -jar target/scheduler-service-1.0.0.jar
```

### 5. 访问API

默认API地址：http://localhost:8080/swagger-ui.html

## 扩展开发

### 添加新的任务类型

1. 创建新的TaskExecutor实现类：

```java
@Component
public class MyCustomTaskExecutor extends AbstractTaskExecutor {
    
    @Override
    public String getType() {
        return "CUSTOM_TYPE";
    }
    
    @Override
    protected TaskResult doExecute(Task task, Long executionId) throws Exception {
        // 实现自定义任务执行逻辑
        return result;
    }
}
```

2. 系统会自动通过TaskExecutorFactory注册和管理新的执行器。

## 文档

- [API文档](docs/api-docs.md)
- [数据库设计](docs/database-design.md)
- [系统流程图](docs/system-flow.md)
- [需求规格说明书](docs/requirements-specification.md)
- [技术架构设计](docs/technical-architecture.md)

## 贡献指南

1. Fork本仓库
2. 创建你的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交你的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个Pull Request

## 许可证

该项目采用MIT许可证 - 详情见[LICENSE](LICENSE)文件
