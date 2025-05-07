#!/bin/bash

# 设置颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 无颜色

# 打印横幅
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}      企业级数据平台 - 环境目录创建      ${NC}"
echo -e "${BLUE}============================================${NC}"

# 创建目录结构
mkdir -p deploy/single-node/{config,docker,init,logs,static}
mkdir -p deploy/single-node/config/{fluentd,logstash/pipeline,nginx/conf.d,prometheus}
mkdir -p deploy/single-node/docker/{app,mysql}
mkdir -p deploy/single-node/init/mysql
mkdir -p deploy/single-node/logs/nginx

# 将文件移动到新的目录结构
if [ -f "docker-compose.yml" ]; then
    mv docker-compose.yml deploy/single-node/
    echo -e "${GREEN}已移动: docker-compose.yml${NC}"
fi

if [ -f ".env" ]; then
    mv .env deploy/single-node/
    echo -e "${GREEN}已移动: .env${NC}"
fi

if [ -f "docker-compose-app.yml" ]; then
    mv docker-compose-app.yml deploy/single-node/
    echo -e "${GREEN}已移动: docker-compose-app.yml${NC}"
fi

if [ -f "docker-compose-frontend.yml" ]; then
    mv docker-compose-frontend.yml deploy/single-node/
    echo -e "${GREEN}已移动: docker-compose-frontend.yml${NC}"
fi

if [ -f "start.sh" ]; then
    mv start.sh deploy/single-node/
    echo -e "${GREEN}已移动: start.sh${NC}"
fi

if [ -f "stop.sh" ]; then
    mv stop.sh deploy/single-node/
    echo -e "${GREEN}已移动: stop.sh${NC}"
fi

if [ -f "README.md" ]; then
    mv README.md deploy/single-node/
    echo -e "${GREEN}已移动: README.md${NC}"
fi

# 移动config目录下的文件
if [ -d "config" ]; then
    find config -type f -exec bash -c 'mkdir -p $(dirname "deploy/single-node/{}") && mv "{}" "deploy/single-node/{}"' \;
    echo -e "${GREEN}已移动: config目录下的文件${NC}"
    rm -rf config
fi

# 移动docker目录下的文件
if [ -d "docker" ]; then
    find docker -type f -exec bash -c 'mkdir -p $(dirname "deploy/single-node/{}") && mv "{}" "deploy/single-node/{}"' \;
    echo -e "${GREEN}已移动: docker目录下的文件${NC}"
    rm -rf docker
fi

# 移动init目录下的文件
if [ -d "init" ]; then
    find init -type f -exec bash -c 'mkdir -p $(dirname "deploy/single-node/{}") && mv "{}" "deploy/single-node/{}"' \;
    echo -e "${GREEN}已移动: init目录下的文件${NC}"
    rm -rf init
fi

echo -e "${BLUE}============================================${NC}"
echo -e "${GREEN}目录结构已创建完成!${NC}"
echo -e "${YELLOW}新的目录结构为:${NC}"
echo -e "deploy/"
echo -e "└── single-node/        # 单机部署环境"
echo -e "    ├── config/         # 服务配置文件"
echo -e "    ├── docker/         # Dockerfile"
echo -e "    ├── init/           # 初始化脚本"
echo -e "    ├── logs/           # 日志目录"
echo -e "    ├── static/         # 静态资源"
echo -e "    ├── docker-compose.yml     # 主配置文件"
echo -e "    ├── docker-compose-app.yml # 应用配置"
echo -e "    ├── docker-compose-frontend.yml # 前端配置"
echo -e "    ├── .env            # 环境变量"
echo -e "    ├── start.sh        # 启动脚本"
echo -e "    ├── stop.sh         # 停止脚本"
echo -e "    └── README.md       # 说明文档"
echo -e "${BLUE}============================================${NC}"
echo -e "${YELLOW}您现在可以在deploy目录下创建集群配置了!${NC}"
echo -e "${BLUE}============================================${NC}"
