@echo off
echo ===== 平台前端项目构建脚本 =====
echo.

:: 检查Node.js是否安装
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo [错误] 未检测到Node.js! 请先安装Node.js
  echo 您可以从 https://nodejs.org/ 下载并安装Node.js LTS版本
  pause
  exit /b 1
)

:: 检查node_modules是否存在
if not exist "node_modules" (
  echo [警告] 未检测到node_modules文件夹，需要先安装依赖
  echo.
  echo [信息] 正在安装依赖...
  npm install
  if %ERRORLEVEL% NEQ 0 (
    echo [错误] 安装依赖失败，请查看上方错误信息!
    pause
    exit /b 1
  )
)

echo.
echo [信息] 选择构建选项:
echo.
echo  1. 构建所有项目 (通用组件库 + 主应用)
echo  2. 仅构建通用组件库
echo  3. 仅构建主应用
echo.
set /p choice="请输入选项 (1-3, 默认1): "

if "%choice%"=="" set choice=1

if "%choice%"=="1" (
  echo.
  echo [信息] 构建所有项目...
  npm run build
) else if "%choice%"=="2" (
  echo.
  echo [信息] 仅构建通用组件库...
  npm run build:common
) else if "%choice%"=="3" (
  echo.
  echo [信息] 仅构建主应用...
  npm run build:app
) else (
  echo.
  echo [错误] 无效选项: %choice%
  pause
  exit /b 1
)

if %ERRORLEVEL% NEQ 0 (
  echo.
  echo [错误] 构建失败，请查看上方错误信息!
) else (
  echo.
  echo [成功] 构建完成!
  echo.
  echo 构建结果位于:
  if "%choice%"=="1" (
    echo  - 通用组件库: platform-vue-ui-common\dist
    echo  - 主应用: platform-vue-ui-app\dist
  ) else if "%choice%"=="2" (
    echo  - 通用组件库: platform-vue-ui-common\dist
  ) else if "%choice%"=="3" (
    echo  - 主应用: platform-vue-ui-app\dist
  )
)

pause
