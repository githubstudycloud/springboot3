# platform-bom

## 项目概述

`platform-bom` (Bill of Materials) 是企业级数据平台的版本定义模块，负责统一管理平台内所有组件的版本号。该模块通过Maven的BOM机制，确保整个平台使用一致且兼容的组件版本，有效解决依赖冲突问题。

## 主要职责

1. 统一定义平台所有内部模块的版本号
2. 确保技术栈各组件间的版本兼容性
3. 简化依赖管理，防止版本不一致
4. 作为平台对外发布的版本基准

## 技术要点

- Maven BOM (Bill of Materials) 机制
- 依赖版本管理
- 版本兼容性测试
- 依赖仲裁 (Dependency Mediation)

## 关键配置

### POM文件结构

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../platform-parent/pom.xml</relativePath>
    </parent>
    
    <artifactId>platform-bom</artifactId>
    <packaging>pom</packaging>
    <name>Platform BOM</name>
    <description>Bill of Materials for Enterprise Data Platform</description>
    
    <properties>
        <!-- 基础依赖版本 -->
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <commons-lang3.version>3.14.0</commons-lang3.version>
        <commons-collections4.version>4.4</commons-collections4.version>
        <commons-io.version>2.15.1</commons-io.version>
        <guava.version>32.1.3-jre</guava.version>
        
        <!-- 数据库相关 -->
        <mybatis-spring-boot.version>3.0.2</mybatis-spring-boot.version>
        <mybatis-plus.version>3.5.4.1</mybatis-plus.version>
        <dynamic-datasource.version>4.2.0</dynamic-datasource.version>
        <shardingsphere.version>5.4.1</shardingsphere.version>
        <mysql-connector.version>8.0.33</mysql-connector.version>
        <druid.version>1.2.20</druid.version>
        <mongodb-driver.version>4.11.1</mongodb-driver.version>
        
        <!-- 缓存相关 -->
        <redisson.version>3.24.3</redisson.version>
        <caffeine.version>3.1.8</caffeine.version>
        
        <!-- 消息队列 -->
        <kafka-clients.version>3.5.1</kafka-clients.version>
        <spring-kafka.version>3.1.0</spring-kafka.version>
        <rabbitmq.version>2.4.2</rabbitmq.version>
        
        <!-- 日志和监控 -->
        <logback.version>1.4.11</logback.version>
        <log4j2.version>2.22.0</log4j2.version>
        <slf4j.version>2.0.9</slf4j.version>
        <micrometer.version>1.12.0</micrometer.version>
        <resilience4j.version>2.1.0</resilience4j.version>
        
        <!-- 安全和认证 -->
        <spring-security.version>6.2.0</spring-security.version>
        <jjwt.version>0.11.5</jjwt.version>
        <jakarta-validation.version>3.0.2</jakarta-validation.version>
        
        <!-- 工具库 -->
        <jackson.version>2.15.3</jackson.version>
        <fastjson2.version>2.0.43</fastjson2.version>
        <gson.version>2.10.1</gson.version>
        <hutool.version>5.8.23</hutool.version>
        <knife4j.version>4.3.0</knife4j.version>
        <javassist.version>3.30.2-GA</javassist.version>
        <cglib.version>3.3.0</cglib.version>
        <easyexcel.version>3.3.2</easyexcel.version>
        
        <!-- 测试相关 -->
        <junit-jupiter.version>5.10.1</junit-jupiter.version>
        <mockito.version>5.7.0</mockito.version>
        <assertj.version>3.24.2</assertj.version>
        <testcontainers.version>1.19.3</testcontainers.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <!-- Spring依赖继承自parent -->
            
            <!-- 平台内部模块依赖 -->
            <!-- 通用模块 -->
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-common-core</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-common-web</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-common-security</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-common-logging</artifactId>
                <version>${platform.version}</version>
            </dependency>
            
            <!-- 框架模块 -->
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-framework-core</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-framework-starter</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-framework-domain</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-framework-test</artifactId>
                <version>${platform.version}</version>
                <scope>test</scope>
            </dependency>
            
            <!-- 基础设施模块 -->
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-registry</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-config</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-gateway</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-auth-service</artifactId>
                <version>${platform.version}</version>
            </dependency>
            <!-- 其他平台模块略... -->
            
            <!-- 基础工具依赖 -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
                <scope>provided</scope>
            </dependency>
            
            <!-- Apache Commons -->
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>${commons-lang3.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-collections4</artifactId>
                <version>${commons-collections4.version}</version>
            </dependency>
            <dependency>
                <groupId>commons-io</groupId>
                <artifactId>commons-io</artifactId>
                <version>${commons-io.version}</version>
            </dependency>
            
            <!-- Google Guava -->
            <dependency>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
                <version>${guava.version}</version>
            </dependency>
            
            <!-- 数据库相关 -->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis-spring-boot.version}</version>
            </dependency>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
                <version>${dynamic-datasource.version}</version>
            </dependency>
            <!-- 其他依赖规划略... -->
            
            <!-- 测试相关 -->
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit-jupiter.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.mockito</groupId>
                <artifactId>mockito-core</artifactId>
                <version>${mockito.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.mockito</groupId>
                <artifactId>mockito-junit-jupiter</artifactId>
                <version>${mockito.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.assertj</groupId>
                <artifactId>assertj-core</artifactId>
                <version>${assertj.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>${testcontainers.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

## 版本兼容性管理

### 兼容性策略

1. **主版本兼容性**
   - 主版本号变更意味着不兼容API更改
   - 尽量避免在微小版本中引入不兼容变更
   - 对于必要的不兼容变更，提供迁移指南

2. **次版本兼容性**
   - 次版本更新应保证向后兼容
   - 允许添加新功能和非破坏性API变更
   - 废弃的API应标记为`@Deprecated`并指明替代方案

3. **修订版本兼容性**
   - 仅包含错误修复和性能优化
   - 不应包含新功能或API变更
   - 确保与同一主次版本的其他修订版完全兼容

### 依赖冲突解决

1. **使用excludes排除冲突依赖**
   ```xml
   <dependency>
       <groupId>some.group</groupId>
       <artifactId>some-artifact</artifactId>
       <exclusions>
           <exclusion>
               <groupId>conflicting.group</groupId>
               <artifactId>conflicting-artifact</artifactId>
           </exclusion>
       </exclusions>
   </dependency>
   ```

2. **依赖顺序优先级**
   - Maven依赖调解机制会选择"最近的依赖"
   - 可以调整依赖声明顺序影响解析结果

3. **使用dependencyManagement强制版本**
   - 优先使用BOM来统一版本
   - 对于特殊情况，可以在项目中重新声明依赖版本

## 版本更新流程

1. **定期审查**
   - 每月对第三方依赖进行安全审查
   - 每季度评估升级主要依赖版本的可能性
   - 针对已知安全漏洞的依赖进行即时升级

2. **版本测试**
   - 为每个依赖版本更新创建集成测试
   - 验证新版本兼容性和性能影响
   - 进行依赖分析，确保传递依赖不会引入冲突

3. **版本发布**
   - 遵循语义化版本规范
   - 为每个版本提供详细的变更日志
   - 主要版本更新提供迁移指南

## 最佳实践

1. **保持依赖最小化**
   - 仅声明直接使用的依赖
   - 避免不必要的传递依赖
   - 定期清理未使用的依赖

2. **版本管理策略**
   - 使用属性定义所有版本号
   - 评估第三方依赖的版本兼容性和稳定性
   - 考虑LTS（长期支持）版本优先

3. **依赖安全**
   - 集成OWASP依赖检查工具
   - 订阅依赖库的安全公告
   - 建立漏洞响应流程

## 常见问题及解决方案

1. **依赖冲突**
   - 问题：多个依赖引入同一库的不同版本
   - 解决：使用Maven依赖管理强制版本一致性
   - 工具：使用`mvn dependency:tree -Dverbose`分析依赖树

2. **传递性依赖膨胀**
   - 问题：第三方库引入过多不必要依赖
   - 解决：使用exclusions排除不需要的传递依赖
   - 最佳实践：定期审查和精简依赖

3. **版本兼容性问题**
   - 问题：依赖库版本更新导致不兼容
   - 解决：建立全面的集成测试和兼容性测试
   - 策略：关注依赖库的发布说明和破坏性变更
