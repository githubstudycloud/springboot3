@echo off
echo ===== 平台前端项目一键安装脚本 =====
echo.

:: 检查Node.js是否安装
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
  echo [错误] 未检测到Node.js! 请先安装Node.js
  echo 您可以从 https://nodejs.org/ 下载并安装Node.js LTS版本
  pause
  exit /b 1
)

:: 显示当前Node.js版本
echo [信息] 当前Node.js版本:
node -v
echo.

:: 显示当前npm版本
echo [信息] 当前npm版本:
npm -v
echo.

:: 安装依赖
echo [信息] 开始安装依赖...
npm install
if %ERRORLEVEL% NEQ 0 (
  echo [错误] 安装依赖失败，请查看上方错误信息!
  pause
  exit /b 1
)

echo.
echo [成功] 依赖安装完成!
echo.
echo ===== 如需启动开发服务器，请运行以下命令 =====
echo.
echo npm run dev       - 启动主应用
echo npm run dev:common - 启动通用组件库
echo npm run build     - 构建所有项目
echo.
pause
