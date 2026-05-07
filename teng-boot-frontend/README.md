# teng-boot-frontend

Vue 3 + TypeScript + Vite pnpm workspace monorepo，包含管理后台和用户前台。

## 快速命令

```bash
pnpm install                                        # 安装依赖
pnpm --filter @teng-boot/admin-frontend dev         # 启动后台 (5173)
pnpm --filter @teng-boot/user-frontend dev          # 启动前台 (5174)
npx vue-tsc --noEmit                                # 类型检查
pnpm build                                          # 构建
```

## 技术栈

Vue 3.5 / TypeScript 5.7 / Vite 6 / Pinia 2 / Vue Router 4 / Axios 1 / Element Plus 2.9 / Naive UI 2.40 / @vueuse/core 11 / dayjs / crypto-js

## 项目结构

```
packages/
├── shared/                       # @teng-boot/shared 共享包
│   └── src/
│       ├── api/                  # request.ts + user/role/menu/file/log/captcha API
│       ├── types/                # TypeScript 类型（BaseResponse, PageVO, UserInfo 等）
│       └── utils/                # auth.ts (token/role/permission管理)
├── admin-frontend/               # 后台管理系统 (:5173)
│   └── src/
│       ├── layout/               # MainLayout, Sidebar (动态菜单), Navbar
│       ├── router/               # 动态路由 + 权限守卫
│       ├── views/                # login, dashboard, user, role, menu, file, log
│       ├── stores/               # Pinia (user, app)
│       ├── components/           # SliderCaptcha 等
│       └── hooks/                # usePermission
└── user-frontend/                # 用户前台 (:5174)
    └── src/
        ├── views/                # home, login, register, profile
        ├── components/           # SliderCaptcha
        └── router/               # 路由守卫
```

## 关键约束

- Token 放 `Authorization` header，**不加 Bearer 前缀**
- API 响应格式 `{ code, data, message }`，`code=200` 成功
- `code=40100` 自动清除 token 并跳转登录页
- 滑块验证码使用 AJ-Captcha AES-ECB 加密
- 后台路由守卫：未登录 → `/login`，非 admin → `/403`
- Vite proxy `/api` → `http://localhost:8088/api`

## 设计风格

| 前端 | UI 框架 | 主题色 | 设计特点 |
|------|---------|--------|----------|
| admin-frontend | Element Plus | 蓝色 `#409EFF` | 专业后台风格，深色侧边栏 |
| user-frontend | Naive UI | 琥珀 `#F0A020` | 现代简洁，Fraunces 字体 |
