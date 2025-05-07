#!/bin/bash

# 设置颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 无颜色

# 打印横幅
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  企业级数据平台 - Docker Swarm集群初始化  ${NC}"
echo -e "${BLUE}============================================${NC}"

# 检查权限
if [ "$EUID" -ne 0 ]; then
  echo -e "${RED}请使用root权限运行此脚本${NC}"
  exit 1
fi

# 检查 Docker 是否已安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装！${NC}"
    echo -e "请安装 Docker 后再运行此脚本。"
    echo -e "安装指南: https://docs.docker.com/get-docker/"
    exit 1
fi

# 获取主机IP
read -p "请输入第一个管理节点的IP地址: " MANAGER_IP

# 初始化Swarm集群
echo -e "${YELLOW}正在初始化Swarm集群...${NC}"
docker swarm init --advertise-addr $MANAGER_IP

# 获取Manager和Worker的join token
MANAGER_TOKEN=$(docker swarm join-token manager -q)
WORKER_TOKEN=$(docker swarm join-token worker -q)

echo -e "${GREEN}Swarm集群初始化成功！${NC}"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}添加管理节点命令:${NC}"
echo -e "docker swarm join --token $MANAGER_TOKEN $MANAGER_IP:2377"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}添加工作节点命令:${NC}"
echo -e "docker swarm join --token $WORKER_TOKEN $MANAGER_IP:2377"
echo -e "${BLUE}============================================${NC}"

# 创建网络
echo -e "${YELLOW}正在创建网络...${NC}"
docker network create --driver overlay --attachable platform_net
echo -e "${GREEN}网络创建成功！${NC}"

# 设置节点标签
echo -e "${YELLOW}设置节点标签...${NC}"
NODE_ID=$(docker node ls --format "{{.ID}}" -f "self=true")
docker node update --label-add role=manager --label-add zone=core $NODE_ID
echo -e "${GREEN}当前节点已标记为管理节点！${NC}"

# 创建存储目录
echo -e "${YELLOW}创建存储目录...${NC}"
mkdir -p /data/exports/{mysql-master,mysql-slave,redis-master,redis-slave,mongo1,mongo2,mongo3,kafka1,kafka2,kafka3,zookeeper1-data,zookeeper1-logs,zookeeper2-data,zookeeper2-logs,zookeeper3-data,zookeeper3-logs,elasticsearch1,elasticsearch2,elasticsearch3,prometheus,grafana,alertmanager,rabbitmq1,rabbitmq2,rabbitmq3}

chmod -R 777 /data/exports
echo -e "${GREEN}存储目录创建成功！${NC}"

# 创建Docker Secrets
echo -e "${YELLOW}创建Docker Secrets...${NC}"

# 生成随机密码
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 16)
REDIS_PASSWORD=$(openssl rand -base64 16)
MONGO_ROOT_USERNAME="admin"
MONGO_ROOT_PASSWORD=$(openssl rand -base64 16)
ELASTIC_PASSWORD=$(openssl rand -base64 16)
GRAFANA_ADMIN_PASSWORD=$(openssl rand -base64 16)
RABBITMQ_PASSWORD=$(openssl rand -base64 16)
RABBITMQ_ERLANG_COOKIE=$(openssl rand -base64 32)

# 创建临时密码文件
echo -n "$MYSQL_ROOT_PASSWORD" > ./mysql_root_password.txt
echo -n "$REDIS_PASSWORD" > ./redis_password.txt
echo -n "$MONGO_ROOT_USERNAME" > ./mongo_root_username.txt
echo -n "$MONGO_ROOT_PASSWORD" > ./mongo_root_password.txt
echo -n "$ELASTIC_PASSWORD" > ./elastic_password.txt
echo -n "$GRAFANA_ADMIN_PASSWORD" > ./grafana_admin_password.txt
echo -n "$RABBITMQ_PASSWORD" > ./rabbitmq_password.txt

# 创建Docker Secrets
docker secret create mysql_root_password ./mysql_root_password.txt
docker secret create redis_password ./redis_password.txt
docker secret create mongo_root_username ./mongo_root_username.txt
docker secret create mongo_root_password ./mongo_root_password.txt
docker secret create elastic_password ./elastic_password.txt
docker secret create grafana_admin_password ./grafana_admin_password.txt
docker secret create rabbitmq_password ./rabbitmq_password.txt

# 创建应用服务密码
docker secret create auth_db_password ./mysql_root_password.txt
docker secret create collect_db_password ./mysql_root_password.txt
docker secret create governance_db_password ./mysql_root_password.txt
docker secret create scheduler_db_password ./mysql_root_password.txt

# 删除临时密码文件
rm -f ./*.txt

echo -e "${GREEN}Docker Secrets创建成功！${NC}"

# 创建环境变量文件
echo -e "${YELLOW}创建环境变量文件...${NC}"
cat > .env << EOF
# 生成的环境变量文件，用于Docker Compose

# 服务镜像仓库地址
REGISTRY_URL=localhost:5000

# 服务版本标签
TAG=latest

# RabbitMQ配置
RABBITMQ_ERLANG_COOKIE=$RABBITMQ_ERLANG_COOKIE
RABBITMQ_DEFAULT_USER=admin
EOF

echo -e "${GREEN}环境变量文件创建成功！${NC}"

echo -e "${BLUE}============================================${NC}"
echo -e "${GREEN}Swarm集群初始化完成！${NC}"
echo -e "${YELLOW}请在其他节点上执行相应的join命令，并给节点设置合适的标签。${NC}"
echo -e "${YELLOW}请保存以下凭据信息：${NC}"
echo -e "MySQL Root密码: $MYSQL_ROOT_PASSWORD"
echo -e "Redis密码: $REDIS_PASSWORD"
echo -e "MongoDB用户名: $MONGO_ROOT_USERNAME"
echo -e "MongoDB密码: $MONGO_ROOT_PASSWORD"
echo -e "Elasticsearch密码: $ELASTIC_PASSWORD"
echo -e "Grafana管理员密码: $GRAFANA_ADMIN_PASSWORD"
echo -e "RabbitMQ密码: $RABBITMQ_PASSWORD"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}完成其他节点的加入后，可以部署服务栈：${NC}"
echo -e "docker stack deploy -c stacks/platform-data.yml platform-data"
echo -e "docker stack deploy -c stacks/platform-msg.yml platform-msg"
echo -e "docker stack deploy -c stacks/platform-monitor.yml platform-monitor"
echo -e "docker stack deploy -c stacks/platform-core.yml platform-core"
echo -e "${BLUE}============================================${NC}"
