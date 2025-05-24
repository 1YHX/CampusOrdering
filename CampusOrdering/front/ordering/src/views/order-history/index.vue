<template>
  <div class="order-history-container">
    <div class="header-section">
      <h2>订单记录</h2>
      <div class="total-info" v-if="total > 0">
        <span class="total-text">共找到 <strong>{{ total }}</strong> 条订单记录</span>
      </div>
      <div class="filter-section">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
        <el-select 
          v-model="statusFilter" 
          placeholder="订单状态" 
          @change="handleStatusChange"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="待支付" value="pending" />
          <el-option label="已支付" value="paid" />
          <el-option label="准备中" value="preparing" />
          <el-option label="待取餐" value="ready" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
        <el-select 
          v-model="typeFilter" 
          placeholder="订单类型" 
          @change="handleTypeChange"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="预约订餐" value="reservation" />
          <el-option label="堂食点餐" value="dine_in" />
        </el-select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 订单列表 -->
    <div v-else class="orders-section">
      <el-card 
        v-for="order in orders" 
        :key="order.id" 
        class="order-card"
        :body-style="{ padding: '20px' }"
      >
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag 
              :type="getStatusType(order.status)" 
              size="small"
            >
              {{ getStatusText(order.status) }}
            </el-tag>
            <el-tag 
              :type="getTypeType(order.orderType)" 
              size="small"
            >
              {{ getTypeText(order.orderType) }}
            </el-tag>
            <span v-if="order.pickupNo" class="pickup-no">取餐号：{{ order.pickupNo }}</span>
          </div>
          <div class="order-time">
            下单时间：{{ formatDate(order.createTime) }}
          </div>
        </div>

        <div class="order-content">
          <!-- 就餐时间 -->
          <div class="eat-time">
            <span class="eat-time-label">就餐时间：</span>
            <span class="eat-time-text">{{ formatDate(order.eatTime) }}</span>
          </div>
          
          <!-- 订单明细会在加载时获取 -->
          <div class="order-items" v-if="order.details && order.details.length > 0">
            <div 
              v-for="item in order.details" 
              :key="item.id" 
              class="order-item"
            >
              <div class="item-info">
                <span class="item-name">{{ item.dishName }}</span>
                <span class="item-type">{{ getTypeText(item.dishType) }}</span>
                <span class="item-spec">x{{ item.quantity }}</span>
              </div>
              <div class="item-price">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
            </div>
          </div>
          
          <div v-if="order.remark" class="order-remarks">
            <span class="remarks-label">备注：</span>
            <span class="remarks-text">{{ order.remark }}</span>
          </div>
          
          <!-- 金额信息 -->
          <div class="amount-info">
            <div v-if="order.subsidyAmount && order.subsidyAmount > 0" class="subsidy-amount">
              补贴金额：¥{{ order.subsidyAmount.toFixed(2) }}
            </div>
            <div class="payment-method" v-if="order.paymentMethod">
              支付方式：{{ getPaymentMethodText(order.paymentMethod) }}
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            <span v-if="order.details && order.details.length > 0">共 {{ getTotalQuantity(order.details) }} 件商品</span>
            <div class="amount-details">
              <span class="total-amount">总金额：¥{{ order.amount ? order.amount.toFixed(2) : '0.00' }}</span>
              <span class="actual-amount">实付：¥{{ order.actualAmount ? order.actualAmount.toFixed(2) : '0.00' }}</span>
            </div>
          </div>
          <div class="order-actions">
            <el-button 
              v-if="order.status === 'pending'" 
              type="danger" 
              size="small"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>
            <el-button 
              v-if="order.status === 'pending'" 
              type="primary" 
              size="small"
              @click="payOrder(order)"
            >
              立即支付
            </el-button>
            <el-button 
              size="small"
              @click="viewOrderDetail(order)"
            >
              查看详情
            </el-button>
            <el-button 
              v-if="order.status === 'completed'" 
              type="primary" 
              size="small"
              @click="reorder(order)"
            >
              再来一单
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <div v-if="!orders.length && !loading" class="empty-state">
        <el-empty description="暂无订单记录" />
      </div>
    </div>

    <!-- 分页 - 移除条件限制，只要不在加载中就显示 -->
    <div class="pagination-section" v-if="!loading">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :hide-on-single-page="false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, cancelOrder as cancelOrderApi } from '@/api/order'
import { useRouter } from 'vue-router'

const router = useRouter()

// 状态管理
const orders = ref([])
const dateRange = ref([])
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 方法
const handleDateChange = () => {
  currentPage.value = 1 // 重置到第一页
  loadOrders()
}

const handleStatusChange = () => {
  currentPage.value = 1 // 重置到第一页
  loadOrders()
}

const handleTypeChange = () => {
  currentPage.value = 1 // 重置到第一页
  loadOrders()
}

const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'paid': 'info',
    'preparing': 'primary',
    'ready': 'success',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
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
  return statusMap[status] || '未知'
}

const getTypeType = (type) => {
  return type === 'reservation' ? 'primary' : 'success'
}

const getTypeText = (type) => {
  const typeMap = {
    'reservation': '预约订餐',
    'dine_in': '堂食点餐'
  }
  return typeMap[type] || '未知'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getTotalQuantity = (items) => {
  if (!items || !Array.isArray(items)) return 0
  return items.reduce((sum, item) => sum + item.quantity, 0)
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrderApi(order.id)
    ElMessage.success('订单已取消')
    // 重新加载订单列表
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const payOrder = async (order) => {
  // 跳转到支付页面
  router.push(`/order-payment/${order.id}`)
}

const viewOrderDetail = (order) => {
  // 跳转到订单详情页面
  router.push(`/order-detail/${order.id}`)
}

const reorder = (order) => {
  // 重新下单逻辑
  ElMessage.info('再来一单功能开发中...')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadOrders()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadOrders()
}

const getPaymentMethodText = (method) => {
  const methodMap = {
    'cash': '现金支付',
    'subsidy': '补贴支付',
    'mixed': '混合支付'
  }
  return methodMap[method] || '未知'
}

// 从数据库加载订单数据
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 添加状态过滤
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    // 添加订单类型过滤
    if (typeFilter.value) {
      params.orderType = typeFilter.value
    }
    
    // 添加日期过滤
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    console.log('请求参数:', params)
    const { data } = await getUserOrders(params)
    console.log('后端返回数据:', data)
    
    // 处理MyBatis Plus分页返回的数据结构
    if (data && typeof data === 'object') {
      if (data.records && Array.isArray(data.records)) {
        // MyBatis Plus分页结果
        orders.value = data.records
        total.value = data.total || 0
        console.log('分页数据处理完成 - 订单数量:', orders.value.length, '总数:', total.value)
      } else if (Array.isArray(data)) {
        // 简单数组
        orders.value = data
        total.value = data.length
        console.log('数组数据处理完成 - 订单数量:', orders.value.length)
      } else {
        // 其他情况
        orders.value = []
        total.value = 0
        console.log('未知数据格式:', data)
      }
    } else {
      orders.value = []
      total.value = 0
      console.log('无效数据:', data)
    }
    
    // 为每个订单加载详情
    if (orders.value.length > 0) {
      await loadOrderDetails()
    }
    
    console.log('最终订单数据:', orders.value)
    console.log('总数:', total.value)
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载订单详情
const loadOrderDetails = async () => {
  try {
    // 动态导入订单详情API
    const { getOrderDetails } = await import('@/api/order')
    
    // 为每个订单加载详情
    for (const order of orders.value) {
      try {
        const { data: details } = await getOrderDetails(order.id)
        order.details = details || []
      } catch (error) {
        console.error(`加载订单 ${order.id} 详情失败:`, error)
        order.details = []
      }
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-history-container {
  padding: 20px;
}

.header-section {
  margin-bottom: 20px;
}

.header-section h2 {
  margin: 0 0 20px 0;
  color: #303133;
}

.total-info {
  margin-bottom: 15px;
}

.total-text {
  color: #606266;
  font-size: 14px;
}

.total-text strong {
  color: #409eff;
  font-weight: 600;
}

.filter-section {
  display: flex;
  gap: 15px;
  align-items: center;
}

.orders-section {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 15px;
  border-radius: 8px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-no {
  font-weight: 500;
  color: #303133;
}

.order-time {
  color: #909399;
  font-size: 14px;
}

.order-content {
  margin-bottom: 15px;
}

.order-items {
  margin-bottom: 10px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}

.item-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-name {
  color: #303133;
  font-size: 14px;
}

.item-spec {
  color: #909399;
  font-size: 14px;
}

.item-price {
  color: #f56c6c;
  font-weight: 500;
}

.order-remarks {
  background: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.remarks-label {
  color: #606266;
}

.remarks-text {
  color: #303133;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.total-amount {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pickup-no {
  color: #f56c6c;
  font-weight: 500;
  font-size: 12px;
}

.eat-time {
  margin-bottom: 15px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.eat-time-label {
  color: #606266;
  font-weight: 500;
}

.eat-time-text {
  color: #303133;
  margin-left: 8px;
}

.loading-section {
  padding: 20px;
}

.amount-info {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.subsidy-amount {
  color: #67c23a;
  font-weight: 500;
}

.payment-method {
  color: #909399;
}

.amount-details {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.total-amount {
  color: #606266;
  font-size: 14px;
}

.actual-amount {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}

.item-type {
  color: #909399;
  font-size: 12px;
  margin-left: 8px;
}
</style> 