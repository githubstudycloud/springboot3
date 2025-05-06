# 平台报表引擎模块 (Platform Report Engine)

报表引擎模块是一个强大、灵活的报表生成和分发系统，基于DDD和六边形架构设计，与数据可视化模块紧密集成，提供专业的报表生成、管理和分发能力。

## 核心功能

1. **模板管理**：创建、编辑和管理报表模板，支持自定义布局、样式和组件
2. **报表生成**：根据模板生成多种格式的报表(PDF, Excel, Word等)
3. **参数化查询**：支持在生成报表时传入动态参数
4. **版本控制**：自动管理报表版本，支持查看和比较历史报表
5. **计划任务**：配置定时生成和分发报表的计划
6. **分发功能**：支持通过多种渠道分发报表(邮件、共享文件夹、FTP等)

## 架构设计

模块遵循DDD(领域驱动设计)和六边形架构(端口与适配器)原则，将核心业务逻辑与技术实现分离：

```
platform-report-engine/
├── domain/                 # 领域层 - 核心业务逻辑
│   ├── model/              # 领域模型
│   ├── repository/         # 仓储接口
│   ├── service/            # 领域服务
│   └── event/              # 领域事件
├── application/            # 应用层 - 应用服务和用例
│   ├── service/            # 应用服务
│   └── dto/                # 数据传输对象
├── infrastructure/         # 基础设施层 - 技术实现
│   ├── persistence/        # 持久化实现
│   ├── service/            # 技术服务实现
│   └── config/             # 配置
└── interfaces/             # 接口层 - 与外部系统交互
    ├── rest/               # REST API
    └── scheduler/          # 调度任务
```

## 技术栈

- **后端框架**：Spring Boot 3.x
- **数据库**：MySQL 5.7
- **报表生成**：JasperReports, POI
- **调度系统**：Quartz
- **邮件发送**：JavaMail
- **文档生成**：OpenAPI 3.0 (Swagger)

## 与其他模块的集成

- **数据可视化模块**：紧密集成数据可视化模块，复用其数据源和数据集定义，实现数据从展示到报表的无缝流转
- **认证授权模块**：遵循统一权限体系，确保报表安全性和访问控制
- **调度系统**：与平台统一调度系统集成，实现报表生成任务的高效调度和管理
- **监控仪表板**：将报表生成和分发状态集成到监控仪表板，实时监控系统健康状况

## 快速开始

### 依赖配置

添加Maven依赖：

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>platform-report-engine</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 数据库配置

```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/platform_report
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL57Dialect
```

### 报表存储配置

```properties
# 报表文件存储路径
report.storage.path=./report-storage

# 邮件配置
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=report@example.com
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## API接口

### 模板管理

- `POST /api/templates`：创建模板
- `GET /api/templates/{id}`：获取模板详情
- `GET /api/templates`：查询模板列表
- `PUT /api/templates/{id}/basic-info`：更新模板基本信息
- `PUT /api/templates/{id}/layout`：更新模板布局
- `PUT /api/templates/{id}/style`：更新模板样式
- `POST /api/templates/{id}/components`：添加模板组件
- `PUT /api/templates/{id}/components/{componentId}`：更新模板组件
- `DELETE /api/templates/{id}/components/{componentId}`：删除模板组件
- `POST /api/templates/{id}/publish`：发布模板
- `GET /api/templates/{id}/export`：导出模板
- `POST /api/templates/import`：导入模板
- `POST /api/templates/{id}/preview`：预览模板

### 报表管理

- `POST /api/reports`：创建报表
- `GET /api/reports/{id}`：获取报表详情
- `GET /api/reports`：查询报表列表
- `POST /api/reports/{id}/generate`：生成报表
- `POST /api/reports/{id}/generate-async`：异步生成报表
- `GET /api/reports/{id}/generation-status`：获取生成状态
- `POST /api/reports/{id}/regenerate`：重新生成报表
- `GET /api/reports/{id}/download`：下载报表
- `GET /api/reports/{id}/versions`：获取报表版本列表
- `GET /api/reports/{id}/versions/compare`：比较报表版本

### 计划管理

- `POST /api/schedules`：创建计划
- `GET /api/schedules/{id}`：获取计划详情
- `GET /api/schedules`：查询计划列表
- `PUT /api/schedules/{id}`：更新计划
- `POST /api/schedules/{id}/pause`：暂停计划
- `POST /api/schedules/{id}/resume`：恢复计划
- `POST /api/schedules/{id}/cancel`：取消计划
- `POST /api/schedules/{id}/trigger`：触发计划执行
- `GET /api/schedules/{id}/execution-history`：获取执行历史

### 分发管理

- `POST /api/distributions`：创建分发
- `GET /api/distributions/{id}`：获取分发详情
- `GET /api/distributions`：查询分发列表
- `POST /api/distributions/{id}/channels`：添加分发渠道
- `PUT /api/distributions/{id}/channels/{channelId}`：更新分发渠道
- `DELETE /api/distributions/{id}/channels/{channelId}`：删除分发渠道
- `POST /api/distributions/{id}/start`：开始分发
- `POST /api/distributions/{id}/start-async`：异步开始分发
- `GET /api/distributions/{id}/status`：获取分发状态
- `POST /api/distributions/{id}/retry`：重试分发
- `GET /api/distributions/channel-types`：获取渠道类型

## 开发者指南

### 添加新的报表类型

1. 在`TemplateType`枚举中添加新类型
2. 在`JasperReportEngineService`中实现相应的生成方法
3. 更新转换器和控制器以支持新类型

### 扩展分发渠道

1. 在`ChannelType`枚举中添加新渠道类型
2. 在`DistributionService`中实现相应的分发方法
3. 定义渠道所需的属性和校验规则

## 开发状态

目前报表引擎模块已完成领域模型设计、API定义及基础架构的实现。核心的业务逻辑和技术基础设施已搭建完毕，为后续功能开发提供了坚实基础。更多详细开发计划请查看[开发计划文档](./TODO.md)。

## 未来计划

- 支持更多报表格式(如图表、仪表盘)
- 增强报表定制能力(如条件格式、动态图表)
- 集成更多分发渠道(如微信、企业微信)
- 添加报表批量处理功能
- 性能优化和监控功能
- 支持AI辅助报表设计和内容生成
- 实现报表模板市场，提供预定义模板

## 贡献指南

我们欢迎各种形式的贡献，包括但不限于提交问题、改进文档、修复缺陷或添加新功能。请参考[贡献指南](../docs/contributing.md)了解如何参与项目开发。
