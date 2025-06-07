# 🧪 测试环境部署指南

## 概述

测试环境采用单机Docker部署方式，所有组件以单实例运行，资源消耗较低，适合功能测试和集成测试。

## 系统要求

- **操作系统**: Ubuntu 20.04+ / CentOS 7+ / macOS 10.15+ / Windows 10+
- **Docker版本**: 20.10+
- **内存**: 最少 8GB，推荐 16GB
- **存储**: 最少 50GB 可用空间
- **CPU**: 最少 4核，推荐 8核

## 快速开始

### 方式一：Docker Compose 部署（推荐）

```bash
# 1. 进入测试环境目录
cd environments/testing

# 2. 启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f

# 5. 停止服务
docker-compose down
```

### 方式二：单独Docker安装

```bash
# 1. 进入脚本目录
cd environments/testing/scripts

# 2. 给脚本执行权限
chmod +x setup-docker-individual.sh

# 3. 运行安装脚本
./setup-docker-individual.sh

# 4. 清理环境（可选）
./setup-docker-individual.sh --cleanup
```

## 服务组件

### 核心数据存储

| 服务 | 端口 | 用户名 | 密码 | 数据库 |
|------|------|--------|------|--------|
| MySQL | 3306 | platform_user | testing_user_pass_2024 | platform_testing |
| Redis | 6379 | - | testing_redis_pass_2024 | - |
| MongoDB | 27017 | mongo_admin | testing_mongo_pass_2024 | platform_testing |

### 服务治理与消息队列

| 服务 | 端口 | 管理界面 | 用户名 | 密码 |
|------|------|----------|--------|------|
| Nacos | 8848 | http://localhost:8848/nacos | nacos | nacos |
| RabbitMQ | 5672/15672 | http://localhost:15672 | rabbit_admin | testing_rabbit_pass_2024 |
| Kafka | 9092 | - | - | - |
| RocketMQ | 9876 | - | - | - |

### 监控与可视化

| 服务 | 端口 | 管理界面 | 用户名 | 密码 |
|------|------|----------|--------|------|
| Prometheus | 9090 | http://localhost:9090 | - | - |
| Grafana | 3000 | http://localhost:3000 | admin | testing_grafana_pass_2024 |

### 应用服务

| 服务 | 端口 | 访问地址 | 描述 |
|------|------|----------|------|
| Platform API | 8080 | http://localhost:8080 | 后端API服务 |
| Platform Web | 8081 | http://localhost:8081 | 前端Web服务 |
| Nginx | 80/443 | http://localhost | 反向代理和负载均衡 |

## 连接配置

### 数据库连接

**MySQL 连接示例**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/platform_testing?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: platform_user
    password: testing_user_pass_2024
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**Redis 连接示例**:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: testing_redis_pass_2024
    database: 0
```

**MongoDB 连接示例**:
```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: platform_testing
      username: mongo_admin
      password: testing_mongo_pass_2024
      authentication-database: admin
```

### 消息队列连接

**RabbitMQ 连接示例**:
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: rabbit_admin
    password: testing_rabbit_pass_2024
    virtual-host: platform_testing
```

**Kafka 连接示例**:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: platform-testing
    producer:
      retries: 3
```

**RocketMQ 连接示例**:
```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: platform-testing-producer
  consumer:
    group: platform-testing-consumer
```

## 常用命令

### Docker Compose 命令

```bash
# 启动服务
docker-compose up -d

# 查看运行状态
docker-compose ps

# 查看实时日志
docker-compose logs -f [service_name]

# 重启特定服务
docker-compose restart [service_name]

# 停止服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器、网络、数据卷
docker-compose down -v
```

### Docker 命令

```bash
# 查看所有容器
docker ps -a

# 查看容器日志
docker logs -f [container_name]

# 进入容器
docker exec -it [container_name] /bin/bash

# 查看网络
docker network ls

# 查看数据卷
docker volume ls

# 清理未使用的资源
docker system prune -a
```

## 数据备份与恢复

### MySQL 备份与恢复

```bash
# 备份数据库
docker exec mysql-testing mysqldump -u platform_user -ptesting_user_pass_2024 platform_testing > backup.sql

# 恢复数据库
docker exec -i mysql-testing mysql -u platform_user -ptesting_user_pass_2024 platform_testing < backup.sql
```

### MongoDB 备份与恢复

```bash
# 备份数据库
docker exec mongodb-testing mongodump --host localhost --port 27017 --username mongo_admin --password testing_mongo_pass_2024 --authenticationDatabase admin --db platform_testing --out /backup

# 恢复数据库
docker exec mongodb-testing mongorestore --host localhost --port 27017 --username mongo_admin --password testing_mongo_pass_2024 --authenticationDatabase admin --db platform_testing /backup/platform_testing
```

### Redis 备份与恢复

```bash
# 手动触发备份
docker exec redis-testing redis-cli -a testing_redis_pass_2024 BGSAVE

# 查看备份文件
docker exec redis-testing ls -la /data/dump.rdb
```

## 故障排除

### 常见问题

**1. 端口冲突**
```bash
# 查看端口占用
netstat -tlnp | grep [port]
# 或者
lsof -i :[port]

# 修改 docker-compose.yml 中的端口映射
```

**2. 内存不足**
```bash
# 查看系统内存使用
free -h

# 查看Docker容器内存使用
docker stats

# 调整 JVM 内存参数
```

**3. 磁盘空间不足**
```bash
# 查看磁盘使用情况
df -h

# 清理Docker未使用的资源
docker system prune -a

# 清理数据卷
docker volume prune
```

**4. 服务启动失败**
```bash
# 查看详细日志
docker-compose logs [service_name]

# 检查配置文件
docker-compose config

# 重新拉取镜像
docker-compose pull
```

### 健康检查

**检查所有服务状态**:
```bash
#!/bin/bash
echo "=== 服务健康检查 ==="

# MySQL
if docker exec mysql-testing mysqladmin ping -h localhost --silent; then
    echo "✅ MySQL: 正常"
else
    echo "❌ MySQL: 异常"
fi

# Redis
if docker exec redis-testing redis-cli -a testing_redis_pass_2024 ping | grep -q PONG; then
    echo "✅ Redis: 正常"
else
    echo "❌ Redis: 异常"
fi

# MongoDB
if docker exec mongodb-testing mongosh --host localhost --eval "db.runCommand('ping')" --quiet; then
    echo "✅ MongoDB: 正常"
else
    echo "❌ MongoDB: 异常"
fi

# Nacos
if curl -f http://localhost:8848/nacos/v1/console/health/readiness 2>/dev/null; then
    echo "✅ Nacos: 正常"
else
    echo "❌ Nacos: 异常"
fi

# RabbitMQ
if docker exec rabbitmq-testing rabbitmq-diagnostics -q ping; then
    echo "✅ RabbitMQ: 正常"
else
    echo "❌ RabbitMQ: 异常"
fi
```

## 性能调优

### 内存优化

**Java 服务内存设置**:
```yaml
# docker-compose.yml 中的环境变量
environment:
  JAVA_OPTS: "-Xms512m -Xmx1024m -XX:+UseG1GC"
```

**数据库内存设置**:
```yaml
# MySQL
command: 
  - --innodb-buffer-pool-size=512M
  - --query-cache-size=128M

# MongoDB  
command: mongod --wiredTigerCacheSizeGB 0.5
```

### 磁盘优化

**使用SSD存储数据卷**:
```yaml
volumes:
  mysql_testing_data:
    driver: local
    driver_opts:
      type: none
      device: /ssd/mysql_data
      o: bind
```

## 安全配置

### 密码管理

所有默认密码都在配置文件中，生产环境部署时请务必修改：

- MySQL root密码: `testing_root_pass_2024`
- MySQL用户密码: `testing_user_pass_2024`
- Redis密码: `testing_redis_pass_2024`
- MongoDB密码: `testing_mongo_pass_2024`
- RabbitMQ密码: `testing_rabbit_pass_2024`
- Grafana密码: `testing_grafana_pass_2024`

### 网络安全

```bash
# 创建独立网络
docker network create --driver bridge platform-testing-secure

# 限制容器间通信
docker network create --driver bridge --internal platform-testing-internal
```

## 扩展配置

### 添加新服务

1. 在 `docker-compose.yml` 中添加服务定义
2. 配置网络和数据卷
3. 添加环境变量和配置文件
4. 更新文档

### 自定义配置

配置文件位置：
- MySQL: `config/mysql/`
- Redis: `config/redis/`
- Nginx: `config/nginx/`
- Prometheus: `config/prometheus/`
- Grafana: `config/grafana/`

## 相关链接

- [Docker官方文档](https://docs.docker.com/)
- [Docker Compose文档](https://docs.docker.com/compose/)
- [项目架构文档](../../ARCHITECTURE-DDD-HEXAGON.md)
- [部署环境对比](../../ENVIRONMENT-SETUP-ARCHITECTURE.md) 