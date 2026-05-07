import { post, del } from './request'
import type { BaseResponse, PageVO } from '../types'

/** 文件记录视图对象 */
export interface FileRecordVO {
  id: number
  fileName: string
  fileSize: number
  fileType: string
  fileUrl: string
  bizType: string
  userId: number
  createTime: string
}

/** 文件查询参数 */
export interface FileQuery {
  pageNo: number
  pageSize: number
  fileName?: string
  fileType?: string
  bizType?: string
}

/** 分页查询文件列表 */
export const listFileByPage = (data: FileQuery) =>
  post<BaseResponse<PageVO<FileRecordVO>>>('/admin/files/list/page', data)

/** 删除文件 */
export const deleteFile = (id: number) =>
  del<BaseResponse<boolean>>(`/admin/files/${id}`)
