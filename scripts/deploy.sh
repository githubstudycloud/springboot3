#!/bin/bash

# ==================================================
# SpringBoot + Vue 项目部署脚本
# ==================================================

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 配置参数
DEPLOY_ENV=${1:-prod}
BUILD_VERSION=$(date +%Y%m%d_%H%M%S)

print_info "开始部署 - 环境: $DEPLOY_ENV, 版本: $BUILD_VERSION"

# 检查环境
check_prerequisites() {
    print_info "检查部署环境..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装"
        exit 1
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装"
        exit 1
    fi
    
    print_success "环境检查通过"
}

# 构建后端
build_backend() {
    print_info "构建后端应用..."
    
    if [ -d "backend" ]; then
        cd backend
        
        # Maven 打包
        print_info "执行 Maven 打包..."
        mvn clean package -DskipTests
        
        if [ $? -eq 0 ]; then
            print_success "后端构建成功"
        else
            print_error "后端构建失败"
            exit 1
        fi
        
        cd ..
    else
        print_error "backend 目录不存在"
        exit 1
    fi
}

# 构建前端
build_frontend() {
    print_info "构建前端应用..."
    
    if [ -d "frontend" ]; then
        cd frontend
        
        # 安装依赖
        print_info "安装前端依赖..."
        npm ci
        
        # 构建生产版本
        print_info "构建生产版本..."
        npm run build
        
        if [ $? -eq 0 ]; then
            print_success "前端构建成功"
        else
            print_error "前端构建失败"
            exit 1
        fi
        
        cd ..
    else
        print_error "frontend 目录不存在"
        exit 1
    fi
}

# 构建 Docker 镜像
build_images() {
    print_info "构建 Docker 镜像..."
    
    # 构建后端镜像
    print_info "构建后端镜像..."
    docker build -t project-backend:$BUILD_VERSION ./backend
    docker tag project-backend:$BUILD_VERSION project-backend:latest
    
    # 构建前端镜像
    print_info "构建前端镜像..."
    docker build -t project-frontend:$BUILD_VERSION ./frontend
    docker tag project-frontend:$BUILD_VERSION project-frontend:latest
    
    print_success "Docker 镜像构建完成"
}

# 备份数据库
backup_database() {
    print_info "备份数据库..."
    
    # 创建备份目录
    mkdir -p database/backup
    
    # 备份 MySQL
    BACKUP_FILE="database/backup/mysql_backup_$BUILD_VERSION.sql"
    docker exec project-mysql mysqldump -u root -prootpass mydb > $BACKUP_FILE
    
    if [ $? -eq 0 ]; then
        print_success "数据库备份完成: $BACKUP_FILE"
    else
        print_warning "数据库备份失败，继续部署"
    fi
}

# 停止旧服务
stop_old_services() {
    print_info "停止旧服务..."
    
    docker-compose down
    
    # 清理未使用的镜像
    docker image prune -f
    
    print_success "旧服务已停止"
}

# 启动新服务
start_new_services() {
    print_info "启动新服务..."
    
    # 设置环境变量
    export COMPOSE_PROFILES=prod
    
    # 启动服务
    docker-compose up -d
    
    print_success "新服务启动完成"
}

# 健康检查
health_check() {
    print_info "执行健康检查..."
    
    # 等待服务启动
    sleep 30
    
    # 检查后端健康状态
    print_info "检查后端服务..."
    for i in {1..10}; do
        if curl -s http://localhost:8080/actuator/health | grep -q "UP"; then
            print_success "后端服务健康检查通过"
            break
        fi
        if [ $i -eq 10 ]; then
            print_error "后端服务健康检查失败"
            exit 1
        fi
        sleep 5
    done
    
    # 检查前端服务
    print_info "检查前端服务..."
    if curl -s http://localhost:80 > /dev/null; then
        print_success "前端服务健康检查通过"
    else
        print_error "前端服务健康检查失败"
        exit 1
    fi
}

# 部署成功通知
deployment_success() {
    print_success "================== 部署成功 =================="
    echo
    print_info "部署信息："
    echo "  📅 部署时间: $(date)"
    echo "  🏷️  版本号: $BUILD_VERSION"
    echo "  🌍 环境: $DEPLOY_ENV"
    echo
    print_info "服务地址："
    echo "  🌐 前端应用: http://localhost"
    echo "  🚀 后端API: http://localhost:8080"
    echo "  📊 API文档: http://localhost:8080/swagger-ui/index.html"
    echo
    print_info "管理工具："
    echo "  🗄️  数据库: http://localhost:8081"
    echo "  🔴 Redis: http://localhost:8082"
    echo
    print_success "=============================================="
}

# 回滚函数
rollback() {
    print_warning "开始回滚..."
    
    # 停止当前服务
    docker-compose down
    
    # 这里可以添加回滚到上一个版本的逻辑
    print_info "回滚功能需要根据具体需求实现"
}

# 主函数
main() {
    case "${2:-deploy}" in
        "rollback")
            rollback
            ;;
        "deploy"|*)
            print_info "开始自动化部署..."
            
            # 检查环境
            check_prerequisites
            
            # 备份数据库
            backup_database
            
            # 构建应用
            build_backend
            build_frontend
            
            # 构建镜像
            build_images
            
            # 停止旧服务
            stop_old_services
            
            # 启动新服务
            start_new_services
            
            # 健康检查
            health_check
            
            # 部署成功
            deployment_success
            ;;
    esac
}

# 执行主函数
main "$@" 