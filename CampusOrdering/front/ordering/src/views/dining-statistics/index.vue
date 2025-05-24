<template>
  <div class="dining-statistics-container">
    <div class="header-section">
      <h2>用餐统计</h2>
      <div class="date-picker-section">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          @change="handleDateChange"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="refreshData" :loading="refreshing">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 实时统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon reservation">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ reservationStats.reservationCount || 0 }}</div>
              <div class="stat-label">预订人数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon dishes">
              <el-icon><Bowl /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ reservationStats.totalDishQuantity || 0 }}</div>
              <div class="stat-label">菜品数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingStats.pendingCount || 0 }}</div>
              <div class="stat-label">待就餐人数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ completedStats.completedCount || 0 }}</div>
              <div class="stat-label">已完成订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计信息 -->
    <el-row :gutter="20" class="detail-section">
      <!-- 预订统计详情 -->
      <el-col :span="12">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>预订统计详情</span>
              <el-tag v-if="selectedDate">{{ selectedDate }}</el-tag>
            </div>
          </template>
          
          <div v-loading="loadingReservation">
            <div class="detail-item">
              <span class="detail-label">预订人数：</span>
              <span class="detail-value">{{ reservationStats.reservationCount || 0 }} 人</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">菜品总数：</span>
              <span class="detail-value">{{ reservationStats.totalDishQuantity || 0 }} 份</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">菜品种类：</span>
              <span class="detail-value">{{ reservationStats.dishVariety || 0 }} 种</span>
            </div>
            
            <!-- 时间段分布 -->
            <div class="time-slots" v-if="reservationStats.timeSlots">
              <h4>预订时间分布</h4>
              <div class="time-slot-grid">
                <div 
                  v-for="(count, timeSlot) in reservationStats.timeSlots" 
                  :key="timeSlot"
                  class="time-slot-item"
                >
                  <div class="time-slot-label">{{ timeSlot }}</div>
                  <div class="time-slot-count">{{ count }} 人</div>
                  <el-progress 
                    :percentage="getTimeSlotPercentage(count)" 
                    :show-text="false"
                    :stroke-width="8"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待就餐统计详情 -->
      <el-col :span="12">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>待就餐统计</span>
              <el-tag type="warning" v-if="pendingStats.oldestOrderTime">
                最长等待：{{ formatWaitTime(pendingStats.oldestOrderTime) }}
              </el-tag>
            </div>
          </template>
          
          <div v-loading="loadingPending">
            <div class="detail-item">
              <span class="detail-label">总待就餐：</span>
              <span class="detail-value">{{ pendingStats.pendingCount || 0 }} 人</span>
            </div>
            
            <!-- 按状态分类 -->
            <div class="status-distribution">
              <h4>订单状态分布</h4>
              <div class="status-items">
                <div class="status-item">
                  <el-tag type="info">已支付</el-tag>
                  <span class="status-count">{{ pendingStats.paidCount || 0 }}</span>
                </div>
                <div class="status-item">
                  <el-tag type="warning">准备中</el-tag>
                  <span class="status-count">{{ pendingStats.preparingCount || 0 }}</span>
                </div>
                <div class="status-item">
                  <el-tag type="success">待取餐</el-tag>
                  <span class="status-count">{{ pendingStats.readyCount || 0 }}</span>
                </div>
              </div>
            </div>
            
            <!-- 按类型分类 -->
            <div class="type-distribution">
              <h4>订单类型分布</h4>
              <div class="type-items">
                <div class="type-item">
                  <span class="type-label">预约订餐：</span>
                  <span class="type-count">{{ pendingStats.pendingReservation || 0 }} 人</span>
                </div>
                <div class="type-item">
                  <span class="type-label">堂食点餐：</span>
                  <span class="type-count">{{ pendingStats.pendingDineIn || 0 }} 人</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 已完成订单和热门菜品 -->
    <el-row :gutter="20" class="detail-section">
      <!-- 已完成订单详情 -->
      <el-col :span="12">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>已完成订单详情</span>
              <el-tag v-if="selectedDate">{{ selectedDate }}</el-tag>
            </div>
          </template>
          
          <div v-loading="loadingCompleted">
            <div class="detail-item">
              <span class="detail-label">完成订单数：</span>
              <span class="detail-value">{{ completedStats.completedCount || 0 }} 单</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">完成金额：</span>
              <span class="detail-value amount">¥{{ (completedStats.completedAmount || 0).toFixed(2) }}</span>
            </div>
            
            <!-- 按类型分类 -->
            <div class="type-distribution">
              <h4>完成订单类型</h4>
              <div class="type-items">
                <div class="type-item">
                  <span class="type-label">预约订餐：</span>
                  <span class="type-count">{{ completedStats.completedReservation || 0 }} 单</span>
                </div>
                <div class="type-item">
                  <span class="type-label">堂食点餐：</span>
                  <span class="type-count">{{ completedStats.completedDineIn || 0 }} 单</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 热门菜品 -->
      <el-col :span="12">
        <el-card class="detail-card">
          <template #header>
            <div class="card-header">
              <span>热门菜品排行</span>
              <el-tag v-if="selectedDate">{{ selectedDate }}</el-tag>
            </div>
          </template>
          
          <div v-loading="loadingPopular">
            <div class="popular-dishes" v-if="popularDishes.length > 0">
              <div 
                v-for="(dish, index) in popularDishes" 
                :key="index"
                class="popular-dish-item"
              >
                <div class="dish-rank">{{ index + 1 }}</div>
                <div class="dish-info">
                  <div class="dish-name">{{ dish.dishName }}</div>
                  <div class="dish-stats">
                    <span class="dish-quantity">销量: {{ dish.quantity }}</span>
                    <span class="dish-revenue">营收: ¥{{ dish.revenue.toFixed(2) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 总体统计概览 -->
    <el-row :gutter="20" class="summary-section">
      <el-col :span="24">
        <el-card class="summary-card">
          <template #header>
            <div class="card-header">
              <span>今日总体概览</span>
              <el-tag v-if="selectedDate">{{ selectedDate }}</el-tag>
            </div>
          </template>
          
          <div v-loading="loadingOverall">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">总订单数</div>
                  <div class="summary-value">{{ overallStats.totalOrders || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">就餐总人数</div>
                  <div class="summary-value">{{ overallStats.totalUsers || 0 }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">总营业额</div>
                  <div class="summary-value amount">¥{{ (overallStats.totalAmount || 0).toFixed(2) }}</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="summary-item">
                  <div class="summary-label">平均订单金额</div>
                  <div class="summary-value amount">¥{{ (overallStats.avgOrderAmount || 0).toFixed(2) }}</div>
                </div>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" class="order-type-summary">
              <el-col :span="12">
                <div class="summary-item">
                  <div class="summary-label">预约订餐</div>
                  <div class="summary-value">{{ overallStats.reservationOrders || 0 }} 单</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="summary-item">
                  <div class="summary-label">堂食点餐</div>
                  <div class="summary-value">{{ overallStats.dineInOrders || 0 }} 单</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, 
  Calendar, 
  Bowl, 
  Clock, 
  Check 
} from '@element-plus/icons-vue'
import {
  getDiningStatistics,
  getReservationStatistics,
  getPendingDiningStatistics,
  getCompletedOrderStatistics,
  getPopularDishesStatistics
} from '@/api/statistics'

// 响应式数据
const selectedDate = ref(new Date().toISOString().slice(0, 10))
const refreshing = ref(false)

// 各类统计数据
const reservationStats = ref({})
const pendingStats = ref({})
const completedStats = ref({})
const popularDishes = ref([])
const overallStats = ref({})

// 加载状态
const loadingReservation = ref(false)
const loadingPending = ref(false)
const loadingCompleted = ref(false)
const loadingPopular = ref(false)
const loadingOverall = ref(false)

// 计算属性
const maxTimeSlotCount = computed(() => {
  if (!reservationStats.value.timeSlots) return 1
  return Math.max(...Object.values(reservationStats.value.timeSlots), 1)
})

// 方法
const handleDateChange = () => {
  loadData()
}

const refreshData = async () => {
  refreshing.value = true
  try {
    await loadData()
    ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    refreshing.value = false
  }
}

const getTimeSlotPercentage = (count) => {
  return Math.round((count / maxTimeSlotCount.value) * 100)
}

const formatWaitTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const orderTime = new Date(time)
  const diffMinutes = Math.floor((now - orderTime) / (1000 * 60))
  
  if (diffMinutes < 60) {
    return `${diffMinutes}分钟`
  } else {
    const hours = Math.floor(diffMinutes / 60)
    const minutes = diffMinutes % 60
    return `${hours}小时${minutes}分钟`
  }
}

const loadReservationStats = async () => {
  loadingReservation.value = true
  try {
    const response = await getReservationStatistics(selectedDate.value)
    reservationStats.value = response.data || {}
  } catch (error) {
    console.error('加载预订统计失败:', error)
    ElMessage.error('加载预订统计失败')
  } finally {
    loadingReservation.value = false
  }
}

const loadPendingStats = async () => {
  loadingPending.value = true
  try {
    const response = await getPendingDiningStatistics()
    pendingStats.value = response.data || {}
  } catch (error) {
    console.error('加载待就餐统计失败:', error)
    ElMessage.error('加载待就餐统计失败')
  } finally {
    loadingPending.value = false
  }
}

const loadCompletedStats = async () => {
  loadingCompleted.value = true
  try {
    const response = await getCompletedOrderStatistics(selectedDate.value)
    completedStats.value = response.data || {}
  } catch (error) {
    console.error('加载已完成订单统计失败:', error)
    ElMessage.error('加载已完成订单统计失败')
  } finally {
    loadingCompleted.value = false
  }
}

const loadPopularDishes = async () => {
  loadingPopular.value = true
  try {
    const response = await getPopularDishesStatistics(selectedDate.value, 10)
    popularDishes.value = response.data?.popularDishes || []
  } catch (error) {
    console.error('加载热门菜品统计失败:', error)
    ElMessage.error('加载热门菜品统计失败')
  } finally {
    loadingPopular.value = false
  }
}

const loadOverallStats = async () => {
  loadingOverall.value = true
  try {
    const response = await getDiningStatistics(selectedDate.value)
    overallStats.value = response.data || {}
  } catch (error) {
    console.error('加载总体统计失败:', error)
    ElMessage.error('加载总体统计失败')
  } finally {
    loadingOverall.value = false
  }
}

const loadData = async () => {
  await Promise.all([
    loadReservationStats(),
    loadPendingStats(),
    loadCompletedStats(),
    loadPopularDishes(),
    loadOverallStats()
  ])
}

// 生命周期
onMounted(() => {
  loadData()
  
  // 设置定时刷新（每30秒刷新一次待就餐数据）
  setInterval(() => {
    loadPendingStats()
  }, 30000)
})
</script>

<style scoped>
.dining-statistics-container {
  padding: 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.date-picker-section {
  display: flex;
  gap: 10px;
  align-items: center;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.reservation {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.dishes {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #e67e22;
}

.stat-icon.completed {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #27ae60;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-card {
  min-height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  color: #606266;
}

.detail-value {
  font-weight: bold;
  color: #303133;
}

.detail-value.amount {
  color: #f56c6c;
}

.time-slots, .status-distribution, .type-distribution {
  margin-top: 20px;
}

.time-slots h4, .status-distribution h4, .type-distribution h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}

.time-slot-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.time-slot-item {
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.time-slot-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.time-slot-count {
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.status-items, .type-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-item, .type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.status-count, .type-count {
  font-weight: bold;
  color: #303133;
}

.popular-dishes {
  max-height: 400px;
  overflow-y: auto;
}

.popular-dish-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.popular-dish-item:last-child {
  border-bottom: none;
}

.dish-rank {
  width: 30px;
  height: 30px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 15px;
  font-size: 14px;
}

.dish-rank:first-child {
  background: #f56c6c;
}

.dish-rank:nth-child(2) {
  background: #e6a23c;
}

.dish-rank:nth-child(3) {
  background: #67c23a;
}

.dish-info {
  flex: 1;
}

.dish-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.dish-stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.dish-quantity {
  color: #67c23a;
}

.dish-revenue {
  color: #f56c6c;
}

.summary-section {
  margin-bottom: 20px;
}

.summary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.summary-card :deep(.el-card__header) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.summary-card :deep(.el-card__body) {
  background: transparent;
}

.summary-item {
  text-align: center;
  padding: 10px;
}

.summary-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 5px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.summary-value.amount {
  color: #ffd700;
}

.order-type-summary {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}
</style> 