# Platform Auth Service

## 模块概述
`platform-auth-service`模块是统一认证授权服务，基于DDD（领域驱动设计）和六边形架构实现。该模块负责用户认证、权限管理、角色分配和单点登录等核心安全功能，为整个系统提供统一的安全保障。

## 主要功能
- 用户认证（登录/注册/登出）
- JWT令牌管理
- 用户权限与角色管理
- 密码策略管理
- 多租户支持
- 安全审计日志

## 技术特点
- 基于JWT的无状态认证
- 遵循DDD和六边形架构设计
- 分层设计：领域层、应用层、基础设施层、接口层
- 领域模型与框架解耦
- 使用Redis实现令牌黑名单和刷新令牌存储

## 目录结构
```
platform-auth-service
├── src/main/java
│   └── com/example/platform/auth
│       ├── AuthServiceApplication.java     // 应用入口
│       ├── application                     // 应用服务层
│       │   ├── dto                         // 数据传输对象
│       │   └── service                     // 应用服务实现
│       ├── domain                          // 领域层
│       │   ├── model                       // 领域模型
│       │   ├── repository                  // 仓储接口
│       │   └── service                     // 领域服务
│       ├── infrastructure                  // 基础设施层
│       │   ├── config                      // 配置类
│       │   ├── mapper                      // 对象映射器
│       │   ├── persistence                 // 持久化实现
│       │   │   ├── entity                  // 数据库实体
│       │   │   └── repository              // JPA仓储实现
│       │   └── security                    // 安全组件
│       └── interfaces                      // 接口层
│           └── controller                  // API控制器
├── src/main/resources
│   ├── application.yml                     // 应用配置
│   └── bootstrap.yml                       // 启动配置
└── pom.xml
```

## 领域模型
主要领域模型包括：
- User: 用户实体
- Role: 角色实体
- Permission: 权限实体
- Tenant: 租户实体

## API接口
认证接口：
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/refresh-token` - 刷新令牌

用户管理接口：
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `POST /api/users/change-password` - 修改密码

角色权限接口：
- `GET /api/roles` - 获取角色列表
- `GET /api/permissions` - 获取权限列表
- `POST /api/roles/{roleId}/permissions` - 分配权限到角色

## 配置说明
关键配置项说明（位于application.yml）：

```yaml
security:
  jwt:
    secret-key: your-secret-key           # JWT密钥
    token-validity-in-seconds: 86400      # 令牌有效期（秒）
    refresh-token-validity-days: 30       # 刷新令牌有效期（天）
```

## 安全实现
1. 基于Spring Security实现认证框架
2. 自定义JWT过滤器处理令牌认证
3. 使用BCrypt进行密码加密
4. 基于RBAC（基于角色的访问控制）模型实现权限控制

## 启动说明
1. 确保Redis服务已启动（用于令牌存储）
2. 确保MySQL服务已启动（用于用户数据存储）
3. 确保注册中心服务已启动
4. 启动服务：
   ```bash
   mvn spring-boot:run
   ```
5. 默认端口：8081
