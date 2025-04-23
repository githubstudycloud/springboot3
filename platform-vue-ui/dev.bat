@echo off
echo ===== 平台前端项目开发服务器启动脚本 =====
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
echo [信息] 选择要启动的项目:
echo.
echo  1. 仅启动主应用 (platform-vue-ui-app)
echo  2. 仅启动通用组件库 (platform-vue-ui-common)
echo  3. 启动所有项目
echo.
set /p choice="请输入选项 (1-3, 默认1): "

if "%choice%"=="" set choice=1

if "%choice%"=="1" (
  echo.
  echo [信息] 正在启动主应用...
  npm run dev
) else if "%choice%"=="2" (
  echo.
  echo [信息] 正在启动通用组件库...
  npm run dev:common
) else if "%choice%"=="3" (
  echo.
  echo [信息] 正在启动所有项目...
  start "通用组件库" cmd /c "npm run dev:common"
  timeout /t 5
  npm run dev
) else (
  echo.
  echo [错误] 无效选项: %choice%
  pause
  exit /b 1
)
