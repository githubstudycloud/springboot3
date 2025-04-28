#!/bin/bash
# 启动DDD示例应用
# 该脚本确保使用Java 21并设置正确的JVM参数

echo "正在启动DDD示例应用..."

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | grep -i version | cut -d'"' -f2)
if [[ ! $JAVA_VERSION == 21* ]]; then
    echo "警告: 没有检测到Java 21。请确保已安装JDK 21并正确设置JAVA_HOME环境变量。"
    echo "当前Java版本: $JAVA_VERSION"
    exit 1
fi

# 显示当前版本
echo "使用Java版本:"
java -version

# 设置JVM参数
JAVA_OPTS="--enable-preview -Xmx512m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -Dspring.profiles.active=dev -Dfile.encoding=UTF-8"

echo "使用以下JVM参数:"
echo $JAVA_OPTS

# 更新Maven依赖（可选）
read -p "是否更新Maven依赖？(Y/N): " UPDATE_DEPS
if [[ $UPDATE_DEPS == "Y" || $UPDATE_DEPS == "y" ]]; then
    echo "正在更新Maven依赖..."
    mvn clean install -DskipTests -U
fi

echo ""
echo "正在编译并启动应用..."
cd platform-ddd-demo
mvn spring-boot:run -Dspring-boot.run.jvmArguments="$JAVA_OPTS"

if [ $? -ne 0 ]; then
    echo "应用启动失败。请检查上面的错误信息。"
else
    echo "应用已成功启动。"
fi
