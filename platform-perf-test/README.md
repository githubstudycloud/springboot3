# Platform Performance Test Framework

该框架提供了一个完整的解决方案，用于对平台的Java后端和Vue前端进行自动化性能测试。通过这个框架，您可以监控和优化系统性能，确保应用程序在不同负载下都能保持高效运行。

## 框架概述

这是一个模块化设计的Python父子模块，专门用于对Java和Vue进行自动化性能测试。该框架主要包含以下部分：

### 目录结构：
```
platform-perf-test/
├── __init__.py              # 包定义
├── run_tests.py             # 主执行脚本
├── config.py                # 配置设置
├── utils.py                 # 通用工具函数
├── requirements.txt         # Python依赖
├── README.md                # 使用说明文档
├── java-perf-test/          # Java测试子模块
│   ├── __init__.py
│   ├── runner.py            # Java测试运行器
│   ├── utils.py             # Java测试工具类
│   └── jmeter/              # JMeter测试计划
└── vue-perf-test/           # Vue测试子模块
    ├── __init__.py
    ├── runner.py            # Vue测试运行器
    ├── utils.py             # Vue测试工具类
    └── scripts/             # Puppeteer测试脚本
```

## 主要功能

### Java后端测试
- API响应时间和吞吐量测试
- 服务资源使用监控（CPU、内存、IO）
- 线程状态和潜在死锁检测
- 垃圾回收性能分析
- 自动端点发现和测试

### Vue前端测试
- 页面加载性能测试
- 组件渲染时间测量
- JavaScript和CSS覆盖率分析
- 包大小和依赖分析
- 用户交互响应性测试

### 通用功能
- 统一的命令行界面
- 测试结果保存和报告生成
- 灵活的配置选项
- 详细的日志记录

## 模块优势
- 模块化设计，便于扩展和维护
- 全面的测试覆盖范围
- 可定制的性能指标和阈值
- 自动化的测试流程
- 详细的性能分析报告

## 安装要求

- Python 3.8+
- Node.js 14+
- Java 8+
- JMeter（用于Java API测试）
- Puppeteer（用于Vue前端测试）
- Lighthouse（可选，用于前端性能审计）

### 安装依赖

```bash
# 安装Python依赖
pip install -r requirements.txt

# 安装Node.js依赖（用于Vue测试）
cd vue-perf-test
npm install puppeteer lighthouse
```

## 使用方法

### 运行所有测试

```bash
python run_tests.py --all
```

### 只测试Java后端

```bash
python run_tests.py --java
```

### 只测试Vue前端

```bash
python run_tests.py --vue
```

### 其他选项

```bash
# 测试特定Java模块
python run_tests.py --java --java-modules platform-gateway platform-scheduler

# 测试特定Vue页面
python run_tests.py --vue --vue-pages / /dashboard /admin

# 设置测试持续时间和并发用户数
python run_tests.py --all --duration 120 --users 50

# 设置Java虚拟机选项
python run_tests.py --java --jvm-opts "-Xms1g,-Xmx4g,-XX:+UseG1GC"

# 在普通模式（非无头模式）下运行Vue测试
python run_tests.py --vue --headless false
```

## 测试结果

测试结果将保存在`results`目录中，包括：

- JSON格式的性能指标
- 响应时间分布图
- 资源使用趋势
- 潜在性能问题的报告

每次测试都会在`results`目录下创建一个带有时间戳的文件夹，包含该次测试的所有结果。您也可以通过`--output-dir`选项指定自定义的输出目录。

## 配置文件

您可以在`config.py`中修改默认配置，包括：

- 要测试的模块列表
- 默认的JVM选项
- 测试持续时间和并发用户数
- 报告生成选项
- 性能阈值警报设置

## 扩展框架

### 添加新的Java测试

1. 在`java-perf-test/jmeter/`目录中创建新的JMeter测试计划
2. 在`java-perf-test/utils.py`中添加新的分析函数
3. 在`config.py`中更新要测试的模块列表

### 添加新的Vue测试

1. 在`vue-perf-test/scripts/`目录中创建新的Puppeteer测试脚本
2. 在`vue-perf-test/utils.py`中添加新的分析函数
3. 在`config.py`中更新要测试的页面列表

## 贡献与支持

如果您发现任何问题或有改进建议，请提交issue或pull request。

## 许可证

本项目采用MIT许可证。详情请参阅LICENSE文件。
