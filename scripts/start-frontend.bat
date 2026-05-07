@echo off
cd /d "%~dp0\..\teng-boot-frontend"
echo === 安装依赖 ===
call pnpm install
echo === 启动后台管理 (端口 5173) ===
start "Admin Frontend" cmd /c "pnpm --filter @teng-boot/admin-frontend dev"
echo === 启动用户前台 (端口 5174) ===
start "User Frontend" cmd /c "pnpm --filter @teng-boot/user-frontend dev"
echo 前端已启动，请分别查看两个窗口
pause
