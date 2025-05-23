<template>
  <div class="order-history-container">
    <div class="header-section">
      <h2>订单记录</h2>
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
          <el-option label="待支付" value="PENDING" />
          <el-option label="已支付" value="PAID" />
          <el-option label="准备中" value="PREPARING" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-select 
          v-model="typeFilter" 
          placeholder="订单类型" 
          @change="handleTypeChange"
          clearable
        >
          <el-option label="全部" value="" />
          <el-option label="提前订餐" value="ADVANCE" />
          <el-option label="堂食点餐" value="DINE_IN" />
        </el-select>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-section">
      <el-card 
        v-for="order in filteredOrders" 
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
          </div>
          <div class="order-time">
            {{ formatDate(order.createTime) }}
          </div>
        </div>

        <div class="order-content">
          <div class="order-items">
            <div 
              v-for="item in order.items" 
              :key="item.id" 
              class="order-item"
            >
              <img :src="item.dishImage" class="item-image" />
              <div class="item-info">
                <span class="item-name">{{ item.dishName }}</span>
                <span class="item-spec">x{{ item.quantity }}</span>
              </div>
              <div class="item-price">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
            </div>
          </div>
          
          <div v-if="order.remarks" class="order-remarks">
            <span class="remarks-label">备注：</span>
            <span class="remarks-text">{{ order.remarks }}</span>
          </div>
        </div>

        <div class="order-footer">
          <div class="order-total">
            <span>共 {{ getTotalQuantity(order.items) }} 件商品</span>
            <span class="total-amount">实付：¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <el-button 
              v-if="order.status === 'PENDING'" 
              type="danger" 
              size="small"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>
            <el-button 
              v-if="order.status === 'PENDING'" 
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
              v-if="order.status === 'COMPLETED'" 
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
      <div v-if="!filteredOrders.length" class="empty-state">
        <el-empty description="暂无订单记录" />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 状态管理
const orders = ref([])
const dateRange = ref([])
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 模拟数据 - 实际应该从后端获取
const mockOrders = ref([
  {
    id: 1,
    orderNo: 'ORD20241122001',
    status: 'COMPLETED',
    orderType: 'ADVANCE',
    totalAmount: 45.00,
    createTime: new Date('2024-11-22 12:30:00'),
    remarks: '少放辣椒',
    items: [
      {
        id: 1,
        dishName: '红烧肉',
        dishImage: 'https://example.com/hongshaorou.jpg',
        quantity: 1,
        price: 25.00
      },
      {
        id: 2,
        dishName: '米饭',
        dishImage: 'https://example.com/rice.jpg',
        quantity: 2,
        price: 10.00
      }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD20241122002',
    status: 'PENDING',
    orderType: 'DINE_IN',
    totalAmount: 38.00,
    createTime: new Date('2024-11-22 18:15:00'),
    remarks: '',
    items: [
      {
        id: 1,
        dishName: '宫保鸡丁',
        dishImage: 'https://example.com/gongbaojiding.jpg',
        quantity: 1,
        price: 28.00
      },
      {
        id: 2,
        dishName: '紫菜蛋花汤',
        dishImage: 'https://example.com/soup.jpg',
        quantity: 1,
        price: 10.00
      }
    ]
  }
])

// 计算属性
const filteredOrders = computed(() => {
  return mockOrders.value.filter(order => {
    const matchesStatus = !statusFilter.value || order.status === statusFilter.value
    const matchesType = !typeFilter.value || order.orderType === typeFilter.value
    
    let matchesDate = true
    if (dateRange.value && dateRange.value.length === 2) {
      const orderDate = new Date(order.createTime)
      const startDate = new Date(dateRange.value[0])
      const endDate = new Date(dateRange.value[1])
      endDate.setHours(23, 59, 59, 999) // 包含结束日期的整天
      matchesDate = orderDate >= startDate && orderDate <= endDate
    }
    
    return matchesStatus && matchesType && matchesDate
  })
})

// 方法
const handleDateChange = () => {
  // 实现日期筛选逻辑
}

const handleStatusChange = () => {
  // 实现状态筛选逻辑
}

const handleTypeChange = () => {
  // 实现类型筛选逻辑
}

const getStatusType = (status) => {
  const statusMap = {
    'PENDING': 'warning',
    'PAID': 'info',
    'PREPARING': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'PREPARING': '准备中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}

const getTypeType = (type) => {
  return type === 'ADVANCE' ? 'primary' : 'success'
}

const getTypeText = (type) => {
  const typeMap = {
    'ADVANCE': '提前订餐',
    'DINE_IN': '堂食点餐'
  }
  return typeMap[type] || '未知'
}

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getTotalQuantity = (items) => {
  return items.reduce((sum, item) => sum + item.quantity, 0)
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/order/${order.id}/cancel`)
    ElMessage.success('订单已取消')
    // 更新订单状态
    order.status = 'CANCELLED'
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const payOrder = async (order) => {
  try {
    await request.post(`/order/${order.id}/pay`)
    ElMessage.success('支付成功')
    // 更新订单状态
    order.status = 'PAID'
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  }
}

const viewOrderDetail = (order) => {
  // 跳转到订单详情页面或显示详情弹窗
  ElMessage.info('订单详情功能开发中...')
}

const reorder = (order) => {
  // 重新下单逻辑
  ElMessage.info('再来一单功能开发中...')
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  // 重新加载数据
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // 重新加载数据
}

const loadOrders = async () => {
  loading.value = true
  try {
    // 实际应该调用后端API
    // const { data } = await request.get('/order/list', {
    //   params: {
    //     page: currentPage.value,
    //     size: pageSize.value,
    //     status: statusFilter.value,
    //     type: typeFilter.value,
    //     startDate: dateRange.value?.[0],
    //     endDate: dateRange.value?.[1]
    //   }
    // })
    // orders.value = data.records
    // total.value = data.total
    
    // 使用模拟数据
    orders.value = mockOrders.value
    total.value = mockOrders.value.length
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
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
</style> 