# 企业级数据平台 - 单机部署方案

这是一个基于Docker Compose的企业级数据平台单机测试环境，旨在提供完整的开发和测试环境，同时优化内存使用以适应单机部署。

## 系统要求

- Docker Engine 20.10.0+
- Docker Compose 2.0.0+
- 最小内存: 2GB (基础服务)
- 推荐内存: 4GB (包含监控)
- 完整环境: 6GB+ (所有服务)

## 快速开始

1. 进入单机部署目录:
```bash
cd deploy/single-node
```

2. 配置环境变量 (可选):
```bash
# 编辑配置文件
vi .env
```

3. 启动环境:
```bash
# 执行启动脚本
./start.sh
# 或直接使用Docker Compose
docker-compose up -d
```

4. 关闭环境:
```bash
# 执行关闭脚本
./stop.sh
# 或直接使用Docker Compose
docker-compose down
```

## 服务组件

### 核心服务

| 服务 | 描述 | 端口 | 管理界面 |
|------|------|------|----------|
| MySQL | 关系型数据库 | 3306 | - |
| Redis | 缓存服务 | 6379 | - |
| MongoDB | 文档数据库 | 27017 | - |
| Nacos | 服务注册与配置中心 | 8848 | http://localhost/nacos/ |
| Nginx | 反向代理/网关 | 80/443 | - |

### 消息队列服务

| 服务 | 描述 | 端口 | 管理界面 |
|------|------|------|----------|
| Zookeeper | 分布式协调服务 | 2181 | - |
| Kafka | 分布式消息队列 | 9092 | - |
| RabbitMQ | 消息队列 | 5672 | http://localhost:15672/ |

### 监控服务

| 服务 | 描述 | 端口 | 管理界面 |
|------|------|------|----------|
| Elasticsearch | 搜索引擎/日志存储 | 9200/9300 | - |
| Kibana | 日志可视化 | 5601 | http://localhost/kibana/ |
| Prometheus | 监控系统 | 9090 | http://localhost/prometheus/ |
| Grafana | 监控可视化 | 3000 | http://localhost/grafana/ |

### 扩展服务 (按需启用)

| 服务 | 描述 | 端口 | 启用方式 |
|------|------|------|----------|
| PostgreSQL | 关系型数据库 | 5432 | `docker-compose --profile database up -d postgres` |
| ClickHouse | 列式存储分析型数据库 | 8123/9000 | `docker-compose --profile analytics up -d clickhouse` |
| Consul | 服务网格 | 8500 | `docker-compose --profile service-mesh up -d consul` |
| Jaeger/Zipkin | 分布式追踪 | 16686/9411 | `docker-compose --profile tracing up -d jaeger zipkin` |
| Logstash/Fluentd | 日志收集 | - | `docker-compose --profile logging up -d logstash fluentd` |
| MinIO | 对象存储 | 9000/9001 | `docker-compose --profile storage up -d minio` |

## 内存优化配置

为了在有限资源环境下运行，本环境已进行以下优化:

1. 所有服务都配置了明确的内存限制
2. Java应用使用了最小化的JVM参数
3. 使用Docker Compose profiles按需启动服务
4. 数据库和中间件均采用最小化配置

## 部署指南

### 开发环境

```bash
# 启动基础服务
docker-compose up -d mysql redis nacos nginx

# 启动应用
docker-compose -f docker-compose-app.yml up -d
```

### 测试环境 (带监控)

```bash
# 启动基础服务+监控
docker-compose up -d mysql redis nacos nginx elasticsearch kibana prometheus grafana

# 启动消息队列
docker-compose up -d zookeeper kafka rabbitmq
```

### 自定义服务组合

可以使用profiles选择性启动特定服务:

```bash
# 启动基础服务+追踪
docker-compose --profile tracing up -d mysql redis nacos nginx jaeger

# 启动分析型数据库
docker-compose --profile analytics up -d clickhouse
```

## 应用接入

1. 添加后端应用到Docker网络:
```yaml
networks:
  platform_net:
    external: true
```

2. 配置应用连接到服务:
```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://mysql:3306/platform?useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=${MYSQL_ROOT_PASSWORD}

# Redis配置
spring.redis.host=redis
spring.redis.port=6379
spring.redis.password=${REDIS_PASSWORD}

# Nacos配置
spring.cloud.nacos.discovery.server-addr=nacos:8848
spring.cloud.nacos.config.server-addr=nacos:8848

# Kafka配置
spring.kafka.bootstrap-servers=kafka:9092
```

## 文件结构说明

```
single-node/
├── config/                    # 服务配置文件
│   ├── fluentd/               # Fluentd配置
│   ├── logstash/              # Logstash配置
│   │   ├── pipeline/          # Logstash管道配置
│   │   └── pipelines.yml      # Logstash管道定义
│   ├── nginx/                 # Nginx配置
│   │   ├── conf.d/            # Nginx站点配置
│   │   └── nginx.conf         # Nginx主配置
│   └── prometheus/            # Prometheus配置
│       └── prometheus.yml     # Prometheus抓取配置
├── docker/                    # Dockerfile目录
│   ├── app/                   # 应用Dockerfile
│   └── mysql/                 # MySQL自定义Dockerfile
├── init/                      # 初始化脚本
│   └── mysql/                 # MySQL初始化SQL
│       ├── init.sql           # 数据库初始化脚本
│       └── init-schema.sql    # 表结构初始化脚本
├── logs/                      # 日志目录
├── static/                    # 静态资源目录
├── .env                       # 环境变量配置
├── docker-compose.yml         # 主Docker Compose配置
├── docker-compose-app.yml     # 应用部署配置
├── docker-compose-frontend.yml # 前端部署配置
├── start.sh                   # 启动脚本
└── stop.sh                    # 停止脚本
```

## 常见问题

### 1. 内存不足?

如果遇到内存不足的问题，可以采取以下措施:
- 只启动必要的服务
- 调整服务的内存限制
- 增加swap空间

### 2. 如何查看服务日志?

```bash
# 查看特定服务日志
docker-compose logs -f mysql
```

### 3. 如何修改配置文件?

大多数配置文件挂载在`config/`目录下，可直接编辑后重启服务:

```bash
# 编辑配置
vi config/nginx/conf.d/default.conf

# 重启服务
docker-compose restart nginx
```

### 4. 如何备份数据?

```bash
# 备份MySQL数据
docker exec -it mysql mysqldump -u root -p{MYSQL_ROOT_PASSWORD} platform > backup.sql
```

## 与集群环境的差异

本单机环境与集群环境的主要差异：

1. **高可用性**: 单机环境不提供服务高可用性
2. **扩展性**: 单机环境资源有限，无法水平扩展
3. **配置简化**: 单机环境采用了简化配置，更容易启动和维护
4. **资源优化**: 单机环境针对内存和CPU进行了优化，适合开发测试

如需生产级部署，请参考集群环境配置。

## 技术支持

如有问题，请联系技术支持团队或提交Issue。
