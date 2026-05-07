#!/bin/bash
# 后端一键启动脚本
cd "$(dirname "$0")/../teng-boot-backend"
echo "=== 编译后端 ==="
mvn compile -DskipTests -q
echo "=== 启动后端 (端口 8088) ==="
mvn spring-boot:run -DskipTests
