#!/bin/bash
# 此脚本设置正确的文件权限和配置

# 设置shell脚本的执行权限
echo "为shell脚本添加执行权限..."
chmod +x run-ddd-demo.sh

# 设置正确的行尾字符(转换CRLF到LF)
echo "修复文件行尾字符..."
if command -v dos2unix >/dev/null 2>&1; then
    dos2unix run-ddd-demo.sh
    echo "已使用dos2unix修复行尾字符"
else
    echo "警告: dos2unix未安装，可能导致shell脚本在Linux/macOS上执行失败"
    echo "建议安装dos2unix并手动转换文件格式"
fi

echo "配置完成。"
echo "在Linux/macOS系统上，请使用 ./run-ddd-demo.sh 启动应用"
echo "在Windows系统上，请使用 run-ddd-demo.bat 启动应用"
