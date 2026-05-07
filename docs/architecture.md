# 分层架构说明

## 后端分层架构

### 表现层（Controller）
- 处理 HTTP 请求，接收前端参数
- 参数校验（@Valid / @Validated）
- 调用 Service 层执行业务逻辑
- 统一返回 BaseResponse 格式
- 按功能分为 controller/admin/（管理端接口）和 controller/（通用接口）

### 业务层（Service）
- 封装核心业务逻辑
- 事务管理（@Transactional）
- 调用 Mapper 层进行数据持久化
- 调用 Manager 层访问第三方服务

### 管理层（Manager）
- 封装第三方服务调用，如 CosManager（腾讯云 COS 文件上传）
- 隔离外部依赖变更对业务层的影响

### 持久层（Mapper / Entity）
- MyBatis-Plus 3.5.16 ORM 框架
- Entity：数据库表映射实体类
- Mapper：数据访问接口，继承 BaseMapper
- 支持逻辑删除（isDelete 字段）、自动填充、分页插件

### 切面层（AOP）
- AuthInterceptor：基于 @AuthCheck 注解的权限校验，拦截未授权访问
- OperationLogAspect：操作日志记录
- RateLimitAspect：接口限流

### 过滤器层（Filter）
- XssFilter：XSS 跨站脚本过滤，清理请求参数中的恶意脚本
- LoginInterceptor：JWT 登录状态校验拦截器

### 配置层（Config）
- Spring Bean 配置（Redis、Redisson、MyBatis-Plus 分页插件、CORS 跨域等）
- 多环境配置：application-dev.yml / application-test.yml / application-prod.yml

## 前端分层架构

### 视图层（Views）
- 页面级组件，放在 packages/*-frontend/src/views/
- 使用 Vue 3 Composition API（`<script setup lang="ts">`）

### 布局层（Layout）
- 主布局组件（侧边栏、导航栏、面包屑）
- 后台管理使用 Element Plus 布局组件
- 用户前台使用 Naive UI 布局组件

### 状态层（Stores）
- Pinia 状态管理
- 管理用户登录态、权限信息、全局状态

### 路由层（Router）
- Vue Router 4.x 动态路由
- 路由守卫检查用户角色权限
- 非 admin 角色访问后台管理重定向到 /403

### API 层（shared/api）
- 统一封装 Axios 请求
- 请求/响应拦截器（Token 传递、错误处理）
- Token 直接传 JWT 字符串，不加 Bearer 前缀
- code !== 200 时弹出错误提示，40100 清除 token

### 类型层（shared/types）
- TypeScript 类型定义
- API 请求/响应类型、实体类型

### 工具层（shared/utils）
- 通用工具函数
- auth 工具（Token 存取、角色判断）

## RBAC 权限模型

```
user (用户)
  |
  |--< user_role (用户角色关联) >--|
                                   |
                               role (角色)
                                   |
                               role_menu (角色菜单关联)
                                   |
                               menu (菜单权限)
```

- user：用户表，存储用户基本信息
- role：角色表，如 admin、user 等
- menu：菜单权限表，存储可访问的菜单和按钮权限
- user_role：用户与角色的多对多关联
- role_menu：角色与菜单的多对多关联

## 数据流

```
用户登录 -> 后端签发 JWT -> 前端存储 Token ->
每次请求携带 Token -> LoginInterceptor 校验 JWT ->
AuthInterceptor 检查 @AuthCheck 注解 -> 通过则放行，否则 403
```

## 部署架构

```
Nginx 反向代理
  |
  |-- /api/* -> 后端 API 服务（端口 8088）
  |-- /admin/* -> admin-frontend 静态资源
  |-- /* -> user-frontend 静态资源
  |
后端 -> MySQL 8（数据持久化）
后端 -> Redis 7+（缓存、分布式锁、滑块验证码存储）
```
