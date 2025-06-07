# SpringBoot + Vue 企业级DDD平台

## 🎯 项目概述

这是一个基于**DDD六边形架构**的企业级全栈平台，采用Spring Boot 3.x + Vue 3.x技术栈，支持数据采集、处理、分析的完整业务流程。项目采用领域驱动设计(DDD)和六边形架构模式，具备高度的可扩展性和可维护性。

## 📚 完整文档

> **📖 查看完整文档索引**: [DOCS.md](./DOCS.md) - 包含所有架构设计、开发指南和部署文档

### 核心架构文档
- [🎯 **DDD六边形架构设计**](./ARCHITECTURE-DDD-HEXAGON.md) - 领域驱动+六边形架构核心设计
- [🎨 **Vue前端架构**](./FRONTEND-VUE-ARCHITECTURE.md) - 前端完整架构设计  
- [🐍 **Python & Shell架构**](./PYTHON-SHELL-ARCHITECTURE.md) - 脚本管理架构
- [🗄️ **SQL数据库管理架构**](./SQL-MANAGEMENT-ARCHITECTURE.md) - 数据库版本管理
- [🔐 **配置加密架构**](./CONFIG-ENCRYPTION-ARCHITECTURE.md) - 安全配置管理
- [🐳 **环境配置架构**](./ENVIRONMENT-SETUP-ARCHITECTURE.md) - 多环境Docker部署配置

## 🏗️ 技术栈

### 后端技术
- **Spring Boot 3.2.x** - 主框架
- **Spring Security 6.x** - 安全框架
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 主数据库
- **Redis 7.x** - 缓存数据库
- **Maven 3.9.x** - 构建工具
- **JDK 21** - Java版本

### 前端技术
- **Vue 3.x** - 前端框架
- **Vue Router 4.x** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI组件库
- **Vite 5.x** - 构建工具
- **TypeScript** - 类型支持

### 辅助技术
- **Python 3.11+** - 数据处理和脚本
- **Shell Script** - 自动化脚本
- **Docker** - 容器化部署
- **Git** - 版本控制

## 📁 DDD六边形架构结构

```
springboot3Main011/
├── 📚 DOCS.md                              # 📖 完整文档索引
├── 📄 README.md                            # 🎯 项目概述
├── 🔧 pom.xml                              # Maven父工程配置
├── 🐳 docker-compose.yml                   # Docker服务编排
├── 🚫 .gitignore                           # Git忽略规则
├── 
├── 🎯 DDD六边形架构核心模块
│   ├── platform-domain/                   # 🧠 领域层
│   │   ├── data-collection/               # 数据采集领域
│   │   ├── data-processing/               # 数据处理领域
│   │   ├── scheduling/                    # 调度领域
│   │   └── audit/                         # 审计领域
│   │
│   ├── platform-application/              # 🚀 应用层
│   │   └── platform-external-api-service/ # 外部API管理服务
│   │
│   ├── platform-adapter/                  # 🔌 适配器层
│   │   ├── web/                           # Web适配器
│   │   ├── persistence/                   # 持久化适配器
│   │   ├── messaging/                     # 消息适配器
│   │   └── external/                      # 外部系统适配器
│   │
│   └── platform-starter-suite/            # ⚙️ 启动器套件
│       └── platform-starter-rocketmq/     # RocketMQ多厂商启动器
│
├── 💻 前端应用
│   └── frontend/                          # 🎨 Vue 3 + TypeScript前端
│       ├── package.json                   # npm依赖配置
│       ├── vite.config.ts                 # Vite构建配置
│       ├── src/
│       │   ├── views/                     # 页面组件
│       │   ├── components/                # 通用组件
│       │   ├── composables/               # 组合式函数
│       │   ├── stores/                    # Pinia状态管理
│       │   └── api/                       # API请求封装
│       └── Dockerfile                     # 容器化配置
│
├── 🐍 脚本与自动化
│   └── scripts/                           # 自动化脚本集合
│       ├── build.sh                       # 🔨 构建脚本
│       ├── deploy.sh                      # 🚀 部署脚本
│       ├── start.sh                       # ▶️ 启动脚本
│       └── python/                        # Python数据处理
│           ├── projects/                  # 复杂数据项目
│           ├── scripts/                   # 单功能脚本
│           └── shared/                    # 共享模块
│
├── 🗄️ 数据管理
│   └── database/                          # 数据库版本管理
│       ├── schema/                        # 结构定义
│       │   ├── init/                      # 初始化脚本
│       │   ├── migrations/                # 迁移脚本
│       │   └── procedures/                # 存储过程
│       ├── archive/                       # 历史归档
│       └── environments/                  # 环境配置
│
├── ☸️ 云原生部署
│   ├── k8s/                              # Kubernetes配置
│   │   ├── infrastructure/               # 基础设施
│   │   ├── services/                     # 服务部署
│   │   └── monitoring/                   # 监控配置
│   └── config/                           # 配置管理
│       └── nginx/                        # 反向代理配置
│
├── 📖 文档与规范
│   ├── docs/                             # 业务文档
│   ├── 🏗️ ARCHITECTURE-DDD-HEXAGON.md     # DDD六边形架构设计
│   ├── 🎨 FRONTEND-VUE-ARCHITECTURE.md    # Vue前端架构
│   ├── 🐍 PYTHON-SHELL-ARCHITECTURE.md   # Python&Shell架构
│   ├── 🗄️ SQL-MANAGEMENT-ARCHITECTURE.md  # SQL管理架构
│   ├── 🔐 CONFIG-ENCRYPTION-ARCHITECTURE.md # 配置加密架构
│   ├── 📚 PROJECT-ARCHIVE-ARCHITECTURE.md # 项目归档架构
│   └── 📋 coding-rules-config.yml         # 编码规范配置
│
└── 🗃️ 历史备份
    └── archive-backup/                    # 旧版本模块备份
        ├── platform-legacy-modules/      # 历史模块
        └── ARCHITECTURE-*.md              # 旧架构文档
```

### 🎯 架构特点

- **🧠 领域驱动**: 核心业务逻辑集中在领域层，保持业务纯粹性
- **🔌 六边形结构**: 适配器模式实现外部依赖解耦，便于测试和扩展
- **⚙️ 组件化**: 启动器套件提供可插拔的功能组件
- **🔐 安全第一**: 配置加密、权限控制、审计日志全覆盖
- **📊 数据驱动**: 完整的数据采集→处理→分析→展示流程
- **☸️ 云原生**: Docker + K8s + 监控体系，支持弹性伸缩

## 🚀 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+
- Docker & Docker Compose (可选)

### 开发环境启动

1. **启动数据库**
   ```bash
   # 使用Docker启动MySQL和Redis
   docker-compose up -d mysql redis
   ```

2. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

### 生产环境部署

```bash
# 一键部署
./scripts/deploy.sh
```

## 🛠️ 开发指南

### 后端开发
- 使用Spring Boot标准项目结构
- 遵循RESTful API设计规范
- 使用JPA进行数据库操作
- 集成Spring Security进行权限控制

### 前端开发
- 使用Vue 3 Composition API
- 采用TypeScript进行类型检查
- 使用Element Plus作为UI框架
- 遵循Vue官方代码风格指南

### 数据库设计
- 使用MySQL作为主数据库
- Redis用于缓存和会话存储
- 提供数据库迁移脚本

## 📚 文档

- [API文档](docs/api/README.md)
- [部署指南](docs/deployment/README.md)
- [开发规范](docs/development/README.md)
- [架构设计](docs/architecture/README.md)

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交变更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。 