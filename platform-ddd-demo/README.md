# DDD架构示例项目

这是一个基于Spring Boot 3.x和领域驱动设计(DDD)架构的示例项目，展示了如何使用DDD设计思想构建现代化Java应用程序。

## 项目架构

项目采用经典的DDD分层架构:

### 1. 领域层 (Domain Layer)

- 领域模型: 实体、值对象、聚合根
- 领域服务: 处理跨聚合的业务逻辑
- 领域事件: 领域事件定义和处理
- 仓储接口: 定义仓储的抽象接口

### 2. 应用层 (Application Layer)

- 应用服务: 协调领域对象完成用户用例
- 命令/查询: 封装用户操作
- DTO: 数据传输对象

### 3. 基础设施层 (Infrastructure Layer)

- 仓储实现: 基于JPA的仓储实现
- 外部系统集成: 与外部系统的集成实现
- 技术服务: 事务、安全等技术细节

### 4. 接口层 (Interfaces Layer)

- REST控制器: 处理HTTP请求
- 异常处理: 统一异常处理

## 示例业务领域

本示例项目实现了一个简单的订单管理系统，包括：

- 创建订单
- 添加订单项
- 订单状态流转 (创建、确认、支付、发货、交付、完成、取消)
- 订单查询

## 核心概念

### 实体 (Entity)

具有唯一标识的对象，如`Order`

### 值对象 (Value Object)

没有唯一标识，通过所有属性值来判断相等性的对象，如`OrderId`，`CustomerId`

### 聚合根 (Aggregate Root)

聚合是一组相关对象的集合，被视为数据更改的单元。聚合根是聚合的入口点，示例中的`Order`是一个聚合根。

### 仓储 (Repository)

提供访问聚合根的方法，隐藏持久化细节。

### 领域服务 (Domain Service)

封装不适合放在实体或值对象中的领域逻辑。

## 运行项目

1. 确保已安装JDK 17+和Maven
2. 克隆项目
3. 运行 `mvn spring-boot:run`
4. 访问 http://localhost:8080/ddd-demo/

## H2数据库控制台

项目使用H2内存数据库，可以通过以下URL访问H2控制台:

- URL: http://localhost:8080/ddd-demo/h2-console
- JDBC URL: jdbc:h2:mem:ddddb
- 用户名: sa
- 密码: (留空)

## API接口

### 创建订单

```
POST /api/orders
{
  "customerId": "customer123"
}
```

### 添加订单项

```
POST /api/orders/items
{
  "orderId": "order123",
  "productId": "product456",
  "quantity": 2,
  "unitPrice": 99.99
}
```

### 确认订单

```
POST /api/orders/{orderId}/confirm
```

### 支付订单

```
POST /api/orders/{orderId}/pay
```

### 取消订单

```
POST /api/orders/{orderId}/cancel
```

### 获取订单详情

```
GET /api/orders/{orderId}
```

### 获取客户的所有订单

```
GET /api/orders/customer/{customerId}
```

## 最佳实践

- 使用Factory创建复杂聚合
- 保持聚合边界小而清晰
- 通过Domain Events实现跨聚合业务流程
- 使用CQRS模式分离读写操作
- 保持领域模型的纯粹性，避免将技术细节泄漏到领域层
