<template>
  <div class="register-container">
    <div class="register-background">
      <div class="circles">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <el-card class="register-card animate__animated animate__fadeInUp">
      <div class="register-content">
        <div class="register-header">
          <div class="brand">
            <div class="logo-wrapper">
              <img src="@/assets/logo.svg" alt="logo" class="register-logo">
            </div>
            <h2>注册账号</h2>
            <p class="register-subtitle">加入校园订餐系统</p>
          </div>
        </div>
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form" label-position="top">
          <el-form-item prop="role" label="角色">
            <el-select
              v-model="registerForm.role"
              placeholder="请选择角色"
              class="custom-select"
              :prefix-icon="ElementPlusIconsVue.UserFilled"
            >
              <el-option label="学生" value="student" />
              <el-option label="员工" value="staff" />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="username" label="用户名">
            <el-input 
              v-model="registerForm.username"
              placeholder="请输入用户名"
              :prefix-icon="ElementPlusIconsVue.User"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item 
            :prop="registerForm.role === 'student' ? 'studentNo' : 'employeeNo'" 
            :label="registerForm.role === 'student' ? '学号' : '工号'"
          >
            <el-input 
              v-model="idNumber"
              :placeholder="registerForm.role === 'student' ? '请输入学号' : '请输入工号'"
              :prefix-icon="ElementPlusIconsVue.IdCard"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="realName" label="真实姓名">
            <el-input 
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              :prefix-icon="ElementPlusIconsVue.User"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="phone" label="手机号">
            <el-input 
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="ElementPlusIconsVue.Phone"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="email" label="邮箱">
            <el-input 
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              :prefix-icon="ElementPlusIconsVue.Message"
              class="custom-input"
            />
          </el-form-item>

          <template v-if="registerForm.role === 'student'">
            <el-form-item prop="college" label="学院">
              <el-input 
                v-model="registerForm.college"
                placeholder="请输入学院"
                :prefix-icon="ElementPlusIconsVue.School"
                class="custom-input"
              />
            </el-form-item>

            <el-form-item prop="major" label="专业">
              <el-input 
                v-model="registerForm.major"
                placeholder="请输入专业"
                :prefix-icon="ElementPlusIconsVue.Collection"
                class="custom-input"
              />
            </el-form-item>

            <el-form-item prop="className" label="班级">
              <el-input 
                v-model="registerForm.className"
                placeholder="请输入班级"
                :prefix-icon="ElementPlusIconsVue.UserGroup"
                class="custom-input"
              />
            </el-form-item>

            <el-form-item prop="grade" label="年级">
              <el-input 
                v-model="registerForm.grade"
                placeholder="请输入年级"
                :prefix-icon="ElementPlusIconsVue.Calendar"
                class="custom-input"
              />
            </el-form-item>
          </template>

          <template v-if="registerForm.role === 'staff'">
            <el-form-item prop="position" label="职位">
              <el-input 
                v-model="registerForm.position"
                placeholder="请输入职位"
                :prefix-icon="ElementPlusIconsVue.Position"
                class="custom-input"
              />
            </el-form-item>

            <el-form-item prop="department" label="部门">
              <el-input 
                v-model="registerForm.department"
                placeholder="请输入部门"
                :prefix-icon="ElementPlusIconsVue.Office"
                class="custom-input"
              />
            </el-form-item>
          </template>

          <el-form-item prop="password" label="密码">
            <el-input 
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="ElementPlusIconsVue.Lock"
              show-password
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input 
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="ElementPlusIconsVue.Lock"
              show-password
              class="custom-input"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister" class="register-button" :disabled="loading">
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
            <div class="login-link">
              已有账号？
              <el-link type="primary" @click="router.push('/login')">返回登录</el-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElCard, ElForm, ElFormItem, ElInput, ElButton, ElSelect, ElOption, ElLink } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import request from '@/utils/request'
import 'animate.css'
import 'element-plus/dist/index.css'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  role: '',
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  // 学生特有字段
  studentNo: '',
  college: '',
  major: '',
  className: '',
  grade: '',
  // 员工特有字段
  employeeNo: '',
  position: '',
  department: ''
})

// 添加计算属性来处理学号/工号的值
const idNumber = computed({
  get: () => {
    return registerForm.role === 'student' ? registerForm.studentNo : registerForm.employeeNo
  },
  set: (value) => {
    if (registerForm.role === 'student') {
      registerForm.studentNo = value
    } else {
      registerForm.employeeNo = value
    }
  }
})

// 手机号验证规则
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 邮箱验证规则
const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

// 确认密码验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive({
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  employeeNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请输入学院', trigger: 'blur' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ]
})

// 根据角色重置表单
watch(() => registerForm.role, (newRole) => {
  if (newRole === 'student') {
    registerForm.employeeNo = ''
    registerForm.position = ''
    registerForm.department = ''
  } else if (newRole === 'staff') {
    registerForm.studentNo = ''
    registerForm.college = ''
    registerForm.major = ''
    registerForm.className = ''
    registerForm.grade = ''
  }
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构建注册数据
        const registerData = {
          role: registerForm.role,
          username: registerForm.username,
          password: registerForm.password,
          realName: registerForm.realName,
          phone: registerForm.phone,
          email: registerForm.email
        }

        // 根据角色添加特定字段
        if (registerForm.role === 'student') {
          Object.assign(registerData, {
            studentNo: registerForm.studentNo,
            college: registerForm.college,
            major: registerForm.major,
            className: registerForm.className,
            grade: registerForm.grade
          })
        } else if (registerForm.role === 'staff') {
          Object.assign(registerData, {
            employeeNo: registerForm.employeeNo,
            position: registerForm.position,
            department: registerForm.department
          })
        }

        await request.post('/api/auth/register', registerData)
        ElMessage.success('注册成功')
        router.push('/login')
      } catch (error) {
        console.error('注册错误:', error)
        ElMessage.error('网络请求失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background: #f0f2f5;
  padding: 40px 0;
}

.register-background {
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

.register-card {
  width: 500px;
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

.register-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.register-content {
  padding: 32px;
}

.register-header {
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

.register-logo {
  width: 60px;
  height: 60px;
  filter: brightness(0) invert(1);
}

.register-header h2 {
  margin: 0;
  font-size: 28px;
  color: #1890ff;
  font-weight: 600;
  letter-spacing: 1px;
}

.register-subtitle {
  margin: 8px 0 0;
  color: #666;
  font-size: 14px;
}

.register-form {
  margin-top: 24px;
}

.custom-input :deep(.el-input__wrapper),
.custom-select :deep(.el-input__wrapper) {
  box-shadow: none !important;
  border: 2px solid #eee;
  border-radius: 8px;
  transition: all 0.3s ease;
  padding: 8px 12px;
  background: #f8f9fa;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-select :deep(.el-input__wrapper:hover) {
  border-color: #1890ff;
  background: white;
}

.custom-input :deep(.el-input__wrapper.is-focus),
.custom-select :deep(.el-input__wrapper.is-focus) {
  border-color: #1890ff;
  background: white;
}

.custom-input :deep(.el-input__inner) {
  height: 38px;
}

.custom-input :deep(.el-input__prefix-icon),
.custom-select :deep(.el-input__prefix-icon) {
  font-size: 18px;
  color: #999;
}

.register-button {
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

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(24, 144, 255, 0.3);
}

.register-button:disabled {
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  opacity: 0.7;
}

.login-link {
  text-align: center;
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

.login-link :deep(.el-link) {
  margin-left: 8px;
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}
</style> 