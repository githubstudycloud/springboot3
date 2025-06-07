# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 安装必要的包
RUN apt-get update && apt-get install -y \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

# 复制jar文件
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} config-server.jar

# 创建配置目录
RUN mkdir -p /app/config/pub \
    && mkdir -p /app/config/dev \
    && mkdir -p /app/config/test \
    && mkdir -p /app/config/common \
    && mkdir -p /app/config-backup \
    && mkdir -p /app/logs

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1g" \
    CONFIG_GIT_URI="" \
    CONFIG_USERNAME="config-admin" \
    CONFIG_PASSWORD="config-admin-2024" \
    NACOS_SERVER_ADDR="localhost:8848"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8888/config/actuator/health || exit 1

# 暴露端口
EXPOSE 8888

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar config-server.jar"] 