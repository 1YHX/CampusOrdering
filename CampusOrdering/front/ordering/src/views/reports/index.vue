<template>
  <div class="reports-container">
    <div class="header-section">
      <h2>报表统计</h2>
      <div class="filter-section">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="refreshData" :loading="refreshing">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
<!--        <el-button @click="exportReport">-->
<!--          <el-icon><Download /></el-icon>-->
<!--          导出报表-->
<!--        </el-button>-->
      </div>
    </div>

    <!-- 综合数据概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon total-orders">
              <el-icon><Document /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ comprehensiveData.totalOrders || 0 }}</div>
              <div class="card-label">总订单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon total-revenue">
              <el-icon><Money /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">¥{{ (comprehensiveData.totalRevenue || 0).toFixed(2) }}</div>
              <div class="card-label">总营收</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon total-quantity">
              <el-icon><Bowl /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ comprehensiveData.totalQuantity || 0 }}</div>
              <div class="card-label">总销量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card">
          <div class="card-content">
            <div class="card-icon avg-amount">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">¥{{ (comprehensiveData.avgOrderAmount || 0).toFixed(2) }}</div>
              <div class="card-label">平均订单金额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 报表内容 -->
    <el-tabs v-model="activeTab" type="card" class="report-tabs">
      <!-- 销量排行榜 -->
      <el-tab-pane label="销量排行榜" name="sales">
        <el-card class="report-card">
          <template #header>
            <div class="card-header">
              <span>菜品销量排行榜</span>
              <el-tag v-if="dateRange && dateRange.length === 2">
                {{ dateRange[0] }} 至 {{ dateRange[1] }}
              </el-tag>
            </div>
          </template>
          
          <div v-loading="loadingSales">
            <div class="ranking-container" v-if="salesData.dishSalesRanking && salesData.dishSalesRanking.length > 0">
              <div class="ranking-list">
                <div 
                  v-for="(item, index) in salesData.dishSalesRanking.slice(0, 20)" 
                  :key="item.dishName"
                  class="ranking-item"
                  :class="{ 'top-three': index < 3 }"
                >
                  <div class="ranking-number">
                    <span class="rank" :class="getRankClass(index)">{{ index + 1 }}</span>
                  </div>
                  <div class="dish-info">
                    <div class="dish-name">{{ item.dishName }}</div>
                    <el-tag size="small" :type="getTypeTagType(item.dishType)">
                      {{ getTypeText(item.dishType) }}
                    </el-tag>
                  </div>
                  <div class="dish-stats">
                    <div class="quantity">销量：{{ item.quantity }} 份</div>
                    <div class="percentage">占比：{{ item.percentage.toFixed(1) }}%</div>
                  </div>
                  <div class="progress-bar">
                    <el-progress 
                      :percentage="item.percentage" 
                      :show-text="false"
                      :stroke-width="8"
                      :color="getProgressColor(index)"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-data">
              <el-empty description="暂无销量数据" />
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 营收排行榜 -->
      <el-tab-pane label="营收排行榜" name="revenue">
        <el-card class="report-card">
          <template #header>
            <div class="card-header">
              <span>菜品营收排行榜</span>
              <el-tag v-if="dateRange && dateRange.length === 2">
                {{ dateRange[0] }} 至 {{ dateRange[1] }}
              </el-tag>
            </div>
          </template>
          
          <div v-loading="loadingRevenue">
            <div class="ranking-container" v-if="revenueData.dishRevenueRanking && revenueData.dishRevenueRanking.length > 0">
              <div class="ranking-list">
                <div 
                  v-for="(item, index) in revenueData.dishRevenueRanking.slice(0, 20)" 
                  :key="item.dishName"
                  class="ranking-item"
                  :class="{ 'top-three': index < 3 }"
                >
                  <div class="ranking-number">
                    <span class="rank" :class="getRankClass(index)">{{ index + 1 }}</span>
                  </div>
                  <div class="dish-info">
                    <div class="dish-name">{{ item.dishName }}</div>
                    <el-tag size="small" :type="getTypeTagType(item.dishType)">
                      {{ getTypeText(item.dishType) }}
                    </el-tag>
                  </div>
                  <div class="dish-stats">
                    <div class="revenue">营收：¥{{ item.revenue.toFixed(2) }}</div>
                    <div class="quantity">销量：{{ item.quantity }} 份</div>
                    <div class="percentage">占比：{{ item.percentage.toFixed(1) }}%</div>
                  </div>
                  <div class="progress-bar">
                    <el-progress 
                      :percentage="item.percentage" 
                      :show-text="false"
                      :stroke-width="8"
                      :color="getProgressColor(index)"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-data">
              <el-empty description="暂无营收数据" />
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 价格分析 -->
      <el-tab-pane label="价格分析" name="price">
        <el-card class="report-card">
          <template #header>
            <div class="card-header">
              <span>菜品价格分析</span>
              <el-tag v-if="dateRange && dateRange.length === 2">
                {{ dateRange[0] }} 至 {{ dateRange[1] }}
              </el-tag>
            </div>
          </template>
          
          <div v-loading="loadingPrice">
            <div class="price-analysis-container" v-if="priceData.priceAnalysis && priceData.priceAnalysis.length > 0">
              <div class="summary-info">
                <div class="summary-item">
                  <span class="label">平均菜品价格：</span>
                  <span class="value">¥{{ (priceData.avgPrice || 0).toFixed(2) }}</span>
                </div>
              </div>
              
              <el-table 
                :data="priceData.priceAnalysis.slice(0, 20)" 
                stripe
                style="width: 100%"
                :default-sort="{ prop: 'avgPrice', order: 'descending' }"
              >
                <el-table-column type="index" label="排名" width="80" align="center" />
                <el-table-column prop="dishName" label="菜品名称" min-width="150">
                  <template #default="scope">
                    <div class="dish-cell">
                      <span class="dish-name">{{ scope.row.dishName }}</span>
                      <el-tag size="small" :type="getTypeTagType(scope.row.dishType)">
                        {{ getTypeText(scope.row.dishType) }}
                      </el-tag>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="avgPrice" label="平均价格" width="120" align="center" sortable>
                  <template #default="scope">
                    <span class="price-text">¥{{ scope.row.avgPrice.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="maxPrice" label="最高价格" width="120" align="center" sortable>
                  <template #default="scope">
                    <span class="price-text">¥{{ scope.row.maxPrice.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="minPrice" label="最低价格" width="120" align="center" sortable>
                  <template #default="scope">
                    <span class="price-text">¥{{ scope.row.minPrice.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="priceRange" label="价格区间" width="120" align="center" sortable>
                  <template #default="scope">
                    <span class="price-range">¥{{ scope.row.priceRange.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="quantity" label="销量" width="100" align="center" sortable>
                  <template #default="scope">
                    <span class="quantity-text">{{ scope.row.quantity }} 份</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="empty-data">
              <el-empty description="暂无价格分析数据" />
            </div>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 综合报表 -->
      <el-tab-pane label="综合报表" name="comprehensive">
        <div class="comprehensive-container">
          <el-row :gutter="20">
            <!-- Top 10 销量 -->
            <el-col :span="12">
              <el-card class="mini-report-card">
                <template #header>
                  <span>Top 10 销量菜品</span>
                </template>
                <div class="mini-ranking-list">
                  <div 
                    v-for="(item, index) in (comprehensiveData.topSalesDishes || []).slice(0, 10)" 
                    :key="item.dishName"
                    class="mini-ranking-item"
                  >
                    <span class="mini-rank">{{ index + 1 }}</span>
                    <span class="mini-dish-name">{{ item.dishName }}</span>
                    <span class="mini-value">{{ item.quantity }}份</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- Top 10 营收 -->
            <el-col :span="12">
              <el-card class="mini-report-card">
                <template #header>
                  <span>Top 10 营收菜品</span>
                </template>
                <div class="mini-ranking-list">
                  <div 
                    v-for="(item, index) in (comprehensiveData.topRevenueDishes || []).slice(0, 10)" 
                    :key="item.dishName"
                    class="mini-ranking-item"
                  >
                    <span class="mini-rank">{{ index + 1 }}</span>
                    <span class="mini-dish-name">{{ item.dishName }}</span>
                    <span class="mini-value">¥{{ item.revenue.toFixed(2) }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <!-- Top 10 单价 -->
            <el-col :span="12">
              <el-card class="mini-report-card">
                <template #header>
                  <span>Top 10 单价菜品</span>
                </template>
                <div class="mini-ranking-list">
                  <div 
                    v-for="(item, index) in (comprehensiveData.topPriceDishes || []).slice(0, 10)" 
                    :key="item.dishName"
                    class="mini-ranking-item"
                  >
                    <span class="mini-rank">{{ index + 1 }}</span>
                    <span class="mini-dish-name">{{ item.dishName }}</span>
                    <span class="mini-value">¥{{ item.avgPrice.toFixed(2) }}</span>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- 订单类型分布 -->
            <el-col :span="12">
              <el-card class="mini-report-card">
                <template #header>
                  <span>订单类型分布</span>
                </template>
                <div class="type-distribution">
                  <div class="type-item">
                    <span class="type-label">预约订餐：</span>
                    <span class="type-value">{{ comprehensiveData.reservationOrders || 0 }} 单</span>
                  </div>
                  <div class="type-item">
                    <span class="type-label">堂食点餐：</span>
                    <span class="type-value">{{ comprehensiveData.dineInOrders || 0 }} 单</span>
                  </div>
                  <div class="type-chart">
                    <el-progress 
                      type="circle" 
                      :percentage="getOrderTypePercentage('reservation')"
                      :width="100"
                    >
                      <template #default="{ percentage }">
                        <span class="percentage-text">预约<br/>{{ percentage }}%</span>
                      </template>
                    </el-progress>
                    <el-progress 
                      type="circle" 
                      :percentage="getOrderTypePercentage('dine_in')"
                      :width="100"
                      color="#f56c6c"
                    >
                      <template #default="{ percentage }">
                        <span class="percentage-text">堂食<br/>{{ percentage }}%</span>
                      </template>
                    </el-progress>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, 
  Download, 
  Document, 
  Money, 
  Bowl, 
  TrendCharts 
} from '@element-plus/icons-vue'
import {
  getDishSalesReport,
  getDishRevenueReport,
  getDishPriceAnalysisReport,
  getComprehensiveReport
} from '@/api/statistics'

// 响应式数据
const activeTab = ref('sales')
const dateRange = ref([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
  new Date().toISOString().slice(0, 10)
])
const refreshing = ref(false)

// 各类报表数据
const salesData = ref({})
const revenueData = ref({})
const priceData = ref({})
const comprehensiveData = ref({})

// 加载状态
const loadingSales = ref(false)
const loadingRevenue = ref(false)
const loadingPrice = ref(false)
const loadingComprehensive = ref(false)

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

const exportReport = () => {
  ElMessage.info('导出功能开发中...')
}

const loadData = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return
  
  const [startDate, endDate] = dateRange.value
  
  // 并行加载所有数据
  await Promise.all([
    loadSalesReport(startDate, endDate),
    loadRevenueReport(startDate, endDate),
    loadPriceReport(startDate, endDate),
    loadComprehensiveReport(startDate, endDate)
  ])
}

const loadSalesReport = async (startDate, endDate) => {
  loadingSales.value = true
  try {
    const response = await getDishSalesReport(startDate, endDate)
    salesData.value = response.data || {}
  } catch (error) {
    console.error('加载销量报表失败:', error)
    ElMessage.error('加载销量报表失败')
  } finally {
    loadingSales.value = false
  }
}

const loadRevenueReport = async (startDate, endDate) => {
  loadingRevenue.value = true
  try {
    const response = await getDishRevenueReport(startDate, endDate)
    revenueData.value = response.data || {}
  } catch (error) {
    console.error('加载营收报表失败:', error)
    ElMessage.error('加载营收报表失败')
  } finally {
    loadingRevenue.value = false
  }
}

const loadPriceReport = async (startDate, endDate) => {
  loadingPrice.value = true
  try {
    const response = await getDishPriceAnalysisReport(startDate, endDate)
    priceData.value = response.data || {}
  } catch (error) {
    console.error('加载价格分析失败:', error)
    ElMessage.error('加载价格分析失败')
  } finally {
    loadingPrice.value = false
  }
}

const loadComprehensiveReport = async (startDate, endDate) => {
  loadingComprehensive.value = true
  try {
    const response = await getComprehensiveReport(startDate, endDate)
    comprehensiveData.value = response.data || {}
  } catch (error) {
    console.error('加载综合报表失败:', error)
    ElMessage.error('加载综合报表失败')
  } finally {
    loadingComprehensive.value = false
  }
}

// 工具方法
const getRankClass = (index) => {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return ''
}

const getProgressColor = (index) => {
  if (index === 0) return '#FFD700'
  if (index === 1) return '#C0C0C0'
  if (index === 2) return '#CD7F32'
  return '#409eff'
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

const getTypeText = (type) => {
  const typeMap = {
    'dish': '菜品',
    'staple': '主食',
    'combo': '套餐',
    'product': '商品'
  }
  return typeMap[type] || type
}

const getOrderTypePercentage = (type) => {
  const total = (comprehensiveData.value.reservationOrders || 0) + (comprehensiveData.value.dineInOrders || 0)
  if (total === 0) return 0
  
  const count = type === 'reservation' ? 
    (comprehensiveData.value.reservationOrders || 0) : 
    (comprehensiveData.value.dineInOrders || 0)
  
  return Math.round((count / total) * 100)
}

// 生命周期
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reports-container {
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

.filter-section {
  display: flex;
  gap: 15px;
  align-items: center;
}

/* 概览卡片 */
.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  text-align: center;
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.card-icon.total-orders {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.total-revenue {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.total-quantity {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.avg-amount {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-info {
  text-align: left;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.card-label {
  color: #909399;
  font-size: 14px;
}

/* 报表标签页 */
.report-tabs {
  margin-top: 20px;
}

.report-card {
  min-height: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

/* 排行榜样式 */
.ranking-container {
  max-height: 800px;
  overflow-y: auto;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s;
}

.ranking-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.ranking-item.top-three {
  background: linear-gradient(135deg, #fef9e7 0%, #fff8e1 100%);
  border-color: #f39c12;
}

.ranking-number {
  margin-right: 20px;
}

.rank {
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border-radius: 50%;
  font-weight: bold;
  color: white;
  background: #909399;
}

.rank.gold {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
}

.rank.silver {
  background: linear-gradient(135deg, #C0C0C0 0%, #A8A8A8 100%);
}

.rank.bronze {
  background: linear-gradient(135deg, #CD7F32 0%, #B8860B 100%);
}

.dish-info {
  flex: 1;
  margin-right: 20px;
}

.dish-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.dish-stats {
  min-width: 150px;
  text-align: right;
  margin-right: 20px;
}

.dish-stats > div {
  margin-bottom: 4px;
  font-size: 14px;
}

.quantity, .revenue {
  color: #409eff;
  font-weight: 600;
}

.percentage {
  color: #67c23a;
  font-weight: 600;
}

.progress-bar {
  width: 150px;
}

/* 价格分析表格 */
.price-analysis-container {
  background: white;
}

.summary-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-item .label {
  color: #606266;
  font-weight: 500;
}

.summary-item .value {
  color: #409eff;
  font-weight: 600;
  font-size: 18px;
}

.dish-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dish-name {
  font-weight: 600;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
}

.price-range {
  color: #e6a23c;
  font-weight: 600;
}

.quantity-text {
  color: #67c23a;
  font-weight: 600;
}

/* 综合报表 */
.comprehensive-container {
  background: white;
}

.mini-report-card {
  height: 400px;
}

.mini-ranking-list {
  max-height: 320px;
  overflow-y: auto;
}

.mini-ranking-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f7fa;
}

.mini-ranking-item:last-child {
  border-bottom: none;
}

.mini-rank {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  background: #e4e7ed;
  color: #606266;
  font-weight: bold;
  margin-right: 15px;
  font-size: 12px;
}

.mini-dish-name {
  flex: 1;
  color: #303133;
  font-weight: 500;
}

.mini-value {
  color: #409eff;
  font-weight: 600;
}

.type-distribution {
  text-align: center;
}

.type-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 8px 0;
}

.type-label {
  color: #606266;
}

.type-value {
  color: #303133;
  font-weight: 600;
}

.type-chart {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.percentage-text {
  font-size: 12px;
  color: #606266;
  line-height: 1.2;
}

.empty-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .filter-section {
    justify-content: center;
  }
  
  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .dish-stats {
    min-width: auto;
    text-align: left;
  }
  
  .progress-bar {
    width: 100%;
  }
  
  .type-chart {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }
}
</style> 