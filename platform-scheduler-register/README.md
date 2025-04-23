# Platform Scheduler Register（平台调度器注册服务）

## 模块简介

Platform Scheduler Register是平台调度系统的任务注册和管理服务，负责任务的创建、修改、删除和验证。该服务为用户和其他系统组件提供了管理定时任务的统一接口，确保任务定义的有效性和一致性。

## 主要功能

- **任务注册**：创建新的调度任务
- **任务管理**：更新和删除现有任务
- **任务验证**：验证任务参数和调度表达式
- **任务模板**：管理任务模板
- **批量操作**：支持批量任务注册和管理
- **权限控制**：基于角色的任务管理权限

## 与调度器生态系统的协作

本服务是调度器三件套之一：

1. **platform-scheduler**：核心调度引擎，负责任务触发和执行
2. **platform-scheduler-register**（本模块）：任务注册服务，负责任务定义管理
3. **platform-scheduler-query**：任务查询服务，负责状态查询和历史记录

通过职责分离，提高了系统的可扩展性和维护性。

## 目录结构

```
platform-scheduler-register/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── scheduler/
│       │               └── register/
│       │                   ├── config/            # 配置类
│       │                   ├── controller/        # REST控制器
│       │                   ├── service/           # 业务逻辑
│       │                   ├── repository/        # 数据访问
│       │                   ├── model/             # 数据模型
│       │                   ├── validator/         # 参数验证器
│       │                   └── exception/         # 异常处理
│       └── resources/
│           └── application.yml        # 应用配置文件
└── pom.xml                            # Maven配置文件
```

## API接口

### 任务管理接口

```
POST   /api/v1/tasks                  # 创建新任务
GET    /api/v1/tasks                  # 查询任务列表
GET    /api/v1/tasks/{taskId}         # 获取任务详情
PUT    /api/v1/tasks/{taskId}         # 更新任务
DELETE /api/v1/tasks/{taskId}         # 删除任务
```

### 模板管理接口

```
POST   /api/v1/templates              # 创建任务模板
GET    /api/v1/templates              # 获取模板列表
GET    /api/v1/templates/{id}         # 获取模板详情
PUT    /api/v1/templates/{id}         # 更新模板
DELETE /api/v1/templates/{id}         # 删除模板
POST   /api/v1/templates/{id}/tasks   # 基于模板创建任务
```

### 批量操作接口

```
POST   /api/v1/tasks/batch            # 批量创建任务
PUT    /api/v1/tasks/batch            # 批量更新任务
DELETE /api/v1/tasks/batch            # 批量删除任务
```

## 数据模型

### 任务模型（Task）

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "cronExpression": "string",
  "taskClass": "string",
  "taskData": "json string",
  "status": "ACTIVE|PAUSED|DISABLED",
  "priority": "integer",
  "maxRetryCount": "integer",
  "createdBy": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp",
  "timeoutSeconds": "integer"
}
```

### 任务模板（Template）

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "taskClass": "string",
  "taskDataSchema": "json schema string",
  "defaultCronExpression": "string",
  "defaultPriority": "integer",
  "defaultMaxRetryCount": "integer",
  "defaultTimeoutSeconds": "integer"
}
```

## 任务验证

注册服务在创建或更新任务时执行多项验证：

1. **必填字段验证**：确保必要参数存在
2. **Cron表达式验证**：确保调度表达式有效
3. **任务类验证**：确保指定的任务类存在
4. **任务数据验证**：确保任务数据符合预期格式
5. **业务规则验证**：应用特定的业务验证规则

## 事件发布

注册服务在任务状态变更时发布事件：

- **任务创建事件**：新任务创建时
- **任务更新事件**：任务定义变更时
- **任务删除事件**：任务被移除时

这些事件通过消息队列传递给其他服务，特别是调度引擎，以更新其内部状态。

## 安全控制

任务注册服务实现了细粒度的访问控制：

1. **认证**：要求有效的用户令牌
2. **授权**：基于角色的权限控制
3. **审计**：记录所有任务管理操作
4. **数据隔离**：按租户或用户组隔离任务数据

## 性能优化

为支持高并发任务管理，实现了以下优化：

1. **缓存**：任务定义和模板缓存
2. **批处理**：优化批量操作性能
3. **异步处理**：非关键操作异步执行
4. **数据库优化**：索引优化和查询优化

## 监控指标

该服务暴露以下关键指标：

- **注册速率**：每秒任务注册数
- **API延迟**：接口响应时间
- **验证失败率**：任务验证失败比例
- **活跃任务数**：系统中活跃任务总数

## 部署建议

注册服务是任务管理的入口，建议：

1. **高可用部署**：至少2个实例
2. **数据库冗余**：主从复制或集群
3. **网络安全**：内部网络部署，避免直接暴露
4. **会话亲和性**：长连接请求保持会话亲和

## 开发指南

### 自定义验证器

可以通过实现`TaskValidator`接口创建自定义验证器：

```java
@Component
public class CustomTaskValidator implements TaskValidator {
    @Override
    public void validate(TaskDTO task) throws ValidationException {
        // 自定义验证逻辑
        if (invalidCondition) {
            throw new ValidationException("Invalid task configuration");
        }
    }
    
    @Override
    public int getOrder() {
        // 验证器执行顺序
        return 100;
    }
}
```

### 事件监听器

可以实现事件监听器响应任务变更：

```java
@Component
public class TaskEventListener {
    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        // 处理任务创建事件
    }
    
    @EventListener
    public void onTaskUpdated(TaskUpdatedEvent event) {
        // 处理任务更新事件
    }
}
```

## 故障排除

常见问题及解决方案：

1. **验证失败**：检查任务参数和cron表达式
2. **任务未生效**：确认任务状态和调度引擎连接
3. **权限拒绝**：验证用户权限配置
4. **系统过载**：考虑增加实例或优化数据库查询
5. **数据不一致**：检查事件发布和消费状态

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基本任务管理功能
  - 任务模板支持
  - 参数验证
  - 事件发布
  - 安全控制
