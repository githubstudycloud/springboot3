#!/bin/bash
# 此脚本用于快速测试解决依赖版本问题后的项目
# 先清理并重新编译项目，然后运行示例应用

echo "================================"
echo " 正在测试Maven依赖配置..."
echo "================================"

# 设置颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}开始清理项目...${NC}"

# 清理项目
mvn clean -U
if [ $? -ne 0 ]; then
    echo -e "${RED}清理项目失败，请检查Maven配置${NC}"
    exit 1
fi

echo -e "${GREEN}清理项目成功${NC}"
echo -e "${YELLOW}开始检查依赖树...${NC}"

# 检查依赖树
mvn dependency:tree > dependency-tree.log
if [ $? -ne 0 ]; then
    echo -e "${RED}检查依赖树失败，请检查Maven配置${NC}"
    exit 1
fi

echo -e "${GREEN}依赖树检查成功，结果已保存到dependency-tree.log${NC}"

# 检查Spring Boot依赖
grep "org.springframework.boot" dependency-tree.log > /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}未找到Spring Boot依赖，请检查配置${NC}"
    exit 1
fi

echo -e "${GREEN}Spring Boot依赖检查成功${NC}"

# 检查H2依赖
grep "com.h2database" dependency-tree.log > /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}未找到H2数据库依赖，请检查配置${NC}"
    exit 1
fi

echo -e "${GREEN}H2数据库依赖检查成功${NC}"

# 检查Lombok依赖
grep "org.projectlombok" dependency-tree.log > /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}未找到Lombok依赖，请检查配置${NC}"
    exit 1
fi

echo -e "${GREEN}Lombok依赖检查成功${NC}"
echo -e "${YELLOW}开始编译项目...${NC}"

# 编译项目
mvn compile -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}编译项目失败，请检查代码或Maven配置${NC}"
    exit 1
fi

echo -e "${GREEN}编译项目成功${NC}"
echo -e "${YELLOW}开始打包项目...${NC}"

# 打包项目
mvn package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}打包项目失败，请检查Maven配置${NC}"
    exit 1
fi

echo -e "${GREEN}打包项目成功${NC}"
echo -e "${YELLOW}开始运行DDD示例应用...${NC}"

# 运行应用
cd platform-ddd-demo
mvn spring-boot:run -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}运行应用失败，请检查应用配置${NC}"
    cd ..
    exit 1
fi

cd ..

echo "================================"
echo -e "${GREEN}测试成功，所有依赖问题已解决${NC}"
echo "================================"
exit 0
