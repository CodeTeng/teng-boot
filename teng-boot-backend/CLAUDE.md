# teng-boot-backend 后端服务

## 项目概述
基于 Spring Boot 3.5.14 + Java 17 + MyBatis-Plus 3.5.16 的后端服务，提供 RBAC 权限管理、JWT 认证、滑块验证码、腾讯云 COS 文件存储等功能。

## 快速命令
- 启动：mvn spring-boot:run -DskipTests
- 编译：mvn compile
- 测试：mvn test
- 打包：mvn package -DskipTests
- 清理：mvn clean

## 技术栈
Spring Boot 3.5.14 / Java 17 / MyBatis-Plus 3.5.16 / MySQL 8 / Druid 1.2.28 / Redis + Redisson / JWT (jjwt 0.13.0) / SpringDoc + Knife4j / Guava 33.6 / Apache Commons / EasyExcel 4.0.3 / 腾讯云 COS

## 项目结构
src/main/java/com/lt/boot/
├── MainApplication.java          # 启动类
├── annotation/                   # @AuthCheck, @LogRecord, @RateLimit, @Sensitive
├── aop/                          # AuthInterceptor, LoginInterceptor, OperationLogAspect, RateLimitAspect
├── common/                       # BaseResponse, ErrorCode, ResultUtils, PageVO, PageQuery
├── config/                       # Jackson, Knife4j, Redis, Redisson, COS, WebMvc, 线程池, DataInitializer
├── constant/                     # UserConstant, CommonConstant, FileConstant, RegexConstants
├── controller/                   # User, Captcha, File, SysLog + admin/ (Role, Menu, AdminFile)
├── exception/                    # BusinessException, GlobalExceptionHandler, ThrowUtils
├── filter/                       # XssFilter, XssHttpServletRequestWrapper
├── manager/                      # CosManager (腾讯云 COS)
├── mapper/                       # MyBatis-Plus Mapper 接口
├── model/
│   ├── dto/                      # 请求 DTO
│   ├── entity/                   # 数据库实体（User, Role, Menu, FileRecord, SysLog 等）
│   ├── enums/                    # 枚举
│   └── vo/                       # 视图对象
├── service/                      # 服务接口 + impl
└── utils/                        # JWT, 雪花ID, IP获取, Excel, SQL校验, ThreadLocal

## 关键约束
- 端口：8088，context-path=/api
- JWT 有效期 2 小时，HS256 签名
- 密码使用 BCrypt，兼容旧 MD5（salt=muziteng）自动升级
- 数据源使用 Druid 连接池，监控页面 /api/druid/index.html
- MyBatis-Plus 逻辑删除字段 isDelete
- 分页最大限制 50 或 200 条
- 接口文档 /api/doc.html（Knife4j）
- COS 密钥通过环境变量注入
- 滑块验证码容差 ±3px，有效期 2 分钟
