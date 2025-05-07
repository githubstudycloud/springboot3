#!/bin/bash

# 设置颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 无颜色

# 打印横幅
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}      企业级数据平台 - 环境启动脚本      ${NC}"
echo -e "${BLUE}============================================${NC}"

# 检查 Docker 是否已安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装！${NC}"
    echo -e "请安装 Docker 后再运行此脚本。"
    echo -e "安装指南: https://docs.docker.com/get-docker/"
    exit 1
fi

# 检查 Docker Compose 是否已安装
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose 未安装！${NC}"
    echo -e "请安装 Docker Compose 后再运行此脚本。"
    echo -e "安装指南: https://docs.docker.com/compose/install/"
    exit 1
fi

# 创建必要的目录
echo -e "${YELLOW}创建必要的目录...${NC}"
mkdir -p logs/nginx
mkdir -p static
mkdir -p frontend

# 询问启动模式
echo -e "${GREEN}请选择启动模式:${NC}"
echo -e "1) 基础服务（MySQL + Redis + Nacos + Nginx）"
echo -e "2) 基础服务 + 消息队列（添加 Kafka + RabbitMQ）"
echo -e "3) 基础服务 + 监控（添加 Prometheus + Grafana + Elasticsearch + Kibana）"
echo -e "4) 完整测试环境（所有服务）"
echo -e "5) 自定义服务"
echo -e "0) 退出"

read -p "请输入选项 [1-5]: " option

case $option in
    1)
        echo -e "${YELLOW}启动基础服务...${NC}"
        docker-compose up -d mysql redis nacos nginx
        ;;
    2)
        echo -e "${YELLOW}启动基础服务 + 消息队列...${NC}"
        docker-compose up -d mysql redis nacos nginx zookeeper kafka rabbitmq
        ;;
    3)
        echo -e "${YELLOW}启动基础服务 + 监控...${NC}"
        docker-compose up -d mysql redis nacos nginx prometheus grafana elasticsearch kibana
        ;;
    4)
        echo -e "${YELLOW}启动完整测试环境...${NC}"
        docker-compose up -d
        ;;
    5)
        echo -e "${GREEN}请选择需要启动的服务（多选，用空格分隔）:${NC}"
        echo -e "- mysql (MySQL数据库)"
        echo -e "- redis (Redis缓存)"
        echo -e "- mongo (MongoDB文档数据库)"
        echo -e "- postgres (PostgreSQL数据库)"
        echo -e "- clickhouse (ClickHouse分析型数据库)"
        echo -e "- zookeeper (ZooKeeper协调服务)"
        echo -e "- kafka (Kafka消息队列)"
        echo -e "- rabbitmq (RabbitMQ消息队列)"
        echo -e "- nacos (Nacos服务注册与配置中心)"
        echo -e "- consul (Consul服务网格)"
        echo -e "- elasticsearch (Elasticsearch搜索引擎)"
        echo -e "- logstash (Logstash日志收集)"
        echo -e "- kibana (Kibana日志可视化)"
        echo -e "- fluentd (Fluentd日志收集代理)"
        echo -e "- prometheus (Prometheus监控系统)"
        echo -e "- grafana (Grafana监控可视化)"
        echo -e "- jaeger (Jaeger分布式追踪)"
        echo -e "- zipkin (Zipkin分布式追踪)"
        echo -e "- nginx (Nginx反向代理)"
        echo -e "- minio (MinIO对象存储)"
        
        read -p "请输入服务名称（例如：mysql redis nacos）: " services
        
        if [ -z "$services" ]; then
            echo -e "${RED}未选择任何服务，退出脚本${NC}"
            exit 0
        fi
        
        echo -e "${YELLOW}启动自定义服务: $services ${NC}"
        docker-compose up -d $services
        ;;
    0)
        echo -e "${YELLOW}退出脚本${NC}"
        exit 0
        ;;
    *)
        echo -e "${RED}无效选项！${NC}"
        exit 1
        ;;
esac

# 等待服务启动
echo -e "${YELLOW}等待服务启动...${NC}"
sleep 10

# 检查服务状态
echo -e "${GREEN}检查服务状态:${NC}"
docker-compose ps

echo -e "${BLUE}============================================${NC}"
echo -e "${GREEN}环境已启动完成！${NC}"
echo -e "${YELLOW}访问地址:${NC}"
echo -e "Nacos控制台: http://localhost/nacos/"
echo -e "Grafana监控: http://localhost/grafana/"
echo -e "Kibana日志: http://localhost/kibana/"
echo -e "Prometheus: http://localhost/prometheus/"
echo -e "RabbitMQ管理界面: http://localhost:15672/"
echo -e "${BLUE}============================================${NC}"

# 提示关闭环境的命令
echo -e "${YELLOW}如需关闭环境，请运行：${NC}"
echo -e "docker-compose down"
echo -e "${BLUE}============================================${NC}"
