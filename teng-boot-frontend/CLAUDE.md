# teng-boot-frontend 前端服务

## 项目概述
基于 Vue 3 + TypeScript + Vite 的 pnpm workspace monorepo 前端项目，包含管理后台（Element Plus）和用户前台（Naive UI）。

## 快速命令
- 安装依赖：pnpm install
- 启动后台：pnpm --filter @teng-boot/admin-frontend dev
- 启动前台：pnpm --filter @teng-boot/user-frontend dev
- 类型检查：npx vue-tsc --noEmit
- 构建：pnpm build
- 格式化：pnpm lint

## 技术栈
Vue 3.5 / TypeScript 5.7 / Vite 6 / Pinia 2 / Vue Router 4 / Axios 1 / Element Plus 2.9 / Naive UI 2.40 / @vueuse/core 11 / dayjs

## 项目结构
packages/
├── shared/                       # @teng-boot/shared 共享包
│   └── src/
│       ├── api/                  # request.ts (axios封装) + user/role/menu/file/log/captcha API
│       ├── types/                # TypeScript 类型（BaseResponse, PageVO, UserInfo 等）
│       └── utils/                # auth.ts (token/role/permission管理)
├── admin-frontend/               # 后台管理系统 (端口 5173)
│   └── src/
│       ├── layout/               # MainLayout, Sidebar (动态菜单), Navbar
│       ├── router/               # 动态路由 + 权限守卫（非 admin 跳 /403）
│       ├── views/                # login, dashboard, user, role, menu, file, log
│       ├── stores/               # Pinia (user, app)
│       ├── components/           # SliderCaptcha 等
│       └── hooks/                # usePermission
└── user-frontend/                # 用户前台 (端口 5174)
    └── src/
        ├── views/                # home, login, profile
        ├── components/           # SliderCaptcha
        └── router/               # 路由守卫

## 关键约束
- Token 放 Authorization header，不加 Bearer 前缀
- API 响应统一格式：{ code, data, message }，code=200 成功
- 40100 状态码自动清除 token 并跳转登录页
- 滑块验证码组件暴露 markVerified() 方法供父组件调用
- 后台路由守卫：未登录 → /login，非 admin → /403
- Vite proxy：/api → http://localhost:8088/api
