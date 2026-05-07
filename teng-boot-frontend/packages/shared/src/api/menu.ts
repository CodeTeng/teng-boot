import { get, post, put, del } from './request'
import type { BaseResponse, MenuVO } from '../types'

/** 菜单新增 DTO */
export interface MenuAddDTO {
  menuName: string
  parentId?: number
  orderNum?: number
  path?: string
  component?: string
  query?: string
  menuType: string
  visible?: number
  status?: number
  perms?: string
  icon?: string
  remark?: string
}

/** 菜单更新 DTO */
export interface MenuUpdateDTO extends MenuAddDTO {
  id: number
}

/** 获取菜单树（管理端全部菜单） */
export const getMenuTree = () => get<BaseResponse<MenuVO[]>>('/admin/menus/tree')

/** 获取当前用户可见菜单树 */
export const getUserMenus = () =>
  get<BaseResponse<MenuVO[]>>('/admin/menus/user-tree')

/** 新增菜单 */
export const addMenu = (data: MenuAddDTO) =>
  post<BaseResponse<boolean>>('/admin/menus', data)

/** 更新菜单 */
export const updateMenu = (data: MenuUpdateDTO) =>
  put<BaseResponse<boolean>>('/admin/menus', data)

/** 删除菜单 */
export const deleteMenu = (id: number) =>
  del<BaseResponse<boolean>>(`/admin/menus/${id}`)
