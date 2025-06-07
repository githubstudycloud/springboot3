# 陈述性记忆

## 高价值记忆（评分 ≥ 7）

- 2025/06/07 19:16 SpringBoot+Vue全栈项目架构设计经验：
1. 项目结构采用前后端分离架构，backend目录存放SpringBoot项目，frontend目录存放Vue项目
2. 使用Docker Compose管理MySQL、Redis等基础服务，支持开发和生产环境
3. 提供完整的脚本体系：start.sh启动脚本、deploy.sh部署脚本、build.ps1 Windows构建脚本
4. 数据库设计包含用户权限管理(RBAC)、系统配置、操作日志等基础模块
5. Python脚本用于数据分析，生成用户统计、文章统计、操作日志分析等报表
6. .gitignore文件覆盖Java、Node.js、Python、Docker等多技术栈
7. 技术版本选择：Spring Boot 3.2.x、Vue 3.x、JDK 21、Node.js 18+、MySQL 8.0、Redis 7.x
8. 提供开发工具支持：支持IDEA、VSCode等多种编辑器，包含Adminer数据库管理、Redis Commander等管理工具
9. 自动化部署：包含健康检查、备份、滚动更新等企业级部署特性 --tags springboot vue 全栈架构 项目模板 最佳实践 ##最佳实践 #工具使用 #评分:8 #有效期:长期



- 2025/06/07 19:33 企业级微服务架构设计经验：
1. 3级项目结构：platform-parent > 功能域(common/business) > 具体模块
2. 技术栈选择：Spring Boot 3.2.x + Nacos + Spring Cloud 2023.x + JDK 21
3. 数据采集处理架构：采集服务 -> 消息队列 -> 处理服务 -> 计算服务 -> 展示服务
4. 多环境部署：Docker Compose本地开发 + K8s生产部署
5. 组件可控开启：通过@ConditionalOnProperty注解实现组件按需启用
6. 监控体系：Prometheus + Grafana + ELK + SkyWalking + Spring Boot Admin
7. 配置管理：GitLab配置仓库 + Spring Cloud Config + Nacos配置中心
8. 服务部署策略：推荐一服务一容器，支持按业务域聚合部署
9. 通用启动器设计：统一启动逻辑、自定义Banner、组件自动配置
10. 数据流转：支持全量、增量、版本控制采集，单独和组合计算 --tags 微服务架构 数据平台 企业级 Spring Cloud Nacos K8s Docker ##其他 #评分:8 #有效期:长期