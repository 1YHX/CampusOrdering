<template>
  <div class="dine-in-container">
    <el-row :gutter="20">
      <!-- 左侧菜品列表 -->
      <el-col :span="16">
        <div class="menu-section">
          <div class="filter-section">
            <el-input
              v-model="searchQuery"
              placeholder="搜索菜品..."
              prefix-icon="Search"
              clearable
              @clear="handleSearch"
              @input="handleSearch"
              class="search-input"
            />
            <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange">
              <el-tab-pane 
                v-for="category in categories" 
                :key="category.id"
                :label="category.name" 
                :name="category.id.toString()"
              >
                <el-row :gutter="20">
                  <el-col 
                    v-for="dish in filteredDishes" 
                    :key="dish.id" 
                    :xs="24" 
                    :sm="12" 
                    :md="8" 
                    :lg="8"
                  >
                    <el-card 
                      class="dish-card" 
                      :body-style="{ padding: '10px' }"
                      @click="addToOrder(dish)"
                    >
                      <img :src="dish.image" class="dish-image" />
                      <div class="dish-info">
                        <h3>{{ dish.name }}</h3>
                        <div class="price-section">
                          <span class="price">¥{{ dish.price }}</span>
                          <el-tag 
                            v-if="getOrderQuantity(dish.id) > 0" 
                            type="success" 
                            size="small"
                          >
                            已点 {{ getOrderQuantity(dish.id) }}
                          </el-tag>
                        </div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </el-col>

      <!-- 右侧订单详情 -->
      <el-col :span="8">
        <div class="order-section">
          <el-card class="order-card">
            <template #header>
              <div class="order-header">
                <span>当前订单</span>
                <el-button 
                  type="text" 
                  @click="clearOrder" 
                  :disabled="!orderItems.length"
                >
                  清空
                </el-button>
              </div>
            </template>
            
            <div class="order-content">
              <div v-if="!orderItems.length" class="empty-order">
                <el-empty description="还没有点餐" />
              </div>
              <template v-else>
                <div class="order-items">
                  <div 
                    v-for="item in orderItems" 
                    :key="item.id" 
                    class="order-item"
                  >
                    <div class="item-info">
                      <span class="item-name">{{ item.name }}</span>
                      <span class="item-price">¥{{ item.price }}</span>
                    </div>
                    <div class="item-actions">
                      <el-button-group>
                        <el-button 
                          type="primary" 
                          :icon="Minus" 
                          circle
                          size="small"
                          @click="decreaseQuantity(item)"
                        />
                        <span class="quantity">{{ item.quantity }}</span>
                        <el-button 
                          type="primary" 
                          :icon="Plus" 
                          circle
                          size="small"
                          @click="increaseQuantity(item)"
                        />
                      </el-button-group>
                    </div>
                  </div>
                </div>

                <div class="order-summary">
                  <div class="total-section">
                    <span>总计：</span>
                    <span class="total-amount">¥{{ totalAmount }}</span>
                  </div>
                  <div class="remarks-section">
                    <el-input
                      v-model="remarks"
                      type="textarea"
                      placeholder="添加备注..."
                      :rows="2"
                    />
                  </div>
                  <el-button 
                    type="primary" 
                    class="submit-button"
                    :loading="submitting"
                    @click="submitOrder"
                  >
                    提交订单
                  </el-button>
                </div>
              </template>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Minus } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 状态管理
const searchQuery = ref('')
const activeCategory = ref('1')
const orderItems = ref([])
const remarks = ref('')
const submitting = ref(false)

// 模拟数据 - 实际应该从后端获取
const categories = ref([
  { id: 1, name: '热菜' },
  { id: 2, name: '凉菜' },
  { id: 3, name: '主食' },
  { id: 4, name: '汤类' },
  { id: 5, name: '饮品' }
])

const dishes = ref([
  {
    id: 1,
    name: '红烧肉',
    description: '精选五花肉，传统工艺',
    price: 25.00,
    image: 'https://example.com/hongshaorou.jpg',
    categoryId: 1
  },
  // 更多菜品...
])

// 计算属性
const filteredDishes = computed(() => {
  return dishes.value.filter(dish => {
    const matchesSearch = dish.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory = dish.categoryId === parseInt(activeCategory.value)
    return matchesSearch && matchesCategory
  })
})

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

// 方法
const handleSearch = () => {
  // 实现搜索逻辑
}

const handleCategoryChange = () => {
  // 实现分类切换逻辑
}

const getOrderQuantity = (dishId) => {
  const item = orderItems.value.find(item => item.id === dishId)
  return item ? item.quantity : 0
}

const addToOrder = (dish) => {
  const existingItem = orderItems.value.find(item => item.id === dish.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    orderItems.value.push({
      ...dish,
      quantity: 1
    })
  }
  ElMessage.success('已添加到订单')
}

const increaseQuantity = (item) => {
  item.quantity++
}

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--
  } else {
    const index = orderItems.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      orderItems.value.splice(index, 1)
    }
  }
}

const clearOrder = () => {
  orderItems.value = []
  remarks.value = ''
}

const submitOrder = async () => {
  if (!orderItems.value.length) {
    ElMessage.warning('请先选择菜品')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      items: orderItems.value.map(item => ({
        dishId: item.id,
        quantity: item.quantity,
        price: item.price
      })),
      remarks: remarks.value,
      totalAmount: totalAmount.value,
      orderType: 'DINE_IN'
    }
    
    await request.post('/order/dine-in', orderData)
    ElMessage.success('订单提交成功')
    clearOrder()
  } catch (error) {
    console.error('提交订单失败:', error)
    ElMessage.error('提交订单失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.dine-in-container {
  padding: 20px;
}

.menu-section {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.filter-section {
  margin-bottom: 20px;
}

.search-input {
  margin-bottom: 20px;
}

.dish-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.dish-card:hover {
  transform: translateY(-5px);
}

.dish-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.dish-info {
  margin-top: 10px;
}

.dish-info h3 {
  margin: 0;
  font-size: 14px;
  color: #303133;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.order-section {
  position: sticky;
  top: 20px;
}

.order-card {
  background: #fff;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-content {
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.empty-order {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.order-items {
  flex: 1;
  overflow-y: auto;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #303133;
}

.item-price {
  margin-left: 10px;
  color: #f56c6c;
}

.item-actions {
  display: flex;
  align-items: center;
}

.quantity {
  display: inline-block;
  width: 30px;
  text-align: center;
}

.order-summary {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.total-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.remarks-section {
  margin-bottom: 20px;
}

.submit-button {
  width: 100%;
}
</style> 