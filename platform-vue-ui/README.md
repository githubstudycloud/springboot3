# Platform Vue UI

这是平台的前端 UI 项目，基于 Vue.js 构建，用于连接后端接口并显示数据。

## 项目结构

- `platform-vue-ui-parent` - 父项目
  - `platform-vue-ui-common` - 通用组件和工具
  - `platform-vue-ui-app` - 主应用

## 技术栈

- Vue 3
- Vue Router
- Pinia (状态管理)
- Axios (HTTP 客户端)
- Element Plus (UI 组件库)
- Vite (构建工具)

## 开发指南

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 Yarn >= 1.22.0

### 安装依赖

```bash
# 安装依赖
npm install

# 或使用 Yarn
yarn install
```

### 开发服务器

```bash
# 启动开发服务器
npm run dev

# 或使用 Yarn
yarn dev
```

### 构建生产版本

```bash
# 构建生产版本
npm run build

# 或使用 Yarn
yarn build
```

## 后端 API 连接

项目配置为连接到以下后端 API 端点：

- 开发环境: `http://localhost:8080/api`
- 生产环境: 通过环境变量 `VITE_API_BASE_URL` 配置

## 父子项目关系

- `platform-vue-ui-parent` 管理所有子模块的依赖和构建配置
- `platform-vue-ui-common` 包含可重用的组件、工具和服务
- `platform-vue-ui-app` 是主应用，整合所有组件构建完整界面
