import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'
import type { BaseResponse } from '../types/api'
import { getToken, removeToken, removeUserInfo } from '../utils/auth'
import { notifyError } from '../utils/notification'

/** 创建 axios 实例 */
const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? '/api',
  timeout: 15_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/** 请求拦截器：自动附加 Authorization（直接传 token，不加 Bearer 前缀） */
instance.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error),
)

/**
 * 响应拦截器：统一错误处理
 * - 如果响应类型为 blob，直接返回 response.data（不解析 JSON）
 * - 否则返回 response.data（即 BaseResponse<T>），
 *   后续 wrapper 函数直接得到完整的 BaseResponse。
 */
instance.interceptors.response.use(
  (response) => {
    // blob 响应（文件下载）直接透传
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response.data as unknown as BaseResponse
    }

    const data = response.data as BaseResponse

    if (data.code === 200) {
      return data
    }

    // 业务错误提示
    notifyError(data.message || '请求失败')

    // token 过期 / 未登录
    if (data.code === 40100) {
      removeToken()
      removeUserInfo()
      window.location.href = '/login'
      return Promise.reject(new Error(data.message || '登录已过期，请重新登录'))
    }

    // 其他业务错误
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401) {
      removeToken()
      removeUserInfo()
      window.location.href = '/login'
    }
    const msg = error.response?.data?.message || error.message || '网络错误'
    notifyError(msg)
    console.error('[HTTP Error]', msg)
    return Promise.reject(error)
  },
)

/**
 * 封装 GET 请求
 * 响应拦截器已返回 BaseResponse<T>，此处直接返回
 */
export async function get<T = unknown>(url: string, params?: Record<string, unknown>, config?: AxiosRequestConfig): Promise<T> {
  const res = await instance.get<T>(url, { params, ...config })
  return res as T
}

/** 封装 POST 请求 */
export async function post<T = unknown>(url: string, data?: Record<string, unknown>, config?: AxiosRequestConfig): Promise<T> {
  const res = await instance.post<T>(url, data, config)
  return res as T
}

/** 封装 PUT 请求 */
export async function put<T = unknown>(url: string, data?: Record<string, unknown>, config?: AxiosRequestConfig): Promise<T> {
  const res = await instance.put<T>(url, data, config)
  return res as T
}

/** 封装 DELETE 请求 */
export async function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res = await instance.delete<T>(url, config)
  return res as T
}

export default instance
