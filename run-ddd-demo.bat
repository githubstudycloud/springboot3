@echo off
rem 启动DDD示例应用
rem 该脚本确保使用Java 21并设置正确的JVM参数

echo 正在启动DDD示例应用...

rem 检查Java版本
java -version 2>&1 | findstr "version \"21" > nul
if %errorlevel% neq 0 (
    echo 警告: 没有检测到Java 21。请确保已安装JDK 21并正确设置JAVA_HOME环境变量。
    echo 当前Java版本:
    java -version
    pause
    exit /b 1
)

rem 显示当前版本
echo 使用Java版本:
java -version

rem 设置JVM参数
set JAVA_OPTS=--enable-preview -Xmx512m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -Dspring.profiles.active=dev -Dfile.encoding=UTF-8

echo 使用以下JVM参数:
echo %JAVA_OPTS%

rem 更新Maven依赖（可选）
echo 是否更新Maven依赖？(Y/N)
set /p UPDATE_DEPS=
if /i "%UPDATE_DEPS%"=="Y" (
    echo 正在更新Maven依赖...
    call mvn clean install -DskipTests -U
)

echo.
echo 正在编译并启动应用...
cd platform-ddd-demo
call mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

if %errorlevel% neq 0 (
    echo 应用启动失败。请检查上面的错误信息。
    pause
) else (
    echo 应用已成功启动。
)
