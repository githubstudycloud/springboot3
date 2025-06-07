#!/bin/bash

# 🚀 Beta环境 - Docker Swarm 集群安装脚本
# 作者: Platform Team
# 更新: 2024-01-17
# 描述: 初始化Docker Swarm集群并部署高可用服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker daemon未运行，请启动Docker"
        exit 1
    fi
    
    log_success "Docker环境检查通过"
}

# 初始化Docker Swarm
init_swarm() {
    log_info "初始化Docker Swarm集群..."
    
    if docker info --format '{{.Swarm.LocalNodeState}}' | grep -q "active"; then
        log_warning "Docker Swarm已经初始化"
        return 0
    fi
    
    # 获取本机IP地址
    local_ip=$(ip route get 8.8.8.8 | awk '{print $7; exit}' 2>/dev/null || hostname -I | awk '{print $1}')
    
    # 初始化Swarm
    docker swarm init --advertise-addr "$local_ip" || {
        log_error "Docker Swarm初始化失败"
        return 1
    }
    
    log_success "Docker Swarm初始化成功"
    
    # 显示加入命令
    log_info "工作节点加入命令:"
    docker swarm join-token worker
    
    echo ""
    log_info "管理节点加入命令:"
    docker swarm join-token manager
}

# 设置节点标签
setup_node_labels() {
    log_info "设置节点标签..."
    
    local node_id=$(docker node ls --filter role=manager --format "{{.ID}}" | head -1)
    
    # 为当前节点设置标签
    docker node update --label-add mysql-master=true "$node_id"
    docker node update --label-add mysql-slave=true "$node_id"
    docker node update --label-add redis-master=true "$node_id"
    docker node update --label-add redis-slave=true "$node_id"
    docker node update --label-add mongodb-primary=true "$node_id"
    docker node update --label-add mongodb-secondary=true "$node_id"
    
    log_success "节点标签设置完成"
}

# 创建配置文件
create_configs() {
    log_info "创建MySQL主从配置..."
    
    # 创建MySQL主节点配置
    mkdir -p config/mysql
    cat > config/mysql/master.cnf << 'EOF'
[mysqld]
log-bin=mysql-bin
server-id=1
binlog-format=ROW
gtid-mode=ON
enforce-gtid-consistency=ON
log-replica-updates=ON
binlog-do-db=platform_beta
EOF

    # 创建MySQL从节点配置
    cat > config/mysql/slave.cnf << 'EOF'
[mysqld]
server-id=2
log-bin=mysql-bin
relay-log=mysql-relay-bin
read-only=1
super-read-only=1
gtid-mode=ON
enforce-gtid-consistency=ON
log-replica-updates=ON
EOF

    log_info "创建Redis Sentinel配置..."
    
    # 创建Redis Sentinel配置
    mkdir -p config/redis
    cat > config/redis/sentinel.conf << 'EOF'
port 26379
sentinel monitor redis-master redis-master 6379 2
sentinel auth-pass redis-master beta_redis_pass_2024
sentinel down-after-milliseconds redis-master 5000
sentinel parallel-syncs redis-master 1
sentinel failover-timeout redis-master 10000
EOF

    log_info "创建Nginx负载均衡配置..."
    
    # 创建Nginx配置
    mkdir -p config/nginx
    cat > config/nginx/nginx.beta.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    upstream platform_api {
        server platform-api:8080 max_fails=3 fail_timeout=30s;
    }
    
    upstream platform_web {
        server platform-web:80 max_fails=3 fail_timeout=30s;
    }
    
    server {
        listen 80;
        server_name localhost;
        
        location /api/ {
            proxy_pass http://platform_api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
        
        location / {
            proxy_pass http://platform_web/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
EOF

    log_success "配置文件创建完成"
}

# 部署服务栈
deploy_stack() {
    log_info "部署Beta环境服务栈..."
    
    if ! docker stack ls | grep -q "platform-beta"; then
        docker stack deploy -c docker-compose.yml platform-beta
        log_success "服务栈部署完成"
    else
        log_info "更新现有服务栈..."
        docker stack deploy -c docker-compose.yml platform-beta
        log_success "服务栈更新完成"
    fi
}

# 等待服务启动
wait_for_services() {
    log_info "等待服务启动..."
    
    local max_wait=300  # 5分钟
    local wait_interval=10
    local elapsed=0
    
    while [ $elapsed -lt $max_wait ]; do
        local running_services=$(docker service ls --filter label=com.docker.stack.namespace=platform-beta --format "{{.Replicas}}" | grep -c "1/1\|2/2\|3/3" || echo 0)
        local total_services=$(docker service ls --filter label=com.docker.stack.namespace=platform-beta --format "{{.Name}}" | wc -l)
        
        if [ "$running_services" -eq "$total_services" ] && [ "$total_services" -gt 0 ]; then
            log_success "所有服务启动完成"
            return 0
        fi
        
        log_info "等待服务启动... ($running_services/$total_services 已就绪)"
        sleep $wait_interval
        elapsed=$((elapsed + wait_interval))
    done
    
    log_warning "部分服务可能未完全启动，请检查服务状态"
}

# 配置MySQL主从复制
setup_mysql_replication() {
    log_info "配置MySQL主从复制..."
    
    # 等待MySQL主节点启动
    sleep 60
    
    # 获取主节点状态
    local master_status=$(docker exec $(docker ps -q -f name=platform-beta_mysql-master) mysql -u root -pbeta_root_pass_2024 -e "SHOW MASTER STATUS\G")
    local master_file=$(echo "$master_status" | grep "File:" | awk '{print $2}')
    local master_pos=$(echo "$master_status" | grep "Position:" | awk '{print $2}')
    
    if [ -n "$master_file" ] && [ -n "$master_pos" ]; then
        # 配置从节点
        docker exec $(docker ps -q -f name=platform-beta_mysql-slave | head -1) mysql -u root -pbeta_root_pass_2024 -e "
            STOP SLAVE;
            CHANGE MASTER TO 
                MASTER_HOST='mysql-master',
                MASTER_USER='repl_user',
                MASTER_PASSWORD='beta_repl_pass_2024',
                MASTER_LOG_FILE='$master_file',
                MASTER_LOG_POS=$master_pos;
            START SLAVE;
        " || log_warning "MySQL主从复制配置可能失败，请手动检查"
        
        log_success "MySQL主从复制配置完成"
    else
        log_warning "无法获取MySQL主节点状态，请手动配置复制"
    fi
}

# 初始化MongoDB副本集
setup_mongodb_replica() {
    log_info "初始化MongoDB副本集..."
    
    # 等待MongoDB启动
    sleep 60
    
    # 初始化副本集
    docker exec $(docker ps -q -f name=platform-beta_mongodb-primary) mongosh --eval "
        rs.initiate({
            _id: 'rs0',
            members: [
                { _id: 0, host: 'mongodb-primary:27017', priority: 2 },
                { _id: 1, host: 'mongodb-secondary:27017', priority: 1 }
            ]
        })
    " || log_warning "MongoDB副本集初始化可能失败，请手动检查"
    
    log_success "MongoDB副本集初始化完成"
}

# 显示集群状态
show_cluster_status() {
    log_success "🎉 Beta集群环境部署完成！"
    echo ""
    echo "📊 集群状态:"
    docker node ls
    echo ""
    echo "🔧 服务状态:"
    docker service ls --filter label=com.docker.stack.namespace=platform-beta
    echo ""
    echo "📊 服务访问地址:"
    echo "  MySQL Master: localhost:3306 (用户: platform_user, 密码: beta_user_pass_2024)"
    echo "  MySQL Slave:  localhost:3307"
    echo "  Redis Master: redis-master:6379 (密码: beta_redis_pass_2024)"
    echo "  Redis Sentinel: localhost:26379"
    echo "  MongoDB:      mongodb-primary:27017 (用户: mongo_admin, 密码: beta_mongo_pass_2024)"
    echo "  Nacos:        http://localhost:8848/nacos"
    echo "  RabbitMQ:     http://localhost:15672 (用户: rabbit_admin, 密码: beta_rabbit_pass_2024)"
    echo "  Kafka:        kafka:9092"
    echo "  RocketMQ:     rocketmq-nameserver:9876"
    echo "  Nginx:        http://localhost:80"
    echo "  Prometheus:   http://localhost:9090"
    echo "  Grafana:      http://localhost:3000 (用户: admin, 密码: beta_grafana_pass_2024)"
    echo ""
    echo "🔧 管理命令:"
    echo "  查看服务: docker service ls"
    echo "  查看日志: docker service logs <服务名>"
    echo "  扩展服务: docker service scale <服务名>=<副本数>"
    echo "  删除栈: docker stack rm platform-beta"
    echo ""
    echo "📚 文档: 查看 environments/beta/README.md 获取更多信息"
}

# 清理集群
cleanup_cluster() {
    log_info "清理Beta集群环境..."
    
    # 删除服务栈
    if docker stack ls | grep -q "platform-beta"; then
        docker stack rm platform-beta
        log_success "服务栈已删除"
    fi
    
    # 等待服务完全停止
    log_info "等待服务完全停止..."
    while docker service ls | grep -q "platform-beta"; do
        sleep 5
    done
    
    # 删除数据卷（可选）
    read -p "是否删除所有数据卷？这将删除所有数据！(y/N): " confirm
    if [[ $confirm == [yY] ]]; then
        docker volume prune -f
        log_warning "数据卷已删除"
    fi
    
    log_success "集群清理完成"
}

# 主函数
main() {
    case "$1" in
        --cleanup)
            cleanup_cluster
            exit 0
            ;;
        --status)
            show_cluster_status
            exit 0
            ;;
        --help)
            echo "用法: $0 [选项]"
            echo "选项:"
            echo "  --cleanup    清理Beta集群环境"
            echo "  --status     显示集群状态"
            echo "  --help       显示帮助信息"
            exit 0
            ;;
    esac
    
    log_info "开始部署Beta集群环境..."
    
    check_docker
    init_swarm
    setup_node_labels
    create_configs
    deploy_stack
    wait_for_services
    
    # 配置集群服务
    setup_mysql_replication
    setup_mongodb_replica
    
    show_cluster_status
}

# 脚本入口
main "$@" 