# Teng Boot Scaffold

基于 **Java 17 + Spring Boot 3.5.14 + Vue 3 + TypeScript** 的现代化全栈脚手架。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.14 |
| JDK | Java | 17 |
| ORM | MyBatis-Plus | 3.5.16 |
| 数据库 | MySQL + Druid | 8.0 + 1.2.28 |
| 缓存 | Redis + Redisson | — |
| 认证 | JWT (jjwt) | 0.13.0 |
| 接口文档 | SpringDoc + Knife4j | 2.8.17 / 4.5.0 |
| 验证码 | AJ-Captcha | 1.4.0 |
| 工具库 | Guava + Apache Commons | — |
| 前端框架 | Vue 3 (Composition API) | 3.5 |
| 语言 | TypeScript | 5.7 |
| 构建工具 | Vite | 6 |
| 包管理 | pnpm | 9+ |
| 后台 UI | Element Plus | 2.9 |
| 前台 UI | Naive UI | 2.40 |

## 快速开始

### 环境要求
- JDK 17+ / Maven 3.8+ / Node.js 18+ / pnpm 9+ / MySQL 8.0+ / Redis

### 后端启动
```bash
cd teng-boot-backend
mvn spring-boot:run -DskipTests
```
访问 http://localhost:8088/api/doc.html 查看接口文档

### 前端启动
```bash
cd teng-boot-frontend
pnpm install
pnpm --filter @teng-boot/admin-frontend dev   # 后台管理 :5173
pnpm --filter @teng-boot/user-frontend dev    # 用户前台 :5174
```

### 一键启动
```bash
# Windows
scripts\start-all.bat
```

## 项目结构

```
teng-boot-scaffold/
├── teng-boot-backend/        # 后端 Spring Boot 项目
├── teng-boot-frontend/       # 前端 pnpm monorepo
├── docs/                     # 架构文档、开发指南
├── scripts/                  # 一键启动脚本
├── user-guide/               # 用户手册
├── sql/                      # 数据库初始化脚本
├── Makefile                  # 质量检查命令
└── CLAUDE.md                 # AI 辅助开发文档
```

## 核心功能

- **RBAC 权限管理** — 5 表权限模型（用户→角色→菜单），按钮级权限控制
- **滑块验证码** — AJ-Captcha 行为验证码，AES 加密校验
- **JWT 认证** — 无状态 Token 认证，AOP + 注解权限校验
- **文件管理** — 腾讯云 COS 对象存储，上传自动入库
- **操作日志** — AOP 自动记录，管理端分页查询
- **数据脱敏** — @Sensitive 注解，手机号/邮箱/身份证掩码
- **接口限流** — @RateLimit 注解，Redis Lua 滑动窗口
- **XSS 防护** — 请求参数 HTML 转义过滤

## 质量检查

```bash
make lint-arch    # 编译 + 类型检查
make test         # 运行测试
make check        # 全部质量检查
make build        # 构建项目
```

## 文档导航

- [架构说明](docs/architecture.md)
- [开发指南](docs/development.md)
- [后台管理手册](user-guide/admin-guide.md)
- [API 文档](user-guide/api-doc.md)
- [后端 CLAUDE.md](teng-boot-backend/CLAUDE.md)
- [前端 CLAUDE.md](teng-boot-frontend/CLAUDE.md)

## License

MIT
