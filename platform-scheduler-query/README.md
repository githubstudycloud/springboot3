# Platform Scheduler Query (平台调度查询)

平台调度查询服务是一个基于DDD和六边形架构设计的高性能查询服务，负责提供任务状态查询、历史记录查询和统计分析功能。

## 功能特性

- **实时任务状态查询**：提供实时查询任务运行状态的能力，支持多维度过滤和复杂条件组合
- **历史任务查询**：支持高效查询历史任务执行记录，提供灵活的时间范围和条件查询
- **统计分析功能**：提供丰富的统计分析能力，生成关键指标和趋势数据
- **高性能查询引擎**：针对大数据量场景进行了优化，支持复杂条件组合和聚合分析
- **数据导出功能**：支持多种格式的数据导出，满足不同场景需求
- **任务执行日志查询**：提供完整的任务执行日志查询和分析功能
- **告警规则引擎**：内置告警规则引擎，支持任务异常监控和告警
- **数据归档功能**：支持历史数据的自动归档和清理，确保系统性能稳定

## 技术栈

- **JDK**: Java 21
- **框架**: Spring Boot 3.x, Spring Cloud 2023.0.0
- **数据访问**: Spring Data JPA, QueryDSL
- **缓存**: Redis, Caffeine
- **消息队列**: Kafka
- **查询协议**: RESTful API, GraphQL
- **实时通信**: WebSocket
- **API文档**: SpringDoc OpenAPI
- **监控**: Spring Boot Actuator
- **缓存策略**: 多级缓存设计

## 架构设计

平台调度查询服务基于领域驱动设计(DDD)和六边形架构(Hexagonal Architecture)设计，主要分为以下几层：

### 领域层 (Domain Layer)

包含核心业务模型和领域服务，遵循"框架与业务隔离"原则，不依赖任何外部框架。

- **模型 (Model)**: 包含任务实例视图、查询条件、搜索结果等值对象
- **服务 (Service)**: 定义领域服务接口，如任务状态查询服务、历史查询服务、统计分析服务等
- **事件 (Event)**: 定义领域事件，用于异步通信和系统解耦

### 应用层 (Application Layer)

负责编排领域对象完成用户故事，处理事务边界和安全性。

- **服务实现 (Service Impl)**: 实现领域服务接口，协调领域对象完成业务逻辑
- **端口 (Port)**: 定义输入和输出端口，与外部世界交互

### 基础设施层 (Infrastructure Layer)

提供技术实现，如数据库访问、缓存、消息队列等。

- **持久化 (Persistence)**: 实现仓储接口，提供数据持久化能力
- **缓存 (Cache)**: 实现缓存策略，提高查询性能
- **消息 (Messaging)**: 实现消息发送和接收，支持事件驱动
- **异常检测 (Anomaly Detection)**: 实现异常检测算法，发现异常任务

### 接口层 (Interfaces Layer)

提供用户界面和外部接口，如Web API、WebSocket等。

- **控制器 (Controller)**: 处理HTTP请求，提供RESTful API
- **GraphQL (GraphQL)**: 提供GraphQL查询接口，支持灵活查询
- **WebSocket (WebSocket)**: 提供实时监控功能

## 关键设计考量

1. **查询性能优化**
   - 多级缓存策略：结合内存缓存和Redis分布式缓存
   - 查询计划优化：使用QueryDSL进行动态查询优化
   - 索引设计：精心设计索引，提高查询效率
   - 数据分片：支持大数据量下的数据分片查询

2. **数据一致性**
   - 读写分离：查询服务专注于只读操作，减轻核心服务负担
   - 事件驱动：通过事件驱动实现数据同步和一致性
   - 最终一致性：接受短暂的数据不一致，保证最终一致性

3. **可扩展性**
   - 模块化设计：功能模块化，方便扩展
   - 插件化架构：支持通过插件扩展功能
   - 弹性伸缩：支持水平扩展，应对高并发场景

4. **可用性**
   - 服务降级：当依赖服务不可用时提供降级策略
   - 熔断机制：避免级联故障，保护系统稳定
   - 监控告警：实时监控系统状态，及时发现问题

## 接口列表

### RESTful API

#### 任务状态查询接口
- `GET /api/v1/tasks/{taskId}` - 根据任务ID查询任务详情
- `POST /api/v1/tasks/batch` - 批量查询任务详情
- `GET /api/v1/tasks/job/{jobId}/latest` - 查询作业最近一次执行的任务
- `GET /api/v1/tasks/job/{jobId}/history` - 查询作业执行历史
- `GET /api/v1/tasks/running` - 查询正在运行的任务
- `GET /api/v1/tasks/failed` - 查询失败的任务
- `POST /api/v1/tasks/search` - 高级查询任务
- `GET /api/v1/tasks/waiting-retry` - 查询等待重试的任务
- `GET /api/v1/tasks/executor/{executorId}` - 查询特定执行器的任务
- `GET /api/v1/tasks/timeout` - 查询超时的任务
- `GET /api/v1/tasks/{taskId}/details` - 获取任务详情(包含参数和日志)

#### 任务历史查询接口
- `POST /api/v1/task-history/query` - 根据时间范围查询历史任务
- `GET /api/v1/task-history/trends` - 获取任务执行趋势数据
- `GET /api/v1/task-history/status-distribution` - 获取任务状态分布
- `GET /api/v1/task-history/job/{jobId}/history` - 获取作业执行历史记录
- `GET /api/v1/task-history/duration-distribution` - 获取任务执行时间分布
- `GET /api/v1/task-history/retry-distribution` - 获取任务重试次数分布
- `GET /api/v1/task-history/task/{taskId}/logs` - 获取任务日志数据
- `POST /api/v1/task-history/export` - 导出任务历史数据
- `GET /api/v1/task-history/heatmap` - 获取任务执行热力图数据
- `GET /api/v1/task-history/task/{taskId}/chain` - 查询关联的任务执行链
- `POST /api/v1/task-history/archive` - 归档历史任务数据

#### 任务统计分析接口
- `GET /api/v1/task-analytics/metrics` - 获取任务统计指标
- `GET /api/v1/task-analytics/job/{jobId}/metrics` - 获取作业统计指标
- `GET /api/v1/task-analytics/executor/{executorId}/metrics` - 获取执行器统计指标
- `GET /api/v1/task-analytics/success-rate-trend` - 获取任务成功率趋势
- `GET /api/v1/task-analytics/performance-ranking` - 获取任务性能排行榜
- `GET /api/v1/task-analytics/failure-rate-ranking` - 获取任务失败率排行榜
- `GET /api/v1/task-analytics/executor-load-ranking` - 获取执行器负载排行榜
- `GET /api/v1/task-analytics/health-score` - 计算系统整体健康分数
- `GET /api/v1/task-analytics/peak-hours` - 获取每日任务执行峰值时间
- `GET /api/v1/task-analytics/anomalous-tasks` - 分析并预测异常任务
- `GET /api/v1/task-analytics/job/{jobId}/execution-time-trend` - 获取作业执行时间趋势
- `GET /api/v1/task-analytics/report` - 生成任务执行报表
- `GET /api/v1/task-analytics/resource-utilization` - 获取资源利用率统计
- `GET /api/v1/task-analytics/compare-periods` - 对比任务执行周期

### GraphQL

同时提供GraphQL查询接口，支持更灵活的查询方式，具体可参考GraphQL文档。

## 配置说明

关键配置参数说明：

```yaml
scheduler-query:
  # 查询性能优化配置
  query:
    # 默认缓存过期时间(秒)
    cache-ttl: 300
    # 查询结果最大条数
    max-results: 10000
    # 默认分页大小
    default-page-size: 20
  
  # 归档配置
  archive:
    # 是否启用自动归档
    auto-archive-enabled: true
    # 归档时间阈值(天)，超过这个时间的数据会被归档
    archive-threshold-days: 30
    # 自动归档任务的cron表达式
    auto-archive-cron: '0 0 2 * * ?'
    # 归档数据保留时间(天)
    retention-days: 365
  
  # 告警配置
  alert:
    # 是否启用告警
    enabled: true
    # 告警阈值
    thresholds:
      # 任务失败率阈值(%)
      failure-rate: 10.0
      # 任务超时率阈值(%)
      timeout-rate: 5.0
      # 执行器负载阈值(%)
      executor-load: 80.0
```

## 性能优化

平台调度查询模块在性能方面进行了多种优化：

1. **索引优化**：精心设计的数据库索引，支持高效查询
2. **多级缓存**：组合使用JVM内存缓存(Caffeine)和分布式缓存(Redis)
3. **查询优化**：使用QueryDSL进行动态查询优化，避免不必要的数据库访问
4. **数据分区**：根据时间对数据进行分区，提高查询效率
5. **数据归档**：自动归档历史数据，保持活跃数据集的精简
6. **异步处理**：耗时操作采用异步处理，提高响应速度
7. **并行查询**：支持并行查询处理，充分利用系统资源

## 安装与部署

### 环境要求

- JDK 21+
- MySQL 5.7+
- Redis 7.x+
- Kafka 3.x+

### 构建与运行

```bash
# 克隆项目
git clone https://github.com/yourusername/platform-scheduler-query.git

# 进入项目目录
cd platform-scheduler-query

# 构建
./mvnw clean package -DskipTests

# 运行
java -jar target/platform-scheduler-query-1.0.0-SNAPSHOT.jar
```

### Docker部署

```bash
# 构建Docker镜像
docker build -t platform-scheduler-query:latest .

# 运行容器
docker run -d -p 8083:8083 --name scheduler-query platform-scheduler-query:latest
```

## 未来计划

1. **更强大的实时分析**：增强实时分析能力，提供更丰富的实时洞察
2. **机器学习集成**：引入机器学习模型，实现更智能的异常检测和趋势预测
3. **更丰富的可视化**：提供更丰富的数据可视化能力，直观展示任务执行情况
4. **高级告警策略**：支持更复杂的告警规则和策略，提高告警的精准度
5. **更高效的存储策略**：优化数据存储策略，进一步提高查询性能和降低存储成本

## 贡献指南

欢迎贡献代码或提出建议，详情请参阅[贡献指南](CONTRIBUTING.md)。

## 许可证

本项目采用Apache 2.0许可证，详情请参阅[LICENSE](LICENSE)文件。
