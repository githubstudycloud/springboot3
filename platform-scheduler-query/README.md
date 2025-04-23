# Platform Scheduler Query（平台调度器查询服务）

## 模块简介

Platform Scheduler Query是平台调度系统的查询服务，专注于提供任务状态查询、执行历史分析和统计报告功能。该服务采用读写分离架构，优化查询性能，支持复杂的数据分析和报表生成，为用户和管理员提供全面的任务执行视图。

## 主要功能

- **任务状态查询**：实时查询任务当前状态
- **执行历史查询**：查询任务的历史执行记录
- **统计分析**：提供任务执行统计和趋势分析
- **性能指标**：跟踪任务执行性能指标
- **SLA监控**：监控任务执行的SLA合规性
- **报表生成**：生成定制化的调度报表

## 与调度器生态系统的协作

本服务是调度器三件套之一：

1. **platform-scheduler**：核心调度引擎，负责任务触发和执行
2. **platform-scheduler-register**：任务注册服务，负责任务定义管理
3. **platform-scheduler-query**（本模块）：任务查询服务，专注于数据查询和分析

查询服务通过消费执行事件和直接查询数据库来获取数据，不直接参与任务的调度和执行过程。

## 目录结构

```
platform-scheduler-query/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── scheduler/
│       │               └── query/
│       │                   ├── config/            # 配置类
│       │                   ├── controller/        # REST控制器
│       │                   ├── service/           # 业务逻辑
│       │                   ├── repository/        # 数据访问
│       │                   ├── model/             # 数据模型
│       │                   ├── analyzer/          # 数据分析器
│       │                   └── exception/         # 异常处理
│       └── resources/
│           └── application.yml        # 应用配置文件
└── pom.xml                            # Maven配置文件
```

## 数据存储

查询服务使用多层数据存储策略：

1. **关系型数据库**：存储基本任务信息和最近历史
2. **Elasticsearch**：存储长期历史数据，支持复杂查询
3. **内存缓存**：缓存热点数据，提高查询性能

数据从调度引擎同步，通过消息队列接收执行事件，确保数据的最终一致性。

## API接口

### 任务查询接口

```
GET  /api/v1/tasks                    # 查询任务列表（支持过滤和分页）
GET  /api/v1/tasks/{taskId}           # 获取任务详情
GET  /api/v1/tasks/{taskId}/status    # 获取任务当前状态
```

### 执行历史接口

```
GET  /api/v1/tasks/{taskId}/history              # 获取任务执行历史
GET  /api/v1/tasks/{taskId}/history/{executionId}# 获取特定执行详情
GET  /api/v1/executions                          # 查询所有执行记录（支持过滤）
GET  /api/v1/executions/latest                   # 获取最新执行记录
```

### 统计分析接口

```
GET  /api/v1/statistics/summary                  # 获取总体统计数据
GET  /api/v1/statistics/tasks/{taskId}           # 获取特定任务统计
GET  /api/v1/statistics/trend                    # 获取执行趋势数据
GET  /api/v1/statistics/performance              # 获取性能统计
GET  /api/v1/statistics/sla                      # 获取SLA统计
```

### 报表接口

```
GET  /api/v1/reports/daily                       # 生成每日报表
GET  /api/v1/reports/weekly                      # 生成每周报表
GET  /api/v1/reports/monthly                     # 生成每月报表
GET  /api/v1/reports/custom                      # 生成自定义报表
```

## 查询功能

### 基础查询参数

大多数查询接口支持以下参数：

- **分页**：page, size
- **排序**：sort, direction
- **时间范围**：startDate, endDate
- **状态过滤**：status
- **执行结果**：result
- **执行节点**：nodeId

### 高级查询功能

支持多种高级查询场景：

1. **全文搜索**：通过关键词搜索任务和执行记录
2. **趋势分析**：按时间维度分析执行趋势
3. **异常分析**：查找和聚合错误模式
4. **性能对比**：比较不同时间段的执行性能
5. **SLA统计**：分析服务水平协议达成情况

## 性能优化

为处理大量查询请求，实现了多项性能优化：

1. **读写分离**：独立的查询服务减轻主服务负担
2. **分层缓存**：多级缓存减少数据库访问
3. **查询优化**：针对常见查询场景优化索引
4. **数据分片**：历史数据按时间分片存储
5. **异步处理**：复杂报表生成异步处理

## 数据分析

查询服务提供多种数据分析功能：

1. **执行时间分析**：任务执行时间分布和趋势
2. **失败率分析**：任务失败率和失败原因
3. **负载分析**：系统负载分布和高峰期识别
4. **节点性能**：不同节点的执行效率对比
5. **预测分析**：基于历史数据预测未来趋势

## 数据导出

支持多种格式的数据导出：

1. **CSV格式**：表格数据导出
2. **Excel格式**：格式化报表导出
3. **PDF格式**：标准报告导出
4. **JSON格式**：结构化数据导出

## 监控与告警

查询服务集成了监控和告警功能：

1. **SLA监控**：监控任务执行是否符合SLA
2. **趋势告警**：基于趋势变化触发告警
3. **异常检测**：自动检测异常执行模式
4. **资源监控**：监控服务自身资源使用

## 开发指南

### 自定义统计分析

可以通过实现`StatisticsAnalyzer`接口创建自定义分析器：

```java
@Component
public class CustomStatisticsAnalyzer implements StatisticsAnalyzer {
    @Override
    public StatisticsResult analyze(StatisticsRequest request) {
        // 自定义分析逻辑
        return new StatisticsResult();
    }
    
    @Override
    public String getAnalyzerName() {
        return "custom-analyzer";
    }
}
```

### 自定义报表

可以实现`ReportGenerator`接口创建自定义报表：

```java
@Component
public class CustomReportGenerator implements ReportGenerator {
    @Override
    public ReportResult generate(ReportRequest request) {
        // 报表生成逻辑
        return new ReportResult();
    }
    
    @Override
    public String getReportType() {
        return "custom-report";
    }
}
```

## 数据保留策略

为有效管理历史数据，实行数据保留策略：

1. **热数据**：最近7天数据保存在关系数据库
2. **温数据**：30天内数据保存在Elasticsearch
3. **冷数据**：更早数据归档到低成本存储
4. **数据汇总**：旧数据进行汇总统计后清理原始记录

## 高可用部署

查询服务可以独立扩展以适应高查询负载：

1. **水平扩展**：支持多实例部署
2. **读写分离**：与主服务数据库分离
3. **缓存集群**：分布式缓存支持
4. **负载均衡**：请求在多实例间分发

## 故障排除

常见问题及解决方案：

1. **查询超时**：检查查询复杂度和索引优化
2. **数据不一致**：检查同步状态和最后更新时间
3. **高内存占用**：调整缓存设置和查询批量大小
4. **统计不准确**：验证统计算法和数据来源
5. **报表生成失败**：检查数据完整性和模板配置

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基本查询功能
  - 执行历史查询
  - 简单统计分析
  - 标准报表生成
  - 数据缓存优化
