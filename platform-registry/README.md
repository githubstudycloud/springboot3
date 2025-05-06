# Platform Registry

## 模块概述
`platform-registry`模块是基于Nacos实现的服务注册中心，负责微服务的注册与发现，是整个微服务架构的核心基础设施。该模块为所有微服务提供了服务注册、发现、健康检查和元数据管理等功能。

## 主要功能
- 服务注册与发现
- 服务健康检查
- 服务元数据管理
- 服务实例列表查询

## 技术特点
- 基于Nacos实现服务注册中心
- 支持服务健康状态实时监控
- 提供REST API查询注册服务信息
- 支持集群部署和高可用配置

## 目录结构
```
platform-registry
├── src/main/java
│   └── com/example/platform/registry
│       ├── RegistryApplication.java       // 应用入口
│       ├── config                        // 配置类
│       ├── controller                    // 控制器
│       └── service                       // 服务实现
├── src/main/resources
│   ├── application.yml                   // 应用配置
│   └── bootstrap.yml                     // 启动配置
└── pom.xml
```

## 配置说明
关键配置项说明（位于application.yml）：

```yaml
spring:
  application:
    name: platform-registry
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848       # Nacos服务地址
        namespace: public                 # 命名空间
        group: DEFAULT_GROUP              # 服务分组
```

## 启动说明
1. 确保已安装并启动Nacos服务器
2. 修改application.yml中Nacos服务地址为实际地址
3. 启动服务：
   ```bash
   mvn spring-boot:run
   ```
4. 访问管理界面：http://localhost:8848/nacos (默认账号密码：nacos/nacos)

## 高可用部署
在生产环境中，建议采用Nacos集群部署模式，配置步骤：
1. 配置Nacos集群配置文件
2. 修改application.yml中的server-addr为集群地址列表
3. 配置负载均衡以实现高可用

## API接口
- `GET /registry/services` - 获取所有注册的服务列表
- `GET /registry/service/{serviceName}` - 获取指定服务的实例列表
- `GET /registry/health` - 获取注册中心健康状态
