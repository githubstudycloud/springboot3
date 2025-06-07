# 🚀 Beta环境集群部署指南

## 概述

Beta环境采用Docker Swarm集群部署方式，所有组件以高可用集群模式运行，支持负载均衡、故障转移和自动恢复，适合压力测试和预发布验证。

## 系统要求

### 硬件要求

- **最少节点数**: 3个节点（1个Manager + 2个Worker）
- **推荐节点数**: 5个节点（3个Manager + 2个Worker）
- **内存**: 每节点最少 16GB，推荐 32GB
- **存储**: 每节点最少 100GB 可用空间，推荐 SSD
- **CPU**: 每节点最少 8核，推荐 16核
- **网络**: 节点间延迟 < 10ms，带宽 > 1Gbps

### 软件要求

- **操作系统**: Ubuntu 20.04+ / CentOS 7+ / RHEL 8+
- **Docker版本**: 20.10+
- **Docker Swarm**: 集群模式
- **网络**: 开放端口 2377, 7946, 4789

## 集群架构

### 服务分布

```
┌─────────────────────────────────────────────────────────────┐
│                    Beta 集群架构                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │   Manager   │  │   Manager   │  │   Manager   │          │
│  │   Node-1    │  │   Node-2    │  │   Node-3    │          │
│  │             │  │             │  │             │          │
│  │ MySQL Master│  │ Redis Master│  │MongoDB Primary│        │
│  │ Nacos Leader│  │ Kafka Broker│  │ RocketMQ NS │          │
│  │ Nginx LB    │  │ Prometheus  │  │ Grafana     │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐                          │
│  │   Worker    │  │   Worker    │                          │
│  │   Node-4    │  │   Node-5    │                          │
│  │             │  │             │                          │
│  │ MySQL Slave │  │ Redis Slave │                          │
│  │API Services │  │Web Services │                          │
│  │RabbitMQ     │  │MongoDB Sec  │                          │
│  └─────────────┘  └─────────────┘                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 集群组件配置

| 组件 | 主实例位置 | 从实例位置 | 副本数 | 高可用策略 |
|------|------------|------------|--------|------------|
| MySQL | Manager Node-1 | Worker Node-4,5 | 1+2 | 主从复制 + 自动故障转移 |
| Redis | Manager Node-2 | Worker Node-4,5 | 1+2+3 | Sentinel 高可用 |
| MongoDB | Manager Node-3 | Worker Node-5 | 1+2 | 副本集 + 自动选举 |
| Nacos | All Managers | - | 3 | 集群模式 + DB存储 |
| RabbitMQ | All Nodes | - | 3 | 集群模式 + 镜像队列 |
| Kafka | All Managers | - | 3 | 集群模式 + 副本因子3 |
| RocketMQ | All Managers | All Workers | 3+2 | 集群模式 + 多Master |
| Nginx | Manager Node-1,2 | - | 2 | 负载均衡 + 健康检查 |
| Platform API | All Workers | - | 3 | 无状态服务 + 负载均衡 |
| Platform Web | All Workers | - | 2 | 静态服务 + CDN |

## 快速开始

### 步骤1：准备集群节点

**在所有节点上安装Docker**:
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 启动Docker服务
sudo systemctl enable docker
sudo systemctl start docker

# 添加用户到docker组
sudo usermod -aG docker $USER
```

### 步骤2：初始化Swarm集群

**在Manager主节点上执行**:
```bash
# 进入Beta环境目录
cd environments/beta

# 运行集群安装脚本
chmod +x scripts/setup-docker-cluster.sh
./scripts/setup-docker-cluster.sh
```

**在其他节点上加入集群**:
```bash
# 工作节点加入（从Manager节点获取join token）
docker swarm join --token SWMTKN-1-xxxxxxx manager-ip:2377

# 管理节点加入
docker swarm join --token SWMTKN-1-xxxxxxx manager-ip:2377
```

### 步骤3：设置节点标签

```bash
# 设置Manager节点标签
docker node update --label-add mysql-master=true node-1
docker node update --label-add redis-master=true node-2
docker node update --label-add mongodb-primary=true node-3

# 设置Worker节点标签  
docker node update --label-add mysql-slave=true node-4
docker node update --label-add mysql-slave=true node-5
docker node update --label-add redis-slave=true node-4
docker node update --label-add redis-slave=true node-5
docker node update --label-add mongodb-secondary=true node-5
```

### 步骤4：部署服务栈

```bash
# 部署完整服务栈
docker stack deploy -c docker-compose.yml platform-beta

# 查看部署状态
docker service ls
docker stack ps platform-beta
```

## 集群管理

### 服务管理

```bash
# 查看所有服务
docker service ls

# 查看特定服务详情
docker service inspect platform-beta_mysql-master

# 查看服务日志
docker service logs -f platform-beta_mysql-master

# 扩展服务副本
docker service scale platform-beta_platform-api=5

# 更新服务
docker service update --image new-image:tag platform-beta_platform-api

# 回滚服务
docker service rollback platform-beta_platform-api
```

### 节点管理

```bash
# 查看集群节点
docker node ls

# 查看节点详细信息
docker node inspect node-1

# 设置节点可用性
docker node update --availability drain node-1
docker node update --availability active node-1

# 提升工作节点为管理节点
docker node promote node-4

# 降级管理节点为工作节点
docker node demote node-3

# 移除节点
docker node rm node-5
```

### 网络管理

```bash
# 查看Swarm网络
docker network ls

# 查看overlay网络详情
docker network inspect platform-beta

# 创建新的overlay网络
docker network create --driver overlay --attachable new-network
```

## 数据库集群配置

### MySQL 主从集群

**主节点配置**:
```cnf
# config/mysql/master.cnf
[mysqld]
log-bin=mysql-bin
server-id=1
binlog-format=ROW
gtid-mode=ON
enforce-gtid-consistency=ON
log-replica-updates=ON
binlog-do-db=platform_beta

# 性能优化
innodb_buffer_pool_size=2G
innodb_log_file_size=256M
max_connections=1000
query_cache_size=256M
```

**从节点配置**:
```cnf
# config/mysql/slave.cnf
[mysqld]
server-id=2
log-bin=mysql-bin
relay-log=mysql-relay-bin
read-only=1
super-read-only=1
gtid-mode=ON
enforce-gtid-consistency=ON
log-replica-updates=ON

# 性能优化
innodb_buffer_pool_size=1G
max_connections=500
```

**手动配置主从复制**:
```bash
# 在主节点创建复制用户
docker exec -it mysql-master mysql -u root -p
CREATE USER 'repl_user'@'%' IDENTIFIED BY 'beta_repl_pass_2024';
GRANT REPLICATION SLAVE ON *.* TO 'repl_user'@'%';
SHOW MASTER STATUS;

# 在从节点配置复制
docker exec -it mysql-slave mysql -u root -p
CHANGE MASTER TO
  MASTER_HOST='mysql-master',
  MASTER_USER='repl_user',
  MASTER_PASSWORD='beta_repl_pass_2024',
  MASTER_AUTO_POSITION=1;
START SLAVE;
SHOW SLAVE STATUS\G
```

### Redis Sentinel 高可用

**Sentinel配置**:
```conf
# config/redis/sentinel.conf
port 26379
sentinel monitor redis-master redis-master 6379 2
sentinel auth-pass redis-master beta_redis_pass_2024
sentinel down-after-milliseconds redis-master 5000
sentinel parallel-syncs redis-master 1
sentinel failover-timeout redis-master 10000
sentinel deny-scripts-reconfig yes
```

**客户端连接配置**:
```yaml
spring:
  redis:
    sentinel:
      master: redis-master
      nodes: 
        - sentinel-1:26379
        - sentinel-2:26379
        - sentinel-3:26379
    password: beta_redis_pass_2024
```

### MongoDB 副本集

**初始化副本集**:
```javascript
// 在Primary节点执行
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongodb-primary:27017", priority: 2 },
    { _id: 1, host: "mongodb-secondary-1:27017", priority: 1 },
    { _id: 2, host: "mongodb-secondary-2:27017", priority: 1 }
  ]
})

// 查看副本集状态
rs.status()

// 配置读偏好
rs.slaveOk()
```

## 监控与告警

### Prometheus 集群监控

**监控目标配置**:
```yaml
# config/prometheus/prometheus.beta.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert_rules.yml"

scrape_configs:
  - job_name: 'docker-swarm'
    docker_swarm_sd_configs:
      - host: unix:///var/run/docker.sock
        role: tasks
    relabel_configs:
      - source_labels: [__meta_docker_swarm_task_desired_state]
        regex: running
        action: keep

  - job_name: 'mysql-cluster'
    static_configs:
      - targets: ['mysql-master:3306', 'mysql-slave:3306']

  - job_name: 'redis-cluster'  
    static_configs:
      - targets: ['redis-master:6379', 'redis-slave:6379']

alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']
```

### Grafana 集群面板

**导入预配置面板**:
```yaml
# config/grafana/provisioning/dashboards/dashboard.yml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    editable: true
    updateIntervalSeconds: 10
    options:
      path: /etc/grafana/provisioning/dashboards
```

### 告警规则

**关键告警配置**:
```yaml
# config/prometheus/alert_rules.yml
groups:
  - name: cluster.rules
    rules:
      - alert: ServiceDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Service {{ $labels.instance }} is down"

      - alert: HighMemoryUsage
        expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage on {{ $labels.instance }}"

      - alert: MySQLReplicationLag
        expr: mysql_slave_lag_seconds > 60
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "MySQL replication lag is high"
```

## 负载均衡配置

### Nginx 集群负载均衡

**上游服务配置**:
```nginx
# config/nginx/nginx.beta.conf
upstream platform_api_cluster {
    least_conn;
    server platform-api-1:8080 max_fails=3 fail_timeout=30s;
    server platform-api-2:8080 max_fails=3 fail_timeout=30s;
    server platform-api-3:8080 max_fails=3 fail_timeout=30s;
    
    keepalive 32;
}

upstream platform_web_cluster {
    ip_hash;
    server platform-web-1:80 max_fails=3 fail_timeout=30s;
    server platform-web-2:80 max_fails=3 fail_timeout=30s;
    
    keepalive 16;
}

server {
    listen 80;
    server_name localhost;
    
    # API服务代理
    location /api/ {
        proxy_pass http://platform_api_cluster/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 健康检查
        proxy_next_upstream error timeout invalid_header http_500 http_502 http_503;
        proxy_connect_timeout 5s;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }
    
    # Web服务代理
    location / {
        proxy_pass http://platform_web_cluster/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 健康检查端点
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

## 备份与恢复

### 自动化备份策略

**MySQL集群备份**:
```bash
#!/bin/bash
# scripts/backup-mysql-cluster.sh

BACKUP_DIR="/backup/mysql/$(date +%Y%m%d)"
mkdir -p $BACKUP_DIR

# 从主节点备份
docker exec mysql-master mysqldump \
  -u root -pbeta_root_pass_2024 \
  --all-databases \
  --single-transaction \
  --master-data=2 \
  --flush-logs \
  > $BACKUP_DIR/mysql-master-$(date +%H%M%S).sql

# 验证从节点数据一致性
docker exec mysql-slave mysql \
  -u root -pbeta_root_pass_2024 \
  -e "SHOW SLAVE STATUS\G" \
  > $BACKUP_DIR/slave-status-$(date +%H%M%S).txt

# 压缩备份文件
gzip $BACKUP_DIR/*.sql
```

**MongoDB副本集备份**:
```bash
#!/bin/bash
# scripts/backup-mongodb-cluster.sh

BACKUP_DIR="/backup/mongodb/$(date +%Y%m%d)"
mkdir -p $BACKUP_DIR

# 从Secondary节点备份（减少对Primary的影响）
docker exec mongodb-secondary mongodump \
  --host mongodb-secondary:27017 \
  --username mongo_admin \
  --password beta_mongo_pass_2024 \
  --authenticationDatabase admin \
  --out $BACKUP_DIR/

# 压缩备份
tar -czf $BACKUP_DIR/mongodb-backup-$(date +%H%M%S).tar.gz -C $BACKUP_DIR .
```

### 灾难恢复

**集群故障恢复流程**:
```bash
# 1. 评估集群状态
docker node ls
docker service ls

# 2. 隔离故障节点
docker node update --availability drain <failed-node>

# 3. 重新调度服务
docker service update --force <service-name>

# 4. 恢复数据（如需要）
# MySQL数据恢复
docker exec -i mysql-master mysql -u root -p < backup.sql

# MongoDB数据恢复
docker exec mongodb-primary mongorestore --drop /backup/path

# 5. 验证服务状态
docker service logs <service-name>
```

## 性能调优

### 集群性能优化

**Docker Swarm调优**:
```bash
# 调整心跳间隔
docker swarm update --task-history-limit 3

# 优化网络性能
echo 'net.core.rmem_max = 134217728' >> /etc/sysctl.conf
echo 'net.core.wmem_max = 134217728' >> /etc/sysctl.conf
sysctl -p
```

**服务资源限制**:
```yaml
# docker-compose.yml中的资源配置
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 4G
    reservations:
      cpus: '1'
      memory: 2G
  restart_policy:
    condition: on-failure
    delay: 5s
    max_attempts: 3
```

## 安全配置

### 集群安全加固

**TLS证书配置**:
```bash
# 生成CA证书
openssl genrsa -out ca-key.pem 4096
openssl req -new -x509 -days 365 -key ca-key.pem -sha256 -out ca.pem

# 生成服务器证书
openssl genrsa -out server-key.pem 4096
openssl req -subj "/CN=docker-swarm" -sha256 -new -key server-key.pem -out server.csr
openssl x509 -req -days 365 -sha256 -in server.csr -CA ca.pem -CAkey ca-key.pem -out server-cert.pem
```

**Secrets管理**:
```bash
# 创建密码secrets
echo "beta_mysql_root_password" | docker secret create mysql_root_password -
echo "beta_redis_password" | docker secret create redis_password -

# 在服务中使用secrets
services:
  mysql-master:
    secrets:
      - mysql_root_password
    environment:
      MYSQL_ROOT_PASSWORD_FILE: /run/secrets/mysql_root_password
```

## 故障排除

### 常见集群问题

**1. 节点无法加入集群**
```bash
# 检查防火墙端口
sudo ufw allow 2377/tcp
sudo ufw allow 7946/tcp
sudo ufw allow 7946/udp  
sudo ufw allow 4789/udp

# 检查网络连通性
telnet manager-ip 2377
```

**2. 服务无法启动**
```bash
# 查看服务详细错误
docker service ps --no-trunc platform-beta_mysql-master

# 检查节点资源
docker node inspect node-1 --format '{{.Status.State}}'

# 查看系统资源
docker system df
docker system events
```

**3. 数据库复制异常**
```bash
# MySQL主从状态检查
docker exec mysql-master mysql -u root -p -e "SHOW MASTER STATUS"
docker exec mysql-slave mysql -u root -p -e "SHOW SLAVE STATUS\G"

# Redis Sentinel状态检查  
docker exec redis-sentinel redis-cli -p 26379 SENTINEL masters
docker exec redis-sentinel redis-cli -p 26379 SENTINEL slaves redis-master

# MongoDB副本集状态检查
docker exec mongodb-primary mongosh --eval "rs.status()"
```

### 集群健康检查脚本

```bash
#!/bin/bash
# scripts/cluster-health-check.sh

echo "=== Docker Swarm集群健康检查 ==="

# 检查集群状态
echo "1. 集群节点状态:"
docker node ls

echo -e "\n2. 服务运行状态:"
docker service ls

echo -e "\n3. 服务副本详情:"
docker stack ps platform-beta

echo -e "\n4. 数据库集群状态:"
echo "MySQL主从状态:"
docker exec mysql-master mysql -u root -pbeta_root_pass_2024 -e "SHOW MASTER STATUS" 2>/dev/null || echo "❌ MySQL Master异常"
docker exec mysql-slave mysql -u root -pbeta_root_pass_2024 -e "SHOW SLAVE STATUS\G" | grep "Slave_IO_Running\|Slave_SQL_Running" 2>/dev/null || echo "❌ MySQL Slave异常"

echo -e "\nRedis Sentinel状态:"
docker exec redis-sentinel redis-cli -p 26379 SENTINEL masters 2>/dev/null || echo "❌ Redis Sentinel异常"

echo -e "\nMongoDB副本集状态:"
docker exec mongodb-primary mongosh --quiet --eval "rs.status().ok" 2>/dev/null || echo "❌ MongoDB副本集异常"

echo -e "\n5. 网络连通性:"
docker network ls | grep platform-beta

echo -e "\n=== 健康检查完成 ==="
```

## 相关链接

- [Docker Swarm官方文档](https://docs.docker.com/engine/swarm/)
- [Docker Stack部署文档](https://docs.docker.com/engine/reference/commandline/stack_deploy/)
- [项目架构文档](../../ARCHITECTURE-DDD-HEXAGON.md)
- [环境对比文档](../../ENVIRONMENT-SETUP-ARCHITECTURE.md)
- [测试环境文档](../testing/README.md) 