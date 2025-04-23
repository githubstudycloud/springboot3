#!/bin/bash

# 默认不启动任何服务
START_COMMON=false
START_APP=false

# 参数解析
while [[ $# -gt 0 ]]; do
  case $1 in
    --all)
      START_COMMON=true
      START_APP=true
      shift
      ;;
    --common)
      START_COMMON=true
      shift
      ;;
    --app)
      START_APP=true
      shift
      ;;
    *)
      echo "未知选项: $1"
      exit 1
      ;;
  esac
done

# 如果没有指定任何服务，默认启动主应用
if [[ "$START_COMMON" == "false" && "$START_APP" == "false" ]]; then
  START_APP=true
fi

# 启动服务
if [[ "$START_COMMON" == "true" ]]; then
  echo "启动通用组件库开发服务..."
  cd platform-vue-ui-common
  npm run dev &
  COMMON_PID=$!
  cd ..
  echo "通用组件库开发服务已启动，PID: $COMMON_PID"
fi

if [[ "$START_APP" == "true" ]]; then
  echo "启动主应用开发服务..."
  cd platform-vue-ui-app
  npm run dev &
  APP_PID=$!
  cd ..
  echo "主应用开发服务已启动，PID: $APP_PID"
fi

# 处理关闭信号
trap "echo '关闭所有服务...'; kill $COMMON_PID $APP_PID 2>/dev/null" EXIT INT TERM

# 保持脚本运行
wait
