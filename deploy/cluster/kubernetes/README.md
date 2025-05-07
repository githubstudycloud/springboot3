# Kubernetes集群部署指南

本目录包含使用Kubernetes部署企业级数据平台集群的配置文件和脚本。相比Docker Swarm，Kubernetes提供了更丰富的功能、更强大的扩展性和更完善的生态系统。

## 前提条件

- 至少3台服务器（生产环境建议5台或更多）
- 每台服务器至少8GB内存，生产环境建议16GB以上
- 已安装Kubernetes 1.20+
- 已配置网络插件（如Calico、Flannel等）
- 已配置存储系统（如NFS、Ceph、GlusterFS或云提供商的存储服务）

## 集群架构

```
┌─────────────────────────────────────────────────────────────────┐
│                   Ingress Controller (Nginx/Traefik)            │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                           API Gateway                           │
└───────────────────────────────┬─────────────────────────────────┘
                                │
        ┌─────────────┬─────────┴─────────┬─────────────┐
        │             │                   │             │
┌───────▼─────┐ ┌─────▼─────┐     ┌───────▼─────┐ ┌─────▼─────┐
│  认证服务   │ │ 采集服务  │     │ 数据治理服务│ │调度服务   │
└─────────────┘ └─────────────┘   └─────────────┘ └─────────────┘
        │             │                   │             │
┌───────▼─────────────▼───────────────────▼─────────────▼─────┐
│                       持久化存储层                          │
│    ┌─────────┐   ┌─────────┐    ┌─────────┐   ┌─────────┐   │
│    │  MySQL  │   │  Redis  │    │ MongoDB │   │ Kafka   │   │
│    └─────────┘   └─────────┘    └─────────┘   └─────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 目录结构

```
kubernetes/
├── manifests/                    # K8s资源定义文件
│   ├── namespaces/               # 命名空间定义
│   ├── storage/                  # 存储类和持久卷声明
│   ├── databases/                # 数据库服务
│   │   ├── mysql/                # MySQL集群
│   │   ├── redis/                # Redis集群
│   │   └── mongodb/              # MongoDB集群
│   ├── middleware/               # 中间件服务
│   │   ├── kafka/                # Kafka集群
│   │   ├── rabbitmq/             # RabbitMQ集群
│   │   └── nacos/                # Nacos集群
│   ├── applications/             # 应用服务
│   │   ├── auth-service/         # 认证服务
│   │   ├── collect-service/      # 采集服务
│   │   └── governance-service/   # 治理服务
│   ├── ingress/                  # 入口网关
│   └── monitoring/               # 监控服务
│       ├── prometheus/           # Prometheus监控
│       ├── grafana/              # Grafana可视化
│       └── elastic/              # ELK日志收集
├── helm-charts/                  # Helm图表
│   ├── platform-core/            # 核心服务图表
│   ├── platform-data/            # 数据服务图表
│   └── platform-monitor/         # 监控服务图表
├── scripts/                      # 部署脚本
│   ├── install-k8s.sh            # K8s安装脚本
│   ├── init-cluster.sh           # 集群初始化脚本
│   ├── deploy-platform.sh        # 平台部署脚本
│   └── backup-data.sh            # 数据备份脚本
└── README.md                     # 说明文档
```

## 部署步骤

### 1. 集群准备

如果还没有Kubernetes集群，可以使用提供的脚本创建：

```bash
# 安装Kubernetes
./scripts/install-k8s.sh

# 初始化集群
./scripts/init-cluster.sh
```

如果已有集群，请确保：
- Kubernetes版本至少为1.20+
- 已安装Helm 3.0+
- 已安装StorageClass和必要的存储驱动
- 已安装Ingress Controller

### 2. 创建命名空间

创建必要的命名空间来隔离不同服务：

```bash
kubectl apply -f manifests/namespaces/
```

这将创建以下命名空间：
- `platform-core`：核心服务
- `platform-data`：数据服务
- `platform-middleware`：中间件服务
- `platform-monitoring`：监控服务

### 3. 配置存储

部署存储系统和持久卷：

```bash
kubectl apply -f manifests/storage/
```

### 4. 部署数据库服务

部署各种数据库服务：

```bash
# 部署MySQL集群
kubectl apply -f manifests/databases/mysql/

# 部署Redis集群
kubectl apply -f manifests/databases/redis/

# 部署MongoDB集群
kubectl apply -f manifests/databases/mongodb/
```

### 5. 部署中间件服务

```bash
# 部署Kafka集群
kubectl apply -f manifests/middleware/kafka/

# 部署RabbitMQ集群
kubectl apply -f manifests/middleware/rabbitmq/

# 部署Nacos集群
kubectl apply -f manifests/middleware/nacos/
```

### 6. 部署应用服务

```bash
# 部署认证服务
kubectl apply -f manifests/applications/auth-service/

# 部署采集服务
kubectl apply -f manifests/applications/collect-service/

# 部署治理服务
kubectl apply -f manifests/applications/governance-service/
```

### 7. 部署入口网关

```bash
kubectl apply -f manifests/ingress/
```

### 8. 部署监控服务

```bash
kubectl apply -f manifests/monitoring/
```

### 使用Helm部署（可选）

如果您熟悉Helm，也可以使用Helm图表进行部署：

```bash
# 添加Helm仓库
helm repo add platform-repo https://your-helm-repo.example.com

# 部署核心服务
helm install platform-core platform-repo/platform-core

# 部署数据服务
helm install platform-data platform-repo/platform-data

# 部署监控服务
helm install platform-monitoring platform-repo/platform-monitor
```

## 高可用设计

Kubernetes集群的高可用设计：

1. **控制平面高可用**：至少3个master节点，etcd集群配置
2. **工作节点分布**：合理分布在不同物理机或可用区
3. **Pod反亲和性**：确保关键服务的多个副本分布在不同节点
4. **PodDisruptionBudget**：限制同时维护的节点数量，确保服务可用性
5. **存储高可用**：使用分布式存储或云存储提供数据高可用

## 资源管理

合理配置资源限制和请求：

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

使用HorizontalPodAutoscaler实现自动扩缩容：

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: auth-service
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: auth-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## 网络策略

实施网络隔离确保安全：

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: db-policy
  namespace: platform-data
spec:
  podSelector:
    matchLabels:
      app: mysql
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: platform-core
    - podSelector:
        matchLabels:
          role: backend
    ports:
    - protocol: TCP
      port: 3306
```

## 监控与日志

### Prometheus监控

部署Prometheus Operator和监控组件：

```bash
kubectl apply -f manifests/monitoring/prometheus/
```

### 日志收集

部署ELK或EFK栈收集和分析日志：

```bash
kubectl apply -f manifests/monitoring/elastic/
```

### Grafana仪表板

部署Grafana并配置仪表板：

```bash
kubectl apply -f manifests/monitoring/grafana/
```

## 备份与恢复

实施定期备份策略：

```bash
# 运行备份作业
kubectl apply -f manifests/jobs/backup-job.yaml

# 或使用备份脚本
./scripts/backup-data.sh
```

## 安全建议

提高集群安全性的关键措施：

1. **RBAC授权**：严格控制服务账号权限
2. **Pod安全策略**：限制容器权限和挂载点
3. **Secret加密**：使用外部密钥管理服务或加密静态Secret
4. **镜像安全**：使用私有仓库和镜像签名
5. **网络策略**：实施细粒度的网络隔离

## 常见问题

### 1. Pod无法调度

**症状**：Pod停留在Pending状态
**解决方案**：
- 检查资源限制是否合理
- 验证PersistentVolumeClaim是否可以绑定
- 检查节点污点和容忍度设置

### 2. 服务无法访问

**症状**：无法通过Service访问Pod
**解决方案**：
- 检查Service和Pod的标签选择器是否匹配
- 验证Pod是否处于Ready状态
- 检查网络策略是否过于严格

### 3. 持久化存储问题

**症状**：无法创建或挂载PersistentVolume
**解决方案**：
- 检查StorageClass配置
- 验证底层存储系统状态
- 检查访问权限和配额设置

## 进阶主题

### 多集群部署

使用Federation或Fleet管理多个集群：

1. 配置中央控制平面
2. 定义多集群资源分配
3. 实现集群间服务发现
4. 建立全局负载均衡

### GitOps实践

使用ArgoCD或Flux实现GitOps：

1. 将配置存储在Git仓库
2. 自动同步集群状态
3. 实现环境间的配置继承
4. 配置自动回滚机制

### 服务网格

部署Istio或Linkerd实现服务网格：

1. 增强服务间通信安全
2. 实现细粒度流量控制
3. 提供详细的可观测性
4. 实现金丝雀部署和蓝绿发布

## 版本更新与路线图

请关注以下更新计划：

1. **2025年Q2**：增加服务网格集成
2. **2025年Q3**：强化多集群管理能力
3. **2025年Q4**：整合AI辅助监控与运维

## 联系与支持

如有问题或需要技术支持，请联系：

- 技术支持邮箱：support@example.com
- 问题报告：https://github.com/your-org/platform/issues
