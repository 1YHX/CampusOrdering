<template>
  <div class="subsidy-management-container">
    <div class="header-section">
      <h2>补贴管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增补贴方案
      </el-button>
    </div>

    <!-- 搜索过滤区域 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" @submit.prevent="loadData" inline>
        <el-form-item label="方案名称">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入方案名称" 
            clearable 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="补贴对象">
          <el-select 
            v-model="searchForm.targetType" 
            placeholder="请选择补贴对象" 
            clearable
            style="width: 160px;"
          >
            <el-option label="学生" value="student" />
            <el-option label="员工" value="staff" />
          </el-select>
        </el-form-item>
        <el-form-item label="补贴周期">
          <el-select 
            v-model="searchForm.period" 
            placeholder="请选择补贴周期" 
            clearable
            style="width: 160px;"
          >
            <el-option label="每日" value="daily" />
            <el-option label="每周" value="weekly" />
            <el-option label="每月" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 120px;"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 补贴方案列表 -->
    <el-card class="table-card">
      <el-table 
        :data="subsidyPlans" 
        v-loading="loading"
        style="width: 100%"
        table-layout="auto"
        stripe
        border
      >
        <el-table-column prop="name" label="方案名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="amount" label="补贴金额" width="140" align="right">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="period" label="补贴周期" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getPeriodTagType(scope.row.period)" size="small">
              {{ getPeriodLabel(scope.row.period) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="补贴对象" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.targetType === 'student' ? 'primary' : 'success'" size="small">
              {{ scope.row.targetType === 'student' ? '学生' : '员工' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetGroup" label="补贴群体" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.targetGroup">
              {{ getTargetGroupLabel(scope.row.targetGroup) }}：{{ scope.row.groupValue }}
            </span>
            <span v-else class="all-target">全部</span>
          </template>
        </el-table-column>
        <el-table-column prop="isReset" label="清零规则" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isReset === 1 ? 'warning' : 'info'" size="small">
              {{ scope.row.isReset === 1 ? '清零' : '累积' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cashRatio" label="现金比例" width="120" align="center">
          <template #default="scope">
            <span class="ratio-text">{{ (scope.row.cashRatio * 100).toFixed(0) }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button 
                type="primary" 
                size="small"
                @click="editPlan(scope.row)"
              >
                编辑
              </el-button>
              <el-button 
                :type="scope.row.status === 1 ? 'warning' : 'success'" 
                size="small"
                @click="toggleStatus(scope.row)"
              >
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button 
                type="info" 
                size="small"
                @click="applyPlan(scope.row)"
                :disabled="scope.row.status !== 1"
              >
                应用
              </el-button>
              <el-button 
                type="danger" 
                size="small"
                @click="deletePlan(scope.row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑补贴方案对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="120px"
      >
        <el-form-item label="方案名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入方案名称" />
        </el-form-item>
        
        <el-form-item label="补贴金额" prop="amount">
          <el-input-number
            v-model="formData.amount"
            :min="0"
            :precision="2"
            placeholder="请输入补贴金额"
          />
        </el-form-item>
        
        <el-form-item label="补贴周期" prop="period">
          <el-select v-model="formData.period" placeholder="请选择补贴周期">
            <el-option label="每日" value="daily" />
            <el-option label="每周" value="weekly" />
            <el-option label="每月" value="monthly" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="补贴对象" prop="targetType">
          <el-select 
            v-model="formData.targetType" 
            placeholder="请选择补贴对象"
            @change="handleTargetTypeChange"
          >
            <el-option label="学生" value="student" />
            <el-option label="员工" value="staff" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="补贴群体" prop="targetGroup">
          <el-radio-group v-model="formData.targetGroup" @change="handleTargetGroupChange">
            <el-radio value="">全部{{ formData.targetType === 'student' ? '学生' : '员工' }}</el-radio>
            <el-radio :value="formData.targetType === 'student' ? 'college' : 'department'">
              按{{ formData.targetType === 'student' ? '学院' : '部门' }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item 
          v-if="formData.targetGroup" 
          :label="formData.targetType === 'student' ? '选择学院' : '选择部门'" 
          prop="groupValue"
        >
          <el-select v-model="formData.groupValue" placeholder="请选择">
            <el-option
              v-for="option in groupOptions"
              :key="option"
              :label="option"
              :value="option"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="是否清零" prop="isReset">
          <el-radio-group v-model="formData.isReset">
            <el-radio :value="1">清零（每个周期开始时清零）</el-radio>
            <el-radio :value="0">不清零（累积补贴）</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="现金支付比例" prop="cashRatio">
          <el-slider
            v-model="cashRatioPercentage"
            :min="0"
            :max="100"
            :step="5"
            show-input
            :format-tooltip="formatTooltip"
            style="width: 100%"
          />
          <div class="ratio-description">
            当前设置：{{ cashRatioPercentage }}% 现金支付，{{ 100 - cashRatioPercentage }}% 补贴支付
          </div>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ isEdit ? '更新' : '创建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getSubsidyPlansPage,
  createSubsidyPlan,
  updateSubsidyPlan,
  deleteSubsidyPlan,
  toggleSubsidyPlanStatus,
  applySubsidyPlan,
  getAllColleges,
  getAllDepartments
} from '@/api/subsidy'

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const subsidyPlans = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 搜索表单
const searchForm = ref({
  name: '',
  targetType: '',
  period: '',
  status: null
})

// 分页
const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = ref({
  id: null,
  name: '',
  amount: 0,
  period: '',
  targetType: '',
  targetGroup: '',
  groupValue: '',
  isReset: 1,
  cashRatio: 0,
  status: 1
})

// 现金支付比例（用于滑块显示）
const cashRatioPercentage = computed({
  get() {
    return Math.round(formData.value.cashRatio * 100)
  },
  set(value) {
    formData.value.cashRatio = value / 100
  }
})

// 群体选项
const groupOptions = ref([])

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入方案名称', trigger: 'blur' },
    { min: 2, max: 50, message: '方案名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入补贴金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '补贴金额不能小于0', trigger: 'blur' }
  ],
  period: [
    { required: true, message: '请选择补贴周期', trigger: 'change' }
  ],
  targetType: [
    { required: true, message: '请选择补贴对象', trigger: 'change' }
  ],
  groupValue: [
    { required: true, message: '请选择群体', trigger: 'change' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑补贴方案' : '新增补贴方案'
})

// 监听targetType变化
watch(() => formData.value.targetType, (newValue) => {
  if (newValue) {
    loadGroupOptions()
  }
})

// 方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size,
      ...searchForm.value
    }
    
    const response = await getSubsidyPlansPage(params)
    subsidyPlans.value = response.data.records || []
    pagination.value.total = response.data.total || 0
  } catch (error) {
    console.error('加载补贴方案失败:', error)
    ElMessage.error('加载补贴方案失败')
  } finally {
    loading.value = false
  }
}

const loadGroupOptions = async () => {
  if (!formData.value.targetType) return
  
  try {
    if (formData.value.targetType === 'student') {
      const response = await getAllColleges()
      groupOptions.value = response.data || []
    } else if (formData.value.targetType === 'staff') {
      const response = await getAllDepartments()
      groupOptions.value = response.data || []
    }
  } catch (error) {
    console.error('加载群体选项失败:', error)
    ElMessage.error('加载群体选项失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const editPlan = (plan) => {
  isEdit.value = true
  dialogVisible.value = true
  formData.value = { ...plan }
  loadGroupOptions()
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  formData.value = {
    id: null,
    name: '',
    amount: 0,
    period: '',
    targetType: '',
    targetGroup: '',
    groupValue: '',
    isReset: 1,
    cashRatio: 0,
    status: 1
  }
  groupOptions.value = []
}

const resetSearch = () => {
  searchForm.value = {
    name: '',
    targetType: '',
    period: '',
    status: null
  }
  pagination.value.page = 1
  loadData()
}

const handleTargetTypeChange = () => {
  formData.value.targetGroup = ''
  formData.value.groupValue = ''
  loadGroupOptions()
}

const handleTargetGroupChange = () => {
  formData.value.groupValue = ''
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    const submitData = { ...formData.value }
    if (!submitData.targetGroup) {
      submitData.groupValue = null
    }
    
    if (isEdit.value) {
      await updateSubsidyPlan(submitData.id, submitData)
      ElMessage.success('补贴方案更新成功')
    } else {
      await createSubsidyPlan(submitData)
      ElMessage.success('补贴方案创建成功')
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('保存补贴方案失败:', error)
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg)
    } else {
      ElMessage.error('保存补贴方案失败')
    }
  } finally {
    submitting.value = false
  }
}

const toggleStatus = async (plan) => {
  const action = plan.status === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}补贴方案"${plan.name}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await toggleSubsidyPlanStatus(plan.id)
    ElMessage.success(`补贴方案${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

const applyPlan = async (plan) => {
  try {
    await ElMessageBox.confirm(
      `确定要将补贴方案"${plan.name}"应用到相关用户吗？这将为符合条件的用户创建或更新补贴账户。`,
      '确认应用',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await applySubsidyPlan(plan.id)
    ElMessage.success('补贴方案应用成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('应用补贴方案失败:', error)
      if (error.response?.data?.msg) {
        ElMessage.error(error.response.data.msg)
      } else {
        ElMessage.error('应用补贴方案失败')
      }
    }
  }
}

const deletePlan = async (plan) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除补贴方案"${plan.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteSubsidyPlan(plan.id)
    ElMessage.success('补贴方案删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除补贴方案失败:', error)
      ElMessage.error('删除补贴方案失败')
    }
  }
}

const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.page = 1
  loadData()
}

const handleCurrentChange = (page) => {
  pagination.value.page = page
  loadData()
}

// 工具方法
const getPeriodTagType = (period) => {
  const types = {
    'daily': 'primary',
    'weekly': 'success',
    'monthly': 'warning'
  }
  return types[period] || 'info'
}

const getPeriodLabel = (period) => {
  const labels = {
    'daily': '每日',
    'weekly': '每周',
    'monthly': '每月'
  }
  return labels[period] || period
}

const getTargetGroupLabel = (targetGroup) => {
  const labels = {
    'college': '学院',
    'department': '部门'
  }
  return labels[targetGroup] || targetGroup
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatTooltip = (value) => {
  return `${value}%`
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.subsidy-management-container {
  padding: 20px;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
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
  font-size: 24px;
  font-weight: 600;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-card :deep(.el-form) {
  margin-bottom: 0;
}

.filter-card :deep(.el-form-item) {
  margin-bottom: 18px;
}

.table-card {
  background: white;
  width: 100%;
}

.table-card :deep(.el-card__body) {
  padding: 20px;
}

.amount-text {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.ratio-text {
  color: #409eff;
  font-weight: bold;
  font-size: 14px;
}

.all-target {
  color: #909399;
  font-style: italic;
}

.pagination-section {
  margin-top: 20px;
  text-align: center;
}

.ratio-description {
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
  text-align: center;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.dialog-footer {
  text-align: right;
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 14px;
  width: 100% !important;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-table td) {
  font-size: 14px;
}

:deep(.el-table .cell) {
  padding: 0 8px;
  line-height: 1.5;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 12px;
  font-size: 12px;
  height: 24px;
  line-height: 22px;
}

/* 按钮组样式 */
:deep(.el-button) {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 4px;
}

:deep(.el-button + .el-button) {
  margin-left: 6px;
}

.operation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
  align-items: center;
}

.operation-buttons .el-button {
  margin: 0;
  font-size: 12px;
  padding: 6px 10px;
  min-width: 60px;
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #606266;
}

:deep(.el-form-item__content) {
  width: 100%;
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-radio-group) {
  width: 100%;
}

:deep(.el-slider) {
  margin: 10px 0;
}

/* 对话框样式 */
:deep(.el-dialog) {
  margin-top: 5vh !important;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px 20px;
  border-top: 1px solid #ebeef5;
}

/* 卡片样式 */
:deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fafafa;
}

/* 分页样式 */
:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pagination .el-select) {
  width: 120px !important;
}

:deep(.el-pagination .el-select .el-input) {
  width: 120px !important;
}

:deep(.el-pagination .el-select .el-input__wrapper) {
  width: 120px !important;
}

:deep(.el-pagination .el-select .el-input__inner) {
  width: 120px !important;
  padding-left: 8px !important;
  padding-right: 30px !important;
}

:deep(.el-pagination .el-pager-sizes) {
  margin-right: 10px;
}

/* 响应式优化 */
@media (max-width: 1400px) {
  .subsidy-management-container {
    padding: 15px;
  }
}

@media (max-width: 1200px) {
  .subsidy-management-container {
    padding: 15px;
  }
  
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  :deep(.el-table .cell) {
    padding: 0 6px;
  }
  
  :deep(.el-button) {
    font-size: 11px;
    padding: 4px 8px;
  }
}

@media (max-width: 768px) {
  .subsidy-management-container {
    padding: 10px;
  }
  
  :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
}
</style> 