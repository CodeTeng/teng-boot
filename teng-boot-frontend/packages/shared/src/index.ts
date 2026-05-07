// API 请求
export { get, post, put, del } from './api/request'

// API 函数
export { getCaptcha, checkCaptcha, buildCaptchaVerification } from './api/captcha'
export { login, register, getCurrentUser, updateMyUser, updateMyPwd, listUserByPage, addUser, updateUser, deleteUser, updateUserPwd, exportUser } from './api/user'
export type { UserQuery } from './api/user'
export { listFileByPage, deleteFile } from './api/file'
export type { FileRecordVO, FileQuery } from './api/file'
export {
  getMenuTree,
  getUserMenus,
  addMenu,
  updateMenu,
  deleteMenu,
} from './api/menu'
export type { MenuAddDTO, MenuUpdateDTO } from './api/menu'
export { listLogByPage, getLogById } from './api/log'

// API 函数 - 角色
export {
  listRoleByPage,
  listAllRoles,
  addRole,
  updateRole,
  deleteRole,
  getRoleMenuIds,
  assignRoleMenus,
} from './api/role'
export type { RoleVO, RoleAddDTO, RoleUpdateDTO, RoleQuery, MenuTreeNode } from './api/role'

// 类型
export type {
  BaseResponse,
  PageQuery,
  PageVO,
  CaptchaData,
  LoginDTO,
  RegisterDTO,
  UserInfo,
  UserUpdateMyDTO,
  UserUpdatePwdDTO,
  MenuVO,
  SysLog,
  SysLogQuery,
} from './types'

// 工具函数
export {
  getToken,
  setToken,
  removeToken,
  getUserInfo,
  setUserInfo,
  removeUserInfo,
  hasRole,
  hasPermission,
  isAdmin,
  logout,
} from './utils/auth'

// 通知工具
export { setNotifyError, notifyError } from './utils/notification'
