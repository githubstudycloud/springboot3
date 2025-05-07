FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE="dev"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${ARGS}"]
