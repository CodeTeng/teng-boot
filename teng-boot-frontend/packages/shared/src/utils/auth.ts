import type { UserInfo } from '../types'

const TOKEN_KEY = 'teng-boot-token'
const USER_KEY = 'teng-boot-user-info'

/** 获取存储的 token */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/** 设置 token */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 移除 token */
export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

/** 获取存储的用户信息 */
export function getUserInfo(): UserInfo | null {
  const str = localStorage.getItem(USER_KEY)
  if (!str) return null
  try {
    return JSON.parse(str) as UserInfo
  } catch {
    return null
  }
}

/** 设置用户信息 */
export function setUserInfo(user: UserInfo): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/** 移除用户信息 */
export function removeUserInfo(): void {
  localStorage.removeItem(USER_KEY)
}

/** 检查是否拥有指定角色 */
export function hasRole(role: string): boolean {
  const user = getUserInfo()
  return user?.roles?.includes(role) ?? false
}

/** 检查是否拥有指定权限 */
export function hasPermission(perm: string): boolean {
  const user = getUserInfo()
  return user?.permissions?.includes(perm) ?? false
}

/** 检查是否为管理员 */
export function isAdmin(): boolean {
  return hasRole('admin')
}

/** 退出登录 */
export function logout(): void {
  removeToken()
  removeUserInfo()
}
