import { get, post, put, del } from './request'
import type {
  BaseResponse,
  LoginDTO,
  RegisterDTO,
  UserInfo,
  UserUpdateMyDTO,
  UserUpdatePwdDTO,
  PageVO,
} from '../types'

/** 登录 */
export const login = (data: LoginDTO) =>
  post<BaseResponse<UserInfo>>('/users/login', data)

/** 注册 */
export const register = (data: RegisterDTO) =>
  post<BaseResponse<boolean>>('/users/register', data)

/** 获取当前登录用户信息 */
export const getCurrentUser = () =>
  get<BaseResponse<UserInfo>>('/users/get/login')

/** 修改个人信息 */
export const updateMyUser = (data: UserUpdateMyDTO) =>
  put<BaseResponse<boolean>>('/users/update/my', data)

/** 修改密码 */
export const updateMyPwd = (data: UserUpdatePwdDTO) =>
  put<BaseResponse<boolean>>('/users/update/myPwd', data)

/** 退出登录 */
export const logout = () =>
  post<BaseResponse<boolean>>('/users/logout')

// ===== 管理端 API（以下为后台管理专用） =====

/** 用户查询参数 */
export interface UserQuery {
  pageNo: number
  pageSize: number
  id?: number
  username?: string
  userPhone?: string
  userRealName?: string
  userGender?: number
  userAge?: number
  userEmail?: string
  userProfile?: string
  userRole?: string
  status?: number
}

/** 分页查询用户 */
export const listUserByPage = (data: UserQuery) =>
  post<BaseResponse<PageVO<UserInfo>>>('/users/list/page', data)

/** 新增用户 */
export const addUser = (data: Record<string, unknown>) =>
  post<BaseResponse<boolean>>('/users/add', data)

/** 更新用户 */
export const updateUser = (data: Record<string, unknown>) =>
  put<BaseResponse<boolean>>('/users/update', data)

/** 删除用户 */
export const deleteUser = (ids: number[]) =>
  post<BaseResponse<boolean>>('/users/delete', { ids })

/** 修改用户密码 */
export const updateUserPwd = (id: number, data: { userPassword: string; checkPassword: string }) =>
  put<BaseResponse<boolean>>(`/users/update/pwd/${id}`, data)

/** 导出用户（Excel） */
export const exportUser = (data: Record<string, unknown>) =>
  post('/users/export/page', data, { responseType: 'blob' } as Record<string, unknown>)
