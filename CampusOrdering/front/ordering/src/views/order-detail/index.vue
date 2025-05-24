<template>
  <div class="order-detail-container">
    <div class="header-section">
      <el-button @click="goBack" circle>
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>订单详情</h2>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 订单详情内容 -->
    <div v-else-if="order" class="order-detail-content">
      <!-- 订单状态卡片 -->
      <el-card class="status-card">
        <template #header>
          <div class="status-header">
            <span class="card-title">订单状态</span>
            <el-tag 
              :type="getStatusType(order.status)" 
              size="large"
              class="status-tag"
            >
              <el-icon class="status-icon">
                <component :is="getStatusIcon(order.status)" />
              </el-icon>
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </template>
        
        <div class="status-info">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
            <el-button 
              size="small" 
              text 
              @click="copyOrderNo"
              class="copy-btn"
            >
              <el-icon><DocumentCopy /></el-icon>
              复制
            </el-button>
          </div>
          <div class="info-item" v-if="order.pickupNo">
            <span class="label">取餐号：</span>
            <span class="value pickup-no">{{ order.pickupNo }}</span>
          </div>
        </div>
      </el-card>

      <!-- 订单信息卡片 -->
      <el-card class="order-info-card">
        <template #header>
          <span class="card-title">订单信息</span>
        </template>
        
        <div class="order-basic-info">
          <div class="info-row">
            <span class="label">订单类型：</span>
            <span class="value">
              <el-tag :type="getTypeType(order.orderType)" size="small">
                {{ getTypeText(order.orderType) }}
              </el-tag>
            </span>
          </div>
          <div class="info-row">
            <span class="label">用餐时间：</span>
            <span class="value">{{ formatDate(order.eatTime) }}</span>
          </div>
          <div class="info-row">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="info-row" v-if="order.remark">
            <span class="label">备注：</span>
            <span class="value">{{ order.remark }}</span>
          </div>
        </div>
      </el-card>

      <!-- 订单明细卡片 -->
      <el-card class="order-items-card">
        <template #header>
          <span class="card-title">订单明细</span>
        </template>
        
        <div class="items-list" v-if="orderDetails.length > 0">
          <div v-for="item in orderDetails" :key="item.id" class="order-item">
            <div class="item-content">
              <div class="item-info">
                <h4 class="item-name">{{ item.dishName }}</h4>
                <div class="item-meta">
                  <el-tag size="small" :type="getTypeTagType(item.dishType)">
                    {{ getTypeText(item.dishType) }}
                  </el-tag>
                  <span class="item-price">¥{{ item.price.toFixed(2) }}</span>
                </div>
              </div>
              <div class="item-quantity">
                <span class="quantity">×{{ item.quantity }}</span>
                <span class="amount">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
          </div>
          
          <!-- 合计 -->
          <div class="items-summary">
            <div class="summary-row">
              <span class="label">商品总数：</span>
              <span class="value">{{ getTotalQuantity() }} 件</span>
            </div>
            <div class="summary-row total-row">
              <span class="label">商品总计：</span>
              <span class="value total-amount">¥{{ order.amount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-items">
          <el-empty description="暂无订单明细" />
        </div>
      </el-card>

      <!-- 支付信息卡片 -->
      <el-card class="payment-info-card" v-if="order.status !== 'pending'">
        <template #header>
          <span class="card-title">支付信息</span>
        </template>
        
        <div class="payment-details">
          <div class="payment-row">
            <span class="label">支付方式：</span>
            <span class="value">
              <el-tag 
                :type="getPaymentMethodType(order.paymentMethod)" 
                size="small"
              >
                {{ getPaymentMethodText(order.paymentMethod) }}
              </el-tag>
            </span>
          </div>
          
          <div class="payment-breakdown">
            <div class="breakdown-row">
              <span class="label">订单总金额：</span>
              <span class="value">¥{{ order.amount.toFixed(2) }}</span>
            </div>
            <div class="breakdown-row" v-if="order.subsidyAmount > 0">
              <span class="label">补贴支付：</span>
              <span class="value subsidy-amount">
                -¥{{ order.subsidyAmount.toFixed(2) }}
              </span>
            </div>
            <div class="breakdown-row final-amount">
              <span class="label">实际支付：</span>
              <span class="value actual-amount">¥{{ order.actualAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button 
          v-if="order.status === 'pending'" 
          type="danger" 
          @click="cancelOrder"
          :loading="canceling"
        >
          取消订单
        </el-button>
        <el-button 
          v-if="order.status === 'pending'" 
          type="primary" 
          @click="payOrder"
        >
          立即支付
        </el-button>
        <el-button 
          v-if="order.status === 'completed'" 
          type="primary" 
          @click="reorder"
        >
          再来一单
        </el-button>
        <el-button @click="goBack">
          返回
        </el-button>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-section">
      <el-result
        icon="error"
        title="订单不存在"
        sub-title="找不到对应的订单信息"
      >
        <template #extra>
          <el-button type="primary" @click="goBack">返回</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, 
  DocumentCopy,
  Clock,
  Check,
  Warning,
  Close,
  Loading
} from '@element-plus/icons-vue'
import { 
  getOrderById, 
  getOrderDetails, 
  cancelOrder as cancelOrderApi 
} from '@/api/order'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const canceling = ref(false)
const order = ref(null)
const orderDetails = ref([])

// 方法
const loadOrderData = async () => {
  const orderId = route.params.id
  if (!orderId) {
    ElMessage.error('订单ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    // 并行加载订单信息和订单明细
    const [orderRes, detailsRes] = await Promise.all([
      getOrderById(orderId),
      getOrderDetails(orderId)
    ])

    order.value = orderRes.data
    orderDetails.value = detailsRes.data

    console.log('订单数据:', order.value)
    console.log('订单明细:', orderDetails.value)
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.go(-1)
}

const copyOrderNo = async () => {
  try {
    await navigator.clipboard.writeText(order.value.orderNo)
    ElMessage.success('订单号已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败')
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    canceling.value = true
    await cancelOrderApi(order.value.id)
    ElMessage.success('订单已取消')
    
    // 重新加载订单数据
    loadOrderData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  } finally {
    canceling.value = false
  }
}

const payOrder = () => {
  router.push(`/order-payment/${order.value.id}`)
}

const reorder = () => {
  ElMessage.info('再来一单功能开发中...')
}

// 工具方法
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

const getStatusIcon = (status) => {
  const iconMap = {
    'pending': Clock,
    'paid': Check,
    'preparing': Loading,
    'ready': Check,
    'completed': Check,
    'cancelled': Close
  }
  return iconMap[status] || Clock
}

const getTypeType = (type) => {
  return type === 'reservation' ? 'primary' : 'success'
}

const getTypeText = (type) => {
  const typeMap = {
    'reservation': '预约订餐',
    'dine_in': '堂食点餐',
    'dish': '菜品',
    'staple': '主食',
    'combo': '套餐',
    'product': '商品'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type) => {
  const typeMap = {
    'dish': 'primary',
    'staple': 'success',
    'combo': 'warning',
    'product': 'info'
  }
  return typeMap[type] || 'default'
}

const getPaymentMethodText = (method) => {
  const methodMap = {
    'cash': '现金支付',
    'subsidy': '补贴支付',
    'mixed': '混合支付'
  }
  return methodMap[method] || '未知'
}

const getPaymentMethodType = (method) => {
  const typeMap = {
    'cash': 'warning',
    'subsidy': 'success',
    'mixed': 'primary'
  }
  return typeMap[method] || 'info'
}

const getTotalQuantity = () => {
  return orderDetails.value.reduce((sum, item) => sum + item.quantity, 0)
}

// 生命周期
onMounted(() => {
  loadOrderData()
})
</script>

<style scoped>
.order-detail-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.header-section h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.loading-section {
  padding: 40px 20px;
}

.order-detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 状态卡片 */
.status-card {
  border: 2px solid #e4e7ed;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-tag {
  font-size: 16px;
  padding: 8px 16px;
}

.status-icon {
  margin-right: 6px;
}

.status-info {
  margin-top: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.copy-btn {
  margin-left: 10px;
  padding: 4px 8px;
}

.pickup-no {
  color: #f56c6c;
  font-weight: 600;
  font-size: 18px;
}

/* 订单信息 */
.order-basic-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: #606266;
  font-weight: 500;
  min-width: 100px;
}

.value {
  color: #303133;
  font-weight: 600;
  flex: 1;
  text-align: right;
}

/* 订单明细 */
.items-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 15px;
  background: #fafafa;
}

.item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-info {
  flex: 1;
}

.item-name {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-price {
  color: #909399;
  font-size: 14px;
}

.item-quantity {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.quantity {
  color: #909399;
  font-size: 14px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.items-summary {
  border-top: 2px solid #e4e7ed;
  padding-top: 15px;
  margin-top: 15px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.summary-row.total-row {
  font-size: 18px;
  font-weight: 600;
  margin-top: 10px;
}

.total-amount {
  color: #f56c6c;
}

.empty-items {
  text-align: center;
  padding: 40px 0;
}

/* 支付信息 */
.payment-details {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-breakdown {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.breakdown-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.breakdown-row:last-child {
  margin-bottom: 0;
}

.breakdown-row.final-amount {
  border-top: 1px solid #e4e7ed;
  padding-top: 8px;
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
}

.subsidy-amount {
  color: #67c23a;
}

.actual-amount {
  color: #f56c6c;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.action-buttons .el-button {
  min-width: 120px;
  height: 40px;
}

.error-section {
  padding: 40px 20px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .order-detail-container {
    padding: 15px;
  }
  
  .info-row,
  .payment-row,
  .breakdown-row,
  .summary-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .value {
    text-align: left;
  }
  
  .item-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .item-quantity {
    align-items: flex-start;
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .status-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style> 