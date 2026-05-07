# Teng Boot Scaffold 全栈脚手架

## 1. 项目概述

- 基于 Java 17 + Spring Boot 3.5.14 + Vue 3 + TypeScript 的现代化全栈脚手架
- 后端：teng-boot-backend（Maven 单体项目，打包为 app.jar）
- 前端：teng-boot-frontend（pnpm workspace monorepo，含 admin-frontend / user-frontend / shared 三个包）
- RBAC 权限体系、滑块验证码、JWT 认证、腾讯云 COS 文件存储

## 2. 快速命令

- 后端启动：`cd teng-boot-backend && mvn spring-boot:run -DskipTests`
- 后端测试：`cd teng-boot-backend && mvn test`
- 前端安装：`cd teng-boot-frontend && pnpm install`
- 前端开发（后台）：`cd teng-boot-frontend && pnpm dev:admin`（端口 5173）
- 前端开发（前台）：`cd teng-boot-frontend && pnpm dev:user`（端口 5174）
- 前端构建：`cd teng-boot-frontend && pnpm build`

## 3. 后端架构

- 分层：controller -> service -> mapper，另有 manager 层管理第三方服务（CosManager）
- 安全：JWT（jjwt 0.13.0）+ LoginInterceptor + AuthInterceptor（AOP + @AuthCheck 注解）
- RBAC 5 表：user -> user_role -> role -> role_menu -> menu
- ORM：MyBatis-Plus 3.5.16，逻辑删除、自动填充、分页插件
- 数据源：MySQL 8 + Druid 1.2.28 连接池 + commons-pool2 2.13.1
- 缓存/锁：Redis + Redisson 4.3.1 分布式锁
- 密码：BCrypt（spring-security-crypto），兼容旧 MD5（SALT="muziteng"）自动升级
- 验证码：滑块验证码（Java 2D Graphics 生成，Redis 存储答案，容差 ±3px）
- 文件存储：腾讯云 COS 5.6.269，上传后自动入库 file_record 表
- 接口文档：SpringDoc OpenAPI 2.8.17 + Knife4j 4.5.0（`/api/doc.html`）
- 工具库：Guava 33.6.0-jre + Apache Commons（Lang3 3.20.0 / IO 2.22.0 / Collections4 4.4）+ EasyExcel 4.0.3
- 关键包：controller/admin/（管理端接口）、aop/（切面）、config/（配置）、filter/（XSS 过滤）
- 全局异常处理：GlobalExceptionHandler（@RestControllerAdvice）
- 统一响应：BaseResponse + ResultUtils
- 分页：PageQuery + PageVO

## 4. 前端架构

- 构建：Vite 6 + pnpm workspace monorepo
- 框架：Vue 3.5 Composition API（`<script setup>`）+ TypeScript 5.7
- 后台 UI：Element Plus 2.9（admin-frontend，端口 5173）
- 前台 UI：Naive UI 2.41（user-frontend，端口 5174）
- 状态管理：Pinia 2.x
- 路由：Vue Router 4.x（动态路由 + 权限守卫）
- HTTP：Axios 1.x（统一拦截器，Token 不加 Bearer 前缀）
- 共享包：@teng-boot/shared（类型定义、API 函数、auth 工具）
- 目录结构：packages/shared/、packages/admin-frontend/、packages/user-frontend/
- 权限控制：路由守卫检查 roles 数组，非 admin 用户访问后台重定向 /403
- 滑块验证码：SliderCaptcha.vue 组件（鼠标/触摸拖动，API 校验）
- 工具库：dayjs、@vueuse/core 11.x、sass 1.86

## 5. 关键约束

- Token 传递：Authorization header 直接传 JWT 字符串，不加 Bearer 前缀
- 密码策略：BCrypt 加密，旧 MD5（SALT="muziteng"）兼容自动升级
- 逻辑删除：MyBatis-Plus 全局配置 isDelete 字段
- 分页限制：控制器层限制 pageSize ≤ 50 或 200（根据接口）
- 角色权限：必须通过 user_role 表分配，user 实体上的 userRole 字段仅作兼容保留
- COS 密钥：通过环境变量 COS_ACCESS_KEY / COS_SECRET_KEY 注入
- API 前缀：后端 context-path=/api
- 滑块验证码容差：±3px
- 前端请求：shared/src/api/request.ts 统一拦截，code !== 200 提示错误，40100 清除 token

## 6. 本地开发流程

- 环境要求：JDK 17 + Maven 3.8+ + Node.js 18+ + pnpm 9+ + MySQL 8 + Redis 7+
- 数据库：运行 sql/create_table.sql 建表，DataInitializer 自动插入 RBAC 初始数据
- 启动顺序：MySQL -> Redis -> 后端（8088）-> admin-frontend（5173）-> user-frontend（5174）
- 验证：访问 `http://localhost:8088/api/doc.html` 查看接口文档
- 默认管理员：注册任意用户后，通过数据库 `INSERT INTO user_role` 分配 admin 角色
- E2E 测试：使用 `npx agent-browser` 进行端到端测试/Playwright MCP UI 测试

## 7. 质量检查

- 质量检查统一入口文件：Makefile
- 后端编译：`cd teng-boot-backend && mvn compile`
- 后端测试：`cd teng-boot-backend && mvn test`
- 前端类型检查：`cd teng-boot-frontend && npx vue-tsc --noEmit`
- 前端构建：`cd teng-boot-frontend && pnpm build`
- 代码格式化：前端使用 ESLint（`pnpm lint`）

## 8. 参考项目约定

- 代码风格：Java 使用 Lombok（@Data / @Slf4j），前端使用 `<script setup lang="ts">`
- 命名规范：Java 驼峰命名，数据库下划线命名，前端 camelCase
- Git 提交：使用中文提交信息
- 包管理：后端 Maven，前端 pnpm（不用 npm）
- 测试框架：后端 JUnit 5 + Mockito + H2，前端 vitest（预留）

## 9. 文档导航

- docs/architecture.md — 分层架构说明
- docs/development.md — 环境要求、构建运行、数据库
- sql/create_table.sql — 数据库初始化脚本
