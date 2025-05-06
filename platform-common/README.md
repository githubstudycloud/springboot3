# Platform Common

## 模块概述
`platform-common`模块提供了整个项目的通用工具类和基础组件，是所有其他模块的基础依赖。该模块封装了常用的工具方法、异常处理机制、通用常量等，以减少代码重复并提高开发效率。

## 主要功能

### 通用工具类
- 日期时间处理工具 (`DateTimeUtil`)
- 字符串操作工具 (`StringUtil`)
- JSON处理工具 (`JsonUtil`)
- 分页工具 (`PageResult`, `Pagination`)
- 请求上下文工具 (`RequestContextUtil`)

### 通用响应结构
- 统一响应模型 (`Result<T>`)
- 统一错误码枚举 (`ErrorCode`)

### 异常处理机制
- 业务异常基类 (`BusinessException`)
- 全局异常处理器 (`GlobalExceptionHandler`)

### 常量定义
- 系统常量 (`SystemConstants`)
- 时间格式常量 (`DateTimeConstants`)

## 目录结构
```
platform-common
├── src/main/java
│   └── com/example/platform/common
│       ├── constants           // 常量定义
│       ├── enums               // 枚举定义
│       ├── exception           // 异常处理
│       ├── model               // 模型定义
│       │   └── response        // 响应模型
│       └── utils               // 工具类
└── pom.xml
```

## 使用方式
其他模块只需在pom.xml中添加对platform-common的依赖即可使用其提供的所有功能：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-common</artifactId>
</dependency>
```

## 维护指南
1. 该模块应保持精简，只包含真正通用的功能
2. 添加新功能时应考虑其普适性，特定业务逻辑不应放在此模块中
3. 工具类应设计为无状态的，避免使用实例变量
4. 异常类应提供清晰的错误信息和错误码
5. 定期重构和优化代码，确保高质量和高性能
