@echo off
echo ========================================
echo  Teng Boot Scaffold 一键启动
echo ========================================
echo.
echo [1/2] 启动后端服务...
start "Backend" cmd /c "cd /d %~dp0\..\teng-boot-backend && mvn spring-boot:run -DskipTests"
echo 后端启动中，等待 50 秒...
timeout /t 50 /nobreak > nul
echo.
echo [2/2] 启动前端服务...
start "Admin Frontend" cmd /c "cd /d %~dp0\..\teng-boot-frontend && pnpm --filter @teng-boot/admin-frontend dev"
start "User Frontend" cmd /c "cd /d %~dp0\..\teng-boot-frontend && pnpm --filter @teng-boot/user-frontend dev"
echo.
echo ========================================
echo  启动完成！
echo  后台管理：http://localhost:5173
echo  用户前台：http://localhost:5174
echo  接口文档：http://localhost:8088/api/doc.html
echo ========================================
pause
