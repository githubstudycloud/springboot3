# Platform Dependency

## 模块概述
`platform-dependency`模块是整个项目的依赖管理中心，用于集中管理所有第三方依赖的版本，确保整个项目依赖版本的一致性，避免依赖冲突。

## 主要功能
- 统一管理第三方依赖的版本
- 定义项目模块间的依赖关系
- 提供依赖冲突解决方案
- 简化子模块的依赖声明

## 技术特点
- 采用Maven的依赖管理机制
- 使用`<dependencyManagement>`统一版本控制
- 使用`<pluginManagement>`管理插件配置

## 目录结构
```
platform-dependency
└── pom.xml (依赖管理文件)
```

## 使用方式
其他模块只需在pom.xml中引入此模块作为parent或在`<dependencyManagement>`中import此模块的pom，即可继承统一的依赖版本管理。

### 作为父模块继承
```xml
<parent>
    <groupId>com.example</groupId>
    <artifactId>platform-dependency</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
```

### 作为BOM导入
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>platform-dependency</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 维护指南
1. 添加新依赖时，应在此模块中声明版本
2. 升级依赖版本时，只需在此模块中修改，所有子模块将自动继承新版本
3. 定期检查依赖更新，确保使用最新的稳定版本
4. 避免在子模块中直接指定依赖版本号
