<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" class="profile-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form 
            ref="profileFormRef"
            :model="profileForm" 
            :rules="profileRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>
            
            <el-form-item label="角色">
              <el-input :value="getRoleText(userInfo.role)" disabled />
            </el-form-item>
            
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="部门/院系" prop="department">
              <el-input v-model="profileForm.department" placeholder="请输入部门或院系" />
            </el-form-item>
            
            <el-form-item label="注册时间">
              <el-input :value="formatDate(userInfo.createTime)" disabled />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="updateLoading">
                保存信息
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form 
            ref="passwordFormRef"
            :model="passwordForm" 
            :rules="passwordRules"
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input 
                v-model="passwordForm.oldPassword" 
                type="password" 
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input 
                v-model="passwordForm.newPassword" 
                type="password" 
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="passwordForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, changePassword } from '@/api/user'

const activeTab = ref('basic')
const updateLoading = ref(false)
const passwordLoading = ref(false)

// 用户信息
const userInfo = reactive({
  username: '',
  role: '',
  realName: '',
  phone: '',
  email: '',
  department: '',
  createTime: ''
})

// 个人信息表单
const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  department: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单引用
const profileFormRef = ref(null)
const passwordFormRef = ref(null)

// 个人信息验证规则
const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取角色文本
const getRoleText = (role) => {
  const roleMap = {
    'student': '学生',
    'staff': '员工',
    'admin': '管理员'
  }
  return roleMap[role] || role
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

// 加载用户信息
const loadUserProfile = async () => {
  try {
    const { data } = await getUserProfile()
    Object.assign(userInfo, data)
    // 复制到表单
    profileForm.realName = data.realName || ''
    profileForm.phone = data.phone || ''
    profileForm.email = data.email || ''
    profileForm.department = data.department || ''
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    updateLoading.value = true
    
    await updateUserProfile(profileForm)
    ElMessage.success('个人信息更新成功')
    
    // 重新加载用户信息
    await loadUserProfile()
    
    // 更新localStorage中的用户信息
    const storedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    Object.assign(storedUserInfo, profileForm)
    localStorage.setItem('userInfo', JSON.stringify(storedUserInfo))
    
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      ElMessage.error('更新失败，请稍后重试')
    }
  } finally {
    updateLoading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    await changePassword(passwordForm)
    ElMessage.success('密码修改成功')
    
    // 重置表单
    resetPasswordForm()
    
  } catch (error) {
    if (error !== false) { // 不是表单验证错误
      ElMessage.error('密码修改失败，请检查原密码是否正确')
    }
  } finally {
    passwordLoading.value = false
  }
}

// 重置个人信息表单
const resetForm = () => {
  profileForm.realName = userInfo.realName || ''
  profileForm.phone = userInfo.phone || ''
  profileForm.email = userInfo.email || ''
  profileForm.department = userInfo.department || ''
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 500;
}

.profile-tabs {
  margin-top: 20px;
}

.profile-form,
.password-form {
  max-width: 500px;
  margin: 20px 0;
}

.profile-form .el-form-item,
.password-form .el-form-item {
  margin-bottom: 20px;
}
</style> 