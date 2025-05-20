# platform-parent

## 项目概述

`platform-parent` 是企业级数据平台的顶级父项目，负责管理所有子模块，统一项目版本，定义全局属性和构建配置。作为整个平台的根项目，它确保所有模块遵循一致的构建标准和规范。

## 主要职责

1. 统一版本管理：定义平台整体版本号，确保子模块版本一致
2. 公共属性定义：配置所有模块共享的Maven属性
3. 构建插件管理：统一配置构建和质量检查插件
4. 依赖管理：结合`platform-dependency`管理第三方依赖
5. 模块组织：定义模块结构和继承关系

## 技术栈

- Maven 3.8+：构建工具
- Java 21：基础语言版本
- Spring Boot 3.2.1：核心框架
- Spring Cloud 2023.0.0：微服务框架
- Spring Cloud Alibaba 2022.0.0.0：微服务组件

## 关键配置

### POM文件结构

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.platform</groupId>
    <artifactId>platform-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    
    <name>Enterprise Data Platform</name>
    <description>Enterprise-grade data platform based on SpringBoot 3.x and Vue 3.x</description>
    
    <properties>
        <!-- 基础配置 -->
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>${java.version}</maven.compiler.source>
        <maven.compiler.target>${java.version}</maven.compiler.target>
        
        <!-- Spring生态版本 -->
        <spring-boot.version>3.2.1</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
        
        <!-- 平台版本号（与父项目版本同步）-->
        <platform.version>${project.version}</platform.version>
    </properties>
    
    <modules>
        <module>../platform-bom</module>
        <module>../platform-dependency</module>
        <module>../platform-common</module>
        <module>../platform-framework</module>
        <module>../platform-infrastructure</module>
        <module>../platform-data-governance</module>
        <module>../platform-collect</module>
        <module>../platform-scheduler</module>
        <!-- 其他模块将在开发过程中陆续添加 -->
    </modules>
    
    <dependencyManagement>
        <dependencies>
            <!-- Import platform BOM -->
            <dependency>
                <groupId>com.platform</groupId>
                <artifactId>platform-bom</artifactId>
                <version>${platform.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <pluginManagement>
            <plugins>
                <!-- Spring Boot Maven Plugin -->
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>
                
                <!-- Maven Compiler Plugin -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.11.0</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <encoding>${project.build.sourceEncoding}</encoding>
                        <parameters>true</parameters>
                    </configuration>
                </plugin>
                
                <!-- Maven Source Plugin -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-source-plugin</artifactId>
                    <version>3.3.0</version>
                    <executions>
                        <execution>
                            <id>attach-sources</id>
                            <goals>
                                <goal>jar-no-fork</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                
                <!-- Maven Surefire Plugin -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>3.2.2</version>
                    <configuration>
                        <argLine>-Dfile.encoding=UTF-8</argLine>
                    </configuration>
                </plugin>
                
                <!-- JaCoCo Maven Plugin -->
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>0.8.11</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>prepare-agent</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>report</id>
                            <phase>test</phase>
                            <goals>
                                <goal>report</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                
                <!-- SpotBugs Maven Plugin -->
                <plugin>
                    <groupId>com.github.spotbugs</groupId>
                    <artifactId>spotbugs-maven-plugin</artifactId>
                    <version>4.8.1.0</version>
                    <configuration>
                        <effort>Max</effort>
                        <threshold>Medium</threshold>
                        <failOnError>true</failOnError>
                        <plugins>
                            <plugin>
                                <groupId>com.h3xstream.findsecbugs</groupId>
                                <artifactId>findsecbugs-plugin</artifactId>
                                <version>1.12.0</version>
                            </plugin>
                        </plugins>
                    </configuration>
                </plugin>
                
                <!-- Maven Checkstyle Plugin -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-checkstyle-plugin</artifactId>
                    <version>3.3.1</version>
                    <configuration>
                        <configLocation>checkstyle.xml</configLocation>
                        <encoding>UTF-8</encoding>
                        <consoleOutput>true</consoleOutput>
                        <failsOnError>true</failsOnError>
                        <linkXRef>false</linkXRef>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
        
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
    
    <profiles>
        <profile>
            <id>dev</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <spring.profiles.active>dev</spring.profiles.active>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <spring.profiles.active>test</spring.profiles.active>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <spring.profiles.active>prod</spring.profiles.active>
            </properties>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.github.spotbugs</groupId>
                        <artifactId>spotbugs-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>verify</phase>
                                <goals>
                                    <goal>check</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-checkstyle-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>verify</phase>
                                <goals>
                                    <goal>check</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
    
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
</project>
```

## 最佳实践

1. **版本管理策略**
   - 使用语义化版本号（SemVer）
   - 将具体版本号定义在父POM的properties中
   - 使用BOM管理第三方依赖版本

2. **模块组织原则**
   - 按业务能力而非技术层次组织模块
   - 每个业务域对应一个顶级模块
   - API模块与实现模块分离

3. **构建优化**
   - 合理配置Maven并行构建参数
   - 使用Maven模块化构建，支持增量构建
   - CI/CD流程中使用构建缓存

4. **质量控制**
   - 集成代码质量工具：JaCoCo、SpotBugs、Checkstyle
   - 在生产环境构建中强制执行质量检查
   - 定期审查并调整检查规则

## 实施指南

1. **初始设置**
   - 克隆平台代码库
   - 确保安装JDK 21和Maven 3.8+
   - 从根目录执行`mvn clean install`构建整个项目

2. **添加新模块**
   - 在平台根目录下创建新的模块目录
   - 添加pom.xml并设置parent为platform-parent
   - 在platform-parent的modules部分添加新模块引用

3. **版本更新流程**
   - 版本号更新在platform-parent的properties中进行
   - 使用Maven版本插件批量更新子模块版本
   - 版本号命名规则: x.y.z (主版本.次版本.修订版本)

## 注意事项

1. 不要在父POM中直接添加依赖，应使用dependencyManagement
2. 避免在子模块中覆盖父POM中已定义的插件版本
3. 子模块应只引用必需的依赖，避免依赖膨胀
4. 定期审查和更新第三方库版本，特别是安全补丁
5. 保持构建插件配置的一致性
