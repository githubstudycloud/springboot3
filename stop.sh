#!/bin/bash

# 设置颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # 无颜色

# 打印横幅
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}      企业级数据平台 - 环境关闭脚本      ${NC}"
echo -e "${BLUE}============================================${NC}"

# 检查 Docker 是否已安装
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装！${NC}"
    exit 1
fi

# 检查 Docker Compose 是否已安装
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: Docker Compose 未安装！${NC}"
    exit 1
fi

# 询问关闭方式
echo -e "${GREEN}请选择关闭方式:${NC}"
echo -e "1) 停止服务但保留数据卷"
echo -e "2) 完全卸载（包括数据卷）"
echo -e "0) 取消操作"

read -p "请输入选项 [0-2]: " option

case $option in
    1)
        echo -e "${YELLOW}正在停止所有服务...${NC}"
        docker-compose down
        echo -e "${GREEN}所有服务已停止，数据卷已保留。${NC}"
        ;;
    2)
        echo -e "${RED}警告：此操作将删除所有容器及其关联的数据卷！${NC}"
        read -p "确定要继续吗？(y/n): " confirm
        if [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]]; then
            echo -e "${YELLOW}正在删除所有服务和数据卷...${NC}"
            docker-compose down -v
            echo -e "${GREEN}所有服务和数据卷已删除。${NC}"
        else
            echo -e "${YELLOW}操作已取消。${NC}"
            exit 0
        fi
        ;;
    0)
        echo -e "${YELLOW}操作已取消。${NC}"
        exit 0
        ;;
    *)
        echo -e "${RED}无效选项！${NC}"
        exit 1
        ;;
esac

echo -e "${BLUE}============================================${NC}"
