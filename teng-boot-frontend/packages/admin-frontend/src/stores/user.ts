import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getCurrentUser, logout as logoutApi } from '@teng-boot/shared'
import {
  setToken,
  setUserInfo,
  removeToken,
  removeUserInfo,
  getUserInfo as getStoredUserInfo,
} from '@teng-boot/shared/utils/auth'
import type { LoginDTO, UserInfo } from '@teng-boot/shared/types'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(getStoredUserInfo())
  const token = ref<string | null>(localStorage.getItem('teng-boot-token'))

  /** 登录 */
  async function login(data: LoginDTO) {
    const res = await loginApi(data)
    if (res.code === 200 && res.data?.token) {
      token.value = res.data.token
      setToken(res.data.token)
      userInfo.value = res.data
      setUserInfo(res.data)
    }
    return res.data
  }

  /** 获取当前用户信息 */
  async function fetchUserInfo() {
    const res = await getCurrentUser()
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      setUserInfo(res.data)
    }
    return res.data
  }

  /** 退出登录 */
  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 忽略退出接口错误
    }
    token.value = null
    userInfo.value = null
    removeToken()
    removeUserInfo()
  }

  /** 检查角色 */
  function hasRole(role: string): boolean {
    return userInfo.value?.roles?.includes(role) ?? false
  }

  /** 检查权限 */
  function hasPermission(perm: string): boolean {
    return userInfo.value?.permissions?.includes(perm) ?? false
  }

  return {
    userInfo,
    token,
    login,
    fetchUserInfo,
    logout,
    hasRole,
    hasPermission,
  }
})
