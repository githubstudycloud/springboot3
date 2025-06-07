#!/bin/bash

# ==================================================
# SpringBoot + Vue 项目启动脚本
# ==================================================

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印彩色输出
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

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        print_error "$1 未安装，请先安装 $1"
        exit 1
    fi
}

# 检查环境
check_environment() {
    print_info "检查开发环境..."
    
    # 检查 Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        print_success "Java 版本: $JAVA_VERSION"
    else
        print_error "Java 未安装，请安装 JDK 21+"
        exit 1
    fi
    
    # 检查 Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        print_success "Maven 版本: $MVN_VERSION"
    else
        print_error "Maven 未安装"
        exit 1
    fi
    
    # 检查 Node.js
    if command -v node &> /dev/null; then
        NODE_VERSION=$(node --version)
        print_success "Node.js 版本: $NODE_VERSION"
    else
        print_error "Node.js 未安装，请安装 Node.js 18+"
        exit 1
    fi
    
    # 检查 npm
    if command -v npm &> /dev/null; then
        NPM_VERSION=$(npm --version)
        print_success "npm 版本: $NPM_VERSION"
    else
        print_error "npm 未安装"
        exit 1
    fi
    
    # 检查 Docker (可选)
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | cut -d',' -f1)
        print_success "Docker 版本: $DOCKER_VERSION"
    else
        print_warning "Docker 未安装，将跳过容器化相关功能"
    fi
}

# 启动数据库服务
start_database() {
    print_info "启动数据库服务..."
    
    if command -v docker-compose &> /dev/null || command -v docker &> /dev/null; then
        docker-compose up -d mysql redis
        print_success "数据库服务启动成功"
        
        # 等待数据库启动
        print_info "等待数据库启动..."
        sleep 10
    else
        print_warning "Docker 未安装，请手动启动 MySQL 和 Redis"
    fi
}

# 启动后端服务
start_backend() {
    print_info "启动后端服务..."
    
    if [ -d "backend" ]; then
        cd backend
        
        # 检查是否需要编译
        if [ ! -d "target" ]; then
            print_info "首次启动，正在编译项目..."
            mvn clean compile
        fi
        
        # 启动 Spring Boot
        print_info "启动 Spring Boot 应用..."
        mvn spring-boot:run &
        BACKEND_PID=$!
        
        cd ..
        print_success "后端服务启动成功 (PID: $BACKEND_PID)"
        echo $BACKEND_PID > .backend.pid
    else
        print_error "backend 目录不存在"
        exit 1
    fi
}

# 启动前端服务
start_frontend() {
    print_info "启动前端服务..."
    
    if [ -d "frontend" ]; then
        cd frontend
        
        # 检查是否安装了依赖
        if [ ! -d "node_modules" ]; then
            print_info "首次启动，正在安装依赖..."
            npm install
        fi
        
        # 启动 Vue 开发服务器
        print_info "启动 Vue 开发服务器..."
        npm run dev &
        FRONTEND_PID=$!
        
        cd ..
        print_success "前端服务启动成功 (PID: $FRONTEND_PID)"
        echo $FRONTEND_PID > .frontend.pid
    else
        print_error "frontend 目录不存在"
        exit 1
    fi
}

# 等待服务启动
wait_for_services() {
    print_info "等待服务启动完成..."
    
    # 等待后端服务
    print_info "检查后端服务..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "后端服务就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            print_warning "后端服务启动超时，请检查日志"
        fi
        sleep 2
    done
    
    # 等待前端服务
    print_info "检查前端服务..."
    sleep 5
    print_success "前端服务就绪"
}

# 显示服务信息
show_services() {
    print_success "==================== 服务启动完成 ===================="
    echo
    print_info "服务地址："
    echo "  🌐 前端应用: http://localhost:5173"
    echo "  🚀 后端API: http://localhost:8080"
    echo "  📊 API文档: http://localhost:8080/swagger-ui/index.html"
    echo "  🗄️  数据库管理: http://localhost:8081 (Adminer)"
    echo "  🔴 Redis管理: http://localhost:8082 (Redis Commander)"
    echo
    print_info "停止服务："
    echo "  ./scripts/stop.sh"
    echo
    print_success "======================================================"
}

# 主函数
main() {
    print_info "启动 SpringBoot + Vue 全栈项目..."
    echo
    
    # 检查环境
    check_environment
    echo
    
    # 启动数据库
    start_database
    echo
    
    # 启动后端
    start_backend
    sleep 5
    
    # 启动前端
    start_frontend
    echo
    
    # 等待服务启动
    wait_for_services
    echo
    
    # 显示服务信息
    show_services
}

# 执行主函数
main 