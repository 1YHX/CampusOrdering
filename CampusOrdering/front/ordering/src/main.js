import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 按顺序使用插件
app.use(createPinia())

// 使用 Element Plus
app.use(ElementPlus, {
  locale: zhCn
})

// 最后使用路由，确保其他功能已经准备就绪
app.use(router)

// 等待路由准备就绪后再挂载应用
router.isReady().then(() => {
  app.mount('#app')
  console.log('应用已挂载，路由已就绪')
})