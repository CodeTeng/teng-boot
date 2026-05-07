# API 接口文档

## 接口地址
- 本地开发：http://localhost:8088/api
- 接口文档：http://localhost:8088/api/doc.html（Knife4j / Swagger）

## 认证方式
- Token 放在请求头 Authorization 中（不加 Bearer 前缀）
- 登录接口返回 token，有效期 2 小时

## 主要接口分组
- 用户模块：/users（登录、注册、CRUD）
- 验证码模块：/captcha（滑块验证码生成和校验）
- 文件模块：/file（文件上传）
- 角色管理：/admin/roles（CRUD + 菜单分配）
- 菜单管理：/admin/menus（树形 CRUD）
- 文件管理：/admin/files（分页查询、删除）
- 系统日志：/logs（分页查询）

## 统一响应格式
{
  "code": 200,        // 业务状态码
  "data": {...},      // 响应数据
  "message": "成功"    // 响应消息
}

## 错误码
- 200：成功
- 40000：参数错误
- 40100：未登录
- 40101：无权限
- 42900：请求过于频繁
- 50000：系统异常
