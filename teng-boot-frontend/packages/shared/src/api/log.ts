import { post, get } from './request'
import type { BaseResponse, PageVO, SysLog, SysLogQuery } from '../types'

/** 分页查询操作日志 */
export const listLogByPage = (data: SysLogQuery) =>
  post<BaseResponse<PageVO<SysLog>>>('/logs/list/page', data as Record<string, unknown>)

/** 根据 ID 获取日志详情 */
export const getLogById = (id: number) =>
  get<BaseResponse<SysLog>>(`/logs/get/${id}`)
