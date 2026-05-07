import { useUserStore } from '@/stores/user'

/**
 * 权限检查 hook
 * 判断当前用户是否拥有指定角色或权限
 */
export function usePermission() {
  const userStore = useUserStore()

  /** 检查是否拥有指定角色 */
  function hasRole(role: string): boolean {
    return userStore.hasRole(role)
  }

  /** 检查是否拥有指定角色列表中的任意一个 */
  function hasAnyRole(roles: string[]): boolean {
    return roles.some((role) => userStore.hasRole(role))
  }

  /** 检查是否拥有指定权限 */
  function hasPermission(perm: string): boolean {
    return userStore.hasPermission(perm)
  }

  return {
    hasRole,
    hasAnyRole,
    hasPermission,
    roles: userStore.userInfo?.roles ?? [],
  }
}
