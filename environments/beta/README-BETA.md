# 🚀 Beta环境集群部署指南

## 概述

Beta环境采用Docker Swarm集群部署方式，实现MySQL主从集群、Redis主从架构、Nginx负载均衡等高可用配置，适合压力测试和预发布验证。

## 系统要求

- **最少节点数**: 3个节点（1个Manager + 2个Worker）
- **内存**: 每节点最少 16GB，推荐 32GB
- **存储**: 每节点最少 100GB SSD
- **CPU**: 每节点最少 8核，推荐 16核
- **网络**: 开放端口 2377, 7946, 4789

## 快速部署

### 方式一：Docker Swarm集群部署（推荐）

```bash
# 1. 初始化Swarm集群
cd environments/beta
chmod +x scripts/setup-docker-cluster.sh
./scripts/setup-docker-cluster.sh

# 2. 部署服务栈
docker stack deploy -c docker-compose.yml platform-beta
```

### 方式二：单机模拟集群

```bash
# 单机环境下运行集群配置
docker-compose up -d
```

## 集群架构

### MySQL主从集群
- **Master**: 1个实例，负责写操作
- **Slave**: 2个实例，负责读操作
- **自动故障转移**: 支持主从切换

### Redis高可用集群  
- **Master**: 1个实例
- **Slave**: 2个实例
- **Sentinel**: 3个实例监控

### Nginx负载均衡
- **实例数**: 2个
- **负载算法**: 最少连接数
- **健康检查**: 自动故障切除

## 服务访问

| 服务 | 端口 | 用户名 | 密码 |
|------|------|--------|------|
| MySQL Master | 3306 | platform_user | beta_user_pass_2024 |
| MySQL Slave | 3307 | platform_user | beta_user_pass_2024 |
| Redis Master | 6379 | - | beta_redis_pass_2024 |
| Platform API | 8080 | - | - |
| Nginx LB | 80 | - | - |

## 集群管理

```bash
# 查看集群状态
docker node ls
docker service ls

# 扩展服务
docker service scale platform-beta_platform-api=5

# 查看日志
docker service logs -f platform-beta_mysql-master

# 清理环境
docker stack rm platform-beta
```

## 故障排除

### MySQL集群问题
```bash
# 检查主从状态
docker exec mysql-master mysql -u root -p -e "SHOW MASTER STATUS"
docker exec mysql-slave mysql -u root -p -e "SHOW SLAVE STATUS\G"
```

### Redis集群问题
```bash
# 检查Redis连接
docker exec redis-master redis-cli -a beta_redis_pass_2024 ping
```

### 网络问题
```bash
# 检查Swarm网络
docker network inspect platform-beta
```

## 相关文档

- [测试环境部署](../testing/README.md)
- [项目架构说明](../../ARCHITECTURE-DDD-HEXAGON.md)
- [环境配置对比](../../ENVIRONMENT-SETUP-ARCHITECTURE.md) 