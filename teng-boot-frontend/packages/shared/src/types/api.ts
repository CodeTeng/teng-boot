/** 通用响应体 */
export interface BaseResponse<T = unknown> {
  code: number
  data: T
  message: string
}

/** 分页查询参数 */
export interface PageQuery {
  pageNo: number
  pageSize: number
  sortBy?: string
  isAsc?: boolean
}

/** 分页响应数据 */
export interface PageVO<T> {
  total: number
  pages: number
  list: T[]
}

/** AJ-Captcha 滑块验证码响应数据 */
export interface CaptchaData {
  secretKey: string
  originalImageBase64: string
  jigsawImageBase64: string
}

/** 登录请求体 */
export interface LoginDTO {
  username: string
  userPassword: string
  captchaVerification: string
}

/** 注册请求体 */
export interface RegisterDTO {
  username: string
  userPassword: string
  checkPassword: string
  captchaVerification: string
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  userRole: string
  userPhone?: string
  userRealName?: string
  userGender?: number
  userAge?: number
  userEmail?: string
  userAvatar?: string
  userBirthday?: string
  userProfile?: string
  status: number
  createTime?: string
  token?: string
  roles: string[]
  permissions: string[]
}

/** 修改个人信息请求体 */
export interface UserUpdateMyDTO {
  username: string
  userPhone?: string
  userRealName?: string
  userGender?: number
  userAge?: number
  userEmail?: string
  userBirthday?: string
  userProfile?: string
}

/** 修改密码请求体 */
export interface UserUpdatePwdDTO {
  oldPassword: string
  userPassword: string
  checkPassword: string
}

/** 系统日志 */
export interface SysLog {
  id: number
  userId: number
  username: string
  value: string
  operation: string
  costTime: number
  url: string
  methodName: string
  params: string
  ip: string
  createTime: string
}

/** 日志查询参数 */
export interface SysLogQuery extends PageQuery {
  username?: string
  operation?: string
}

/** 菜单节点 */
export interface MenuVO {
  id: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component?: string
  query?: string
  menuType: string
  visible: number
  status: number
  perms?: string
  icon: string
  children?: MenuVO[]
}
