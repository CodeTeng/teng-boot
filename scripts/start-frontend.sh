#!/bin/bash
# 前端一键启动脚本
cd "$(dirname "$0")/../teng-boot-frontend"
echo "=== 安装依赖 ==="
pnpm install
echo "=== 启动后台管理 (端口 5173) ==="
pnpm --filter @teng-boot/admin-frontend dev &
echo "=== 启动用户前台 (端口 5174) ==="
pnpm --filter @teng-boot/user-frontend dev &
wait
