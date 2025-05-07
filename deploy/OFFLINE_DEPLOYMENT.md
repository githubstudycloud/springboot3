# 企业级数据平台 - 离线部署指南

本文档提供企业级数据平台在离线环境中的部署准备工作，包括所需Docker镜像的获取、传输和加载方法，以及离线环境中的部署注意事项。

## 1. 所需Docker镜像清单

### 核心服务镜像

| 服务类别 | 镜像名称 | 版本 | 大小(估计) |
|---------|---------|------|-----------|
| **数据库** | | | |
| 关系型数据库 | mysql | 5.7 / latest | 450MB |
| 关系型数据库 | postgres | latest | 380MB |
| 文档数据库 | mongo | latest | 650MB |
| 列式数据库 | clickhouse/clickhouse-server | latest | 550MB |
| **缓存服务** | | | |
| 缓存 | redis | latest | 120MB |
| **消息队列** | | | |
| 消息队列 | rabbitmq | management | 250MB |
| 消息队列 | bitnami/kafka | latest | 500MB |
| 分布式协调 | zookeeper | latest | 280MB |
| **服务发现与配置** | | | |
| 服务发现 | nacos/nacos-server | latest | 980MB |
| 服务网格 | consul | latest | 130MB |
| **反向代理** | | | |
| 反向代理/负载均衡 | nginx | latest | 150MB |
| 反向代理/API网关 | traefik | latest | 120MB |

### 监控与日志镜像

| 服务类别 | 镜像名称 | 版本 | 大小(估计) |
|---------|---------|------|-----------|
| **搜索与日志** | | | |
| 搜索引擎 | elasticsearch | latest | 850MB |
| 日志收集 | logstash | latest | 800MB |
| 日志可视化 | kibana | latest | 750MB |
| 日志收集代理 | fluent/fluentd | latest | 120MB |
| 日志收集代理 | docker.elastic.co/beats/filebeat | latest | 300MB |
| **监控系统** | | | |
| 监控系统 | prom/prometheus | latest | 180MB |
| 监控可视化 | grafana/grafana | latest | 300MB |
| 告警管理 | prom/alertmanager | latest | 80MB |
| 节点监控 | prom/node-exporter | latest | 25MB |
| 容器监控 | gcr.io/cadvisor/cadvisor | latest | 180MB |
| **分布式追踪** | | | |
| 分布式追踪 | jaegertracing/all-in-one | latest | 220MB |
| 分布式追踪 | openzipkin/zipkin | latest | 180MB |

### 应用服务镜像

| 服务类别 | 镜像名称 | 版本 | 大小(估计) |
|---------|---------|------|-----------|
| **基础镜像** | | | |
| Java运行时 | eclipse-temurin | 21-jdk-alpine | 350MB |
| 前端构建 | node | 18-alpine | 180MB |
| **平台服务** | | | |
| API网关 | ${REGISTRY_URL}/platform/gateway | latest | 250MB* |
| 认证服务 | ${REGISTRY_URL}/platform/auth-service | latest | 250MB* |
| 采集服务 | ${REGISTRY_URL}/platform/collect-service | latest | 300MB* |
| 治理服务 | ${REGISTRY_URL}/platform/governance-service | latest | 300MB* |
| 调度服务 | ${REGISTRY_URL}/platform/scheduler-service | latest | 250MB* |
| 前端服务 | ${REGISTRY_URL}/platform/frontend | latest | 150MB* |

*注：应用服务镜像大小为估计值，实际大小取决于应用代码和依赖项。

## 2. 镜像准备步骤

### 2.1 在有网络环境中拉取镜像

在有网络连接的环境中，使用以下命令拉取所需的镜像：

```bash
#!/bin/bash
# 创建镜像拉取脚本: pull-images.sh

# 核心服务镜像
# 数据库
docker pull mysql:latest
docker pull postgres:latest
docker pull mongo:latest
docker pull clickhouse/clickhouse-server:latest

# 缓存
docker pull redis:latest

# 消息队列
docker pull rabbitmq:management
docker pull bitnami/kafka:latest
docker pull zookeeper:latest

# 服务发现与配置
docker pull nacos/nacos-server:latest
docker pull consul:latest

# 反向代理
docker pull nginx:latest
docker pull traefik:latest

# 监控与日志镜像
# 搜索与日志
docker pull elasticsearch:latest
docker pull logstash:latest
docker pull kibana:latest
docker pull fluent/fluentd:latest
docker pull docker.elastic.co/beats/filebeat:latest

# 监控系统
docker pull prom/prometheus:latest
docker pull grafana/grafana:latest
docker pull prom/alertmanager:latest
docker pull prom/node-exporter:latest
docker pull gcr.io/cadvisor/cadvisor:latest

# 分布式追踪
docker pull jaegertracing/all-in-one:latest
docker pull openzipkin/zipkin:latest

# 基础镜像
docker pull eclipse-temurin:21-jdk-alpine
docker pull node:18-alpine

# 应用服务镜像 (示例，实际情况需要替换${REGISTRY_URL}为实际的仓库地址)
# docker pull ${REGISTRY_URL}/platform/gateway:latest
# docker pull ${REGISTRY_URL}/platform/auth-service:latest
# docker pull ${REGISTRY_URL}/platform/collect-service:latest
# docker pull ${REGISTRY_URL}/platform/governance-service:latest
# docker pull ${REGISTRY_URL}/platform/scheduler-service:latest
# docker pull ${REGISTRY_URL}/platform/frontend:latest

echo "所有镜像已拉取完成"
```

赋予脚本执行权限并运行：

```bash
chmod +x pull-images.sh
./pull-images.sh
```

### 2.2 将镜像保存为压缩文件

创建保存镜像的脚本：

```bash
#!/bin/bash
# 创建镜像保存脚本: save-images.sh

# 创建输出目录
mkdir -p platform-images
cd platform-images

# 分组保存镜像以便于管理
# 数据库镜像
echo "正在保存数据库镜像..."
docker save mysql:latest postgres:latest mongo:latest clickhouse/clickhouse-server:latest | gzip > database-images.tar.gz

# 缓存和消息队列镜像
echo "正在保存缓存和消息队列镜像..."
docker save redis:latest rabbitmq:management bitnami/kafka:latest zookeeper:latest | gzip > cache-mq-images.tar.gz

# 服务发现与配置镜像
echo "正在保存服务发现镜像..."
docker save nacos/nacos-server:latest consul:latest | gzip > discovery-images.tar.gz

# 反向代理镜像
echo "正在保存反向代理镜像..."
docker save nginx:latest traefik:latest | gzip > proxy-images.tar.gz

# 日志镜像
echo "正在保存日志系统镜像..."
docker save elasticsearch:latest logstash:latest kibana:latest fluent/fluentd:latest docker.elastic.co/beats/filebeat:latest | gzip > logging-images.tar.gz

# 监控镜像
echo "正在保存监控系统镜像..."
docker save prom/prometheus:latest grafana/grafana:latest prom/alertmanager:latest prom/node-exporter:latest gcr.io/cadvisor/cadvisor:latest | gzip > monitoring-images.tar.gz

# 分布式追踪镜像
echo "正在保存分布式追踪镜像..."
docker save jaegertracing/all-in-one:latest openzipkin/zipkin:latest | gzip > tracing-images.tar.gz

# 基础镜像
echo "正在保存基础镜像..."
docker save eclipse-temurin:21-jdk-alpine node:18-alpine | gzip > base-images.tar.gz

# 创建镜像清单
echo "创建镜像清单..."
docker images | grep -v "REPOSITORY" > image-list.txt

echo "所有镜像已保存完成"
cd ..
```

赋予脚本执行权限并运行：

```bash
chmod +x save-images.sh
./save-images.sh
```

### 2.3 传输镜像到离线环境

将保存的压缩文件传输到离线环境中，可以使用物理媒体（如硬盘、U盘）或内网传输工具：

```bash
# 压缩整个目录便于传输
tar -czvf platform-images.tar.gz platform-images/

# 传输到离线环境
# 选项1: 使用物理媒体
# 选项2: 使用内网传输工具，如scp
scp platform-images.tar.gz user@offline-server:/path/to/destination/
```

### 2.4 在离线环境中加载镜像

在离线环境中创建加载镜像的脚本：

```bash
#!/bin/bash
# 创建镜像加载脚本: load-images.sh

# 解压缩传输的文件
tar -xzvf platform-images.tar.gz
cd platform-images

# 加载所有镜像
echo "正在加载数据库镜像..."
docker load < database-images.tar.gz

echo "正在加载缓存和消息队列镜像..."
docker load < cache-mq-images.tar.gz

echo "正在加载服务发现镜像..."
docker load < discovery-images.tar.gz

echo "正在加载反向代理镜像..."
docker load < proxy-images.tar.gz

echo "正在加载日志系统镜像..."
docker load < logging-images.tar.gz

echo "正在加载监控系统镜像..."
docker load < monitoring-images.tar.gz

echo "正在加载分布式追踪镜像..."
docker load < tracing-images.tar.gz

echo "正在加载基础镜像..."
docker load < base-images.tar.gz

echo "所有镜像已加载完成"
docker images
```

赋予脚本执行权限并运行：

```bash
chmod +x load-images.sh
./load-images.sh
```

## 3. 离线环境配置

### 3.1 设置本地Docker Registry（可选）

在离线环境中，可以设置本地Docker Registry以便在多个节点间共享镜像：

```bash
# 创建本地Registry数据存储目录
mkdir -p /data/registry

# 启动本地Registry
docker run -d -p 5000:5000 --restart=always --name registry \
  -v /data/registry:/var/lib/registry \
  registry:2

# 将加载的镜像推送到本地Registry
docker tag mysql:latest localhost:5000/mysql:latest
docker push localhost:5000/mysql:latest

# 可以创建脚本自动化这个过程
```

### 3.2 配置镜像仓库地址

在Docker Compose配置和Kubernetes资源定义中，将镜像路径修改为本地Registry地址：

```yaml
# Docker Compose示例
services:
  mysql:
    image: localhost:5000/mysql:latest
    # ...

# Kubernetes示例
spec:
  containers:
  - name: mysql
    image: localhost:5000/mysql:latest
    # ...
```

## 4. 离线环境依赖准备

### 4.1 准备配置文件和初始化脚本

确保所有必要的配置文件和初始化脚本已包含在部署包中：

```bash
# 创建完整的部署包
tar -czvf platform-deploy.tar.gz \
    deploy/ \
    platform-images/ \
    load-images.sh
```

### 4.2 准备依赖软件包

在离线环境中可能需要安装以下软件包：

- Docker Engine
- Docker Compose
- Git（用于版本控制）
- JDK（用于Java应用部署）
- Node.js（用于前端构建）

可以提前下载这些软件包的安装包或二进制文件：

```bash
# 创建依赖软件包目录
mkdir -p platform-deps

# 下载Docker安装包(根据目标OS调整)
curl -o platform-deps/docker-ce.tar.gz https://download.docker.com/linux/static/stable/x86_64/docker-20.10.9.tgz

# 下载Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.12.2/docker-compose-$(uname -s)-$(uname -m)" -o platform-deps/docker-compose

# 下载Git(根据目标OS调整)
# ...

# 下载JDK
# ...

# 下载Node.js
# ...

# 压缩依赖包
tar -czvf platform-deps.tar.gz platform-deps/
```

## 5. 离线部署步骤

### 5.1 环境准备

1. 安装Docker和必要软件
2. 加载Docker镜像
3. 设置本地Registry（如果需要）

### 5.2 部署数据平台

按照以下顺序部署服务：

1. 基础服务（MySQL、Redis等）
2. 中间件服务（Kafka、RabbitMQ等）
3. 监控和日志服务
4. 应用服务

单机环境：

```bash
cd deploy/single-node
./start.sh
```

集群环境：

```bash
cd deploy/cluster/docker-swarm
./scripts/init-cluster.sh
# 按提示完成集群初始化
./scripts/deploy-stack.sh
```

## 6. 离线环境注意事项

### 6.1 网络配置

- 确保所有节点间网络连通
- 配置必要的DNS和主机名解析
- 如有本地Registry，确保所有节点能访问

### 6.2 存储配置

- 配置共享存储系统
- 为数据卷准备足够的存储空间
- 配置备份策略

### 6.3 资源需求

| 部署类型 | 最小内存要求 | 推荐内存 | CPU核心 | 存储空间 |
|---------|------------|---------|--------|---------|
| 单机基础版 | 2GB | 4GB | 2核 | 50GB |
| 单机完整版 | 6GB | 8GB | 4核 | 100GB |
| 集群(每节点) | 8GB | 16GB | 4-8核 | 200GB+ |

### 6.4 离线更新策略

1. 在联网环境准备新版本镜像
2. 保存并传输到离线环境
3. 加载新镜像并更新服务
4. 备份配置和数据以便回滚

## 7. 故障排除

### 7.1 镜像加载问题

**问题**: 加载镜像时出现错误

**解决方案**:
- 检查压缩文件完整性
- 确保磁盘空间充足
- 尝试逐个加载镜像以定位问题

### 7.2 容器启动失败

**问题**: 服务容器无法启动

**解决方案**:
- 检查Docker日志 `docker logs <container_id>`
- 验证环境变量和配置文件
- 检查资源限制是否合理

### 7.3 服务间通信问题

**问题**: 服务之间无法正常通信

**解决方案**:
- 检查网络配置和DNS解析
- 验证服务名称和端口配置
- 使用Docker网络调试工具排查

## 8. 资源占用估算

全部镜像大小总计约为**8-10GB**，解压后可能达到**20-25GB**。请确保有足够的存储空间和网络带宽用于传输。

### 磁盘空间需求

| 项目 | 大小估计 |
|------|---------|
| Docker镜像 | 20-25GB |
| 配置文件 | <100MB |
| 应用数据 | 视业务量而定，建议预留100GB+ |
| 日志文件 | 视配置而定，建议预留50GB+ |
| 备份数据 | 与应用数据相当 |

## 9. 离线部署检查清单

□ 已获取所有必要的Docker镜像  
□ 已准备完整的配置文件和初始化脚本  
□ 已准备所有依赖软件包  
□ 已规划好存储和网络配置  
□ 已制定数据备份和恢复策略  
□ 已测试镜像加载和服务启动流程  
□ 已准备回滚方案和应急预案
