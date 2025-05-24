<template>
  <div class="order-management-container">
    <div class="header-section">
      <h2>订单管理</h2>
      <div class="actions-section">
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
            v-model="searchForm.orderNo"
            placeholder="订单号"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="订单状态" clearable style="width: 100%">
            <el-option label="待支付" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="准备中" value="preparing" />
            <el-option label="待取餐" value="ready" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.orderType" placeholder="订单类型" clearable style="width: 100%">
            <el-option label="预约订餐" value="reservation" />
            <el-option label="堂食点餐" value="dine_in" />
          </el-select>
        </el-col>
<!--        <el-col :span="4">-->
<!--          <el-select v-model="searchForm.userType" placeholder="用户类型" clearable style="width: 100%">-->
<!--            <el-option label="学生" value="student" />-->
<!--            <el-option label="员工" value="staff" />-->
<!--            <el-option label="管理员" value="admin" />-->
<!--          </el-select>-->
<!--        </el-col>-->
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

    <!-- 订单列表 -->
    <el-card class="table-card">
      <el-table
        :data="orderList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createTime', order: 'descending' }"
        :table-layout="'auto'"
      >
        <el-table-column prop="orderNo" label="订单号" min-width="150" fixed="left" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="userType" label="用户类型" min-width="100">
          <template #default="scope">
            <el-tag :type="getUserTypeTagType(scope.row.userType)">
              {{ getUserTypeText(scope.row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderType" label="订单类型" min-width="100">
          <template #default="scope">
            <el-tag :type="getOrderTypeTagType(scope.row.orderType)">
              {{ getOrderTypeText(scope.row.orderType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="订单金额" min-width="100" align="right">
          <template #default="scope">
            <span class="amount-text">¥{{ scope.row.amount?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="subsidyAmount" label="补贴金额" min-width="100" align="right">
          <template #default="scope">
            <span class="subsidy-text">¥{{ scope.row.subsidyAmount?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" min-width="100" align="right">
          <template #default="scope">
            <span class="actual-text">¥{{ scope.row.actualAmount?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.paymentMethod" :type="getPaymentMethodTagType(scope.row.paymentMethod)">
              {{ getPaymentMethodText(scope.row.paymentMethod) }}
            </el-tag>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pickupNo" label="取餐号" width="80">
          <template #default="scope">
            <span v-if="scope.row.pickupNo" class="pickup-no">{{ scope.row.pickupNo }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="eatTime" label="就餐时间" min-width="150">
          <template #default="scope">
            {{ formatDate(scope.row.eatTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" sortable>
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.remark">{{ scope.row.remark }}</span>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="small" 
                @click="viewOrder(scope.row)"
              >
                查看
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="editOrder(scope.row)"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteOrderItem(scope.row)"
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

    <!-- 编辑订单对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑订单"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="editForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="待支付" value="pending" />
            <el-option label="已支付" value="paid" />
            <el-option label="准备中" value="preparing" />
            <el-option label="待取餐" value="ready" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="就餐时间" prop="eatTime">
          <el-date-picker
            v-model="editForm.eatTime"
            type="datetime"
            placeholder="选择就餐时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="取餐号">
          <el-input v-model="editForm.pickupNo" placeholder="可选" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="editForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="可选"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveOrder" :loading="saving">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看订单详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="订单详情"
      width="800px"
    >
      <div v-if="selectedOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="用户类型">
            <el-tag :type="getUserTypeTagType(selectedOrder.userType)">
              {{ getUserTypeText(selectedOrder.userType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单类型">
            <el-tag :type="getOrderTypeTagType(selectedOrder.orderType)">
              {{ getOrderTypeText(selectedOrder.orderType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusTagType(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="取餐号">
            {{ selectedOrder.pickupNo || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span class="amount-text">¥{{ selectedOrder.amount?.toFixed(2) || '0.00' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="补贴金额">
            <span class="subsidy-text">¥{{ selectedOrder.subsidyAmount?.toFixed(2) || '0.00' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="actual-text">¥{{ selectedOrder.actualAmount?.toFixed(2) || '0.00' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">
            <el-tag v-if="selectedOrder.paymentMethod" :type="getPaymentMethodTagType(selectedOrder.paymentMethod)">
              {{ getPaymentMethodText(selectedOrder.paymentMethod) }}
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="就餐时间">
            {{ formatDate(selectedOrder.eatTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedOrder.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedOrder.remark || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 订单明细 -->
        <div class="order-details-section">
          <h3>订单明细</h3>
          <el-table :data="orderDetails" stripe style="width: 100%">
            <el-table-column prop="dishName" label="菜品名称" />
            <el-table-column prop="dishType" label="菜品类型">
              <template #default="scope">
                <el-tag :type="getDishTypeTagType(scope.row.dishType)">
                  {{ getDishTypeText(scope.row.dishType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" align="right">
              <template #default="scope">
                ¥{{ scope.row.price?.toFixed(2) || '0.00' }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" align="center" />
            <el-table-column prop="amount" label="小计" align="right">
              <template #default="scope">
                ¥{{ scope.row.amount?.toFixed(2) || '0.00' }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { 
  getAllOrders, 
  updateOrderStatus, 
  deleteOrder, 
  getOrderDetails,
  getOrderById
} from '@/api/order'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const deleting = ref(false)
const deleteId = ref(null)
const saving = ref(false)
const orderList = ref([])
const orderDetails = ref([])
const selectedOrder = ref(null)

// 搜索表单
const searchForm = reactive({
  orderNo: '',
  status: '',
  orderType: '',
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
const editFormRef = ref()
const editForm = reactive({
  id: null,
  orderNo: '',
  status: '',
  eatTime: '',
  pickupNo: '',
  remark: ''
})

const editRules = {
  status: [
    { required: true, message: '请选择订单状态', trigger: 'change' }
  ],
  eatTime: [
    { required: true, message: '请选择就餐时间', trigger: 'change' }
  ]
}

// 方法
const loadOrders = async () => {
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
    
    const response = await getAllOrders(params)
    if (response.code === 200) {
      orderList.value = response.data.records || []
      pagination.total = response.data.total || 0
    } else {
      ElMessage.error(response.msg || '获取订单列表失败')
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadOrders()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.current = 1
  loadOrders()
}

const refreshData = () => {
  loadOrders()
}

const handleSizeChange = (val) => {
  pagination.size = val
  pagination.current = 1
  loadOrders()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadOrders()
}

const viewOrder = async (order) => {
  try {
    // 获取订单详情
    const orderResponse = await getOrderById(order.id)
    const detailResponse = await getOrderDetails(order.id)
    
    if (orderResponse.code === 200 && detailResponse.code === 200) {
      selectedOrder.value = orderResponse.data
      orderDetails.value = detailResponse.data || []
      viewDialogVisible.value = true
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

const editOrder = (order) => {
  Object.assign(editForm, {
    id: order.id,
    orderNo: order.orderNo,
    status: order.status,
    eatTime: order.eatTime,
    pickupNo: order.pickupNo || '',
    remark: order.remark || ''
  })
  editDialogVisible.value = true
}

const saveOrder = async () => {
  if (!editFormRef.value) return
  
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    // 这里只更新状态，其他字段需要后端支持完整的订单更新API
    const result = await updateOrderStatus(editForm.id, editForm.status)
    if (result.code === 200) {
      ElMessage.success('订单更新成功')
      editDialogVisible.value = false
      loadOrders()
    } else {
      ElMessage.error(result.msg || '订单更新失败')
    }
  } catch (error) {
    console.error('订单更新失败:', error)
    ElMessage.error('订单更新失败')
  } finally {
    saving.value = false
  }
}

const deleteOrderItem = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除订单 ${order.orderNo} 吗？删除后无法恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deleting.value = true
    deleteId.value = order.id
    
    const result = await deleteOrder(order.id)
    if (result.code === 200) {
      ElMessage.success('订单删除成功')
      loadOrders()
    } else {
      ElMessage.error(result.msg || '订单删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  } finally {
    deleting.value = false
    deleteId.value = null
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

const getUserTypeText = (type) => {
  const typeMap = {
    'student': '学生',
    'staff': '员工',
    'admin': '管理员'
  }
  return typeMap[type] || type
}

const getUserTypeTagType = (type) => {
  const typeMap = {
    'student': 'primary',
    'staff': 'success',
    'admin': 'warning'
  }
  return typeMap[type] || 'info'
}

const getOrderTypeText = (type) => {
  const typeMap = {
    'reservation': '预约订餐',
    'dine_in': '堂食点餐'
  }
  return typeMap[type] || type
}

const getOrderTypeTagType = (type) => {
  const typeMap = {
    'reservation': 'primary',
    'dine_in': 'success'
  }
  return typeMap[type] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '已支付',
    'preparing': '准备中',
    'ready': '待取餐',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'paid': 'primary',
    'preparing': 'info',
    'ready': 'success',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

const getPaymentMethodText = (method) => {
  const methodMap = {
    'cash': '现金支付',
    'subsidy': '补贴支付',
    'mixed': '混合支付'
  }
  return methodMap[method] || method
}

const getPaymentMethodTagType = (method) => {
  const methodMap = {
    'cash': 'success',
    'subsidy': 'primary',
    'mixed': 'warning'
  }
  return methodMap[method] || 'info'
}

const getDishTypeText = (type) => {
  const typeMap = {
    'dish': '菜品',
    'staple': '主食',
    'combo': '套餐',
    'product': '商品'
  }
  return typeMap[type] || type
}

const getDishTypeTagType = (type) => {
  const typeMap = {
    'dish': 'primary',
    'staple': 'success',
    'combo': 'warning',
    'product': 'info'
  }
  return typeMap[type] || 'default'
}

// 生命周期
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-management-container {
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
  min-width: 1200px;
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

.table-card .el-table td[align="right"] {
  text-align: right !important;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

.amount-text {
  color: #f56c6c;
  font-weight: 600;
}

.subsidy-text {
  color: #67c23a;
  font-weight: 600;
}

.actual-text {
  color: #409eff;
  font-weight: 600;
}

.pickup-no {
  font-weight: 600;
  color: #e6a23c;
}

.empty-text {
  color: #c0c4cc;
}

.order-detail {
  padding: 10px 0;
}

.order-details-section {
  margin-top: 30px;
}

.order-details-section h3 {
  margin-bottom: 15px;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
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