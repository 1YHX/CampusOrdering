<template>
  <div class="login-container">
    <div class="login-background">
      <div class="circles">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <el-card class="login-card animate__animated animate__fadeInUp">
      <div class="login-content">
        <div class="login-header">
          <div class="brand">
            <div class="logo-wrapper">
              <img src="@/assets/logo.svg" alt="logo" class="login-logo">
            </div>
            <h2>校园订餐系统</h2>
            <p class="login-subtitle">便捷的校园美食服务平台</p>
          </div>
        </div>
        <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
          <el-form-item prop="role">
            <el-select
              v-model="loginForm.role"
              placeholder="请选择角色"
              class="custom-select"
              :prefix-icon="UserFilled"
            >
              <el-option label="学生" value="student" />
              <el-option label="员工" value="staff" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </el-form-item>
          <el-form-item prop="username">
            <el-input 
              v-model="loginForm.username"
              :placeholder="getPlaceholder"
              :prefix-icon="User"
              class="custom-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              class="custom-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleLogin" class="login-button" :disabled="loading">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
          <div class="register-link">
            还没有账号？
            <el-link type="primary" @click="handleRegisterClick">立即注册</el-link>
          </div>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElCard, ElForm, ElFormItem, ElInput, ElButton, ElSelect, ElOption, ElLink } from 'element-plus'
import { User, Lock, UserFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { setToken, removeToken } from '@/utils/auth'
import 'animate.css'
import 'element-plus/dist/index.css'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 组件挂载时清除旧的登录信息
removeToken()

const loginForm = reactive({
  role: '',
  username: '',
  password: ''
})

const getPlaceholder = computed(() => {
  switch (loginForm.role) {
    case 'student':
      return '请输入学号'
    case 'staff':
      return '请输入工号'
    case 'admin':
      return '请输入用户名'
    default:
      return '请先选择角色'
  }
})

const rules = {
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await request.post('/api/auth/login', {
          role: loginForm.role,
          username: loginForm.username,
          password: loginForm.password
        })
        
        // 直接使用响应数据，因为响应拦截器已经处理了错误情况
        setToken(response.data.token)
        localStorage.setItem('userRole', loginForm.role)
        localStorage.setItem('userInfo', JSON.stringify(response.data.user))
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error(error.response?.data?.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegisterClick = async () => {
  console.log('点击注册按钮')
  console.log('当前路由:', router.currentRoute.value)
  try {
    await router.push({
      name: 'Register'
    })
    console.log('路由跳转成功')
  } catch (error) {
    console.error('路由跳转错误:', error)
    // 尝试使用 window.location
    console.log('尝试使用 window.location 跳转')
    window.location.href = '/register'
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: #f0f2f5;
}

.login-background {
  position: fixed;
  width: 100vw;
  height: 100vh;
  top: 0;
  left: 0;
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  overflow: hidden;
}

.circles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circles div {
  position: absolute;
  display: block;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.2);
  animation: animate 25s linear infinite;
  bottom: -150px;
  border-radius: 50%;
}

.circles div:nth-child(1) {
  left: 25%;
  width: 80px;
  height: 80px;
  animation-delay: 0s;
}

.circles div:nth-child(2) {
  left: 10%;
  width: 20px;
  height: 20px;
  animation-delay: 2s;
  animation-duration: 12s;
}

.circles div:nth-child(3) {
  left: 70%;
  width: 20px;
  height: 20px;
  animation-delay: 4s;
}

.circles div:nth-child(4) {
  left: 40%;
  width: 60px;
  height: 60px;
  animation-delay: 0s;
  animation-duration: 18s;
}

.circles div:nth-child(5) {
  left: 65%;
  width: 20px;
  height: 20px;
  animation-delay: 0s;
}

@keyframes animate {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
    border-radius: 0;
  }
  100% {
    transform: translateY(-1000px) rotate(720deg);
    opacity: 0;
    border-radius: 50%;
  }
}

.login-card {
  width: 420px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
  border: none;
  overflow: hidden;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.login-content {
  padding: 32px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px rgba(24, 144, 255, 0.2);
}

.login-logo {
  width: 60px;
  height: 60px;
  filter: brightness(0) invert(1);
}

.login-header h2 {
  margin: 0;
  font-size: 28px;
  color: #1890ff;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-subtitle {
  margin: 8px 0 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 24px;
}

.custom-input :deep(.el-input__wrapper) {
  box-shadow: none !important;
  border: 2px solid #eee;
  border-radius: 8px;
  transition: all 0.3s ease;
  padding: 8px 12px;
  background: #f8f9fa;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #1890ff;
  background: white;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #1890ff;
  background: white;
}

.custom-input :deep(.el-input__inner) {
  height: 38px;
}

.custom-input :deep(.el-input__prefix-icon) {
  font-size: 18px;
  color: #999;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  border: none;
  border-radius: 8px;
  margin-top: 16px;
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(24, 144, 255, 0.3);
}

.login-button:disabled {
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  opacity: 0.7;
}

:deep(.el-message) {
  min-width: 300px;
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__error) {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

.custom-select {
  width: 100%;
}

.custom-select :deep(.el-input__wrapper) {
  box-shadow: none !important;
  border: 2px solid #eee;
  border-radius: 8px;
  transition: all 0.3s ease;
  padding: 8px 12px;
  background: #f8f9fa;
}

.custom-select :deep(.el-input__wrapper:hover) {
  border-color: #1890ff;
  background: white;
}

.custom-select :deep(.el-input__wrapper.is-focus) {
  border-color: #1890ff;
  background: white;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

.register-link :deep(.el-link) {
  margin-left: 8px;
  font-size: 14px;
}
</style>