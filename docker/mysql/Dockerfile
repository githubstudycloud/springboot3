FROM mysql:latest

ENV TZ=Asia/Shanghai

COPY ./init/ /docker-entrypoint-initdb.d/

# 设置字符集和校对规则
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_general_ci", "--default-time-zone=+8:00"]
