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

### 🧪 Testing环境（单机部署）

**方式一：Docker Compose（推荐）**
```bash
# 进入测试环境目录
cd environments/testing

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

**方式二：单独Docker安装**
```bash
# 进入脚本目录
cd environments/testing/scripts

# 运行安装脚本
chmod +x setup-docker-individual.sh
./setup-docker-individual.sh

# 清理环境
./setup-docker-individual.sh --cleanup
```

**测试环境服务访问**：
- **MySQL**: `localhost:3306` (用户: platform_user, 密码: testing_user_pass_2024)
- **Redis**: `localhost:6379` (密码: testing_redis_pass_2024) 
- **MongoDB**: `localhost:27017` (用户: mongo_admin, 密码: testing_mongo_pass_2024)
- **Nacos**: `http://localhost:8848/nacos` (用户: nacos, 密码: nacos)
- **RabbitMQ**: `http://localhost:15672` (用户: rabbit_admin, 密码: testing_rabbit_pass_2024)
- **Kafka**: `localhost:9092`
- **RocketMQ**: `localhost:9876` (NameServer)
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000` (用户: admin, 密码: testing_grafana_pass_2024)

### 🚀 Beta环境（集群部署）

**方式一：Docker Swarm集群（推荐）**
```bash
# 进入Beta环境目录
cd environments/beta

# 初始化Swarm集群并部署
chmod +x scripts/setup-docker-cluster.sh
./scripts/setup-docker-cluster.sh

# 部署服务栈
docker stack deploy -c docker-compose.yml platform-beta

# 查看集群状态
docker node ls
docker service ls
```

**方式二：单机模拟集群**
```bash
# 单机环境下运行集群配置
cd environments/beta
docker-compose up -d
```

**Beta环境服务访问**：
- **MySQL Master**: `localhost:3306` (用户: platform_user, 密码: beta_user_pass_2024)
- **MySQL Slave**: `localhost:3307` (只读复制)
- **Redis Master**: `redis-master:6379` (密码: beta_redis_pass_2024)
- **Platform API**: `http://localhost:8080` (集群模式，3个实例)
- **Nginx LB**: `http://localhost` (负载均衡器)

**集群管理命令**：
```bash
# 扩展API服务
docker service scale platform-beta_platform-api=5

# 查看服务日志
docker service logs -f platform-beta_mysql-master

# 清理集群
docker stack rm platform-beta
```

### 🏭 生产环境部署（预留）
```bash
# Kubernetes生产环境部署
kubectl apply -f k8s/production/
```

---

*最后更新: 2024-01-17*
*维护者: DevOps团队 & Claude AI* 