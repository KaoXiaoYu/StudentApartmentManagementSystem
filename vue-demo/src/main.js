import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router/index'
import store from './store/index'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './assets/base.css'
import '@/assets/styles/index.scss' // global css

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(store)
app.use(ElementPlus, {
    locale: zhCn,
    size: 'small',
})
app.mount('#app')
