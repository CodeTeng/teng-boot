import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@teng-boot/shared/utils/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/index.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

/** 路由守卫：已登录访问 /login 则跳转首页，未登录访问需鉴权页面则跳转登录页 */
router.beforeEach((to, _from, next) => {
  const token = getToken()

  // 已登录用户访问登录页或注册页 -> 跳转首页
  if ((to.path === '/login' || to.path === '/register') && token) {
    return next('/')
  }

  // 需要登录的页面但未登录 -> 跳转登录页
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  next()
})

export default router
