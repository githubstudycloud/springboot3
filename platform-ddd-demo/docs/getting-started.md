# DDD项目快速上手指南

## 项目介绍

本项目是一个基于Spring Boot 3.x的领域驱动设计(DDD)示例项目，实现了一个订单管理系统，用于演示如何在实际项目中应用DDD架构思想。

## 快速启动

1. 克隆项目：`git clone https://your-repo-url.git`
2. 构建项目：`mvn clean install`
3. 运行项目：`mvn spring-boot:run`
4. 访问API：`http://localhost:8080/ddd-demo/`
5. 访问H2控制台：`http://localhost:8080/ddd-demo/h2-console`
   - JDBC URL: `jdbc:h2:mem:ddddb`
   - 用户名: `sa`
   - 密码: (留空)

## 技术栈

- Spring Boot 3.x
- Spring Data JPA
- H2数据库
- Lombok
- MapStruct
- Hutool

## 核心功能

- 订单管理：创建、确认、支付、取消、完成订单
- 库存管理：库存预留、释放、确认
- 跨领域协调：订单与库存协作
- 领域事件：事件发布与处理
- CQRS模式：命令和查询分离

## 模块说明

- `platform-common`: 通用工具类和基础组件
- `platform-dependencies`: 依赖管理
- `platform-framework`: 基础框架封装
- `platform-ddd-demo`: DDD示例模块

## 领域模型概述

### 订单聚合

- `Order`: 订单聚合根
- `OrderItem`: 订单项实体
- `OrderId`, `CustomerId`, `ProductId`: 值对象
- `OrderStatus`: 订单状态枚举

### 库存聚合

- `Inventory`: 库存聚合根
- `InventoryId`: 值对象

## 领域事件

- `OrderCreatedEvent`: 订单创建事件
- `OrderStatusChangedEvent`: 订单状态变更事件

## 基础组件

- `ValueObject`: 值对象基类
- `Entity`: 实体基类
- `AggregateRoot`: 聚合根基类
- `DomainEvent`: 领域事件基类
- `DomainEventPublisher`: 领域事件发布器

## 常用API

### 订单管理

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

# 获取订单详情
GET /api/orders/{orderId}
```

### 订单处理

```
# 确认订单并预留库存
POST /api/order-processing/{orderId}/confirm-with-inventory

# 支付订单并完成库存预留
POST /api/order-processing/{orderId}/pay-and-complete

# 取消订单并释放库存
POST /api/order-processing/{orderId}/cancel-and-release
```

### 订单查询

```
# 获取所有订单摘要
GET /api/queries/orders

# 根据状态查询订单
GET /api/queries/orders/status/{status}

# 获取订单详情
GET /api/queries/orders/{orderId}
```

## 参考文档

- [领域驱动设计参考](docs/architecture-guide.md)
- [详细架构说明](docs/architecture-guide.md)
- [API接口文档](docs/api-docs.md)

## 开发规范

- 包命名遵循DDD架构分层：`domain`, `application`, `infrastructure`, `interfaces`
- 领域层代码不依赖外部框架，保持纯粹的业务逻辑
- 使用值对象而非原始类型来表达领域概念
- 通过领域事件实现聚合间的通信

## 注意事项

- 确保理解DDD核心概念：实体、值对象、聚合根、领域服务、仓储
- 关注领域边界的划分和聚合设计
- 领域模型应该反映业务语言和概念
- 应用服务应该保持简单，主要用于协调领域对象
