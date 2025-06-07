# 🐳 环境配置架构设计

## 📋 概述

本文档定义了项目的多环境配置策略，包括开发、测试、Beta、生产环境的Docker化部署方案。采用渐进式架构，从单机部署逐步演进到集群部署。

## 🏗️ 环境架构层次

### 环境分类
- **🛠️ Development**: 开发环境（本地开发）
- **🧪 Testing**: 测试环境（Docker单机）
- **🚀 Beta**: 预发布环境（Docker集群）
- **🏭 Production**: 生产环境（K8s集群）

## 🧪 测试环境 (Testing) - Docker单机

### 架构特点
- 所有组件单机部署
- 资源需求较低
- 快速启动和重建
- 适合功能测试和集成测试

### Docker Compose配置

```yaml
# docker-compose.testing.yml
version: '3.8'

services:
  # 核心数据库
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: testing_root_pass
      MYSQL_DATABASE: platform_testing
    ports:
      - "3306:3306"
    volumes:
      - mysql_testing_data:/var/lib/mysql
      - ./database/schema/init:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=mysql_native_password

  # 缓存数据库
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --requirepass testing_redis_pass

  # 文档数据库
  mongodb:
    image: mongo:7
    environment:
      MONGO_INITDB_ROOT_USERNAME: mongo_admin
      MONGO_INITDB_ROOT_PASSWORD: testing_mongo_pass
    ports:
      - "27017:27017"
    volumes:
      - mongodb_testing_data:/data/db

  # 服务注册中心
  nacos:
    image: nacos/nacos-server:v2.3.0
    environment:
      MODE: standalone
      NACOS_AUTH_ENABLE: true
      NACOS_AUTH_TOKEN: testing_nacos_token
    ports:
      - "8848:8848"
      - "9848:9848"
    volumes:
      - nacos_testing_logs:/home/nacos/logs

volumes:
  mysql_testing_data:
  mongodb_testing_data:
  nacos_testing_logs:
```

### 单独Docker安装脚本

```bash
#!/bin/bash
# scripts/setup-testing-env.sh

set -e

echo "🧪 Setting up Testing Environment..."

# 创建网络
docker network create platform-testing || echo "Network already exists"

# 启动MySQL
docker run -d \
  --name mysql-testing \
  --network platform-testing \
  -e MYSQL_ROOT_PASSWORD=testing_root_pass \
  -e MYSQL_DATABASE=platform_testing \
  -p 3306:3306 \
  -v mysql_testing_data:/var/lib/mysql \
  mysql:8.0

echo "✅ Testing Environment Setup Complete!"
```

## 🚀 Beta环境 - Docker集群

### 架构特点
- 组件尽量集群部署
- 高可用性配置
- 负载均衡
- 适合压力测试和预发布验证

### Docker Swarm配置

```yaml
# docker-compose.beta.yml
version: '3.8'

services:
  # MySQL集群 (主从复制)
  mysql-master:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: beta_root_pass
      MYSQL_REPLICATION_USER: repl_user
      MYSQL_REPLICATION_PASSWORD: repl_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_master_data:/var/lib/mysql
    deploy:
      replicas: 1

  # Redis集群
  redis-master:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --requirepass beta_redis_pass
    deploy:
      replicas: 1

  # 应用服务集群
  platform-api:
    build:
      context: .
      dockerfile: platform-application/platform-external-api-service/Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: beta
    deploy:
      replicas: 3

volumes:
  mysql_master_data:
```

## 📊 环境对比

| 功能特性 | Testing | Beta | Production |
|---------|---------|------|------------|
| 部署方式 | Docker Compose | Docker Swarm | Kubernetes |
| 服务副本 | 单副本 | 多副本 | 弹性伸缩 |
| 监控体系 | 基础监控 | 集群监控 | 全链路监控 |
| 资源需求 | 低 | 中等 | 高 |

## 🛠️ 快速启动命令

```bash
# 测试环境快速启动
docker-compose -f docker-compose.testing.yml up -d

# Beta环境快速启动
./scripts/setup-beta-cluster.sh

# 生产环境部署
kubectl apply -f k8s/production/
```

---

*最后更新: 2024-01-17*
*维护者: DevOps团队 & Claude AI* 