import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'token'
const USER_ROLE = 'userRole'
const USER_INFO = 'userInfo'
const LOGIN_TIME = 'loginTime'
const EXPIRE_TIME = 3600000 // 1小时 = 3600000毫秒

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(LOGIN_TIME, Date.now().toString())
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_ROLE)
  localStorage.removeItem(USER_INFO)
  localStorage.removeItem(LOGIN_TIME)
}

export function isTokenExpired() {
  const loginTime = localStorage.getItem(LOGIN_TIME)
  if (!loginTime) return true
  
  const now = Date.now()
  const diff = now - parseInt(loginTime)
  return diff >= EXPIRE_TIME
}

export function setUserInfo(info) {
  localStorage.setItem(USER_INFO, JSON.stringify(info))
  localStorage.setItem(USER_ROLE, info.role)
}

export function getUserInfo() {
  const info = localStorage.getItem(USER_INFO)
  return info ? JSON.parse(info) : null
}

export function getUserRole() {
  return localStorage.getItem(USER_ROLE)
}

export function setupTokenCheck(router) {
  // 每分钟检查一次token是否过期
  setInterval(() => {
    if (isTokenExpired() && router.currentRoute.value.path !== '/login') {
      ElMessage.warning('登录已过期，请重新登录')
      removeToken()
      router.push('/login')
    }
  }, 60000)
  
  // 路由守卫
  router.beforeEach((to, from, next) => {
    if (to.path === '/login') {
      next()
      return
    }
    
    const token = getToken()
    if (!token || isTokenExpired()) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    next()
  })
} 