import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

import App from './App.vue'
import router from './router'
import './styles/index.scss'
import { setNotifyError } from '@teng-boot/shared'

// 注册全局错误通知（Element Plus 消息）
setNotifyError((msg: string) => {
  ElMessage.error(msg)
})

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
