<template>
  <div class="advance-order-container">
    <!-- 菜品分类和搜索区域 -->
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
      <el-select
        v-model="selectedCategory"
        placeholder="选择分类"
        @change="handleCategoryChange"
        class="category-select"
      >
        <el-option label="全部" value="" />
        <el-option
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
      <div class="date-picker">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择用餐日期"
          :disabled-date="disabledDate"
          @change="handleDateChange"
        />
      </div>
    </div>

    <!-- 菜品展示区域 -->
    <div class="dishes-section">
      <el-row :gutter="20">
        <el-col 
          v-for="dish in filteredDishes" 
          :key="dish.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <el-card class="dish-card" :body-style="{ padding: '0px' }">
            <img :src="dish.image" class="dish-image" />
            <div class="dish-info">
              <h3>{{ dish.name }}</h3>
              <p class="description">{{ dish.description }}</p>
              <div class="price-section">
                <span class="price">¥{{ dish.price }}</span>
                <span class="original-price" v-if="dish.originalPrice">
                  ¥{{ dish.originalPrice }}
                </span>
              </div>
              <div class="action-section">
                <el-input-number 
                  v-model="dish.quantity" 
                  :min="0" 
                  :max="10"
                  size="small"
                  @change="handleQuantityChange(dish)"
                />
                <el-button 
                  type="primary" 
                  size="small"
                  :disabled="dish.quantity === 0"
                  @click="addToCart(dish)"
                >
                  加入购物车
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 购物车抽屉 -->
    <el-drawer
      v-model="cartDrawerVisible"
      title="购物车"
      direction="rtl"
      size="30%"
    >
      <div class="cart-content">
        <div v-if="cart.length === 0" class="empty-cart">
          <el-empty description="购物车是空的" />
        </div>
        <template v-else>
          <div class="cart-items">
            <div v-for="item in cart" :key="item.id" class="cart-item">
              <div class="item-info">
                <img :src="item.image" class="item-image" />
                <div class="item-details">
                  <h4>{{ item.name }}</h4>
                  <p class="item-price">¥{{ item.price }}</p>
                </div>
              </div>
              <div class="item-actions">
                <el-input-number
                  v-model="item.quantity"
                  :min="0"
                  :max="10"
                  size="small"
                  @change="handleCartQuantityChange(item)"
                />
                <el-button
                  type="danger"
                  size="small"
                  icon="Delete"
                  circle
                  @click="removeFromCart(item)"
                />
              </div>
            </div>
          </div>
          <div class="cart-footer">
            <div class="total-amount">
              总计：<span class="amount">¥{{ totalAmount }}</span>
            </div>
            <el-button type="primary" @click="handleCheckout">去结算</el-button>
          </div>
        </template>
      </div>
    </el-drawer>

    <!-- 悬浮购物车按钮 -->
    <div class="floating-cart" @click="cartDrawerVisible = true">
      <el-badge :value="totalItems" :hidden="totalItems === 0">
        <el-button type="primary" circle>
          <el-icon><ShoppingCart /></el-icon>
        </el-button>
      </el-badge>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ShoppingCart, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

// 状态管理
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedDate = ref(new Date())
const cartDrawerVisible = ref(false)
const cart = ref([])

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
    originalPrice: 28.00,
    image: 'https://example.com/hongshaorou.jpg',
    categoryId: 1,
    quantity: 0
  },
  // 更多菜品...
])

// 计算属性
const filteredDishes = computed(() => {
  return dishes.value.filter(dish => {
    const matchesSearch = dish.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory = !selectedCategory.value || dish.categoryId === selectedCategory.value
    return matchesSearch && matchesCategory
  })
})

const totalAmount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

const totalItems = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 方法
const handleSearch = () => {
  // 实现搜索逻辑
}

const handleCategoryChange = () => {
  // 实现分类切换逻辑
}

const disabledDate = (time) => {
  // 禁用过去的日期和超过7天的未来日期
  const today = new Date()
  const sevenDaysLater = new Date()
  sevenDaysLater.setDate(today.getDate() + 7)
  return time.getTime() < today.getTime() - 8.64e7 || time.getTime() > sevenDaysLater.getTime()
}

const handleDateChange = () => {
  // 实现日期切换逻辑
}

const handleQuantityChange = (dish) => {
  // 实现数量变化逻辑
}

const addToCart = (dish) => {
  const existingItem = cart.value.find(item => item.id === dish.id)
  if (existingItem) {
    existingItem.quantity += dish.quantity
  } else {
    cart.value.push({
      ...dish,
      quantity: dish.quantity
    })
  }
  dish.quantity = 0
  ElMessage.success('已添加到购物车')
}

const handleCartQuantityChange = (item) => {
  if (item.quantity === 0) {
    removeFromCart(item)
  }
}

const removeFromCart = (item) => {
  const index = cart.value.findIndex(i => i.id === item.id)
  if (index > -1) {
    cart.value.splice(index, 1)
  }
}

const handleCheckout = async () => {
  try {
    const orderData = {
      items: cart.value.map(item => ({
        dishId: item.id,
        quantity: item.quantity,
        price: item.price
      })),
      orderDate: selectedDate.value,
      totalAmount: totalAmount.value
    }
    
    await request.post('/order/advance', orderData)
    ElMessage.success('订单提交成功')
    cart.value = []
    cartDrawerVisible.value = false
  } catch (error) {
    console.error('提交订单失败:', error)
    ElMessage.error('提交订单失败，请重试')
  }
}
</script>

<style scoped>
.advance-order-container {
  padding: 20px;
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.category-select {
  width: 200px;
}

.dishes-section {
  margin-top: 20px;
}

.dish-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.dish-card:hover {
  transform: translateY(-5px);
}

.dish-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.dish-info {
  padding: 14px;
}

.dish-info h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.description {
  font-size: 14px;
  color: #909399;
  margin: 8px 0;
}

.price-section {
  margin: 10px 0;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.original-price {
  color: #909399;
  font-size: 14px;
  text-decoration: line-through;
  margin-left: 8px;
}

.action-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.floating-cart {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 1000;
}

.cart-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.item-details h4 {
  margin: 0;
  font-size: 14px;
}

.item-price {
  color: #f56c6c;
  margin: 4px 0 0;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.total-amount {
  margin-bottom: 10px;
  font-size: 16px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 20px;
}

.empty-cart {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
</style> 