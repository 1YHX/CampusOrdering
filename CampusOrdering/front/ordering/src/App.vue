<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getToken, isTokenExpired, removeToken } from '@/utils/auth'

const router = useRouter()
let tokenCheckInterval

// 定时检查 token 是否过期
const checkToken = () => {
  const token = getToken()
  if (token && isTokenExpired() && router.currentRoute.value.path !== '/login') {
    ElMessage.warning('登录已过期，请重新登录')
    removeToken()
    router.push('/login')
  }
}

onMounted(() => {
  // 每分钟检查一次 token
  tokenCheckInterval = setInterval(checkToken, 60000)
  console.log('App mounted, router ready')
})

onUnmounted(() => {
  // 清除定时器
  if (tokenCheckInterval) {
    clearInterval(tokenCheckInterval)
  }
})
</script>

<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </div>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  height: 100%;
}

/* 重置一些 Element Plus 的默认样式 */
.el-menu {
  border-right: none !important;
}

.el-header {
  --el-header-height: 60px;
  --el-header-padding: 0 20px;
}
</style>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
