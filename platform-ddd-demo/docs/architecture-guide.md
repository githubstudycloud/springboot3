# Spring Boot 3.x DDD架构示例

## 项目概述

这是一个基于Spring Boot 3.x和领域驱动设计(DDD)的示例项目，展示了如何在实际项目中应用DDD架构思想。该项目实现了一个简单的订单管理系统，包括订单创建、确认、支付、发货、交付和库存管理等功能。

## DDD架构概念

领域驱动设计(Domain-Driven Design, DDD)是一种软件开发方法论，它专注于核心领域逻辑，强调业务领域概念与代码模型的一致性。DDD的主要优势包括：

1. **更好地表达业务复杂性**：通过领域模型清晰地表达复杂的业务规则和逻辑
2. **业务与技术的协同**：促进技术团队和业务专家之间的沟通
3. **可扩展性和可维护性**：清晰的边界划分使系统更容易扩展和维护
4. **关注核心问题**：将重点放在业务问题的解决上，而非技术实现

## 项目架构

项目采用经典的DDD分层架构:

### 1. 领域层 (Domain Layer)

领域层是DDD中最核心的部分，包含业务逻辑和规则。主要包括：

- **实体（Entity）**：具有唯一标识的对象，如`Order`
- **值对象（Value Object）**：没有唯一标识，通过属性值判断相等性的对象，如`OrderId`、`CustomerId`
- **聚合根（Aggregate Root）**：聚合的入口点，如`Order`
- **领域服务（Domain Service）**：处理跨实体业务逻辑，如`OrderDomainService`
- **领域事件（Domain Event）**：捕获领域中的事件，如`OrderCreatedEvent`
- **仓储接口（Repository Interface）**：定义持久化接口，如`OrderRepository`

### 2. 应用层 (Application Layer)

应用层负责协调领域对象完成用户用例。主要包括：

- **应用服务（Application Service）**：如`OrderApplicationService`
- **DTO（Data Transfer Object）**：跨层数据传输对象，如`OrderDTO`
- **命令/查询（Command/Query）**：封装用户操作，如`CreateOrderCommand`

### 3. 基础设施层 (Infrastructure Layer)

基础设施层提供技术实现细节。主要包括：

- **仓储实现（Repository Implementation）**：如`OrderRepositoryImpl`
- **ORM映射（ORM Mapping）**：如`OrderEntity`
- **事件发布器（Event Publisher）**：如`SpringDomainEventPublisher`

### 4. 接口层 (Interfaces Layer)

接口层负责与外部系统交互。主要包括：

- **REST控制器（REST Controller）**：如`OrderController`
- **异常处理（Exception Handler）**：如`GlobalExceptionHandler`

## 核心领域模型

### 订单领域

```
Order (聚合根)
 |-- OrderId (值对象)
 |-- CustomerId (值对象)
 |-- OrderStatus (枚举)
 |-- OrderItem (实体)
      |-- ProductId (值对象)
      |-- quantity
      |-- unitPrice
```

### 库存领域

```
Inventory (聚合根)
 |-- InventoryId (值对象)
 |-- ProductId (值对象)
 |-- availableQuantity
 |-- reservedQuantity
```

## 领域事件

项目实现了基于Spring Event的领域事件机制：

1. **OrderCreatedEvent**：订单创建事件
2. **OrderStatusChangedEvent**：订单状态变更事件

## 跨领域服务

**OrderInventoryService**是一个跨领域服务示例，协调订单和库存两个聚合之间的业务逻辑：

1. 确认订单并预留库存
2. 取消订单并释放库存
3. 支付订单并完成库存预留

## CQRS模式

项目实现了简单的CQRS（命令查询职责分离）模式：

1. 命令部分：通过`OrderController`和应用服务提供
2. 查询部分：通过`OrderQueryController`和`OrderQueryService`提供

## 项目结构

```
com.example.demo
 |-- application
 |    |-- command (命令对象)
 |    |-- dto (数据传输对象)
 |    |-- event (事件监听器)
 |    |-- query (查询服务)
 |    |-- service (应用服务)
 |
 |-- domain
 |    |-- event (领域事件)
 |    |-- factory (工厂)
 |    |-- model (领域模型)
 |    |    |-- common (通用基类)
 |    |    |-- inventory (库存聚合)
 |    |    |-- order (订单聚合)
 |    |
 |    |-- repository (仓储接口)
 |    |-- service (领域服务)
 |
 |-- infrastructure
 |    |-- entity (持久化实体)
 |    |-- event (事件发布实现)
 |    |-- repository
 |         |-- impl (仓储实现)
 |         |-- jpa (JPA接口)
 |
 |-- interfaces
      |-- rest (REST控制器)
      |-- exception (异常处理)
```

## 设计原则

1. **领域模型的独立性**：领域模型不依赖于基础设施层
2. **富领域模型**：业务逻辑主要在领域模型中实现
3. **值对象的不可变性**：所有值对象一旦创建不可修改
4. **聚合根管理边界**：外部只能通过聚合根访问聚合内的实体
5. **仓储的抽象**：仓储接口在领域层定义，在基础设施层实现
6. **领域事件**：通过事件实现领域模型间的松耦合通信

## API接口示例

### 命令接口

```
# 创建订单
POST /api/orders
{
  "customerId": "customer123"
}

# 添加订单项
POST /api/orders/items
{
  "orderId": "order123",
  "productId": "product456",
  "quantity": 2,
  "unitPrice": 99.99
}

# 确认订单并预留库存
POST /api/order-processing/{orderId}/confirm-with-inventory

# 支付订单并完成库存预留
POST /api/order-processing/{orderId}/pay-and-complete

# 取消订单并释放库存
POST /api/order-processing/{orderId}/cancel-and-release
```

### 查询接口

```
# 获取所有订单摘要
GET /api/queries/orders

# 根据状态查询订单
GET /api/queries/orders/status/{status}

# 获取订单详情
GET /api/queries/orders/{orderId}
```

## 使用指南

1. 了解领域模型：从`domain/model`包开始了解核心领域概念
2. 理解应用服务：查看`application/service`了解如何组织用例
3. 分析REST接口：通过`interfaces/rest`了解API设计
4. 学习领域事件：通过`domain/event`了解事件通信机制
5. 掌握CQRS模式：比较`OrderController`和`OrderQueryController`

## 最佳实践

1. **统一语言**：使用业务术语命名类和方法
2. **小聚合**：保持聚合边界小而清晰
3. **值对象优先**：尽可能使用值对象而非基本类型
4. **领域事件**：通过事件实现领域间的通信
5. **工厂方法**：使用工厂方法创建复杂对象
6. **仓储封装**：通过仓储抽象持久化细节
7. **应用服务薄**：保持应用服务简单，主要用于协调
8. **CQRS分离**：命令和查询使用不同的模型和服务

## 扩展方向

1. 引入领域事件存储
2. 实现事件溯源
3. 添加更复杂的业务规则
4. 实现分布式事务
5. 集成消息队列进行异步处理
