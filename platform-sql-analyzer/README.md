# SQL分析器模块

基于SpringBoot的SQL分析工具，用于管理和执行SQL脚本，分析MySQL数据库性能，查询任意数据库。

## 功能特点

- **通用SQL查询**: 执行预定义或自定义SQL，查询任意数据库
- **多数据源支持**: 动态配置和管理多个数据源连接
- **MySQL性能分析**: 提供丰富的MySQL性能分析工具
  - 表大小分析
  - 表使用情况分析
  - 表增长速度分析
  - 未使用索引分析
  - 表碎片分析
- **性能监控**: 定期收集数据库性能指标
  - 内存使用情况
  - 连接状态
  - 系统状态
  - 慢查询分析
  - 表锁竞争分析
- **SQL优化建议**: 提供数据库优化建议
  - 缺失索引检测
  - 冗余索引识别
  - 表结构优化建议
- **历史数据分析**: 收集和分析历史性能数据
  - 每小时/每日数据收集
  - 数据趋势分析
  - 自动清理过期数据
- **脚本管理**: 分类管理预定义SQL脚本
- **缓存支持**: 提供查询结果缓存机制，提高性能

## 主要功能模块

### 1. SQL脚本管理

SQL脚本按分类存储在`resources/sql`目录下，支持通过元数据注释定义脚本属性：

```sql
-- @name: script_name
-- @description: 脚本描述信息
-- @params: param1:type1, param2:type2
-- @timeout: 30000
-- @dbType: mysql
```

### 2. 数据源管理

- 动态添加、移除数据源
- 支持连接任意MySQL 5.7数据库

### 3. MySQL性能分析

提供多种性能分析工具：

- **表大小分析**: 分析表数据大小、索引大小、总大小等
- **表使用情况**: 分析表的读写情况，发现未使用的表
- **表增长分析**: 估算表的增长速度和趋势
- **索引使用分析**: 发现未使用的索引，分析索引效率
- **表碎片分析**: 分析表碎片率，提供优化建议
- **慢查询分析**: 识别并分析执行缓慢的SQL查询
- **连接状态分析**: 监控数据库连接池状态
- **内存使用分析**: 分析MySQL内存使用情况
- **表锁竞争分析**: 发现可能存在锁竞争的表

### 4. SQL优化建议

提供数据库优化建议：

- **缺失索引检测**: 识别可能需要添加索引的表和列
- **冗余索引识别**: 发现可以合并或删除的冗余索引
- **表结构优化建议**: 分析表结构并提供优化建议

### 5. 性能数据收集

- 定期收集性能数据（每小时/每日）
- 生成历史性能趋势报告
- 自动清理过期数据

## API接口

### 查询接口

- `POST /api/sql-analyzer/query`: 执行SQL查询

### 脚本管理接口

- `GET /api/sql-analyzer/scripts/categories`: 获取所有脚本分类
- `GET /api/sql-analyzer/scripts/{category}`: 获取指定分类下的所有脚本
- `GET /api/sql-analyzer/scripts/{category}/{name}`: 获取脚本详情

### 数据源管理接口

- `POST /api/sql-analyzer/datasources`: 添加数据源
- `GET /api/sql-analyzer/datasources`: 获取所有数据源名称
- `DELETE /api/sql-analyzer/datasources/{name}`: 移除数据源

### MySQL分析接口

- `GET /api/sql-analyzer/database/{dataSource}/{database}`: 获取数据库信息
- `GET /api/sql-analyzer/database/{dataSource}/{database}/table/{table}/columns`: 分析表字段
- `GET /api/sql-analyzer/database/{dataSource}/{database}/table/{table}/indexes`: 分析表索引
- `GET /api/sql-analyzer/database/{dataSource}/{database}/unused-indexes`: 查找未使用索引

### 性能监控接口

- `GET /api/sql-analyzer/performance/overview/{dataSource}`: 获取性能概况
- `GET /api/sql-analyzer/performance/slow-queries/{dataSource}`: 获取慢查询列表
- `GET /api/sql-analyzer/performance/table-locks/{dataSource}/{database}`: 获取表锁竞争情况
- `GET /api/sql-analyzer/performance/history/{dataSource}/{frequency}`: 获取历史性能数据
- `POST /api/sql-analyzer/performance/collect/{dataSource}`: 立即执行数据收集

### 优化建议接口

- `GET /api/sql-analyzer/optimization/missing-indexes/{dataSource}/{database}`: 获取缺失索引建议
- `GET /api/sql-analyzer/optimization/redundant-indexes/{dataSource}/{database}`: 获取冗余索引建议
- `GET /api/sql-analyzer/optimization/table-optimization/{dataSource}/{database}`: 获取表优化建议
- `GET /api/sql-analyzer/optimization/report/{dataSource}/{database}`: 生成优化报告

## 配置说明

主要配置项:

```yaml
# 数据库连接池配置
datasource:
  default:
    jdbc-url: jdbc:mysql://localhost:3306/information_schema
    username: root
    password: password

# SQL分析器配置
sql-analyzer:
  # 定义脚本检索路径
  script-locations: classpath:sql/**/*.sql
  # 默认SQL超时时间（毫秒）
  default-timeout: 30000
  # 是否启用SQL缓存
  cache-enabled: true
  # 缓存有效期（分钟）
  cache-expiration: 10
  # 历史数据存储路径
  history-data-path: ./data/history
  # 性能数据收集配置
  data-collection:
    # 是否启用数据收集
    enabled: true
    # 保留历史数据的时间（天）
    retention-days:
      hourly: 7
      daily: 90
      weekly: 365
```

## 使用方法

1. 启动应用
2. 添加数据源(可使用默认数据源)
3. 使用API执行分析查询
4. 查看优化建议和性能报告

## 示例查询

查询表大小:

```json
{
  "dataSource": "default",
  "scriptName": "table_size_analysis",
  "category": "table_analysis",
  "parameters": {
    "dbName": "mydatabase"
  }
}
```

自定义SQL查询:

```json
{
  "dataSource": "default",
  "customSql": "SELECT table_name, engine, table_rows FROM information_schema.tables WHERE table_schema = :dbName",
  "parameters": {
    "dbName": "mydatabase"
  }
}
```

生成优化报告:

```
GET /api/sql-analyzer/optimization/report/default/mydatabase
```

## 依赖关系

- Spring Boot 3.2.9
- HikariCP 5.0.1
- MySQL Connector 8.0.33

## 未来计划

- 添加图形化Dashboard展示性能数据
- 支持更多数据库类型(PostgreSQL, SQL Server等)
- 添加自动化优化建议执行功能
- 集成机器学习模型进行性能预测
- 添加告警通知机制

## 贡献指南

欢迎贡献新的SQL脚本和功能，请遵循以下步骤：

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证
