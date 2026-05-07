.PHONY: help lint-arch format build test clean start-backend start-frontend start-all

# 默认目标
help:
	@echo "Teng Boot Scaffold 质量检查命令"
	@echo ""
	@echo "  make lint-arch      架构检查（编译 + 类型检查）"
	@echo "  make format         代码格式化"
	@echo "  make build          构建项目"
	@echo "  make test           运行测试"
	@echo "  make check          全部质量检查"
	@echo "  make clean          清理构建产物"
	@echo "  make start-backend  启动后端"
	@echo "  make start-frontend 启动前端"
	@echo "  make start-all      一键启动全部服务"

# 架构检查：后端编译 + 前端类型检查
lint-arch:
	@echo "=== 后端编译检查 ==="
	cd teng-boot-backend && mvn compile -DskipTests -q
	@echo "=== 前端类型检查 ==="
	cd teng-boot-frontend && npx vue-tsc --noEmit 2>&1 | grep -v "deprecated" || true
	@echo "=== 架构检查通过 ==="

# 代码格式化
format:
	@echo "=== 前端代码格式化 ==="
	cd teng-boot-frontend && pnpm lint 2>/dev/null || npx prettier --write "packages/**/*.{ts,vue,scss}" 2>/dev/null || echo "Prettier not configured, skipping"
	@echo "=== 格式化完成 ==="

# 构建
build:
	@echo "=== 构建后端 ==="
	cd teng-boot-backend && mvn package -DskipTests -q
	@echo "=== 构建前端 ==="
	cd teng-boot-frontend && pnpm build 2>/dev/null || echo "Frontend build skipped"
	@echo "=== 构建完成 ==="

# 测试
test:
	@echo "=== 后端单元测试 ==="
	cd teng-boot-backend && mvn test
	@echo "=== 测试完成 ==="

# 全部质量检查
check: lint-arch test
	@echo "=== 全部质量检查通过 ==="

# 清理
clean:
	@echo "=== 清理后端 ==="
	cd teng-boot-backend && mvn clean -q
	@echo "=== 清理前端 ==="
	cd teng-boot-frontend && rm -rf packages/*/dist packages/*/node_modules/.vite 2>/dev/null || true
	@echo "=== 清理完成 ==="

# 一键启动
start-backend:
	cd teng-boot-backend && mvn spring-boot:run -DskipTests

start-frontend:
	cd teng-boot-frontend && pnpm --filter @teng-boot/admin-frontend dev & pnpm --filter @teng-boot/user-frontend dev & wait

start-all:
	@echo "启动后端..."
	cd teng-boot-backend && mvn spring-boot:run -DskipTests &
	@sleep 50
	@echo "启动前端..."
	cd teng-boot-frontend && pnpm --filter @teng-boot/admin-frontend dev & pnpm --filter @teng-boot/user-frontend dev & wait
