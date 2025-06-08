# 使用官方OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用用户
RUN groupadd -r platform && useradd -r -g platform platform

# 复制Maven构建的JAR文件
COPY target/platform-gateway-*.jar app.jar

# 设置JVM参数
ENV JAVA_OPTS="-server -Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# 设置应用参数
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 切换到应用用户
USER platform

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"] 