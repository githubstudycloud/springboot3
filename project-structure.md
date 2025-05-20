# 企业级数据平台项目结构

```
platform-parent/                           # 父项目，管理所有子模块
├── platform-bom/                          # Bill of Materials，统一版本管理
├── platform-dependency/                   # 依赖管理模块
├── platform-common/                       # 通用工具和组件
│   ├── platform-common-core/              # 核心工具类
│   ├── platform-common-web/               # Web相关通用组件
│   ├── platform-common-security/          # 安全相关通用组件
│   └── platform-common-logging/           # 日志相关通用组件
├── platform-framework/                    # 框架层
│   ├── platform-framework-core/           # 核心框架
│   ├── platform-framework-starter/        # 自动配置starter
│   ├── platform-framework-domain/         # 领域框架支持
│   └── platform-framework-test/           # 测试支持框架
├── platform-infrastructure/               # 基础设施层
│   ├── platform-registry/                 # 服务注册中心(Nacos)
│   ├── platform-config/                   # 配置中心
│   ├── platform-gateway/                  # API网关
│   ├── platform-auth-service/             # 认证授权服务
│   ├── platform-monitor-dashboard/        # 监控仪表盘
│   └── platform-notification-service/     # 通知服务
├── platform-data-governance/              # 数据治理模块
│   ├── platform-data-governance-api/      # 数据治理API
│   ├── platform-data-governance-core/     # 数据治理核心
│   ├── platform-data-governance-metadata/ # 元数据管理
│   ├── platform-data-governance-quality/  # 数据质量
│   ├── platform-data-governance-standard/ # 数据标准
│   ├── platform-data-governance-lineage/  # 数据血缘
│   └── platform-data-governance-cleansing/# 数据清洗
├── platform-collect/                      # 数据采集模块
│   ├── platform-collect-api/              # 数据采集API
│   ├── platform-collect-core/             # 数据采集核心
│   ├── platform-collect-admin/            # 数据采集管理界面
│   ├── platform-collect-connectors/       # 数据源连接器
│   ├── platform-collect-processors/       # 数据处理器
│   └── platform-collect-loaders/          # 数据加载器
├── platform-scheduler/                    # 调度系统
│   ├── platform-scheduler-api/            # 调度系统API
│   ├── platform-scheduler-core/           # 调度核心框架
│   ├── platform-scheduler-register/       # 任务注册
│   ├── platform-scheduler-executor/       # 任务执行器
│   └── platform-scheduler-query/          # 调度查询服务
├── platform-storage-service/              # 存储服务
│   ├── platform-storage-api/              # 存储服务API
│   ├── platform-storage-core/             # 存储服务核心
│   ├── platform-storage-file/             # 文件存储
│   ├── platform-storage-object/           # 对象存储
│   └── platform-storage-hdfs/             # HDFS存储
├── platform-integration-hub/              # 集成中心
│   ├── platform-integration-api/          # 集成中心API
│   ├── platform-integration-core/         # 集成中心核心
│   ├── platform-integration-adapters/     # 第三方系统适配器
│   └── platform-integration-protocol/     # 协议转换
├── platform-data-visualization/           # 数据可视化
│   ├── platform-visualization-api/        # 可视化API
│   ├── platform-visualization-core/       # 可视化核心
│   └── platform-visualization-ui/         # 可视化前端
├── platform-report-engine/                # 报表引擎
│   ├── platform-report-api/               # 报表API
│   ├── platform-report-core/              # 报表核心
│   └── platform-report-templates/         # 报表模板
└── docs/                                  # 项目文档
    ├── architecture/                      # 架构文档
    ├── api/                               # API文档
    ├── development/                       # 开发文档
    └── deployment/                        # 部署文档
```

## 模块功能与职责说明

### 核心基础模块

- **platform-parent**: 顶级父项目，管理所有子模块，统一项目版本，定义全局属性和插件
- **platform-bom**: 定义平台所有组件的版本号，实现统一版本管理
- **platform-dependency**: 管理和控制所有第三方依赖，定义依赖版本和兼容性策略
- **platform-common**: 提供各类通用工具和基础组件，如工具类、常量、通用异常等
- **platform-framework**: 框架层组件，提供DDD实现支持、自动配置等功能

### 基础设施层

- **platform-registry**: 基于Nacos的服务注册与发现模块
- **platform-config**: 配置中心，基于GitLab实现配置管理
- **platform-gateway**: API网关，实现路由、认证、限流等功能
- **platform-auth-service**: 统一认证授权服务，实现用户认证和权限管理
- **platform-monitor-dashboard**: 监控仪表盘，提供系统监控和指标展示
- **platform-notification-service**: 通知服务，支持多渠道告警通知

### 数据治理模块

- **platform-data-governance**: 数据治理模块集合，包含元数据管理、数据质量、数据标准等子模块

### 数据采集模块

- **platform-collect**: 数据采集模块集合，负责从各种数据源收集数据

### 调度系统

- **platform-scheduler**: 调度系统模块集合，负责任务调度和执行

### 存储服务

- **platform-storage-service**: 存储服务模块集合，提供文件、对象、HDFS等多种存储管理

### 集成中心

- **platform-integration-hub**: 集成中心模块集合，负责与第三方系统集成

### 可视化和报表

- **platform-data-visualization**: 数据可视化模块，提供数据展示功能
- **platform-report-engine**: 报表引擎，支持报表生成和管理
```
