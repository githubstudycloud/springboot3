# 📚 项目文档索引

## 🏗️ 架构设计文档

### 项目状态
- [**📊 当前状态总结**](./CURRENT-STATUS.md) - 第四阶段完成状态详细报告
- [**📋 项目改进计划**](./PROJECT-IMPROVEMENT-PLAN.md) - 详细的改进方案和下一步计划
- [**🚀 第五阶段执行计划**](./NEXT-PHASE-PLAN.md) - 详细的下阶段开发计划和里程碑

### 核心架构
- [**🎯 DDD六边形架构设计**](./ARCHITECTURE-DDD-HEXAGON.md) - 核心架构设计，领域驱动+六边形架构
- [**📋 编码规范配置**](./coding-rules-config.yml) - 自动化编码规范和质量检查

### 技术栈架构
- [**🎨 Vue前端架构**](./FRONTEND-VUE-ARCHITECTURE.md) - Vue 3 + TypeScript + Vite前端完整架构
- [**🐍 Python & Shell脚本架构**](./PYTHON-SHELL-ARCHITECTURE.md) - Python项目和Shell脚本管理架构
- [**🗄️ SQL数据库管理架构**](./SQL-MANAGEMENT-ARCHITECTURE.md) - 数据库版本管理和历史归档架构
- [**🔐 配置加密架构**](./CONFIG-ENCRYPTION-ARCHITECTURE.md) - 配置文件加密和安全管理架构
- [**🐳 环境配置架构**](./ENVIRONMENT-SETUP-ARCHITECTURE.md) - 多环境Docker部署和集群配置架构
- [**📚 项目历史归档架构**](./PROJECT-ARCHIVE-ARCHITECTURE.md) - 项目历史和AI协作记录归档架构

## 🚀 快速开始

### 环境要求
- **Java**: 21+
- **Node.js**: 18+
- **Python**: 3.9+
- **MySQL**: 8.0+
- **Redis**: 7.x+
- **Docker**: 20.10+

### 启动步骤
1. **环境准备**
   ```bash
   # 启动基础服务
   docker-compose up -d mysql redis
   ```

2. **后端启动**
   ```bash
   # 编译项目
   mvn clean install
   
   # 启动外部API服务
   cd platform-application/platform-external-api-service
   mvn spring-boot:run
   ```

3. **前端启动**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 📁 项目结构

### DDD六边形架构模块
```
├── platform-domain/              # 🎯 领域层
│   ├── data-collection/          # 数据采集领域
│   ├── data-processing/          # 数据处理领域  
│   ├── scheduling/               # 调度领域
│   └── audit/                    # 审计领域
├── platform-application/         # 🚀 应用层
│   └── platform-external-api-service/  # 外部API管理服务
├── platform-adapter/             # 🔌 适配器层
│   ├── web/                      # Web适配器
│   ├── persistence/              # 持久化适配器
│   ├── messaging/                # 消息适配器
│   └── external/                 # 外部系统适配器
└── platform-starter-suite/       # ⚙️ 启动器套件
    └── platform-starter-rocketmq/ # RocketMQ多厂商启动器
```

### 支撑体系
```
├── frontend/                     # 🎨 Vue3前端应用
├── scripts/                      # 🐍 Python & Shell脚本
├── database/                     # 🗄️ 数据库管理
├── k8s/                         # ☸️ Kubernetes部署
├── config/                       # ⚙️ 配置文件
├── docs/                        # 📖 文档目录
└── archive-backup/              # 🗃️ 历史版本备份
```

## 🔧 开发指南

### 代码规范
- 遵循 [编码规范配置](./coding-rules-config.yml) 中定义的规则
- 使用Maven Checkstyle插件进行代码检查
- 提交前运行 `mvn verify` 确保质量

### 分支策略
- `main`: 生产发布分支
- `develop`: 开发集成分支
- `feature/*`: 功能开发分支
- `hotfix/*`: 紧急修复分支

### 提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型说明：
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

## 🔄 部署流程

### 开发环境
```bash
# 启动开发环境
docker-compose -f docker-compose.dev.yml up -d
```

### 测试环境
```bash
# 构建测试镜像
mvn clean package -Ptest
docker build -t platform:test .

# 部署到测试环境
kubectl apply -f k8s/testing/
```

### 生产环境
```bash
# 构建生产镜像
mvn clean package -Pprod
docker build -t platform:prod .

# 部署到生产环境
kubectl apply -f k8s/production/
```

## 📊 监控与运维

### 健康检查
- **应用健康**: `/actuator/health`
- **系统指标**: `/actuator/metrics`
- **配置信息**: `/actuator/configprops`

### 日志查看
```bash
# 查看应用日志
kubectl logs -f deployment/platform-api

# 查看系统日志
kubectl logs -f deployment/platform-gateway
```

### 性能监控
- **Prometheus**: 指标收集
- **Grafana**: 监控面板
- **Jaeger**: 分布式追踪
- **ELK**: 日志分析

## 🧪 测试策略

### 单元测试
```bash
# 运行单元测试
mvn test
```

### 集成测试
```bash
# 运行集成测试
mvn verify -Pintegration-test
```

### 端到端测试
```bash
# 前端E2E测试
cd frontend
npm run test:e2e
```

## 🔗 相关链接

### 官方文档
- [Spring Boot 3.2.x](https://spring.io/projects/spring-boot)
- [Vue 3](https://vuejs.org/)
- [Docker](https://docs.docker.com/)
- [Kubernetes](https://kubernetes.io/docs/)

### 开发工具
- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [VS Code](https://code.visualstudio.com/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)

## 📞 技术支持

### 问题反馈
- 🐛 **Bug报告**: [GitHub Issues](https://github.com/your-org/platform/issues)
- 💡 **功能建议**: [GitHub Discussions](https://github.com/your-org/platform/discussions)
- 📧 **技术支持**: tech-support@yourcompany.com

### 开发团队
- **架构师**: 负责整体架构设计
- **后端开发**: Java/Spring Boot开发
- **前端开发**: Vue.js/TypeScript开发
- **运维工程师**: Docker/K8s部署运维
- **AI助手**: Claude - 架构设计和代码评审

---

## 📈 项目演进历程

### v1.0.0 - 基础架构 (2024-01)
- ✅ SpringBoot + Vue基础框架
- ✅ Docker容器化部署
- ✅ MySQL + Redis数据存储

### v1.1.0 - 微服务化 (2024-01)
- ✅ 三级Maven模块结构
- ✅ 数据采集处理系统
- ✅ Nacos服务注册发现

### v1.2.0 - DDD六边形架构 (2024-01)
- ✅ 领域驱动设计实践
- ✅ 六边形架构模式
- ✅ RocketMQ多厂商支持
- ✅ 外部API统一管理
- ✅ 配置加密安全存储

### v2.0.0 - 企业级平台 (计划中)
- 🔄 微服务治理完善
- 🔄 大数据分析能力
- 🔄 AI/ML集成支持
- 🔄 多租户架构

---

*最后更新: 2024-01-17*
*维护者: 开发团队 & Claude AI* 