@echo off
cd /d "%~dp0\..\teng-boot-backend"
echo === 编译后端 ===
call mvn compile -DskipTests -q
echo === 启动后端 (端口 8088) ===
call mvn spring-boot:run -DskipTests
pause
