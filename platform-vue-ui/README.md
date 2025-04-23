# 平台管理系统 Vue 前端项目

## 项目概述

本项目是平台管理系统的前端部分，使用 Vue 3 技术栈开发，采用父子项目结构，用于连接后端接口并显示数据。项目依托于微服务架构后端平台，为用户提供直观、高效的界面交互体验。前端项目独立打包，可以直接部署到 Nginx 服务器。

## 前置准备

### 安装 Node.js（必须步骤）

1. 访问 https://nodejs.org/ 下载 LTS 版本的 Node.js
2. 运行安装程序，确保勾选"自动添加到PATH"选项
3. 安装完成后，重启电脑确保环境变量生效
4. 打开命令提示符，输入`node -v`和`npm -v`验证安装成功

### 快速开始（推荐方式）

项目提供了一系列批处理文件，简化操作：

1. **安装依赖**：双击运行 `setup.bat`
2. **启动开发服务器**：双击运行 `dev.bat`（会提供选项菜单）
3. **构建项目**：双击运行 `build.bat`（会提供选项菜单）

批处理文件会自动处理环境检查、依赖安装和命令执行，非常适合新手使用。

### IDE 推荐配置

#### IntelliJ IDEA

1. 在IDEA中，找到`platform-vue-ui`文件夹
2. 使用批处理文件或通过IDEA的终端运行npm命令

如果要在IDEA中配置运行:
1. 点击"运行" -> "编辑配置"
2. 点击"+"，选择"npm"
3. 配置名称：如"dev"
4. npm命令：选择"run"
5. 脚本：输入"dev"
6. 工作目录：选择`platform-vue-ui`目录
7. 点击"确定"保存配置

#### Visual Studio Code

项目已配置好VSCode支持，包含推荐的扩展和设置，直接使用VSCode打开项目文件夹即可。

推荐安装的扩展：
- Vue Language Features (Volar)
- TypeScript Vue Plugin (Vscode)
- ESLint
- Prettier

## 技术栈

- **框架**: Vue 3
- **语言**: TypeScript
- **构建工具**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **打包集成**: Maven (独立打包，非内嵌到后端)

## 项目结构

项目采用父子结构，包含以下主要模块：

```
platform-vue-ui/                # 父项目
├── platform-vue-ui-common/     # 通用组件库
│   ├── src/
│   │   ├── api/                # API接口封装
│   │   ├── components/         # 通用UI组件
│   │   ├── types/              # 类型定义
│   │   ├── utils/              # 工具函数
│   │   └── index.ts            # 模块导出
│   ├── package.json            # 依赖管理
│   └── vite.config.ts          # 构建配置
│
├── platform-vue-ui-app/        # 主应用
│   ├── public/                 # 静态资源
│   ├── src/
│   │   ├── assets/             # 资源文件
│   │   ├── components/         # 业务组件
│   │   ├── layouts/            # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── views/              # 页面视图
│   │   ├── App.vue             # 应用入口
│   │   └── main.ts             # 主入口
│   ├── index.html              # HTML模板
│   ├── package.json            # 依赖管理
│   └── vite.config.ts          # 构建配置
│
├── setup.bat                   # 安装依赖脚本
├── dev.bat                     # 开发服务器启动脚本
├── build.bat                   # 构建脚本
├── nginx/                      # Nginx配置模板
├── pom.xml                     # Maven配置
├── package.json                # 工作区配置
└── README.md                   # 项目说明
```

## 开发指南

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 Yarn >= 1.22.0
- JDK 11 (仅用于Maven集成)
- Maven 3.6+ (仅用于Maven集成)

### 安装与启动

**1. 使用批处理文件 (Windows 环境推荐):**

```bash
# 安装依赖
双击运行 setup.bat

# 启动开发服务器
双击运行 dev.bat
# 然后根据菜单选择:
# 1. 仅启动主应用
# 2. 仅启动通用组件库
# 3. 启动所有项目

# 构建项目
双击运行 build.bat
# 然后根据菜单选择:
# 1. 构建所有项目
# 2. 仅构建通用组件库
# 3. 仅构建主应用
```

**2. 使用npm命令 (任何环境):**

```bash
# 安装依赖
npm install

# 启动开发服务器 (所有子项目)
npm run dev          # 启动主应用
npm run dev:common   # 启动通用组件库

# 或使用启动脚本控制启动哪些子项目
./start-dev.sh --all      # 启动所有服务
./start-dev.sh --common   # 只启动通用组件库
./start-dev.sh --app      # 只启动主应用
```

**3. 使用Docker Compose:**

```bash
# 启动所有服务
docker-compose up

# 只启动特定服务
docker-compose up vue-app

# 后台启动
docker-compose up -d
```

**4. 使用Maven:**

```bash
# 在platform-parent目录下执行
mvn clean install -Pdev
```

### 构建生产版本

**1. 使用批处理文件 (Windows 环境推荐):**

```bash
# 构建项目
双击运行 build.bat
# 然后选择构建选项
```

**2. 使用npm:**

```bash
# 构建所有项目
npm run build

# 只构建通用组件库
npm run build:common

# 只构建主应用
npm run build:app
```

**3. 使用Maven:**

```bash
# 在platform-parent目录下执行
mvn clean install -Pprod
# 构建后的zip包在target目录中
```

### 常见问题解决

**1. 'node' 不是内部或外部命令**
- 确保 Node.js 已正确安装
- 检查 Node.js 是否已添加到系统环境变量
- 重启计算机后重试

**2. npm install 失败**
- 检查网络连接
- 尝试设置淘宝镜像: `npm config set registry https://registry.npmmirror.com`
- 清除 npm 缓存: `npm cache clean --force`

**3. 端口被占用**
- 修改 `vite.config.ts` 中的端口号
- 或关闭占用端口的应用

## 项目功能

### 1. 用户认证与授权

- 登录/登出功能
- 基于JWT的认证
- 权限控制与路由守卫

### 2. 任务管理

- 任务列表展示
- 任务创建、编辑、删除
- 任务状态控制（启动/暂停）
- 任务详情查看

### 3. 系统监控

- 实时数据仪表盘
- 系统资源使用监控
- 任务执行状态统计
- 系统日志查询

### 4. 数据源管理

- 数据源配置
- 连接测试
- 数据预览

### 5. 节点管理

- 集群节点监控
- 节点状态管理
- 资源分配

## 配置说明

### 环境变量

项目使用 `.env` 文件管理环境变量：

- `.env`: 通用环境变量
- `.env.development`: 开发环境变量
- `.env.production`: 生产环境变量

主要配置项：

```
# API基础路径
VITE_API_BASE_URL=/api

# 环境标识
VITE_ENV=development
```

### 后端API连接

- 开发环境: 默认连接到 `http://localhost:8080/api`
- 生产环境: 默认连接到 `/api` (相对路径)

可以通过修改 `.env.*` 文件中的 `VITE_API_BASE_URL` 配置API路径。

### Nginx配置

项目包含了开发环境和生产环境的Nginx配置模板：

- `nginx/dev.conf`: 开发环境配置，用于代理前端开发服务器和后端API
- `nginx/prod.conf`: 生产环境配置，包含完整的HTTPS、缓存和安全配置

#### 生产环境部署

将构建好的前端项目部署到Nginx：

```bash
# 解压构建产物
unzip platform-vue-ui-1.0.0-SNAPSHOT.zip -d /usr/share/nginx/html/platform-ui/

# 配置Nginx
cp nginx/prod.conf /etc/nginx/conf.d/platform.conf

# 修改配置中的域名和SSL证书路径
nano /etc/nginx/conf.d/platform.conf

# 重新加载Nginx配置
nginx -s reload
```

## 多子项目管理

项目结构设计为独立工作区，可以灵活控制启动和构建不同的子项目：

1. **使用批处理文件 (Windows)：**
   - `setup.bat` - 安装依赖
   - `dev.bat` - 通过菜单选择启动哪些子项目
   - `build.bat` - 通过菜单选择构建哪些子项目

2. **NPM工作区脚本：**
   - `npm run dev` - 启动主应用
   - `npm run dev:common` - 启动通用组件库
   - `npm run build` - 构建所有项目
   - `npm run build:common` - 只构建通用组件库
   - `npm run build:app` - 只构建主应用

3. **启动脚本 (Linux/Mac)：**
   - `./start-dev.sh --all` - 启动所有服务
   - `./start-dev.sh --common` - 只启动通用组件库
   - `./start-dev.sh --app` - 只启动主应用

4. **Docker Compose：**
   - `docker-compose up` - 启动所有服务
   - `docker-compose up vue-app` - 只启动主应用
   - `docker-compose up vue-common` - 只启动通用组件库

## 最佳实践

1. **组件复用**: 通用组件放在 `platform-vue-ui-common` 中，业务组件放在 `platform-vue-ui-app` 中
2. **类型检查**: 使用 TypeScript 类型定义确保代码健壮性
3. **状态管理**: 使用 Pinia 进行集中状态管理，避免组件间直接通信
4. **按需加载**: 路由组件使用异步加载，提高首屏加载速度
5. **API封装**: 统一在 `api` 目录下封装所有接口，便于维护和管理
6. **独立部署**: 前端项目独立打包，部署到Nginx，与后端解耦

## 常见问题

### 1. 如何添加新页面?

1. 在 `platform-vue-ui-app/src/views` 目录下创建新页面组件
2. 在 `platform-vue-ui-app/src/router/index.ts` 中添加路由配置
3. 如果需要状态管理，在 `platform-vue-ui-app/src/store/modules` 中添加状态模块

### 2. 如何添加新的通用组件?

1. 在 `platform-vue-ui-common/src/components` 目录下创建新组件
2. 在 `platform-vue-ui-common/src/components/index.ts` 中导出组件
3. 重新构建通用组件库: `npm run build:common`
4. 在主应用中导入和使用该组件

### 3. 调试技巧

- 使用 Vue Devtools 浏览器扩展进行组件调试
- 使用 `console.log` 或 IDE 断点调试 JavaScript/TypeScript
- 检查网络请求确保 API 调用正常
- 使用 `.env.local` 文件配置本地环境变量（不会提交到代码库）

## 许可证

[MIT License](LICENSE)

## 联系方式

项目负责人: [Your Name] - [your.email@example.com]
