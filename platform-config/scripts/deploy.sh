#!/bin/bash

# Platform Config Server 部署脚本
# 支持 Docker 单机、Docker 集群和 Kubernetes 部署

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# 帮助信息
show_help() {
    cat << EOF
Platform Config Server 部署脚本

Usage: $0 [OPTIONS] COMMAND

COMMANDS:
    docker-single     Docker 单机部署
    docker-cluster    Docker Swarm 集群部署
    k8s              Kubernetes 集群部署
    build            构建镜像
    clean            清理资源
    help             显示帮助信息

OPTIONS:
    -e, --env ENV           环境 (dev|test|pub, 默认: dev)
    -r, --registry REGISTRY 镜像仓库地址
    -t, --tag TAG          镜像标签 (默认: latest)
    -n, --namespace NS     Kubernetes命名空间 (默认: platform)
    --git-uri URI          GitLab仓库地址
    --git-username USER    GitLab用户名
    --git-password PASS    GitLab密码
    --config-user USER     Config Server用户名
    --config-pass PASS     Config Server密码
    -v, --verbose          详细输出
    -h, --help             显示帮助

Examples:
    $0 docker-single -e dev
    $0 docker-cluster -e test --git-uri https://gitlab.example.com/config/platform-config.git
    $0 k8s -e pub -n production
    $0 build -t v1.0.0
    $0 clean

EOF
}

# 默认参数
ENV="dev"
REGISTRY=""
TAG="latest"
NAMESPACE="platform"
GIT_URI=""
GIT_USERNAME=""
GIT_PASSWORD=""
CONFIG_USER="config-admin"
CONFIG_PASS="config-admin-2024"
VERBOSE=false

# 解析参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--env)
                ENV="$2"
                shift 2
                ;;
            -r|--registry)
                REGISTRY="$2"
                shift 2
                ;;
            -t|--tag)
                TAG="$2"
                shift 2
                ;;
            -n|--namespace)
                NAMESPACE="$2"
                shift 2
                ;;
            --git-uri)
                GIT_URI="$2"
                shift 2
                ;;
            --git-username)
                GIT_USERNAME="$2"
                shift 2
                ;;
            --git-password)
                GIT_PASSWORD="$2"
                shift 2
                ;;
            --config-user)
                CONFIG_USER="$2"
                shift 2
                ;;
            --config-pass)
                CONFIG_PASS="$2"
                shift 2
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            -*)
                log_error "Unknown option $1"
                show_help
                exit 1
                ;;
            *)
                COMMAND="$1"
                shift
                ;;
        esac
    done
}

# 检查依赖
check_dependencies() {
    local missing_deps=()
    
    case $COMMAND in
        docker-single|docker-cluster)
            if ! command -v docker &> /dev/null; then
                missing_deps+=("docker")
            fi
            if [[ $COMMAND == "docker-cluster" ]] && ! command -v docker &> /dev/null; then
                log_error "Docker Swarm mode not available"
                exit 1
            fi
            ;;
        k8s)
            if ! command -v kubectl &> /dev/null; then
                missing_deps+=("kubectl")
            fi
            ;;
        build)
            if ! command -v docker &> /dev/null; then
                missing_deps+=("docker")
            fi
            if ! command -v mvn &> /dev/null; then
                missing_deps+=("maven")
            fi
            ;;
    esac
    
    if [[ ${#missing_deps[@]} -gt 0 ]]; then
        log_error "Missing dependencies: ${missing_deps[*]}"
        exit 1
    fi
}

# 构建镜像
build_image() {
    log_info "Building Config Server image..."
    
    # 构建 JAR
    if [[ ! -f target/platform-config-server-*.jar ]]; then
        log_info "Building JAR file..."
        mvn clean package -DskipTests
    fi
    
    # 构建 Docker 镜像
    local image_name="platform/config-server"
    if [[ -n $REGISTRY ]]; then
        image_name="$REGISTRY/platform/config-server"
    fi
    
    log_info "Building Docker image: $image_name:$TAG"
    docker build -t "$image_name:$TAG" .
    
    if [[ -n $REGISTRY ]]; then
        log_info "Pushing image to registry..."
        docker push "$image_name:$TAG"
    fi
    
    log_info "Image build completed: $image_name:$TAG"
}

# Docker 单机部署
deploy_docker_single() {
    log_info "Deploying Config Server with Docker Compose..."
    
    # 准备环境变量文件
    cat > .env << EOF
ENV=$ENV
TAG=$TAG
GIT_URI=$GIT_URI
GIT_USERNAME=$GIT_USERNAME
GIT_PASSWORD=$GIT_PASSWORD
CONFIG_USERNAME=$CONFIG_USER
CONFIG_PASSWORD=$CONFIG_PASS
EOF
    
    # 启动服务
    docker-compose up -d
    
    # 等待服务启动
    log_info "Waiting for services to start..."
    sleep 30
    
    # 检查服务状态
    check_service_health "http://localhost:8888/config/actuator/health"
    
    log_info "Config Server deployed successfully!"
    log_info "Access URL: http://localhost:8888/config"
    log_info "Username: $CONFIG_USER"
    log_info "Password: $CONFIG_PASS"
}

# Docker 集群部署
deploy_docker_cluster() {
    log_info "Deploying Config Server with Docker Swarm..."
    
    # 初始化 Swarm（如果未初始化）
    if ! docker info --format '{{.Swarm.LocalNodeState}}' | grep -q active; then
        log_info "Initializing Docker Swarm..."
        docker swarm init
    fi
    
    # 创建配置
    echo "$GIT_USERNAME" | docker secret create config-git-username - || true
    echo "$GIT_PASSWORD" | docker secret create config-git-password - || true
    echo "$CONFIG_USER" | docker secret create config-username - || true
    echo "$CONFIG_PASS" | docker secret create config-password - || true
    
    # 准备 stack 文件
    cat > docker-stack.yml << EOF
version: '3.8'

services:
  config-server:
    image: platform/config-server:$TAG
    ports:
      - "8888:8888"
    environment:
      - JAVA_OPTS=-Xms512m -Xmx1g -Dspring.profiles.active=cluster
      - CONFIG_GIT_URI=$GIT_URI
      - NACOS_SERVER_ADDR=nacos:8848
      - CONFIG_GITLAB_ENABLED=true
    secrets:
      - config-git-username
      - config-git-password
      - config-username
      - config-password
    volumes:
      - ./config-repository:/app/config:ro
    deploy:
      replicas: 3
      restart_policy:
        condition: on-failure
      placement:
        constraints:
          - node.role == worker
    networks:
      - platform-network

networks:
  platform-network:
    driver: overlay
    external: true

secrets:
  config-git-username:
    external: true
  config-git-password:
    external: true
  config-username:
    external: true
  config-password:
    external: true
EOF
    
    # 部署 stack
    docker stack deploy -c docker-stack.yml platform-config
    
    log_info "Config Server cluster deployed successfully!"
    log_info "Access URL: http://localhost:8888/config"
}

# Kubernetes 部署
deploy_k8s() {
    log_info "Deploying Config Server to Kubernetes..."
    
    # 创建命名空间
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    
    # 创建 Secret
    kubectl create secret generic config-server-secret \
        --from-literal=git-uri="$GIT_URI" \
        --from-literal=git-username="$GIT_USERNAME" \
        --from-literal=git-password="$GIT_PASSWORD" \
        --from-literal=config-username="$CONFIG_USER" \
        --from-literal=config-password="$CONFIG_PASS" \
        --from-literal=encrypt-key="platform-config-secret-key-2024" \
        -n $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    
    # 创建 PVC
    cat << EOF | kubectl apply -f -
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: config-backup-pvc
  namespace: $NAMESPACE
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: config-logs-pvc
  namespace: $NAMESPACE
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
EOF
    
    # 应用 ConfigMap 和 Deployment
    kubectl apply -f k8s/ -n $NAMESPACE
    
    # 等待部署完成
    kubectl rollout status deployment/platform-config-server -n $NAMESPACE
    
    # 获取访问信息
    local nodeport=$(kubectl get svc config-server-lb -n $NAMESPACE -o jsonpath='{.spec.ports[0].nodePort}')
    local cluster_ip=$(kubectl get svc config-server-service -n $NAMESPACE -o jsonpath='{.spec.clusterIP}')
    
    log_info "Config Server deployed to Kubernetes successfully!"
    log_info "Cluster IP: http://$cluster_ip:8888/config"
    log_info "NodePort: http://<node-ip>:$nodeport/config"
}

# 检查服务健康状态
check_service_health() {
    local url=$1
    local max_attempts=30
    local attempt=1
    
    while [[ $attempt -le $max_attempts ]]; do
        if curl -f -s "$url" > /dev/null 2>&1; then
            log_info "Service is healthy!"
            return 0
        fi
        
        log_debug "Health check attempt $attempt/$max_attempts failed, retrying..."
        sleep 10
        ((attempt++))
    done
    
    log_error "Service health check failed after $max_attempts attempts"
    return 1
}

# 清理资源
clean_resources() {
    log_info "Cleaning up resources..."
    
    case $1 in
        docker-single)
            docker-compose down -v
            ;;
        docker-cluster)
            docker stack rm platform-config
            docker secret rm config-git-username config-git-password config-username config-password || true
            ;;
        k8s)
            kubectl delete namespace $NAMESPACE --ignore-not-found=true
            ;;
        all)
            docker-compose down -v || true
            docker stack rm platform-config || true
            kubectl delete namespace $NAMESPACE --ignore-not-found=true || true
            docker rmi platform/config-server:$TAG || true
            ;;
    esac
    
    log_info "Cleanup completed"
}

# 主函数
main() {
    parse_args "$@"
    
    if [[ -z $COMMAND ]]; then
        log_error "No command specified"
        show_help
        exit 1
    fi
    
    check_dependencies
    
    case $COMMAND in
        build)
            build_image
            ;;
        docker-single)
            deploy_docker_single
            ;;
        docker-cluster)
            deploy_docker_cluster
            ;;
        k8s)
            deploy_k8s
            ;;
        clean)
            clean_resources all
            ;;
        help)
            show_help
            ;;
        *)
            log_error "Unknown command: $COMMAND"
            show_help
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@" 