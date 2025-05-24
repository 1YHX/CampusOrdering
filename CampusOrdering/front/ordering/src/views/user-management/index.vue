<template>
  <div class="user-management-container">
    <div class="header-section">
      <h2>用户管理</h2>
      <div class="actions-section">
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/姓名/学号/工号/手机/邮箱"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.userType" placeholder="用户类型" clearable style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="员工" value="staff" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <div class="search-buttons">
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table
        :data="userList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createTime', order: 'descending' }"
        :table-layout="'auto'"
      >
        <el-table-column prop="username" label="用户名" min-width="120" fixed="left" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="userTypeText" label="用户类型" min-width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTagType(scope.row.userType)">
              {{ scope.row.userTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        
        <!-- 学生特有字段 -->
        <el-table-column prop="studentNo" label="学号" min-width="100" v-if="showStudentColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'student'">{{ scope.row.studentNo || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" min-width="120" v-if="showStudentColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'student'">{{ scope.row.college || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业" min-width="120" v-if="showStudentColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'student'">{{ scope.row.major || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        
        <!-- 员工特有字段 -->
        <el-table-column prop="employeeNo" label="工号" min-width="100" v-if="showStaffColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'staff'">{{ scope.row.employeeNo || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" min-width="120" v-if="showStaffColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'staff'">{{ scope.row.position || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="部门" min-width="120" v-if="showStaffColumns">
          <template #default="scope">
            <span v-if="scope.row.userType === 'staff'">{{ scope.row.department || '-' }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="small" 
                @click="viewUser(scope.row)"
              >
                查看
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="editUser(scope.row)"
              >
                编辑
              </el-button>
              <el-button 
                :type="scope.row.status === 1 ? 'info' : 'success'" 
                size="small" 
                @click="toggleUserStatus(scope.row)"
                :loading="statusUpdating && updateStatusId === scope.row.id"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteUserItem(scope.row)"
                :loading="deleting && deleteId === scope.row.id"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <!-- 基础信息 -->
        <el-form-item label="用户类型" prop="userType" v-if="!isEdit">
          <el-select v-model="editForm.userType" placeholder="请选择用户类型" style="width: 100%" @change="onUserTypeChange">
            <el-option label="学生" value="student" />
            <el-option label="员工" value="staff" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <!-- 学生特有字段 -->
        <template v-if="editForm.userType === 'student'">
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="editForm.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="学院">
            <el-input v-model="editForm.college" placeholder="请输入学院" />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="editForm.major" placeholder="请输入专业" />
          </el-form-item>
          <el-form-item label="班级">
            <el-input v-model="editForm.className" placeholder="请输入班级" />
          </el-form-item>
          <el-form-item label="年级">
            <el-input v-model="editForm.grade" placeholder="请输入年级" />
          </el-form-item>
        </template>
        
        <!-- 员工特有字段 -->
        <template v-if="editForm.userType === 'staff'">
          <el-form-item label="工号" prop="employeeNo">
            <el-input v-model="editForm.employeeNo" placeholder="请输入工号" />
          </el-form-item>
          <el-form-item label="职位">
            <el-input v-model="editForm.position" placeholder="请输入职位" />
          </el-form-item>
          <el-form-item label="部门">
            <el-input v-model="editForm.department" placeholder="请输入部门" />
          </el-form-item>
        </template>
        
        <el-form-item label="初始密码" v-if="!isEdit">
          <el-input v-model="editForm.password" placeholder="不填则默认为123456" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser" :loading="saving">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看用户详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="用户详情"
      width="600px"
    >
      <div v-if="selectedUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ selectedUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ selectedUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">
            <el-tag :type="getUserTypeTagType(selectedUser.userType)">
              {{ selectedUser.userTypeText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedUser.status === 1 ? 'success' : 'danger'">
              {{ selectedUser.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedUser.email || '-' }}</el-descriptions-item>
          
          <!-- 学生特有信息 -->
          <template v-if="selectedUser.userType === 'student'">
            <el-descriptions-item label="学号">{{ selectedUser.studentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="学院">{{ selectedUser.college || '-' }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ selectedUser.major || '-' }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ selectedUser.className || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年级">{{ selectedUser.grade || '-' }}</el-descriptions-item>
          </template>
          
          <!-- 员工特有信息 -->
          <template v-if="selectedUser.userType === 'staff'">
            <el-descriptions-item label="工号">{{ selectedUser.employeeNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ selectedUser.position || '-' }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ selectedUser.department || '-' }}</el-descriptions-item>
          </template>
          
          <el-descriptions-item label="创建时间">{{ formatDate(selectedUser.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(selectedUser.updateTime) }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="detail-actions">
          <el-button type="warning" @click="resetUserPassword(selectedUser)">
            重置密码
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { 
  pageQueryUsers, 
  createUser, 
  updateUser,
  deleteUser, 
  updateUserStatus,
  resetPassword
} from '@/api/userManagement'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const deleting = ref(false)
const deleteId = ref(null)
const statusUpdating = ref(false)
const updateStatusId = ref(null)
const saving = ref(false)
const userList = ref([])
const selectedUser = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  userType: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

// 编辑相关
const editDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: null,
  userType: '',
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  // 学生字段
  studentNo: '',
  college: '',
  major: '',
  className: '',
  grade: '',
  // 员工字段
  employeeNo: '',
  position: '',
  department: ''
})

const editRules = {
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  employeeNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ]
}

// 计算属性
const showStudentColumns = computed(() => {
  return !searchForm.userType || searchForm.userType === 'student'
})

const showStaffColumns = computed(() => {
  return !searchForm.userType || searchForm.userType === 'staff'
})

// 方法
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    
    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    
    const response = await pageQueryUsers(params)
    if (response.code === 200) {
      userList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.current = 1
  loadUsers()
}

const refreshData = () => {
  loadUsers()
}

const handleSizeChange = (val) => {
  pagination.size = val
  pagination.current = 1
  loadUsers()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadUsers()
}

const openAddDialog = () => {
  isEdit.value = false
  resetEditForm()
  editDialogVisible.value = true
}

const viewUser = (user) => {
  selectedUser.value = user
  viewDialogVisible.value = true
}

const editUser = (user) => {
  isEdit.value = true
  Object.assign(editForm, user)
  editDialogVisible.value = true
}

const resetEditForm = () => {
  Object.keys(editForm).forEach(key => {
    editForm[key] = ''
  })
  editForm.id = null
}

const onUserTypeChange = () => {
  // 清空特定字段
  editForm.studentNo = ''
  editForm.college = ''
  editForm.major = ''
  editForm.className = ''
  editForm.grade = ''
  editForm.employeeNo = ''
  editForm.position = ''
  editForm.department = ''
}

const saveUser = async () => {
  if (!editFormRef.value) return
  
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    let result
    if (isEdit.value) {
      // 编辑用户
      const updateData = { ...editForm }
      delete updateData.id
      delete updateData.userType
      result = await updateUser(editForm.userType, editForm.id, updateData)
    } else {
      // 创建用户
      const createData = { ...editForm }
      delete createData.id
      result = await createUser(editForm.userType, createData)
    }
    
    if (result.code === 200) {
      ElMessage.success(isEdit.value ? '用户更新成功' : '用户创建成功')
      editDialogVisible.value = false
      loadUsers()
    } else {
      ElMessage.error(result.msg || (isEdit.value ? '用户更新失败' : '用户创建失败'))
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error('保存用户失败')
  } finally {
    saving.value = false
  }
}

const toggleUserStatus = async (user) => {
  statusUpdating.value = true
  updateStatusId.value = user.id
  
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const result = await updateUserStatus(user.userType, user.id, newStatus)
    if (result.code === 200) {
      ElMessage.success('用户状态更新成功')
      loadUsers()
    } else {
      ElMessage.error(result.msg || '用户状态更新失败')
    }
  } catch (error) {
    console.error('更新用户状态失败:', error)
    ElMessage.error('更新用户状态失败')
  } finally {
    statusUpdating.value = false
    updateStatusId.value = null
  }
}

const deleteUserItem = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 ${user.realName}(${user.username}) 吗？删除后无法恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deleting.value = true
    deleteId.value = user.id
    
    const result = await deleteUser(user.userType, user.id)
    if (result.code === 200) {
      ElMessage.success('用户删除成功')
      loadUsers()
    } else {
      ElMessage.error(result.msg || '用户删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  } finally {
    deleting.value = false
    deleteId.value = null
  }
}

const resetUserPassword = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户 ${user.realName}(${user.username}) 的密码吗？密码将重置为123456`,
      '确认重置密码',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const result = await resetPassword(user.userType, user.id)
    if (result.code === 200) {
      ElMessage.success('密码重置成功')
    } else {
      ElMessage.error(result.msg || '密码重置失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }
}

// 工具方法
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getUserTypeTagType = (type) => {
  const typeMap = {
    'student': 'primary',
    'staff': 'success',
    'admin': 'warning'
  }
  return typeMap[type] || 'info'
}

// 生命周期
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-section h2 {
  margin: 0;
  color: #303133;
}

.actions-section {
  display: flex;
  gap: 10px;
}

.search-card {
  margin-bottom: 20px;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.table-card {
  background: white;
  overflow-x: auto;
}

.table-card .el-table {
  width: 100% !important;
  min-width: 1400px;
}

.table-card .el-table th,
.table-card .el-table td {
  padding: 8px 4px;
}

.table-card .el-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  text-align: center;
}

.table-card .el-table td {
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  justify-content: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.empty-text {
  color: #c0c4cc;
}

.user-detail {
  padding: 10px 0;
}

.detail-actions {
  margin-top: 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .search-buttons {
    justify-content: center;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style> 