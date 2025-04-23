# Platform Common（平台公共组件）

## 模块简介

Platform Common是平台的基础公共组件模块，提供了所有其他微服务模块共享的工具类、模型、异常处理和配置。本模块设计为可以被所有其他服务依赖并重用，从而减少代码重复，统一错误处理方式和响应格式。

## 主要功能

- **公共模型（Model）**：标准响应对象、数据传输对象（DTO）等
- **异常处理**：全局异常处理器和自定义异常类
- **工具类**：常用工具方法集合
- **切面（Aspect）**：资源监控和其他横切关注点
- **常量定义**：系统级常量和配置值

## 目录结构

```
platform-common/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── platform/
│                   └── common/
│                       ├── aspect/            # AOP切面定义
│                       │   └── ResourceMonitorAspect.java   # 资源监控切面
│                       ├── config/            # 公共配置类
│                       ├── constants/         # 常量定义
│                       │   └── CommonConstants.java         # 通用常量类
│                       ├── exception/         # 异常处理
│                       │   ├── BusinessException.java       # 业务异常
│                       │   ├── ForbiddenException.java      # 权限拒绝异常
│                       │   ├── GlobalExceptionHandler.java  # 全局异常处理器
│                       │   ├── ResourceNotFoundException.java # 资源未找到异常
│                       │   ├── ServerBusyException.java     # 服务器繁忙异常
│                       │   └── UnauthorizedException.java   # 未授权异常
│                       ├── model/             # 数据模型
│                       │   └── Result.java                  # 统一响应模型
│                       └── utils/             # 工具类
│                           └── ResourceMonitorUtil.java     # 资源监控工具
└── pom.xml                # Maven配置文件
```

## 使用方法

要在其他服务中使用此模块，请在项目的pom.xml中添加以下依赖：

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>platform-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

## 主要组件说明

### 统一响应模型（Result）

所有API响应都应使用`Result`类进行封装，保证响应格式一致：

```java
// 成功响应
Result.success(data);

// 失败响应
Result.fail(errorCode, errorMessage);

// 服务器错误
Result.error(message);

// 客户端错误
Result.clientError(message);
```

### 资源监控

资源监控组件用于检测服务的CPU和内存使用情况，防止服务过载：

- `ResourceMonitorUtil`：提供CPU和内存使用率检测
- `ResourceMonitorAspect`：自动拦截控制器请求，当资源过载时拒绝请求

### 全局异常处理

`GlobalExceptionHandler`提供全局异常处理，将异常转换为统一的API响应格式。

自定义异常类型：
- `BusinessException`：业务逻辑异常
- `ResourceNotFoundException`：资源未找到异常
- `UnauthorizedException`：未授权异常
- `ForbiddenException`：权限不足异常
- `ServerBusyException`：服务器繁忙异常

## 设计说明

此模块遵循以下设计原则：

1. **共享重用原则**（CRP）：提取所有服务通用功能
2. **接口隔离原则**（ISP）：模块接口粒度合适，避免过度依赖
3. **单一职责原则**（SRP）：每个类只有一个变更的原因
4. **开闭原则**（OCP）：通过扩展而非修改来添加新功能

## 开发指南

扩展此模块时，请遵循以下准则：

1. 只添加真正需要共享的功能
2. 避免添加只适用于特定服务的代码
3. 保持向后兼容性
4. 编写完整的单元测试
5. 更新文档说明新增功能

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基础工具类
  - 统一响应模型
  - 异常处理框架
  - 资源监控组件
