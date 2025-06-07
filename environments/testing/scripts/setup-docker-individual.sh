#!/bin/bash

# 🧪 测试环境 - 单独Docker容器安装脚本
# 作者: Platform Team
# 更新: 2024-01-17
# 描述: 逐个启动Docker容器，适合资源有限的开发机器

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

# 清理现有容器
cleanup_containers() {
    log_info "清理现有的测试环境容器..."
    
    containers=(
        "mysql-testing"
        "redis-testing"
        "mongodb-testing"
        "nacos-testing"
        "rabbitmq-testing"
        "zookeeper-testing"
        "kafka-testing"
        "rocketmq-nameserver-testing"
        "rocketmq-broker-testing"
        "prometheus-testing"
        "grafana-testing"
        "nginx-testing"
        "platform-api-testing"
        "platform-web-testing"
    )
    
    for container in "${containers[@]}"; do
        if docker ps -a -q -f name="$container" | grep -q .; then
            log_warning "停止并删除容器: $container"
            docker stop "$container" 2>/dev/null || true
            docker rm "$container" 2>/dev/null || true
        fi
    done
    
    # 删除网络
    if docker network ls | grep -q "platform-testing"; then
        log_warning "删除网络: platform-testing"
        docker network rm platform-testing 2>/dev/null || true
    fi
}

# 创建网络
create_network() {
    log_info "创建Docker网络: platform-testing"
    docker network create platform-testing \
        --driver bridge \
        --subnet=172.20.0.0/16 \
        --ip-range=172.20.240.0/20
    log_success "网络创建成功"
}

# 启动MySQL
start_mysql() {
    log_info "启动MySQL数据库..."
    docker run -d \
        --name mysql-testing \
        --network platform-testing \
        -e MYSQL_ROOT_PASSWORD=testing_root_pass_2024 \
        -e MYSQL_DATABASE=platform_testing \
        -e MYSQL_USER=platform_user \
        -e MYSQL_PASSWORD=testing_user_pass_2024 \
        -p 3306:3306 \
        -v mysql_testing_data:/var/lib/mysql \
        -v "$(pwd)/../../database/schema/init:/docker-entrypoint-initdb.d:ro" \
        --restart unless-stopped \
        mysql:8.0 \
        --default-authentication-plugin=mysql_native_password \
        --character-set-server=utf8mb4 \
        --collation-server=utf8mb4_unicode_ci
    
    log_info "等待MySQL启动..."
    sleep 30
    
    # 健康检查
    for i in {1..30}; do
        if docker exec mysql-testing mysqladmin ping -h localhost --silent; then
            log_success "MySQL启动成功"
            return 0
        fi
        log_info "等待MySQL就绪... ($i/30)"
        sleep 2
    done
    
    log_error "MySQL启动失败"
    return 1
}

# 启动Redis
start_redis() {
    log_info "启动Redis缓存..."
    docker run -d \
        --name redis-testing \
        --network platform-testing \
        -p 6379:6379 \
        -v redis_testing_data:/data \
        --restart unless-stopped \
        redis:7-alpine \
        redis-server --requirepass testing_redis_pass_2024 --appendonly yes
    
    log_info "等待Redis启动..."
    sleep 10
    
    # 健康检查
    if docker exec redis-testing redis-cli -a testing_redis_pass_2024 ping | grep -q PONG; then
        log_success "Redis启动成功"
    else
        log_error "Redis启动失败"
        return 1
    fi
}

# 启动MongoDB
start_mongodb() {
    log_info "启动MongoDB文档数据库..."
    docker run -d \
        --name mongodb-testing \
        --network platform-testing \
        -e MONGO_INITDB_ROOT_USERNAME=mongo_admin \
        -e MONGO_INITDB_ROOT_PASSWORD=testing_mongo_pass_2024 \
        -e MONGO_INITDB_DATABASE=platform_testing \
        -p 27017:27017 \
        -v mongodb_testing_data:/data/db \
        --restart unless-stopped \
        mongo:7
    
    log_info "等待MongoDB启动..."
    sleep 20
    
    # 健康检查
    for i in {1..15}; do
        if docker exec mongodb-testing mongosh --host localhost --eval "db.runCommand('ping')" --quiet; then
            log_success "MongoDB启动成功"
            return 0
        fi
        log_info "等待MongoDB就绪... ($i/15)"
        sleep 2
    done
    
    log_error "MongoDB启动失败"
    return 1
}

# 启动Nacos
start_nacos() {
    log_info "启动Nacos服务注册中心..."
    docker run -d \
        --name nacos-testing \
        --network platform-testing \
        -e MODE=standalone \
        -e NACOS_AUTH_ENABLE=true \
        -e NACOS_AUTH_TOKEN=testing_nacos_token_2024 \
        -e NACOS_AUTH_IDENTITY_KEY=testing_nacos_identity \
        -e NACOS_AUTH_IDENTITY_VALUE=testing_nacos_value \
        -e JVM_XMS=512m \
        -e JVM_XMX=512m \
        -p 8848:8848 \
        -p 9848:9848 \
        -v nacos_testing_logs:/home/nacos/logs \
        -v nacos_testing_data:/home/nacos/data \
        --restart unless-stopped \
        nacos/nacos-server:v2.3.0
    
    log_info "等待Nacos启动..."
    sleep 60
    
    # 健康检查
    for i in {1..30}; do
        if curl -f http://localhost:8848/nacos/v1/console/health/readiness 2>/dev/null; then
            log_success "Nacos启动成功"
            return 0
        fi
        log_info "等待Nacos就绪... ($i/30)"
        sleep 2
    done
    
    log_error "Nacos启动失败"
    return 1
}

# 启动RabbitMQ
start_rabbitmq() {
    log_info "启动RabbitMQ消息队列..."
    docker run -d \
        --name rabbitmq-testing \
        --network platform-testing \
        -e RABBITMQ_DEFAULT_USER=rabbit_admin \
        -e RABBITMQ_DEFAULT_PASS=testing_rabbit_pass_2024 \
        -e RABBITMQ_DEFAULT_VHOST=platform_testing \
        -p 5672:5672 \
        -p 15672:15672 \
        -v rabbitmq_testing_data:/var/lib/rabbitmq \
        --restart unless-stopped \
        rabbitmq:3.12-management
    
    log_info "等待RabbitMQ启动..."
    sleep 30
    
    # 健康检查
    for i in {1..20}; do
        if docker exec rabbitmq-testing rabbitmq-diagnostics -q ping; then
            log_success "RabbitMQ启动成功"
            return 0
        fi
        log_info "等待RabbitMQ就绪... ($i/20)"
        sleep 3
    done
    
    log_error "RabbitMQ启动失败"
    return 1
}

# 启动Kafka
start_kafka() {
    log_info "启动Zookeeper..."
    docker run -d \
        --name zookeeper-testing \
        --network platform-testing \
        -e ZOOKEEPER_CLIENT_PORT=2181 \
        -e ZOOKEEPER_TICK_TIME=2000 \
        -e ZOOKEEPER_LOG4J_ROOT_LOGLEVEL=WARN \
        -v zookeeper_testing_data:/var/lib/zookeeper/data \
        -v zookeeper_testing_logs:/var/lib/zookeeper/log \
        --restart unless-stopped \
        confluentinc/cp-zookeeper:7.4.0
    
    log_info "等待Zookeeper启动..."
    sleep 20
    
    log_info "启动Kafka..."
    docker run -d \
        --name kafka-testing \
        --network platform-testing \
        -e KAFKA_BROKER_ID=1 \
        -e KAFKA_ZOOKEEPER_CONNECT=zookeeper-testing:2181 \
        -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT \
        -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka-testing:29092 \
        -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
        -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
        -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
        -e KAFKA_LOG4J_ROOT_LOGLEVEL=WARN \
        -p 9092:9092 \
        -v kafka_testing_data:/var/lib/kafka/data \
        --restart unless-stopped \
        confluentinc/cp-kafka:7.4.0
    
    log_info "等待Kafka启动..."
    sleep 30
    
    # 健康检查
    for i in {1..20}; do
        if docker exec kafka-testing kafka-broker-api-versions --bootstrap-server localhost:9092 &>/dev/null; then
            log_success "Kafka启动成功"
            return 0
        fi
        log_info "等待Kafka就绪... ($i/20)"
        sleep 3
    done
    
    log_error "Kafka启动失败"
    return 1
}

# 启动RocketMQ
start_rocketmq() {
    log_info "启动RocketMQ NameServer..."
    docker run -d \
        --name rocketmq-nameserver-testing \
        --network platform-testing \
        -e JAVA_OPT="-Duser.home=/home/rocketmq -Xms256m -Xmx256m" \
        -p 9876:9876 \
        -v rocketmq_nameserver_logs:/home/rocketmq/logs \
        --restart unless-stopped \
        apache/rocketmq:5.1.4 \
        sh mqnamesrv
    
    log_info "等待RocketMQ NameServer启动..."
    sleep 15
    
    log_info "启动RocketMQ Broker..."
    docker run -d \
        --name rocketmq-broker-testing \
        --network platform-testing \
        -e NAMESRV_ADDR=rocketmq-nameserver-testing:9876 \
        -e JAVA_OPT="-Duser.home=/home/rocketmq -Xms256m -Xmx256m" \
        -p 10911:10911 \
        -p 10912:10912 \
        -v rocketmq_broker_logs:/home/rocketmq/logs \
        -v rocketmq_broker_data:/home/rocketmq/store \
        --restart unless-stopped \
        apache/rocketmq:5.1.4 \
        sh mqbroker -c /home/rocketmq/rocketmq-5.1.4/conf/broker.conf
    
    log_success "RocketMQ启动成功"
}

# 启动监控系统
start_monitoring() {
    log_info "启动Prometheus监控..."
    docker run -d \
        --name prometheus-testing \
        --network platform-testing \
        -p 9090:9090 \
        -v prometheus_testing_data:/prometheus \
        --restart unless-stopped \
        prom/prometheus:v2.45.0 \
        --config.file=/etc/prometheus/prometheus.yml \
        --storage.tsdb.path=/prometheus \
        --storage.tsdb.retention.time=200h \
        --web.enable-lifecycle
    
    log_info "启动Grafana可视化..."
    docker run -d \
        --name grafana-testing \
        --network platform-testing \
        -e GF_SECURITY_ADMIN_USER=admin \
        -e GF_SECURITY_ADMIN_PASSWORD=testing_grafana_pass_2024 \
        -e GF_USERS_ALLOW_SIGN_UP=false \
        -p 3000:3000 \
        -v grafana_testing_data:/var/lib/grafana \
        --restart unless-stopped \
        grafana/grafana:10.0.0
    
    log_success "监控系统启动成功"
}

# 显示连接信息
show_connection_info() {
    log_success "🎉 测试环境部署完成！"
    echo ""
    echo "📊 服务访问地址:"
    echo "  MySQL:     localhost:3306 (用户: platform_user, 密码: testing_user_pass_2024)"
    echo "  Redis:     localhost:6379 (密码: testing_redis_pass_2024)"
    echo "  MongoDB:   localhost:27017 (用户: mongo_admin, 密码: testing_mongo_pass_2024)"
    echo "  Nacos:     http://localhost:8848/nacos (用户: nacos, 密码: nacos)"
    echo "  RabbitMQ:  http://localhost:15672 (用户: rabbit_admin, 密码: testing_rabbit_pass_2024)"
    echo "  Kafka:     localhost:9092"
    echo "  RocketMQ:  localhost:9876 (NameServer)"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana:   http://localhost:3000 (用户: admin, 密码: testing_grafana_pass_2024)"
    echo ""
    echo "🔧 管理命令:"
    echo "  查看所有容器: docker ps"
    echo "  查看日志: docker logs <容器名>"
    echo "  停止所有: docker stop \$(docker ps -q --filter network=platform-testing)"
    echo "  清理环境: $0 --cleanup"
    echo ""
    echo "📚 文档: 查看 environments/testing/README.md 获取更多信息"
}

# 主函数
main() {
    if [[ "$1" == "--cleanup" ]]; then
        log_info "清理测试环境..."
        cleanup_containers
        log_success "清理完成"
        exit 0
    fi
    
    if [[ "$1" == "--help" ]]; then
        echo "用法: $0 [选项]"
        echo "选项:"
        echo "  --cleanup    清理现有测试环境"
        echo "  --help       显示帮助信息"
        exit 0
    fi
    
    log_info "开始部署测试环境..."
    
    check_docker
    cleanup_containers
    create_network
    
    # 启动基础服务
    start_mysql
    start_redis
    start_mongodb
    start_nacos
    
    # 启动消息队列
    start_rabbitmq
    start_kafka
    start_rocketmq
    
    # 启动监控系统
    start_monitoring
    
    show_connection_info
}

# 脚本入口
main "$@" 