# 项目配置及常见问题解决指南

## 1. Java版本要求

本项目强制要求使用Java 21。确保你的环境配置如下：

- JDK 21（推荐使用OpenJDK 21或Oracle JDK 21）
- Maven 3.9+
- 配置好JAVA_HOME环境变量

## 2. 导入项目到IDE

### IntelliJ IDEA

1. 启动IntelliJ IDEA
2. 选择 "Open" 或 "Import Project"
3. 选择项目根目录
4. 选择 "Import project from external model" 并选择 "Maven"
5. 勾选 "Import Maven projects automatically"
6. 点击 "Finish"

### Eclipse / Spring Tool Suite

1. 启动Eclipse或STS
2. 选择 "File" > "Import" > "Maven" > "Existing Maven Projects"
3. 选择项目根目录
4. 点击 "Finish"

## 3. 确保Java版本正确设置

**IntelliJ IDEA**

1. 打开项目设置 (File > Project Structure)
2. 在 "Project" 选项卡中，确保 "Project SDK" 和 "Project language level" 均设置为 "21"
3. 在 "Modules" 选项卡中，对所有模块设置 "Language level" 为 "21"

**Eclipse / STS**

1. 右键点击项目 > Properties > Java Compiler
2. 取消勾选 "Use compliance from execution environment"
3. 设置 "Compiler compliance level" 为 "21"
4. 右键点击项目 > Properties > Java Build Path > Libraries
5. 确保JRE System Library是JavaSE-21

## 4. 解决Lombok相关问题

如果你在IDE中看到与Lombok相关的编译错误，请确保：

### IntelliJ IDEA

1. 安装Lombok插件：File > Settings > Plugins > 搜索"Lombok" > 安装
2. 启用注解处理：File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors > 勾选"Enable annotation processing"
3. 重新构建项目

### Eclipse / STS

1. 运行Lombok安装程序（下载lombok.jar并双击运行）
2. 确认lombok.jar已添加到eclipse.ini中
3. 重启Eclipse
4. 刷新项目并重新构建

## 5. Maven编译问题

如果遇到Maven编译问题，可以尝试：

```bash
# 清理项目
mvn clean

# 强制更新依赖
mvn clean install -U

# 跳过测试
mvn clean install -DskipTests
```

## 6. 运行DDD示例项目

```bash
# 进入ddd示例模块目录
cd platform-ddd-demo

# 通过Maven运行
mvn spring-boot:run

# 或者构建后运行jar
mvn clean package
java -jar target/platform-ddd-demo-1.0.0-SNAPSHOT.jar
```

## 7. 常见问题

### 1. 找不到符号 / 无法解析符号

这通常与Lombok或MapStruct有关。确保：
- IDE中安装了Lombok插件
- 启用了注解处理
- 项目正确引用了Lombok和MapStruct依赖
- Maven配置了正确的annotationProcessorPaths

### 2. Java版本不兼容

如果你看到与Java版本相关的错误，请确保：
- JDK 21已正确安装
- JAVA_HOME环境变量指向JDK 21
- IDE中项目和模块的Java版本都设置为21
- Maven配置使用JDK 21

### 3. application.yml中的spring.mvc.pathmatch.matching-strategy配置警告

在Spring Boot 3.x中，默认的URL匹配策略已经改变。如果看到相关警告，可在application.yml中添加：

```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
```

### 4. 运行时找不到main方法

确保platform-ddd-demo模块的spring-boot-maven-plugin配置了正确的mainClass：

```xml
<configuration>
    <mainClass>com.example.demo.DddDemoApplication</mainClass>
</configuration>
```

## 8. 联系支持

如有任何问题，请联系项目维护人员或在项目仓库中提交Issue。
