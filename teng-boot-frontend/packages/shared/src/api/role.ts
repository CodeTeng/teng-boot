import { get, post, put, del } from './request'
import type { BaseResponse, PageVO } from '../types'

/** 角色视图对象 */
export interface RoleVO {
  id: number
  roleName: string
  roleKey: string
  roleSort: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
  creater?: number
  updater?: number
}

/** 新增角色请求体 */
export interface RoleAddDTO {
  roleName: string
  roleKey: string
  roleSort?: number
  status?: number
  remark?: string
}

/** 更新角色请求体 */
export interface RoleUpdateDTO extends RoleAddDTO {
  id: number
}

/** 分页查询参数 */
export interface RoleQuery {
  pageNo: number
  pageSize: number
  roleName?: string
  roleKey?: string
  status?: number
}

/** 分页查询角色 */
export const listRoleByPage = (data: RoleQuery) =>
  post<BaseResponse<PageVO<RoleVO>>>('/admin/roles/list/page', data)

/** 查询所有角色 */
export const listAllRoles = () =>
  get<BaseResponse<RoleVO[]>>('/admin/roles/all')

/** 新增角色 */
export const addRole = (data: RoleAddDTO) =>
  post<BaseResponse<boolean>>('/admin/roles', data)

/** 更新角色 */
export const updateRole = (data: RoleUpdateDTO) =>
  put<BaseResponse<boolean>>('/admin/roles', data)

/** 删除角色 */
export const deleteRole = (id: number) =>
  del<BaseResponse<boolean>>(`/admin/roles/${id}`)

/** 获取角色已分配的菜单 ID 列表 */
export const getRoleMenuIds = (roleId: number) =>
  get<BaseResponse<number[]>>(`/admin/roles/${roleId}/menus`)

/** 分配角色菜单 */
export const assignRoleMenus = (roleId: number, menuIds: number[]) =>
  post<BaseResponse<boolean>>(`/admin/roles/${roleId}/menus`, menuIds)

/** 获取菜单树（全量） */
export const getMenuTree = () =>
  get<BaseResponse<MenuTreeNode[]>>('/admin/menus/tree')

/** 菜单树节点 */
export interface MenuTreeNode {
  id: number
  menuName: string
  parentId: number
  children?: MenuTreeNode[]
}
