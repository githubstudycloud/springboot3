# Platform Utilities (platform-utils)

这是一个实用工具模块，提供了多种辅助工具，用于简化平台项目的开发和维护工作。

## 工具列表

### 1. 包名修改工具 (package_renamer.py)

这个工具用于修改项目中的Java包名，包括Java源代码、配置文件、注释和注解中的引用。

**主要功能：**
- 自动修改Java源代码中的package声明
- 自动修改import语句中的包引用
- 自动修改注解中的包引用
- 自动修改XML、属性文件等配置文件中的包引用
- 自动修改目录结构以匹配新的包名
- 支持干运行模式，可以预览而不实际修改文件

**使用示例：**
```bash
# 将包名从com.platform更改为com.aaa.aa.plat
python package_renamer.py /path/to/project com.platform com.aaa.aa.plat

# 干运行模式，只显示将要进行的更改而不实际修改
python package_renamer.py /path/to/project com.platform com.aaa.aa.plat --dry-run
```

### 2. Java结构生成器 (java_structure_generator.py)

这个工具用于生成Java项目目录结构的可视化表示，帮助开发人员理解项目组织。

**主要功能：**
- 递归分析Java项目目录结构
- 识别Java包和类的信息
- 支持多种输出格式（文本、Markdown、JSON）
- 提供过滤选项，可以包含或排除特定的文件和目录
- 生成项目结构的统计信息

**使用示例：**
```bash
# 生成文本格式的项目结构
python java_structure_generator.py /path/to/java/project

# 生成Markdown格式并输出到文件
python java_structure_generator.py /path/to/java/project -f markdown -o structure.md

# 限制目录深度
python java_structure_generator.py /path/to/java/project -d 3

# 排除特定目录
python java_structure_generator.py /path/to/java/project -e "target,build"
```

### 3. Vue结构生成器 (vue_structure_generator.py)

这个工具用于生成Vue.js项目目录结构的可视化表示，帮助前端开发人员理解项目组织。

**主要功能：**
- 递归分析Vue项目目录结构
- 识别Vue组件和页面
- 分析路由配置
- 提取组件属性和结构信息
- 支持多种输出格式（文本、Markdown、JSON）
- 提供过滤选项，可以包含或排除特定的文件和目录

**使用示例：**
```bash
# 生成文本格式的项目结构
python vue_structure_generator.py /path/to/vue/project

# 生成Markdown格式并输出到文件
python vue_structure_generator.py /path/to/vue/project -f markdown -o vue-structure.md

# 排除node_modules目录
python vue_structure_generator.py /path/to/vue/project -e "node_modules"

# 不包含组件详细信息
python vue_structure_generator.py /path/to/vue/project --no-component-info
```

## 安装和依赖

这些工具基于Python 3.6+，主要使用标准库，无需额外依赖。

## 在项目中的位置

这些工具位于项目的`platform-utils`目录中，是`platform-parent`项目的一部分。
