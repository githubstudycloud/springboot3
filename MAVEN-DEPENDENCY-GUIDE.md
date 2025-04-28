# Maven 依赖问题解决指南

## Spring Boot 依赖版本问题

在 Spring Boot 项目中，经常会遇到依赖版本号找不到的问题，特别是当项目模块较多或依赖管理不完善时。以下是解决这类问题的关键步骤：

### 1. 根本原因

Spring Boot 项目中找不到依赖版本号通常由以下原因导致：

- 父 POM 中缺少 `spring-boot-dependencies` 的导入
- 没有正确使用 Maven 的依赖管理机制
- Spring Boot 和其他框架的版本不匹配
- 子模块没有正确继承父模块的依赖管理

### 2. 解决方案

#### 2.1 在父 POM 中添加 Spring Boot 依赖管理

在父 POM 文件中添加以下配置：

```xml
<properties>
    <!-- 明确定义 Spring Boot 版本 -->
    <spring-boot.version>3.2.9</spring-boot.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 2.2 在子模块中使用继承的依赖版本

在子模块中使用 Spring Boot 依赖时，无需指定版本号：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 2.3 Maven 插件配置

确保 `spring-boot-maven-plugin` 插件配置正确：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <mainClass>com.example.demo.DddDemoApplication</mainClass>
    </configuration>
</plugin>
```

### 3. 注意事项

- **保持版本一致性**：确保所有模块使用相同的 Spring Boot 版本
- **避免版本冲突**：如遇依赖冲突，使用 `mvn dependency:tree -Dverbose` 分析
- **正确的父子关系**：确保子模块正确引用父模块
- **优先使用 BOM**：使用 Bill of Materials 模式导入依赖版本集

### 4. Lombok 问题解决

如果遇到 Lombok 相关错误，确保以下几点：

1. 在 Maven 依赖中正确配置 Lombok：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.32</version>
    <scope>provided</scope>
</dependency>
```

2. 在编译器插件中添加注解处理器路径：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.32</version>
            </path>
            <!-- 如果使用 MapStruct 还需要添加 -->
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.5.5.Final</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

3. 确保 IDE 已安装 Lombok 插件并启用注解处理

### 5. 验证配置

修改完成后，可以通过以下命令验证依赖版本：

```bash
# 查看所有依赖及其版本
mvn dependency:tree

# 查看特定依赖的有效版本
mvn help:evaluate -Dexpression=spring-boot.version -q -DforceStdout

# 清理并重新构建项目
mvn clean install
```

如果上述命令成功执行，说明你的 Maven 依赖配置已正确修复。
