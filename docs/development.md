# 开发指南

## 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| pnpm | 9+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |

## 项目结构

```
teng-boot-scaffold/
├── teng-boot-backend/          # 后端 Maven 项目
│   ├── src/
│   │   ├── main/java/com/lt/  # Java 源码
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── service/       # 业务逻辑层
│   │   │   ├── mapper/        # MyBatis-Plus Mapper
│   │   │   ├── entity/        # 数据实体
│   │   │   ├── manager/       # 第三方服务管理层
│   │   │   ├── aop/           # AOP 切面
│   │   │   ├── config/        # Spring 配置
│   │   │   ├── filter/        # 过滤器
│   │   │   └── common/        # 公共工具
│   │   └── main/resources/    # 配置文件
│   └── pom.xml                # Maven 依赖
├── teng-boot-frontend/         # 前端 pnpm workspace
│   ├── packages/
│   │   ├── shared/            # 共享包（类型、API、工具）
│   │   ├── admin-frontend/    # 后台管理（Element Plus）
│   │   └── user-frontend/     # 用户前台（Naive UI）
│   ├── package.json           # workspace 根配置
│   └── pnpm-workspace.yaml    # pnpm workspace 配置
├── docs/                      # 文档
│   ├── architecture.md        # 分层架构说明
│   └── development.md         # 开发指南
├── sql/
│   └── create_table.sql       # 数据库建表脚本
└── CLAUDE.md                  # 项目说明
```

## 快速开始

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS teng_boot DEFAULT CHARACTER SET utf8mb4;"

# 导入建表脚本
mysql -u root -p teng_boot < sql/create_table.sql
```

### 2. 启动后端

```bash
cd teng-boot-backend
mvn spring-boot:run -DskipTests
```

后端默认启动在 `http://localhost:8088`，API 前缀为 `/api`。

### 3. 启动前端

```bash
cd teng-boot-frontend
pnpm install

# 启动后台管理（Element Plus）
pnpm dev:admin        # http://localhost:5173

# 启动用户前台（Naive UI）
pnpm dev:user         # http://localhost:5174
```

### 4. 访问地址

| 服务 | 地址 |
|------|------|
| 接口文档（Knife4j） | http://localhost:8088/api/doc.html |
| 后台管理 | http://localhost:5173 |
| 用户前台 | http://localhost:5174 |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户表（用户名、密码、邮箱、头像等） |
| role | 角色表（角色编码、角色名称） |
| menu | 菜单权限表（菜单名称、路径、权限标识） |
| user_role | 用户角色关联表 |
| role_menu | 角色菜单关联表 |
| file_record | 文件记录表（COS 文件上传记录） |
| sys_log | 系统日志表（操作日志） |

## 环境变量

| 变量名 | 说明 |
|--------|------|
| COS_ACCESS_KEY | 腾讯云 COS 访问密钥 ID |
| COS_SECRET_KEY | 腾讯云 COS 访问密钥 |

## 多环境配置

| 配置文件 | 适用环境 | 端口 |
|----------|----------|------|
| application-dev.yml | 本地开发 | 8088 |
| application-test.yml | 测试环境 | 8101 |
| application-prod.yml | 生产环境 | 8101 |

## 构建部署

### 后端构建

```bash
cd teng-boot-backend
mvn package -DskipTests
# 构建产物：target/app.jar
java -jar target/app.jar
```

### 前端构建

```bash
cd teng-boot-frontend
pnpm build
# 构建产物：packages/admin-frontend/dist/、packages/user-frontend/dist/
```

### Docker 部署

```bash
docker build -t teng-boot .
docker run -p 8088:8088 teng-boot
```

## 技术栈版本

### 后端

| 依赖 | 版本 |
|------|------|
| Spring Boot | 3.5.14 |
| Java | 17 |
| MyBatis-Plus | 3.5.16 |
| Druid | 1.2.28 |
| Redisson | 4.3.1 |
| jjwt | 0.13.0 |
| SpringDoc OpenAPI | 2.8.17 |
| Knife4j | 4.5.0 |
| 腾讯云 COS | 5.6.269 |
| Commons-Lang3 | 3.20.0 |
| Commons-IO | 2.22.0 |
| Commons-Collections4 | 4.4 |
| Commons-Pool2 | 2.13.1 |
| Guava | 33.6.0-jre |
| EasyExcel | 4.0.3 |

### 前端

| 依赖 | 版本 |
|------|------|
| Vue | 3.5.x |
| Vite | 6.x |
| TypeScript | 5.7.x |
| Element Plus | 2.9.x |
| Naive UI | 2.41.x |
| Pinia | 2.x |
| Vue Router | 4.x |
| Axios | 1.x |
| vue-tsc | 2.2.x |
| sass | 1.86.x |
| @vueuse/core | 11.x |
| dayjs | 1.x |
