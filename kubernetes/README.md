# 平台微服务Kubernetes部署指南

## 概述

本文档提供了平台微服务在Kubernetes环境中的部署指南和配置说明。这些配置文件用于在Kubernetes集群中部署和管理平台的各个微服务组件，包括基础设施服务、核心业务服务和监控服务。

## 目录结构

```
kubernetes/
├── base/                              # 基础配置
│   ├── services/                      # 服务部署配置
│   │   ├── platform-registry/         # 注册中心配置
│   │   ├── platform-config/           # 配置中心配置
│   │   ├── platform-gateway/          # API网关配置
│   │   └── ...                        # 其他服务配置
│   ├── ingress/                       # 入口规则
│   │   ├── platform-ingress.yaml      # 平台API入口
│   │   └── dashboard-ingress.yaml     # 仪表板入口
│   ├── configmaps/                    # 配置映射
│   │   ├── logging-config.yaml        # 日志配置
│   │   └── app-configs.yaml           # 应用配置
│   └── secrets/                       # 敏感配置
│       ├── db-credentials.yaml        # 数据库凭证
│       └── api-keys.yaml              # API密钥
├── environments/                      # 环境特定配置
│   ├── dev/                           # 开发环境
│   │   ├── kustomization.yaml         # 定制化配置
│   │   └── env-config.yaml            # 环境变量
│   ├── test/                          # 测试环境
│   │   ├── kustomization.yaml
│   │   └── env-config.yaml
│   └── prod/                          # 生产环境
│       ├── kustomization.yaml
│       └── env-config.yaml
└── monitoring/                        # 监控配置
    ├── prometheus/                    # Prometheus配置
    │   ├── prometheus-config.yaml     # Prometheus服务配置
    │   └── service-monitors.yaml      # 服务监控配置
    ├── grafana/                       # Grafana配置
    │   ├── grafana-deployment.yaml    # Grafana部署
    │   └── dashboards/                # 预定义仪表板
    └── alerts/                        # 告警规则
        └── alert-rules.yaml           # 告警规则配置
```

## 部署组件

### 基础设施服务

1. **平台注册中心 (platform-registry)**
   - Eureka服务注册中心
   - Spring Boot Admin监控

2. **平台配置中心 (platform-config)**
   - 集中配置管理
   - 动态配置更新

3. **平台API网关 (platform-gateway)**
   - 请求路由
   - 负载均衡
   - 安全控制

### 业务服务

1. **平台数据采集 (platform-collect)**
   - 多源数据采集
   - 数据验证和转换

2. **平台核心处理 (platform-fluxcore)**
   - 数据流处理
   - 业务规则引擎

3. **平台调度服务**
   - 调度引擎 (platform-scheduler)
   - 任务注册 (platform-scheduler-register)
   - 任务查询 (platform-scheduler-query)

### 可视化仪表板

1. **业务仪表板 (platform-buss-dashboard)**
   - 业务指标监控
   - 数据分析和可视化

2. **监控仪表板 (platform-monitor-dashboard)**
   - 系统性能监控
   - 健康状态监控

## 基础配置说明

### 部署配置

每个服务的部署配置包括：

1. **Deployment**: 定义服务的部署规格
   - 容器镜像和版本
   - 资源请求和限制
   - 存活和就绪探针
   - 环境变量配置

2. **Service**: 定义服务访问方式
   - 服务端口映射
   - 服务发现标签
   - 服务类型（ClusterIP/NodePort/LoadBalancer）

3. **ConfigMap**: 配置数据
   - 应用配置
   - 日志配置
   - 其他非敏感配置

4. **Secret**: 敏感配置
   - 数据库凭证
   - API密钥
   - 证书和密钥

### 示例：平台注册中心部署

```yaml
# platform-registry-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: platform-registry
  labels:
    app: platform-registry
spec:
  replicas: 2
  selector:
    matchLabels:
      app: platform-registry
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    metadata:
      labels:
        app: platform-registry
    spec:
      containers:
      - name: platform-registry
        image: ${DOCKER_REGISTRY}/platform-registry:${VERSION}
        ports:
        - containerPort: 8761
        resources:
          requests:
            memory: "512Mi"
            cpu: "200m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 60
          periodSeconds: 10
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 120
          periodSeconds: 30
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: platform-env-config
              key: environment
        - name: REGISTRY_USERNAME
          valueFrom:
            secretKeyRef:
              name: platform-registry-secret
              key: username
        - name: REGISTRY_PASSWORD
          valueFrom:
            secretKeyRef:
              name: platform-registry-secret
              key: password
---
# platform-registry-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: platform-registry
  labels:
    app: platform-registry
spec:
  selector:
    app: platform-registry
  ports:
  - port: 8761
    targetPort: 8761
  type: ClusterIP
```

## 环境配置

环境特定配置通过Kustomize管理，每个环境目录包含：

1. **kustomization.yaml**: 定义基础配置和环境特定配置的组合
2. **env-config.yaml**: 环境特定的配置映射
3. **资源补丁**: 针对不同环境的资源配置调整

### 示例：开发环境配置

```yaml
# dev/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
- ../base

namespace: platform-dev

configMapGenerator:
- name: platform-env-config
  behavior: merge
  literals:
  - environment=dev

patchesStrategicMerge:
- resources-patch.yaml

images:
- name: ${DOCKER_REGISTRY}/platform-registry
  newTag: latest
```

```yaml
# dev/resources-patch.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: platform-registry
spec:
  replicas: 1
  template:
    spec:
      containers:
      - name: platform-registry
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "200m"
```

## 监控配置

监控基于Prometheus和Grafana实现：

1. **Prometheus配置**:
   - 服务发现规则
   - 数据保留策略
   - 抓取配置

2. **ServiceMonitors**:
   - 定义监控目标
   - 指标抓取端点
   - 抓取频率

3. **Grafana仪表板**:
   - 预定义监控视图
   - 数据源配置
   - 告警阈值

### 示例：服务监控配置

```yaml
# service-monitors.yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: platform-services-monitor
  labels:
    app: platform
spec:
  selector:
    matchLabels:
      app: platform
  endpoints:
  - port: metrics
    path: /actuator/prometheus
    interval: 15s
  namespaceSelector:
    matchNames:
    - platform-dev
    - platform-test
    - platform-prod
```

## 部署流程

### 初始部署

1. 部署基础设施服务
   ```
   kubectl apply -k environments/dev/
   ```

2. 确认基础服务正常运行
   ```
   kubectl get pods -n platform-dev
   ```

3. 部署监控组件
   ```
   kubectl apply -f monitoring/
   ```

### 滚动更新

使用Kubernetes滚动更新策略进行无缝更新：

1. 更新服务镜像版本
   ```
   kubectl set image deployment/platform-service container-name=new-image:version -n platform-dev
   ```

2. 监控更新进度
   ```
   kubectl rollout status deployment/platform-service -n platform-dev
   ```

3. 如需回滚
   ```
   kubectl rollout undo deployment/platform-service -n platform-dev
   ```

## 金丝雀发布

通过Kubernetes的部署策略实现金丝雀发布：

1. 创建新版本部署配置，设置少量副本
2. 配置服务选择器同时匹配新旧版本
3. 逐步增加新版本副本，减少旧版本副本
4. 监控新版本性能和错误率
5. 完成迁移或回滚到旧版本

### 示例：金丝雀发布配置

```yaml
# canary-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: platform-service-canary
spec:
  replicas: 1  # 小比例副本
  selector:
    matchLabels:
      app: platform-service
      version: v2  # 新版本标签
  template:
    metadata:
      labels:
        app: platform-service
        version: v2
    spec:
      containers:
      - name: platform-service
        image: ${DOCKER_REGISTRY}/platform-service:v2
```

```yaml
# service-canary.yaml
apiVersion: v1
kind: Service
metadata:
  name: platform-service
spec:
  selector:
    app: platform-service  # 同时匹配v1和v2版本
  ports:
  - port: 80
    targetPort: 8080
```

## 资源管理

根据服务特性调整资源配置：

1. **CPU密集型服务**（如platform-fluxcore）:
   - 较高的CPU请求和限制
   - 适当的内存配置

2. **内存密集型服务**（如缓存和分析服务）:
   - 较高的内存请求和限制
   - 适当的JVM堆配置

3. **I/O密集型服务**（如platform-collect）:
   - 优化存储类型
   - 配置适当的I/O限制

## 网络策略

使用Kubernetes网络策略控制服务间通信：

1. 限制服务只能与必要的服务通信
2. 限制外部访问只能通过API网关
3. 为不同环境配置不同级别的网络策略

### 示例：网络策略

```yaml
# network-policy.yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: platform-service-policy
spec:
  podSelector:
    matchLabels:
      app: platform-service
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: platform-gateway
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: platform-database
    ports:
    - protocol: TCP
      port: 3306
  - to:
    - podSelector:
        matchLabels:
          app: platform-registry
    ports:
    - protocol: TCP
      port: 8761
```

## 存储配置

为需要持久化存储的服务配置持久卷：

1. 配置中心：配置文件存储
2. 数据库：数据和日志存储
3. 监控系统：指标和日志存储

### 示例：持久卷配置

```yaml
# persistent-volume.yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: platform-data-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: standard
```

## 安全配置

Kubernetes环境的安全配置包括：

1. **RBAC（基于角色的访问控制）**：
   - 服务账户配置
   - 角色和角色绑定
   - 集群角色和集群角色绑定

2. **Secret管理**：
   - 敏感信息加密存储
   - 密钥轮换策略
   - 访问控制

3. **Pod安全策略**：
   - 容器安全上下文
   - 特权控制
   - 卷和挂载限制

### 示例：RBAC配置

```yaml
# rbac-config.yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: platform-service-account
  namespace: platform-dev
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: platform-service-role
  namespace: platform-dev
rules:
- apiGroups: [""]
  resources: ["configmaps", "secrets"]
  verbs: ["get", "list"]
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: platform-service-role-binding
  namespace: platform-dev
subjects:
- kind: ServiceAccount
  name: platform-service-account
  namespace: platform-dev
roleRef:
  kind: Role
  name: platform-service-role
  apiGroup: rbac.authorization.k8s.io
```

## 备份和恢复策略

为确保系统数据安全，实施以下备份策略：

1. **数据库备份**：
   - 定期全量备份
   - 增量备份
   - 备份验证和恢复测试

2. **配置备份**：
   - Git仓库备份
   - ConfigMap和Secret备份
   - 版本管理

3. **应用状态备份**：
   - 持久卷数据备份
   - 状态导出和导入机制

### 示例：数据库备份作业

```yaml
# db-backup-job.yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: platform-db-backup
  namespace: platform-dev
spec:
  schedule: "0 2 * * *"  # 每天凌晨2点
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: db-backup
            image: mysql:8.0
            command:
            - /bin/sh
            - -c
            - |
              TIMESTAMP=$(date +%Y%m%d%H%M%S)
              mysqldump -h ${DB_HOST} -u ${DB_USER} -p${DB_PASSWORD} --all-databases > /backup/all-db-${TIMESTAMP}.sql
              gzip /backup/all-db-${TIMESTAMP}.sql
            env:
            - name: DB_HOST
              value: platform-mysql
            - name: DB_USER
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: username
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: password
            volumeMounts:
            - name: backup-volume
              mountPath: /backup
          volumes:
          - name: backup-volume
            persistentVolumeClaim:
              claimName: backup-pvc
          restartPolicy: OnFailure
```

## 故障恢复计划

制定全面的故障恢复计划，包括：

1. **服务中断处理**：
   - 关键服务恢复优先级
   - 回滚到稳定版本的流程
   - 数据恢复程序

2. **灾难恢复**：
   - 跨区域备份和恢复
   - 主备切换流程
   - 业务连续性保障

3. **故障演练**：
   - 定期故障模拟
   - 恢复流程测试
   - 团队响应训练

## 监控和告警策略

建立全面的监控和告警系统：

1. **基础设施监控**：
   - 节点资源使用率
   - 网络流量和延迟
   - 存储性能和容量

2. **应用监控**：
   - 服务健康状态
   - 响应时间和吞吐量
   - 错误率和异常

3. **业务监控**：
   - 业务关键指标
   - 用户体验指标
   - SLA合规性

### 示例：Prometheus告警规则

```yaml
# alert-rules.yaml
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: platform-alerts
  namespace: monitoring
spec:
  groups:
  - name: platform.rules
    rules:
    - alert: HighCpuUsage
      expr: sum(rate(container_cpu_usage_seconds_total{namespace=~"platform-.+",container_name!=""}[5m])) by (pod) / sum(kube_pod_container_resource_limits_cpu_cores{namespace=~"platform-.+",container!=""}) by (pod) > 0.85
      for: 5m
      labels:
        severity: warning
      annotations:
        summary: "Pod {{ $labels.pod }} high CPU usage"
        description: "Pod {{ $labels.pod }} in namespace {{ $labels.namespace }} has high CPU usage (> 85%) for more than 5 minutes."
        
    - alert: HighMemoryUsage
      expr: sum(container_memory_usage_bytes{namespace=~"platform-.+",container_name!=""}) by (pod) / sum(kube_pod_container_resource_limits_memory_bytes{namespace=~"platform-.+",container!=""}) by (pod) > 0.85
      for: 5m
      labels:
        severity: warning
      annotations:
        summary: "Pod {{ $labels.pod }} high memory usage"
        description: "Pod {{ $labels.pod }} in namespace {{ $labels.namespace }} has high memory usage (> 85%) for more than 5 minutes."
        
    - alert: PodCrashLooping
      expr: increase(kube_pod_container_status_restarts_total{namespace=~"platform-.+"}[15m]) > 3
      for: 5m
      labels:
        severity: critical
      annotations:
        summary: "Pod {{ $labels.pod }} crash looping"
        description: "Pod {{ $labels.pod }} in namespace {{ $labels.namespace }} is crash looping ({{ $value }} restarts in 15 minutes)."
```

## 性能优化

根据实际负载情况，对Kubernetes集群进行性能优化：

1. **资源分配优化**：
   - 根据实际使用情况调整资源请求和限制
   - 实施节点亲和性和反亲和性策略
   - 优化Pod散布策略

2. **HPA（水平自动伸缩）**：
   - 基于CPU/内存使用率的自动伸缩
   - 基于自定义指标的自动伸缩
   - 设置最小和最大副本数

3. **VPA（垂直自动伸缩）**：
   - 自动调整资源请求值
   - 分析历史资源使用情况
   - 避免资源过度分配

### 示例：HPA配置

```yaml
# hpa-config.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: platform-service-hpa
  namespace: platform-dev
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: platform-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 100
        periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 20
        periodSeconds: 60
```

## CI/CD配置

配置持续集成和持续部署流水线：

1. **代码构建和测试**：
   - 代码质量检查
   - 单元测试和集成测试
   - 安全扫描

2. **镜像构建和推送**：
   - 多阶段Dockerfile
   - 镜像版本管理
   - 镜像安全扫描

3. **部署自动化**：
   - 环境配置管理
   - 部署验证和测试
   - 蓝绿部署/金丝雀发布

### 示例：GitLab CI配置

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - package
  - deploy-dev
  - deploy-test
  - deploy-prod

variables:
  DOCKER_REGISTRY: harbor.example.com
  K8S_DEV_NAMESPACE: platform-dev
  K8S_TEST_NAMESPACE: platform-test
  K8S_PROD_NAMESPACE: platform-prod

build:
  stage: build
  script:
    - mvn clean compile

test:
  stage: test
  script:
    - mvn test

security-scan:
  stage: test
  script:
    - sonar-scanner

package:
  stage: package
  script:
    - mvn package -DskipTests
    - docker build -t $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA .
    - docker push $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA
    - docker tag $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA $DOCKER_REGISTRY/platform-service:latest
    - docker push $DOCKER_REGISTRY/platform-service:latest

deploy-dev:
  stage: deploy-dev
  script:
    - sed -i "s|image:.*|image: $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA|g" kubernetes/environments/dev/kustomization.yaml
    - kubectl apply -k kubernetes/environments/dev
  environment:
    name: development
    url: https://dev.platform.example.com
  only:
    - dev

deploy-test:
  stage: deploy-test
  script:
    - sed -i "s|image:.*|image: $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA|g" kubernetes/environments/test/kustomization.yaml
    - kubectl apply -k kubernetes/environments/test
  environment:
    name: testing
    url: https://test.platform.example.com
  only:
    - master
  when: manual

deploy-prod:
  stage: deploy-prod
  script:
    - sed -i "s|image:.*|image: $DOCKER_REGISTRY/platform-service:$CI_COMMIT_SHA|g" kubernetes/environments/prod/kustomization.yaml
    - kubectl apply -k kubernetes/environments/prod
  environment:
    name: production
    url: https://platform.example.com
  only:
    - tags
  when: manual
```

## 多集群部署

对于生产环境，考虑采用多集群部署策略：

1. **区域分布**：
   - 多地域部署
   - 就近服务用户
   - 灾难恢复保障

2. **环境隔离**：
   - 开发/测试/生产环境隔离
   - 不同安全级别的服务隔离
   - 关键服务独立集群

3. **流量管理**：
   - 全局负载均衡
   - 跨集群服务发现
   - 流量分配策略

### 示例：多集群服务发现

```yaml
# multi-cluster-service.yaml
apiVersion: networking.istio.io/v1alpha3
kind: ServiceEntry
metadata:
  name: platform-service-remote
spec:
  hosts:
  - platform-service.platform-prod.svc.cluster.remote
  location: MESH_INTERNAL
  ports:
  - number: 80
    name: http
    protocol: HTTP
  resolution: DNS
  endpoints:
  - address: cluster-b-ingress.example.com
    ports:
      http: 80
```

## 最佳实践总结

1. **基础设施即代码**：
   - 所有配置版本控制
   - 通过GitOps流程管理变更
   - 自动化部署和配置

2. **资源管理**：
   - 设置合理的资源请求和限制
   - 实施命名空间资源配额
   - 监控资源使用趋势

3. **安全实践**：
   - 最小权限原则
   - 敏感信息加密存储
   - 定期安全扫描和审计

4. **监控和可观察性**：
   - 全面的指标收集
   - 集中式日志管理
   - 分布式跟踪
   - 健康检查和告警

5. **持续改进**：
   - 性能基准测试
   - 定期架构评审
   - 容量规划和扩展
