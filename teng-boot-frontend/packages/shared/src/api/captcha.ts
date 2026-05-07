import { post } from './request'
import type { BaseResponse } from '../types'

export interface CaptchaData {
  secretKey: string
  originalImageBase64: string
  jigsawImageBase64: string
}

/** 获取 AJ-Captcha 滑块验证码 */
export const getCaptcha = () =>
  post<BaseResponse<CaptchaData>>('/captcha/get', { captchaType: 'blockPuzzle' })

/** 校验 AJ-Captcha 滑块验证码 */
export const checkCaptcha = (data: { captchaType: string; captchaVerification: string }) =>
  post<BaseResponse<boolean>>('/captcha/check', data)

/** 构建 AJ-Captcha AES-ECB 加密的 captchaVerification */
export async function buildCaptchaVerification(offset: number, secretKey: string): Promise<string> {
  const CryptoJS = await import('crypto-js')
  return CryptoJS.AES.encrypt(
    CryptoJS.enc.Utf8.parse(JSON.stringify({ x: offset, y: 5 })),
    CryptoJS.enc.Utf8.parse(secretKey),
    { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 }
  ).toString()
}
