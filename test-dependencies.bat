@echo off
rem 此脚本用于快速测试解决依赖版本问题后的项目
rem 先清理并重新编译项目，然后运行示例应用

echo ================================
echo  正在测试Maven依赖配置...
echo ================================

rem 设置颜色
set RED=41m
set GREEN=42m
set YELLOW=43m
set RESET=[0m

echo [%YELLOW%开始清理项目...%RESET%

rem 清理项目
call mvn clean -U
if %errorlevel% neq 0 (
    echo [%RED%清理项目失败，请检查Maven配置%RESET%
    goto :fail
)

echo [%GREEN%清理项目成功%RESET%
echo [%YELLOW%开始检查依赖树...%RESET%

rem 检查依赖树
call mvn dependency:tree > dependency-tree.log
if %errorlevel% neq 0 (
    echo [%RED%检查依赖树失败，请检查Maven配置%RESET%
    goto :fail
)

echo [%GREEN%依赖树检查成功，结果已保存到dependency-tree.log%RESET%

rem 检查Spring Boot依赖
findstr /C:"org.springframework.boot" dependency-tree.log > nul
if %errorlevel% neq 0 (
    echo [%RED%未找到Spring Boot依赖，请检查配置%RESET%
    goto :fail
)

echo [%GREEN%Spring Boot依赖检查成功%RESET%

rem 检查H2依赖
findstr /C:"com.h2database" dependency-tree.log > nul
if %errorlevel% neq 0 (
    echo [%RED%未找到H2数据库依赖，请检查配置%RESET%
    goto :fail
)

echo [%GREEN%H2数据库依赖检查成功%RESET%

rem 检查Lombok依赖
findstr /C:"org.projectlombok" dependency-tree.log > nul
if %errorlevel% neq 0 (
    echo [%RED%未找到Lombok依赖，请检查配置%RESET%
    goto :fail
)

echo [%GREEN%Lombok依赖检查成功%RESET%
echo [%YELLOW%开始编译项目...%RESET%

rem 编译项目
call mvn compile -DskipTests
if %errorlevel% neq 0 (
    echo [%RED%编译项目失败，请检查代码或Maven配置%RESET%
    goto :fail
)

echo [%GREEN%编译项目成功%RESET%
echo [%YELLOW%开始打包项目...%RESET%

rem 打包项目
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo [%RED%打包项目失败，请检查Maven配置%RESET%
    goto :fail
)

echo [%GREEN%打包项目成功%RESET%
echo [%YELLOW%开始运行DDD示例应用...%RESET%

rem 运行应用
cd platform-ddd-demo
call mvn spring-boot:run -DskipTests
if %errorlevel% neq 0 (
    echo [%RED%运行应用失败，请检查应用配置%RESET%
    cd ..
    goto :fail
)

cd ..
goto :success

:fail
echo ================================
echo [%RED%测试失败，请检查错误信息%RESET%
echo ================================
exit /b 1

:success
echo ================================
echo [%GREEN%测试成功，所有依赖问题已解决%RESET%
echo ================================
exit /b 0
