# 依赖配置修复 - 快速参考指南

## 问题概述

本项目之前存在以下问题：

1. Spring Boot相关依赖（web、validation、jpa、h2、test）版本找不到
2. Java版本控制不一致
3. Lombok依赖配置不正确

## 修复措施

### 1. 父POM配置（pom.xml）

- 添加了`spring-boot.version`属性：`3.2.9`
- 配置了`dependencyManagement`导入Spring Boot依赖BOM
- 添加了`maven.compiler.release`属性确保Java 21版本一致性
- 修改了Maven编译器插件配置，添加了注解处理器路径
- 在`modules`中添加了`platform-ddd-demo`模块

### 2. 子模块POM配置（platform-ddd-demo/pom.xml）

- 移除了Spring Boot依赖的版本号，改为继承父POM中的版本
- 保留了Lombok、MapStruct等工具库的显式版本号
- 配置了Maven编译器插件，确保注解处理正确工作
- 配置了Spring Boot Maven插件，指定了主类

### 3. Git忽略文件

- 扩展了`.gitignore`文件，包含更多IDE和构建工具生成的临时文件
- 添加了特定于不同操作系统的临时文件
- 排除了数据库文件和日志文件

### 4. IDE配置

- 创建了IntelliJ IDEA和Eclipse的项目配置文件
- 创建了通用IDE设置文件`.settings.conf`
- 添加了Java 21配置确保一致的编译环境

### 5. 工具脚本

- 创建了依赖测试脚本：`test-dependencies.bat`和`test-dependencies.sh`
- 创建了应用启动脚本：`run-ddd-demo.bat`和`run-ddd-demo.sh`
- 创建了权限修复脚本：`fix-permissions.sh`和`setup-permissions.sh`

## 快速验证步骤

1. **检查依赖版本**
   ```bash
   mvn dependency:tree | grep spring-boot
   mvn dependency:tree | grep h2
   mvn dependency:tree | grep lombok
   ```

2. **验证Java版本**
   ```bash
   mvn --version
   ```

3. **测试编译**
   ```bash
   # Windows
   test-dependencies.bat
   
   # Linux/macOS
   ./test-dependencies.sh
   ```

4. **运行应用**
   ```bash
   # Windows
   run-ddd-demo.bat
   
   # Linux/macOS
   ./run-ddd-demo.sh
   ```

## 常见问题解决

1. **IDE提示找不到Lombok注解**
    - 确保IDE安装了Lombok插件
    - 启用注解处理：IntelliJ IDEA - Settings/Preferences > Build, Execution, Deployment > Compiler > Annotation
      Processors

2. **仍然报错找不到依赖版本**
    - 强制更新Maven依赖：`mvn clean install -U`
    - 检查本地Maven仓库是否损坏：删除`~/.m2/repository`中的相关目录
    - 确认Maven设置中没有阻止依赖下载的代理或镜像配置

3. **Java版本不一致**
    - 检查JAVA_HOME环境变量是否指向JDK 21
    - 检查Maven配置文件中的Java版本设置
    - 运行`mvn -v`确认Maven使用的Java版本

4. **项目无法启动**
    - 检查应用程序是否有正确的main方法
    - 确认Spring Boot Maven插件的mainClass配置正确
    - 查看启动日志是否有明确的错误信息

## 参考文档

1. [SETUP-GUIDE.md](SETUP-GUIDE.md) - 详细设置指南
2. [MAVEN-DEPENDENCY-GUIDE.md](MAVEN-DEPENDENCY-GUIDE.md) - Maven依赖问题解决
3. [CONFIG-IMPROVEMENTS.md](CONFIG-IMPROVEMENTS.md) - 配置改进清单

## 维护责任

请确保在未来的更新中保持以下关键配置：

1. Spring Boot版本统一定义在父POM中
2. Java版本设置始终保持21（直到明确决定升级）
3. Lombok和MapStruct版本的一致性
4. 所有模块都继承父POM的依赖管理

配置修复和文档由[维护团队]于[日期]完成。
