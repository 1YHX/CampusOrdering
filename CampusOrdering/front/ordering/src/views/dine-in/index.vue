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
                <div v-loading="loading" class="dishes-container">
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
                        <img :src="getDishImageUrl(dish.image)" class="dish-image" />
                        <div class="dish-info">
                          <h3>{{ dish.name }}</h3>
                          <p class="dish-description">{{ dish.description }}</p>
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
                          <div class="dish-meta">
                            <el-tag size="small" :type="getTypeTagType(dish.type)">
                              {{ getTypeLabel(dish.type) }}
                            </el-tag>
                            <span class="stock" v-if="dish.stock !== undefined">
                              库存: {{ dish.stock }}
                            </span>
                          </div>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                  <div v-if="!filteredDishes.length && !loading" class="no-data">
                    <el-empty description="暂无菜品" />
                  </div>
                </div>
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
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Minus } from '@element-plus/icons-vue'
import { getDishCategories, getDishesByCategory, getDishPage } from '@/api/dish'
import { createOrder } from '@/api/order'

// 状态管理
const searchQuery = ref('')
const activeCategory = ref('')
const orderItems = ref([])
const remarks = ref('')
const submitting = ref(false)
const loading = ref(false)

// 数据
const categories = ref([])
const dishes = ref([])

// 计算属性
const filteredDishes = computed(() => {
  if (!searchQuery.value) {
    return dishes.value
  }
  return dishes.value.filter(dish => 
    dish.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (dish.description && dish.description.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

const totalAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

// 生命周期
onMounted(() => {
  loadCategories()
})

// 监听分类变化
watch(activeCategory, (newCategoryId, oldCategoryId) => {
  console.log('activeCategory变化:', oldCategoryId, '->', newCategoryId)
  if (newCategoryId && newCategoryId !== oldCategoryId) {
    const categoryId = parseInt(newCategoryId)
    if (!isNaN(categoryId)) {
      console.log('通过watcher加载分类菜品:', categoryId)
      loadDishes(categoryId)
    }
  }
})

// 方法
const loadCategories = async () => {
  try {
    console.log('开始加载分类...')
    const response = await getDishCategories()
    console.log('分类响应:', response)
    categories.value = response.data || []
    console.log('加载的分类:', categories.value)
    
    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id.toString()
      console.log('设置默认分类:', activeCategory.value)
      loadDishes(categories.value[0].id)
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

const loadDishes = async (categoryId) => {
  if (!categoryId) return
  
  loading.value = true
  try {
    // 所有用户都使用getDishesByCategory按分类查询
    // 这个API会根据后端逻辑返回相应的菜品（学生只能看到上架的）
    const response = await getDishesByCategory(categoryId, { 
      page: 1, 
      size: 50 
    })
    
    dishes.value = response.data?.records || []
    console.log('加载的菜品:', dishes.value)
  } catch (error) {
    console.error('加载菜品失败:', error)
    ElMessage.error('加载菜品失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  // 搜索逻辑在computed中处理
}

const handleCategoryChange = (tab) => {
  console.log('分类切换事件触发:', tab)
  // Element Plus的tab-click事件可能不可靠，主要依赖watcher
}

const getDishImageUrl = (imagePath) => {
  if (!imagePath) {
    return '/src/assets/default-dish.jpg' // 默认图片
  }
  // 如果是相对路径，需要拼接后端服务器地址
  if (imagePath.startsWith('/uploads')) {
    return `http://localhost:8080/api${imagePath}`
  }
  return imagePath
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

const getTypeLabel = (type) => {
  const labelMap = {
    'dish': '菜品',
    'staple': '主食',
    'combo': '套餐',
    'product': '商品'
  }
  return labelMap[type] || type
}

const getOrderQuantity = (dishId) => {
  const item = orderItems.value.find(item => item.id === dishId)
  return item ? item.quantity : 0
}

const addToOrder = (dish) => {
  // 检查菜品状态
  if (dish.status === 0) {
    ElMessage.warning('该菜品已下架，无法添加')
    return
  }
  
  // 检查库存
  if (dish.stock !== undefined && dish.stock <= 0) {
    ElMessage.warning('该菜品库存不足')
    return
  }
  
  const existingItem = orderItems.value.find(item => item.id === dish.id)
  if (existingItem) {
    // 检查是否超过库存
    if (dish.stock !== undefined && existingItem.quantity >= dish.stock) {
      ElMessage.warning('已达到库存上限')
      return
    }
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
  // 检查库存
  if (item.stock !== undefined && item.quantity >= item.stock) {
    ElMessage.warning('已达到库存上限')
    return
  }
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

  // 检查用户信息
  const userId = localStorage.getItem('userId')
  const userType = localStorage.getItem('userType')
  if (!userId || !userType) {
    ElMessage.error('请先登录')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      amount: parseFloat(totalAmount.value),
      subsidyAmount: 0, // 暂时设为0，后续可以根据实际补贴计算
      actualAmount: parseFloat(totalAmount.value),
      paymentMethod: 'cash',
      orderType: 'dine_in',
      eatTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      remark: remarks.value,
      details: orderItems.value.map(item => ({
        dishId: item.id,
        dishName: item.name,
        dishType: item.type,
        price: item.price,
        quantity: item.quantity,
        amount: (item.price * item.quantity).toFixed(2)
      }))
    }
    
    await createOrder(orderData)
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

.dishes-container {
  min-height: 300px;
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
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #303133;
}

.dish-description {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.dish-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stock {
  font-size: 12px;
  color: #909399;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
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