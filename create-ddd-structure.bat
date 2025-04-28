@echo off
REM 创建DDD包结构的批处理脚本

echo 开始创建DDD包结构...

set BASE_DIR=platform-framework\src\main\java\com\example\framework

REM 创建应用层（Application）
mkdir "%BASE_DIR%\application" 2>nul
mkdir "%BASE_DIR%\application\service" 2>nul
mkdir "%BASE_DIR%\application\dto" 2>nul
mkdir "%BASE_DIR%\application\command" 2>nul
mkdir "%BASE_DIR%\application\query" 2>nul

REM 创建接口层（Interfaces）
mkdir "%BASE_DIR%\interfaces" 2>nul
mkdir "%BASE_DIR%\interfaces\rest" 2>nul
mkdir "%BASE_DIR%\interfaces\graphql" 2>nul
mkdir "%BASE_DIR%\interfaces\facade" 2>nul

REM 创建基础设施层（Infrastructure）
mkdir "%BASE_DIR%\infrastructure" 2>nul
mkdir "%BASE_DIR%\infrastructure\persistence" 2>nul
mkdir "%BASE_DIR%\infrastructure\messaging" 2>nul
mkdir "%BASE_DIR%\infrastructure\integration" 2>nul
mkdir "%BASE_DIR%\infrastructure\security" 2>nul

echo DDD包结构创建完成！