import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Naive from 'naive-ui'
import { createDiscreteApi } from 'naive-ui'

import App from './App.vue'
import router from './router'
import './styles/index.scss'
import { setNotifyError } from '@teng-boot/shared'

// 注册全局错误通知（Naive UI 消息）
const { message } = createDiscreteApi(['message'])
setNotifyError((msg: string) => {
  message.error(msg)
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Naive)

app.mount('#app')
