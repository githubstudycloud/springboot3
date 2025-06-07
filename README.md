# SpringBoot + Vue 全栈项目

## 🎯 项目概述

这是一个基于Spring Boot 3.x + Vue 3.x的现代化全栈Web应用项目，支持多种技术栈集成开发。

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

## 📁 项目结构

```
springboot3Main011/
├── README.md                          # 项目说明文档
├── .gitignore                         # Git忽略文件
├── docker-compose.yml                 # Docker编排文件
├── 
├── backend/                           # 后端Spring Boot项目
│   ├── pom.xml                        # Maven配置
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/project/
│   │   │   │       ├── ProjectApplication.java    # 启动类
│   │   │   │       ├── config/                    # 配置类
│   │   │   │       ├── controller/                # 控制器
│   │   │   │       ├── service/                   # 服务层
│   │   │   │       ├── entity/                    # 实体类
│   │   │   │       ├── repository/                # 数据访问层
│   │   │   │       ├── dto/                       # 数据传输对象
│   │   │   │       ├── exception/                 # 异常处理
│   │   │   │       └── utils/                     # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml                # 主配置文件
│   │   │       ├── application-dev.yml            # 开发环境配置
│   │   │       ├── application-prod.yml           # 生产环境配置
│   │   │       ├── static/                        # 静态资源
│   │   │       └── templates/                     # 模板文件
│   │   └── test/                                  # 测试代码
│   └── Dockerfile                                 # 后端Docker文件
│
├── frontend/                          # 前端Vue项目
│   ├── package.json                   # npm配置
│   ├── vite.config.ts                 # Vite配置
│   ├── tsconfig.json                  # TypeScript配置
│   ├── index.html                     # 入口HTML
│   ├── public/                        # 公共资源
│   ├── src/
│   │   ├── main.ts                    # 入口文件
│   │   ├── App.vue                    # 根组件
│   │   ├── components/                # 通用组件
│   │   ├── views/                     # 页面组件
│   │   ├── router/                    # 路由配置
│   │   ├── store/                     # 状态管理
│   │   ├── api/                       # API接口
│   │   ├── utils/                     # 工具函数
│   │   ├── types/                     # TypeScript类型
│   │   ├── styles/                    # 样式文件
│   │   └── assets/                    # 静态资源
│   └── Dockerfile                     # 前端Docker文件
│
├── scripts/                           # 自动化脚本
│   ├── build.sh                       # 构建脚本
│   ├── deploy.sh                      # 部署脚本
│   ├── start.sh                       # 启动脚本
│   └── python/                        # Python脚本
│       ├── data-migration.py          # 数据迁移
│       ├── data-analysis.py           # 数据分析
│       └── requirements.txt           # Python依赖
│
├── database/                          # 数据库相关
│   ├── schema/                        # 数据库结构
│   │   ├── init.sql                   # 初始化脚本
│   │   ├── tables.sql                 # 表结构
│   │   └── data.sql                   # 初始数据
│   ├── migrations/                    # 数据库迁移
│   └── backup/                        # 备份脚本
│
├── docs/                             # 项目文档
│   ├── api/                          # API文档
│   ├── deployment/                   # 部署文档
│   ├── development/                  # 开发文档
│   └── architecture/                 # 架构设计文档
│
├── config/                           # 配置文件
│   ├── nginx/                        # Nginx配置
│   ├── docker/                       # Docker相关配置
│   └── k8s/                          # Kubernetes配置
│
└── tests/                            # 集成测试
    ├── api-tests/                    # API测试
    ├── e2e-tests/                    # 端到端测试
    └── performance-tests/            # 性能测试
```

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