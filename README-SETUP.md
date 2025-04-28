# Spring Boot 3.2.9 项目使用指南

本项目为基于Spring Boot 3.2.9的现代化微服务平台基础模块，包含了以下核心组件：

1. **platform-dependencies** - 统一管理项目所有依赖及其版本
2. **platform-common** - 提供通用工具类、常量、基础DTO等
3. **platform-framework** - 框架核心抽象，提供可扩展的基础能力

## 环境要求

- JDK 21+
- Maven 3.8.0+
- IDE 推荐使用IntelliJ IDEA 2023.3+

## 重要说明

### POM文件XML标签问题

当前POM文件中部分XML标签有问题，需要修复：

1. 主要需要替换的标签：
   - `<n>` 应替换为 `<name>`

2. 修复方法：
   - 方式一：手动修改 - 将项目中所有 POM 文件中的 `<n>` 标签修改为 `<name>` 标签
   - 方式二：使用正则替换 - 在 IDE 中使用正则表达式全局替换 `<n>` -> `<name>` 和 `</n>` -> `</name>`
   - 方式三：使用提供的 `correct-pom.xml` 作为参考，修改各模块的 POM 文件

### 版本兼容性

本项目使用Spring Boot 3.2.9版本，主要依赖版本配置如下：

- Spring Boot: 3.2.9
- Spring Cloud: 2023.0.2
- Java: 21
- Jackson: 2.16.2
- Lombok: 1.18.32

请确保您的环境与这些版本兼容。

## 快速开始

1. 修复POM文件中的XML标签问题
2. 编译项目：`mvn clean install`
3. 按照架构设计文档开发后续模块

## 技术支持

如有问题，请参考项目文档或联系技术支持团队。