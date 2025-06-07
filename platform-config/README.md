# Platform Config Server

企业级分布式配置中心服务，支持GitLab集成、本地fallback和多环境部署。

## 目录结构

```
platform-config/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/platform/config/
│       │       ├── ConfigServerApplication.java     # 主启动类
│       │       ├── controller/
│       │       │   └── ConfigController.java        # 配置管理API
│       │       └── service/
│       │           └── ConfigManagementService.java # 配置管理服务
│       └── resources/
│           └── application.yml                      # 主配置文件
├── config-repository/                              # 配置文件仓库
│   ├── common/                                     # 公共配置
│   │   ├── common/                                 # 全环境公共配置
│   │   ├── dev/                                    # 开发环境公共配置
│   │   ├── test/                                   # 测试环境公共配置
│   │   └── pub/                                    # 生产环境公共配置
│   └── platform-api/                              # 应用特定配置
│       ├── common/                                 # 应用公共配置
│       ├── dev/                                    # 应用开发环境配置
│       ├── test/                                   # 应用测试环境配置
│       └── pub/                                    # 应用生产环境配置
├── k8s/                                           # Kubernetes配置
│   ├── config-server-deployment.yaml
│   ├── config-server-configmap.yaml
│   └── config-server-secret.yaml
├── scripts/
│   └── deploy.sh                                  # 部署脚本
├── Dockerfile                                     # Docker镜像构建
├── docker-compose.yml                            # Docker Compose配置
└── README.md                                      # 项目文档
```

## 功能特性

### 核心功能
- **多配置源支持**: GitLab + 本地文件 fallback
- **环境隔离**: dev、test、pub 三环境配置
- **配置加密**: 敏感信息自动加密存储
- **动态刷新**: 运行时配置热更新
- **版本管理**: 基于Git的配置版本控制

### 高级功能
- **配置备份**: 自动备份和恢复机制
- **健康检查**: 多维度服务健康监控
- **安全认证**: Basic Auth + JWT双重保护
- **审计日志**: 完整的配置变更追踪
- **集群部署**: 支持分布式高可用部署

## 配置结构说明

### 配置文件优先级
1. `{application}/{profile}/application-{profile}.yml` (最高优先级)
2. `{application}/common/application.yml`
3. `common/{profile}/application-{profile}.yml`
4. `common/common/application.yml` (最低优先级)

### 环境配置
- **dev**: 开发环境，调试模式开启，详细日志
- **test**: 测试环境，性能测试，模拟数据
- **pub**: 生产环境，安全加固，集群模式

### 配置示例

#### 应用配置获取URL
```
# 获取platform-api在dev环境的配置
GET http://config-server:8888/config/platform-api/dev/main

# 获取加密配置
GET http://config-server:8888/config/encrypt/status
POST http://config-server:8888/config/encrypt -d "sensitive-data"
```

## 部署方式

### 1. Docker 单机部署

```bash
# 构建镜像
./scripts/deploy.sh build

# 部署服务
./scripts/deploy.sh docker-single -e dev \
  --git-uri "https://gitlab.example.com/config/platform-config.git" \
  --git-username "your-username" \
  --git-password "your-password"

# 访问服务
curl http://localhost:8888/config/actuator/health
```

### 2. Docker 集群部署

```bash
# 初始化Swarm集群
docker swarm init

# 部署集群
./scripts/deploy.sh docker-cluster -e test \
  --git-uri "https://gitlab.example.com/config/platform-config.git"

# 查看服务状态
docker service ls
docker service logs platform-config_config-server
```

### 3. Kubernetes 部署

```bash
# 部署到K8s
./scripts/deploy.sh k8s -e pub -n production \
  --git-uri "https://gitlab.example.com/config/platform-config.git"

# 查看部署状态
kubectl get pods -n production
kubectl logs -f deployment/platform-config-server -n production
```

## 环境变量配置

### 必需环境变量
| 变量名 | 描述 | 示例 |
|--------|------|------|
| `CONFIG_GIT_URI` | GitLab仓库地址 | `https://gitlab.example.com/config/platform-config.git` |
| `CONFIG_GIT_USERNAME` | GitLab用户名 | `config-user` |
| `CONFIG_GIT_PASSWORD` | GitLab密码/Token | `your-token` |

### 可选环境变量
| 变量名 | 描述 | 默认值 |
|--------|------|--------|
| `CONFIG_USERNAME` | Config Server用户名 | `config-admin` |
| `CONFIG_PASSWORD` | Config Server密码 | `config-admin-2024` |
| `CONFIG_ENCRYPT_KEY` | 配置加密密钥 | `platform-config-secret-key-2024` |
| `NACOS_SERVER_ADDR` | Nacos服务地址 | `localhost:8848` |
| `CONFIG_GITLAB_ENABLED` | 是否启用GitLab | `true` |

## API 接口

### 配置管理API

#### 刷新配置
```http
POST /config/management/refresh/{application}
Authorization: Basic Y29uZmlnLWFkbWluOmNvbmZpZy1hZG1pbi0yMDI0
```

#### 切换配置源
```http
POST /config/management/switch-source?source=native
Authorization: Basic Y29uZmlnLWFkbWluOmNvbmZpZy1hZG1pbi0yMDI0
```

#### 获取配置状态
```http
GET /config/management/status
Authorization: Basic Y29uZmlnLWFkbWluOmNvbmZpZy1hZG1pbi0yMDI0
```

#### 同步配置到GitLab
```http
POST /config/management/sync-to-gitlab
Authorization: Basic Y29uZmlnLWFkbWluOmNvbmZpZy1hZG1pbi0yMDI0
```

#### 备份配置
```http
POST /config/management/backup
Authorization: Basic Y29uZmlnLWFkbWluOmNvbmZpZy1hZG1pbi0yMDI0
```

### 标准Config Server API

#### 获取配置
```http
# 获取指定应用配置
GET /config/{application}/{profile}[/{label}]

# 示例
GET /config/platform-api/dev/main
GET /config/platform-api/test
GET /config/platform-gateway/pub/v1.0
```

#### 配置加密解密
```http
# 加密数据
POST /config/encrypt
Content-Type: text/plain
Body: your-sensitive-data

# 解密数据
POST /config/decrypt
Content-Type: text/plain
Body: {cipher}encrypted-data-here
```

## 客户端配置

### Spring Boot 应用配置

#### bootstrap.yml
```yaml
spring:
  application:
    name: platform-api
  cloud:
    config:
      uri: http://config-server:8888/config
      username: config-admin
      password: config-admin-2024
      profile: dev
      label: main
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1
```

#### 依赖配置
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

### 配置刷新
```java
@RestController
@RefreshScope
public class ConfigTestController {
    
    @Value("${platform.api.version:unknown}")
    private String apiVersion;
    
    @GetMapping("/config")
    public String getConfig() {
        return "Current API version: " + apiVersion;
    }
}
```

## 运维管理

### 监控检查

#### 健康检查
```bash
# 检查服务健康状态
curl http://localhost:8888/config/actuator/health

# 检查配置仓库状态
curl http://localhost:8888/config/actuator/configprops

# 检查环境变量
curl http://localhost:8888/config/actuator/env
```

#### 性能指标
```bash
# 获取性能指标
curl http://localhost:8888/config/actuator/metrics

# 获取Prometheus格式指标
curl http://localhost:8888/config/actuator/prometheus
```

### 日志管理

#### 日志级别调整
```bash
# 动态调整日志级别
curl -X POST http://localhost:8888/config/actuator/loggers/com.platform.config \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

#### 日志查看
```bash
# Docker环境
docker logs platform-config-server

# Kubernetes环境
kubectl logs -f deployment/platform-config-server -n platform

# 本地文件
tail -f /app/logs/config-server.log
```

### 备份恢复

#### 手动备份
```bash
# 备份配置
curl -X POST http://localhost:8888/config/management/backup \
  -u config-admin:config-admin-2024

# 备份目录结构
/app/config-backup/
├── backup_20241201_143022/
│   ├── native/          # 本地配置备份
│   └── gitlab/          # GitLab配置备份
```

#### 恢复配置
```bash
# 从备份恢复
cp -r /app/config-backup/backup_20241201_143022/native/* /app/config/

# 刷新配置
curl -X POST http://localhost:8888/config/management/refresh/platform-api \
  -u config-admin:config-admin-2024
```

## 故障排除

### 常见问题

#### 1. GitLab连接失败
```bash
# 检查网络连通性
curl -I https://gitlab.example.com

# 检查认证信息
git ls-remote https://username:password@gitlab.example.com/config/platform-config.git

# 切换到本地模式
curl -X POST http://localhost:8888/config/management/switch-source?source=native \
  -u config-admin:config-admin-2024
```

#### 2. 配置获取失败
```bash
# 检查配置文件路径
ls -la /app/config/platform-api/dev/

# 检查配置格式
yaml-lint /app/config/platform-api/dev/application-dev.yml

# 查看详细错误日志
kubectl logs deployment/platform-config-server -n platform --tail=100
```

#### 3. 服务启动失败
```bash
# 检查端口占用
netstat -tulpn | grep 8888

# 检查内存使用
free -h

# 检查磁盘空间
df -h
```

### 调试模式

#### 启用调试日志
```yaml
# application.yml
logging:
  level:
    com.platform.config: DEBUG
    org.springframework.cloud.config: DEBUG
    org.springframework.security: DEBUG
```

#### JVM调试参数
```bash
export JAVA_OPTS="-Xms512m -Xmx1g -Ddebug=true -Dspring.profiles.active=dev"
```

## 安全配置

### 配置加密
```bash
# 加密敏感配置
curl -X POST http://localhost:8888/config/encrypt \
  -u config-admin:config-admin-2024 \
  -d "your-sensitive-data"

# 在配置文件中使用
spring:
  datasource:
    password: '{cipher}AQBFrz8w7QczLJ5Lj9CK8DH6...'
```

### 网络安全
- 使用HTTPS传输
- 配置防火墙规则
- 启用访问日志审计
- 定期更新密码和密钥

## 性能优化

### JVM调优
```bash
export JAVA_OPTS="-Xms1g -Xmx2g -XX:NewRatio=1 -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"
```

### 缓存优化
```yaml
spring:
  cloud:
    config:
      server:
        git:
          clone-on-start: true
          force-pull: true
          timeout: 30
```

### 连接池优化
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 版本发布

### 构建发布
```bash
# 打包应用
mvn clean package -DskipTests

# 构建镜像
./scripts/deploy.sh build -t v1.0.0

# 发布到仓库
docker push platform/config-server:v1.0.0
```

### 滚动更新
```bash
# Kubernetes滚动更新
kubectl set image deployment/platform-config-server \
  config-server=platform/config-server:v1.0.1 -n platform

# 监控更新进度
kubectl rollout status deployment/platform-config-server -n platform
```

## 开发指南

### 本地开发
```bash
# 启动开发环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 热重载
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### 集成测试
```bash
# 运行集成测试
mvn test -Dtest=ConfigServerIntegrationTest

# 测试配置获取
curl http://localhost:8888/config/platform-api/dev
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目主页: https://github.com/platform/config-server
- 问题反馈: https://github.com/platform/config-server/issues
- 邮箱: platform-team@example.com