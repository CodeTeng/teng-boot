# teng-boot-backend

Spring Boot 3.5.14 + Java 17 后端服务，提供 RBAC 权限管理、JWT 认证、滑块验证码等功能。

## 快速命令

```bash
mvn spring-boot:run -DskipTests   # 启动 (端口 8088)
mvn compile                       # 编译
mvn test                          # 测试
mvn package -DskipTests           # 打包
```

## 技术栈

Spring Boot 3.5.14 / Java 17 / MyBatis-Plus 3.5.16 / MySQL 8 + Druid 1.2.28 / Redis + Redisson 4.3.1 / JWT (jjwt 0.13.0) / SpringDoc + Knife4j / AJ-Captcha 1.4.0 / Guava 33.6 / Apache Commons / EasyExcel 4.0.3 / 腾讯云 COS

## 项目结构

```
src/main/java/com/lt/boot/
├── MainApplication.java          # 启动类
├── annotation/                   # @AuthCheck, @LogRecord, @RateLimit, @Sensitive
├── aop/                          # AuthInterceptor, LoginInterceptor, OperationLogAspect, RateLimitAspect
├── common/                       # BaseResponse, ErrorCode, ResultUtils, PageVO, PageQuery
├── config/                       # DataInitializer, Jackson, Knife4j, Redis, Redisson, COS, WebMvc
├── constant/                     # UserConstant, CommonConstant, FileConstant, RegexConstants
├── controller/
│   ├── UserController.java       # 用户模块
│   ├── CaptchaController.java    # 验证码模块
│   ├── FileController.java       # 文件上传
│   ├── SysLogController.java     # 操作日志
│   └── admin/                    # 管理端 (Role, Menu, AdminFile)
├── exception/                    # BusinessException, GlobalExceptionHandler, ThrowUtils
├── filter/                       # XssFilter, XssHttpServletRequestWrapper
├── manager/                      # CosManager (腾讯云 COS)
├── mapper/                       # MyBatis-Plus Mapper
├── model/
│   ├── dto/                      # 请求 DTO
│   ├── entity/                   # User, Role, Menu, FileRecord, SysLog 等
│   ├── enums/                    # 枚举 (用户状态/性别/角色等)
│   └── vo/                       # 视图对象
├── service/                      # 服务接口 + impl
└── utils/                        # JWT, 雪花ID, Excel, IP, SQL, ThreadLocal
```

## 关键约束

- 端口 **8088**，context-path `/api`
- 接口文档 `/api/doc.html`（Knife4j）
- Druid 监控 `/api/druid/index.html`
- JWT 有效期 2 小时，HS256 签名
- 密码 BCrypt 加密，兼容旧 MD5 自动升级
- MyBatis-Plus 逻辑删除 `isDelete` 字段
- 分页限制 50~200 条
- COS 密钥通过环境变量 `COS_ACCESS_KEY` / `COS_SECRET_KEY` 注入
- AJ-Captcha 滑块验证码 AES-ECB 加密

## API 接口

| 分组 | 路径 | 说明 |
|------|------|------|
| 用户模块 | `/users` | 登录、注册、CRUD |
| 验证码 | `/captcha` | AJ-Captcha 滑块验证码 |
| 文件上传 | `/file` | COS 文件上传 |
| 角色管理 | `/admin/roles` | RBAC 角色 CRUD |
| 菜单管理 | `/admin/menus` | 菜单树形 CRUD |
| 文件管理 | `/admin/files` | 文件记录分页 |
| 系统日志 | `/logs` | 操作日志查询 |
