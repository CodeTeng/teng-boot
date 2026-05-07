import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken, getUserInfo, setUserInfo, isAdmin, logout } from '@teng-boot/shared/utils/auth'
import { getCurrentUser } from '@teng-boot/shared/api/user'

/** 静态路由 — 无需登录即可访问 */
export const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { hidden: true },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { hidden: true },
  },
]

/** 动态路由 — 需要登录且 admin 角色才能访问 */
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '控制台', icon: 'Odometer' },
      },
      // 系统管理
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' },
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' },
      },
      // 文件管理
      {
        path: 'file',
        name: 'File',
        component: () => import('@/views/file/index.vue'),
        meta: { title: '文件管理', icon: 'Folder' },
      },
      // 系统监控
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/log/index.vue'),
        meta: { title: '操作日志', icon: 'Document' },
      },
    ],
  },
  // 通配符路由放在最后
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...staticRoutes, ...asyncRoutes],
  scrollBehavior: () => ({ top: 0 }),
})

/** 路由守卫：认证 + 用户信息加载 + 角色校验 */
router.beforeEach(async (to, _from, next) => {
  // 访问登录页
  if (to.path === '/login') {
    if (getToken()) {
      return next('/dashboard')
    }
    return next()
  }

  const token = getToken()
  if (!token) {
    return next('/login')
  }

  // 如果还没加载用户信息，尝试获取
  if (!getUserInfo()) {
    try {
      const res = await getCurrentUser()
      if (res.code === 200 && res.data) {
        setUserInfo(res.data)
      } else {
        logout()
        return next('/login')
      }
    } catch {
      logout()
      return next('/login')
    }
  }

  // 管理页面需要 admin 角色，403 页面除外
  if (!isAdmin() && to.path !== '/403') {
    return next('/403')
  }

  next()
})

/**
 * 动态注册路由
 * 在用户登录后获取菜单权限，调用此函数添加动态路由
 */
export function registerAsyncRoutes(routes: RouteRecordRaw[]) {
  routes.forEach((route) => {
    if (!router.hasRoute(route.name as string)) {
      router.addRoute(route)
    }
  })
}

export { registerAsyncRoutes as addAsyncRoutes }
export default router
