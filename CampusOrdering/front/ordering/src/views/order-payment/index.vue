<template>
  <div class="payment-container">
    <div class="header-section">
      <el-button @click="goBack" circle>
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h2>订单支付</h2>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 支付内容 -->
    <div v-else-if="order && paymentInfo" class="payment-content">
      <!-- 订单信息 -->
      <el-card class="order-info-card">
        <template #header>
          <span class="card-title">订单信息</span>
        </template>
        <div class="order-basic-info">
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ order.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">用餐时间：</span>
            <span class="value">{{ formatDate(order.eatTime) }}</span>
          </div>
          <div class="info-row">
            <span class="label">订单类型：</span>
            <span class="value">{{ getTypeText(order.orderType) }}</span>
          </div>
        </div>
        
        <!-- 订单明细 -->
        <div class="order-items" v-if="orderDetails.length > 0">
          <h4>订单明细</h4>
          <div class="item-list">
            <div v-for="item in orderDetails" :key="item.id" class="order-item">
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
        </div>
      </el-card>

      <!-- 补贴信息 -->
      <el-card class="subsidy-info-card">
        <template #header>
          <span class="card-title">补贴信息</span>
        </template>
        
        <div class="subsidy-summary">
          <div class="summary-row">
            <span class="label">订单总金额：</span>
            <span class="value total-amount">¥{{ paymentInfo.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row" v-if="paymentInfo.availableSubsidy > 0">
            <span class="label">可用补贴金额：</span>
            <span class="value subsidy-amount">¥{{ paymentInfo.availableSubsidy.toFixed(2) }}</span>
          </div>
          <div class="summary-row" v-if="paymentInfo.usedSubsidy > 0">
            <span class="label">使用补贴金额：</span>
            <span class="value used-subsidy">¥{{ paymentInfo.usedSubsidy.toFixed(2) }}</span>
          </div>
          <div class="summary-row" v-if="paymentInfo.requiredCashRatio > 0">
            <span class="label">最低现金比例：</span>
            <span class="value cash-ratio">{{ (paymentInfo.requiredCashRatio * 100).toFixed(0) }}%</span>
          </div>
          <div class="summary-row final-amount">
            <span class="label">实际支付金额：</span>
            <span class="value actual-amount">¥{{ paymentInfo.actualAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 补贴明细 -->
        <div class="subsidy-details" v-if="paymentInfo.subsidyAccounts.length > 0">
          <h4>补贴明细</h4>
          <div class="subsidy-list">
            <div v-for="account in paymentInfo.subsidyAccounts" :key="account.accountId" class="subsidy-item">
              <div class="subsidy-info">
                <div class="subsidy-name">{{ account.subsidyName }}</div>
                <div class="subsidy-balance">余额：¥{{ account.balance.toFixed(2) }}</div>
                <div class="subsidy-usage" v-if="account.usedAmount > 0">
                  使用：¥{{ account.usedAmount.toFixed(2) }}
                </div>
                <div class="cash-ratio-info" v-if="account.cashRatio > 0">
                  现金比例要求：{{ (account.cashRatio * 100).toFixed(0) }}%
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 支付方式 -->
      <el-card class="payment-method-card">
        <template #header>
          <span class="card-title">支付方式</span>
        </template>
        
        <div class="payment-method-info">
          <div class="method-display">
            <el-tag :type="getPaymentMethodType(paymentInfo.paymentMethod)" size="large">
              {{ getPaymentMethodText(paymentInfo.paymentMethod) }}
            </el-tag>
          </div>
          
          <div class="method-description" v-if="paymentInfo.paymentMethod === 'mixed'">
            <p>本次支付将使用混合支付方式：</p>
            <ul>
              <li>补贴支付：¥{{ paymentInfo.usedSubsidy.toFixed(2) }}</li>
              <li>现金支付：¥{{ paymentInfo.actualAmount.toFixed(2) }}</li>
            </ul>
          </div>
          <div class="method-description" v-else-if="paymentInfo.paymentMethod === 'subsidy'">
            <p>本次支付将完全使用补贴支付</p>
          </div>
          <div class="method-description" v-else>
            <p>本次支付将使用现金支付</p>
          </div>
        </div>
      </el-card>

      <!-- 支付按钮 -->
      <div class="payment-actions">
        <el-button size="large" @click="goBack">取消</el-button>
        <el-button 
          type="primary" 
          size="large" 
          @click="confirmPayment"
          :loading="paying"
          :disabled="!paymentInfo"
        >
          确认支付 ¥{{ paymentInfo.actualAmount.toFixed(2) }}
        </el-button>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-section">
      <el-result
        icon="error"
        title="加载失败"
        sub-title="订单信息或支付信息加载失败"
      >
        <template #extra>
          <el-button type="primary" @click="loadData">重新加载</el-button>
          <el-button @click="goBack">返回</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderById, getOrderDetails, calculateSubsidy, payOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const paying = ref(false)
const order = ref(null)
const orderDetails = ref([])
const paymentInfo = ref(null)

// 方法
const loadData = async () => {
  const orderId = route.params.id
  if (!orderId) {
    ElMessage.error('订单ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    // 并行加载订单信息、订单明细和支付信息
    const [orderRes, detailsRes, paymentRes] = await Promise.all([
      getOrderById(orderId),
      getOrderDetails(orderId),
      calculateSubsidy(orderId)
    ])

    order.value = orderRes.data
    orderDetails.value = detailsRes.data
    paymentInfo.value = paymentRes.data

    // 验证订单状态
    if (order.value.status !== 'pending') {
      ElMessage.warning('订单状态不允许支付')
      goBack()
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const confirmPayment = async () => {
  try {
    await ElMessageBox.confirm(
      `确认支付 ¥${paymentInfo.value.actualAmount.toFixed(2)} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    paying.value = true
    
    // 调用支付接口
    await payOrder(route.params.id, paymentInfo.value)
    
    ElMessage.success('支付成功！')
    
    // 跳转到订单详情或订单列表
    router.push('/order-history')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      if (error.response?.data?.msg) {
        ElMessage.error(error.response.data.msg)
      } else {
        ElMessage.error('支付失败，请重试')
      }
    }
  } finally {
    paying.value = false
  }
}

const goBack = () => {
  router.go(-1)
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

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.payment-container {
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

.payment-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 订单信息卡片 */
.order-info-card .order-basic-info {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: #606266;
  font-weight: 500;
}

.value {
  color: #303133;
  font-weight: 600;
}

.order-items h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.item-list {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}

.order-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-name {
  font-weight: 600;
  color: #303133;
}

.item-type {
  color: #909399;
  font-size: 12px;
  padding: 2px 6px;
  background: #f0f2f5;
  border-radius: 4px;
}

.item-spec {
  color: #909399;
  font-size: 14px;
}

.item-price {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

/* 补贴信息卡片 */
.subsidy-summary {
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  padding: 8px 0;
}

.summary-row.final-amount {
  border-top: 2px solid #409eff;
  padding-top: 15px;
  font-size: 18px;
}

.total-amount {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.subsidy-amount {
  color: #67c23a;
  font-weight: 600;
}

.used-subsidy {
  color: #e6a23c;
  font-weight: 600;
}

.cash-ratio {
  color: #f56c6c;
  font-weight: 600;
}

.actual-amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 700;
}

.subsidy-details h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.subsidy-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subsidy-item {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #67c23a;
}

.subsidy-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.subsidy-balance {
  color: #67c23a;
  font-size: 14px;
  margin-bottom: 4px;
}

.subsidy-usage {
  color: #e6a23c;
  font-size: 14px;
  margin-bottom: 4px;
}

.cash-ratio-info {
  color: #f56c6c;
  font-size: 12px;
}

/* 支付方式卡片 */
.payment-method-info {
  text-align: center;
}

.method-display {
  margin-bottom: 20px;
}

.method-description {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  text-align: left;
}

.method-description p {
  margin: 0 0 10px 0;
  color: #606266;
}

.method-description ul {
  margin: 0;
  padding-left: 20px;
}

.method-description li {
  color: #303133;
  margin-bottom: 5px;
}

/* 支付按钮 */
.payment-actions {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.payment-actions .el-button {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
}

.error-section {
  padding: 40px 20px;
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .payment-container {
    padding: 15px;
  }
  
  .payment-actions {
    flex-direction: column;
  }
  
  .summary-row,
  .info-row {
    flex-direction: column;
    gap: 5px;
  }
  
  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 