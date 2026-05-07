/**
 * 错误通知工具
 *
 * 在 shared 包中无法直接使用 element-plus 或 naive-ui 的消息组件，
 * 由各个消费应用在入口处注册自己的通知函数。
 */

type NotifyFn = (message: string) => void

let errorHandler: NotifyFn | null = null

/** 注册全局错误通知处理函数 */
export function setNotifyError(handler: NotifyFn): void {
  errorHandler = handler
}

/** 显示错误通知（优先使用已注册的通知函数，fallback 到 console.error） */
export function notifyError(message: string): void {
  if (errorHandler) {
    errorHandler(message)
  } else {
    console.error('[API Error]', message)
  }
}
