# 平台数据可视化模块 (Platform Data Visualization)

## 项目介绍

平台数据可视化模块是一个基于Spring Boot 3.x + Vue 3.x的全栈开发模块，旨在提供强大、灵活的数据可视化功能。该模块遵循DDD（领域驱动设计）和六边形架构（端口与适配器模式），实现了业务逻辑与技术实现的分离，确保系统具有良好的可维护性和可扩展性。

## 核心功能

- **多数据源管理**：支持关系型数据库、NoSQL、文件系统等多种数据源的统一管理和连接
- **数据集处理**：提供数据提取、转换、过滤和聚合功能，支持多种数据格式
- **丰富的图表组件**：内置多种图表类型，包括折线图、柱状图、饼图、散点图、热力图、地图等
- **交互式仪表板**：支持拖拽式仪表板设计，可自由组合不同图表和数据视图
- **定时报表**：支持按计划生成和分发报表，支持多种导出格式（PDF、Excel、CSV等）
- **权限管理**：细粒度的用户权限控制，确保数据安全性

## 技术架构

### 后端技术栈

- **核心框架**：Spring Boot 3.x
- **ORM框架**：Spring Data JPA
- **数据库**：MySQL 5.7
- **缓存**：Redis
- **任务调度**：Quartz
- **API文档**：Swagger/OpenAPI 3

### 前端技术栈（将在独立模块中实现）

- **框架**：Vue 3.x + Vite
- **UI组件**：Element Plus
- **图表库**：ECharts/D3.js
- **状态管理**：Pinia
- **路由**：Vue Router

## 架构设计

本模块基于DDD和六边形架构设计，核心架构如下：

```
platform-data-visualization/
├── domain/                     # 领域层
│   ├── model/                  # 领域模型
│   ├── service/                # 领域服务
│   ├── repository/             # 仓储接口
│   └── exception/              # 领域异常
├── application/                # 应用层
│   ├── service/                # 应用服务
│   ├── dto/                    # 数据传输对象
│   └── assembler/              # DTO转换器
├── infrastructure/             # 基础设施层
│   ├── repository/             # 仓储实现
│   ├── persistence/            # 持久化实体
│   ├── datasource/             # 数据源适配器
│   └── config/                 # 配置类
└── interfaces/                 # 接口层
    ├── rest/                   # REST API
    ├── websocket/              # WebSocket接口
    └── scheduler/              # 定时任务
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 5.7+
- Redis 7.x+

### 构建与运行

1. 克隆代码仓库
```bash
git clone https://your-repository/platform-data-visualization.git
```

2. 修改配置文件
```bash
vim src/main/resources/application.yml
```

3. 编译打包
```bash
mvn clean package
```

4. 运行应用
```bash
java -jar target/platform-data-visualization.jar
```

5. 访问接口文档
```
http://localhost:8083/swagger-ui.html
```

## 接口说明

模块提供了以下REST API：

- `/api/v1/data-sources`：数据源管理接口
- `/api/v1/data-sets`：数据集管理接口
- `/api/v1/charts`：图表管理接口
- `/api/v1/dashboards`：仪表板管理接口
- `/api/v1/reports`：报表管理接口（待实现）

详细的API文档请参考Swagger接口文档。

## 开发指南

请参考[开发文档](./docs/development-guide.md)了解更多开发细节。

## 待办事项

- [ ] 实现报表生成和导出功能
- [ ] 集成公共权限模块
- [ ] 增强数据分析功能
- [ ] 实现前端可视化组件库
- [ ] 优化大数据量图表渲染性能
- [ ] 增加用户偏好设置与主题定制

## 贡献指南

欢迎贡献代码或提出改进建议，请遵循[贡献指南](./docs/contributing.md)参与项目开发。

## 许可证

本项目采用 [MIT 许可证](./LICENSE)。
