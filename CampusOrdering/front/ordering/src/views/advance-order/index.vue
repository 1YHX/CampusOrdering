<template>
  <div class="advance-order-container">
    <!-- 员工管理功能区域 -->
    <div v-if="userRole === 'staff' || userRole === 'admin'" class="management-section">
      <el-card class="management-card">
        <template #header>
          <div class="management-header">
            <span>菜品管理</span>
            <div class="management-actions">
              <el-button type="primary" @click="showAddDishDialog">
                <el-icon><Plus /></el-icon>
                添加菜品
              </el-button>
              <el-button type="success" @click="showCategoryDialog">
                <el-icon><Menu /></el-icon>
                管理分类
              </el-button>
            </div>
          </div>
        </template>
      </el-card>
    </div>

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
      <div class="time-picker">
        <select
          v-model="selectedTime"
          class="time-select"
          @change="handleTimeChange"
        >
          <option value="" disabled>选择用餐时间</option>
          <option
            v-for="time in timeOptions"
            :key="time.value"
            :value="time.value"
          >
            {{ time.label }}
          </option>
        </select>
      </div>
    </div>

    <!-- 菜品展示区域 -->
    <div class="dishes-section" v-loading="loading">
      <el-row :gutter="20">
        <el-col 
          v-for="dish in filteredDishes" 
          :key="dish.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <el-card class="dish-card" :class="{ 'dish-unavailable': dish.status === 0 }" :body-style="{ padding: '0px' }">
            <img :src="getDishImageUrl(dish.image)" class="dish-image" />
            <div class="dish-info">
              <h3>{{ dish.name }}</h3>
              <p class="description">{{ dish.description }}</p>
              <div class="dish-meta">
                <el-tag size="small" :type="getTypeTagType(dish.type)">
                  {{ getTypeLabel(dish.type) }}
                </el-tag>
                <div class="meta-info">
                  <el-tag 
                    size="small" 
                    :type="(dish.status === 0) ? 'danger' : 'success'"
                    style="margin-right: 8px;"
                  >
                    {{ (dish.status === 0) ? '下架' : '上架' }}
                  </el-tag>
                  <span class="stock" v-if="dish.stock !== undefined">
                    库存: {{ dish.stock }}
                  </span>
                </div>
              </div>
              <div class="price-section">
                <span class="price">¥{{ dish.price }}</span>
                <el-tag 
                  v-if="dish.status === 0 && (userRole === 'staff' || userRole === 'admin')" 
                  type="danger" 
                  size="small"
                  style="margin-left: 10px;"
                >
                  已下架
                </el-tag>
              </div>
              <div class="action-section">
                <el-input-number 
                  v-model="dish.quantity" 
                  :min="0" 
                  :max="dish.stock || 10"
                  size="small"
                  @change="handleQuantityChange(dish)"
                />
                <el-button 
                  type="primary" 
                  size="small"
                  :disabled="dish.quantity === 0 || dish.status === 0 || (dish.stock !== undefined && dish.stock <= 0)"
                  @click="addToCart(dish)"
                >
                  加入购物车
                </el-button>
                <!-- 员工管理按钮 -->
                <div v-if="userRole === 'staff' || userRole === 'admin'" class="management-buttons">
                  <el-button 
                    type="warning" 
                    size="small"
                    @click.stop="editDish(dish)"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small"
                    @click.stop="deleteDishItem(dish)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div v-if="!filteredDishes.length && !loading" class="no-data">
        <el-empty description="暂无菜品" />
      </div>
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
                <img :src="getDishImageUrl(item.image)" class="item-image" />
                <div class="item-details">
                  <h4>{{ item.name }}</h4>
                  <p class="item-price">¥{{ item.price }}</p>
                </div>
              </div>
              <div class="item-actions">
                <el-input-number
                  v-model="item.quantity"
                  :min="0"
                  :max="item.stock || 10"
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
            <div class="date-info">
              <span>预约时间：{{ formatDateTime(selectedDate, selectedTime) }}</span>
            </div>
            <div class="total-amount">
              总计：<span class="amount">¥{{ totalAmount }}</span>
            </div>
            <el-button 
              type="primary" 
              @click="handleCheckout"
              :loading="submitting"
            >
              {{ submitting ? '提交中...' : '提交预约' }}
            </el-button>
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

    <!-- 添加/编辑菜品对话框 -->
    <el-dialog
      v-model="dishDialogVisible"
      :title="isEditMode ? '编辑菜品' : '添加菜品'"
      width="600px"
      @close="resetDishForm"
    >
      <el-form :model="dishForm" :rules="dishRules" ref="dishFormRef" label-width="80px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="dishForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品描述" prop="description">
          <el-input
            v-model="dishForm.description"
            type="textarea"
            placeholder="请输入菜品描述"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="菜品价格" prop="price">
          <el-input-number
            v-model="dishForm.price"
            :min="0"
            :precision="2"
            step="0.1"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="菜品类型" prop="type">
          <el-select v-model="dishForm.type" placeholder="请选择类型">
            <el-option label="菜品" value="dish" />
            <el-option label="主食" value="staple" />
            <el-option label="套餐" value="combo" />
            <el-option label="商品" value="product" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="dishForm.stock"
            :min="0"
            placeholder="请输入库存数量"
          />
        </el-form-item>
        <el-form-item label="菜品状态" prop="status">
          <el-select v-model="dishForm.status" placeholder="请选择状态">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload
            class="image-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            accept="image/*"
          >
            <img v-if="dishForm.image" :src="getDishImageUrl(dishForm.image)" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveDish" :loading="saving">
            {{ saving ? '保存中...' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分类管理对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      title="分类管理"
      width="500px"
    >
      <div class="category-management">
        <div class="add-category">
          <el-input
            v-model="newCategoryName"
            placeholder="输入新分类名称"
            style="width: 200px; margin-right: 10px;"
          />
          <el-button type="primary" @click="addCategory">添加分类</el-button>
        </div>
        <el-table :data="categories" style="width: 100%; margin-top: 20px;">
          <el-table-column prop="name" label="分类名称" />
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="editCategory(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, Delete, Plus, Menu } from '@element-plus/icons-vue'
import { getDishCategories, getAvailableDishes, getDishPage } from '@/api/dish'
import { addDish, updateDish, deleteDish, addDishCategory, deleteDishCategory } from '@/api/dish'
import { createOrder } from '@/api/order'
import { uploadImage } from '@/api/upload'
import { useRouter } from 'vue-router'

// 状态管理
const searchQuery = ref('')
const selectedCategory = ref('')
const selectedDate = ref(new Date())
const selectedTime = ref('12:00')
const cartDrawerVisible = ref(false)
const cart = ref([])
const loading = ref(false)
const submitting = ref(false)
const router = useRouter()

// 管理功能状态
const userRole = ref(localStorage.getItem('userType') || '')
const dishDialogVisible = ref(false)
const categoryDialogVisible = ref(false)
const isEditMode = ref(false)
const saving = ref(false)
const dishFormRef = ref(null)
const newCategoryName = ref('')

// 数据
const categories = ref([])
const dishes = ref([])

// 时间选项（6:00-22:00，每30分钟一个选项）
const timeOptions = ref([
  { value: '06:00', label: '06:00' },
  { value: '06:30', label: '06:30' },
  { value: '07:00', label: '07:00' },
  { value: '07:30', label: '07:30' },
  { value: '08:00', label: '08:00' },
  { value: '08:30', label: '08:30' },
  { value: '09:00', label: '09:00' },
  { value: '09:30', label: '09:30' },
  { value: '10:00', label: '10:00' },
  { value: '10:30', label: '10:30' },
  { value: '11:00', label: '11:00' },
  { value: '11:30', label: '11:30' },
  { value: '12:00', label: '12:00' },
  { value: '12:30', label: '12:30' },
  { value: '13:00', label: '13:00' },
  { value: '13:30', label: '13:30' },
  { value: '14:00', label: '14:00' },
  { value: '14:30', label: '14:30' },
  { value: '15:00', label: '15:00' },
  { value: '15:30', label: '15:30' },
  { value: '16:00', label: '16:00' },
  { value: '16:30', label: '16:30' },
  { value: '17:00', label: '17:00' },
  { value: '17:30', label: '17:30' },
  { value: '18:00', label: '18:00' },
  { value: '18:30', label: '18:30' },
  { value: '19:00', label: '19:00' },
  { value: '19:30', label: '19:30' },
  { value: '20:00', label: '20:00' },
  { value: '20:30', label: '20:30' },
  { value: '21:00', label: '21:00' },
  { value: '21:30', label: '21:30' },
  { value: '22:00', label: '22:00' }
])

// 菜品表单
const dishForm = ref({
  id: null,
  name: '',
  categoryId: '',
  description: '',
  price: 0,
  type: 'dish',
  stock: 0,
  image: '',
  status: 1
})

// 表单验证规则
const dishRules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择菜品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入菜品价格', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择菜品类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择菜品状态', trigger: 'change' }
  ]
}

// 上传配置
const uploadAction = ref('http://localhost:8080/api/file/upload')

// 计算属性
const filteredDishes = computed(() => {
  let filtered = dishes.value

  // 按分类过滤
  if (selectedCategory.value) {
    filtered = filtered.filter(dish => dish.categoryId === selectedCategory.value)
  }

  // 按搜索关键词过滤
  if (searchQuery.value) {
    filtered = filtered.filter(dish => 
      dish.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (dish.description && dish.description.toLowerCase().includes(searchQuery.value.toLowerCase()))
    )
  }

  return filtered
})

const totalAmount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

const totalItems = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.quantity, 0)
})

// 生命周期
onMounted(() => {
  loadCategories()
  loadAllDishes()
})

// 方法
const loadCategories = async () => {
  try {
    const response = await getDishCategories()
    categories.value = response.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

const loadAllDishes = async () => {
  loading.value = true
  try {
    let response
    // 员工和管理员可以看到所有菜品（包括下架的），学生只能看到上架的
    if (userRole.value === 'staff' || userRole.value === 'admin') {
      response = await getDishPage({ 
        page: 1, 
        size: 100 
      })
    } else {
      response = await getAvailableDishes({ 
        page: 1, 
        size: 100 
      })
    }
    
    const dishList = response.data?.records || []
    
    // 为每个菜品添加quantity字段
    dishes.value = dishList.map(dish => ({
      ...dish,
      quantity: 0
    }))
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

const handleCategoryChange = () => {
  // 分类过滤逻辑在computed中处理
}

const handleDateChange = () => {
  // 日期变更逻辑
}

const disabledDate = (time) => {
  // 禁用今天之前的日期
  return time.getTime() < Date.now() - 8.64e7
}

const getDishImageUrl = (imagePath) => {
  if (!imagePath) {
    return '/src/assets/default-dish.jpg'
  }
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

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const handleQuantityChange = (dish) => {
  // 数量变化处理
}

const addToCart = (dish) => {
  if (dish.quantity <= 0) {
    ElMessage.warning('请先选择数量')
    return
  }

  // 任何用户都不能将下架菜品添加到购物车
  if (dish.status === 0) {
    ElMessage.warning('该菜品已下架，无法添加')
    return
  }

  if (dish.stock !== undefined && dish.stock <= 0) {
    ElMessage.warning('该菜品库存不足')
    return
  }

  const existingItem = cart.value.find(item => item.id === dish.id)
  if (existingItem) {
    // 检查库存限制
    const newQuantity = existingItem.quantity + dish.quantity
    if (dish.stock !== undefined && newQuantity > dish.stock) {
      ElMessage.warning('已达到库存上限')
      return
    }
    existingItem.quantity = newQuantity
  } else {
    cart.value.push({
      ...dish,
      quantity: dish.quantity
    })
  }

  // 重置菜品数量
  dish.quantity = 0
  ElMessage.success('已添加到购物车')
}

const handleCartQuantityChange = (item) => {
  if (item.quantity <= 0) {
    removeFromCart(item)
  }
}

const removeFromCart = (item) => {
  const index = cart.value.findIndex(cartItem => cartItem.id === item.id)
  if (index > -1) {
    cart.value.splice(index, 1)
    ElMessage.success('已从购物车移除')
  }
}

const handleCheckout = async () => {
  if (cart.value.length === 0) {
    ElMessage.warning('购物车是空的')
    return
  }

  if (!selectedDate.value) {
    ElMessage.warning('请选择用餐日期')
    return
  }

  if (!selectedTime.value) {
    ElMessage.warning('请选择用餐时间')
    return
  }

  // 验证预约时间
  const eatDateTime = new Date(selectedDate.value)
  const timeParts = selectedTime.value.split(':')
  eatDateTime.setHours(parseInt(timeParts[0]), parseInt(timeParts[1]), 0, 0)
  
  const now = new Date()
  const minTime = new Date(now.getTime() + 60 * 60 * 1000) // 当前时间+1小时
  
  if (eatDateTime <= minTime) {
    ElMessage.warning('预约时间至少要在1小时后')
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
    // 使用已经验证过的预约时间
    const orderData = {
      amount: parseFloat(totalAmount.value),
      subsidyAmount: 0,
      actualAmount: parseFloat(totalAmount.value),
      paymentMethod: '',  // 支付方式在支付页面确定
      orderType: 'reservation',
      eatTime: eatDateTime.toISOString().slice(0, 19).replace('T', ' '),
      remark: '',
      details: cart.value.map(item => ({
        dishId: item.id,
        dishName: item.name,
        dishType: item.type,
        price: item.price,
        quantity: item.quantity,
        amount: (item.price * item.quantity).toFixed(2)
      }))
    }

    const response = await createOrder(orderData)
    ElMessage.success('订单创建成功，请完成支付')
    
    // 清空购物车
    cart.value = []
    cartDrawerVisible.value = false
    
    // 重置菜品数量
    dishes.value.forEach(dish => {
      dish.quantity = 0
    })
    
    // 跳转到支付页面
    router.push(`/order-payment/${response.data.id}`)
  } catch (error) {
    console.error('提交预约失败:', error)
    ElMessage.error('提交预约失败，请重试')
  } finally {
    submitting.value = false
  }
}

const formatDateTime = (date, time) => {
  if (!date || !time) return ''
  const eatDateTime = new Date(date)
  eatDateTime.setHours(time.split(':')[0], time.split(':')[1], 0, 0)
  return eatDateTime.toLocaleString('zh-CN')
}

const handleTimeChange = (event) => {
  const value = event.target.value
  console.log('时间选择器值改变:', value)
  selectedTime.value = value
}

// 菜品管理方法
const showAddDishDialog = () => {
  isEditMode.value = false
  resetDishForm()
  dishDialogVisible.value = true
}

const editDish = (dish) => {
  isEditMode.value = true
  dishForm.value = {
    id: dish.id,
    name: dish.name,
    categoryId: dish.categoryId,
    description: dish.description || '',
    price: dish.price,
    type: dish.type,
    stock: dish.stock || 0,
    image: dish.image || '',
    status: dish.status || 1
  }
  dishDialogVisible.value = true
}

const resetDishForm = () => {
  dishForm.value = {
    id: null,
    name: '',
    categoryId: '',
    description: '',
    price: 0,
    type: 'dish',
    stock: 0,
    image: '',
    status: 1
  }
  if (dishFormRef.value) {
    dishFormRef.value.resetFields()
  }
}

const saveDish = async () => {
  if (!dishFormRef.value) return
  
  await dishFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        if (isEditMode.value) {
          await updateDish(dishForm.value.id, dishForm.value)
          ElMessage.success('菜品更新成功')
        } else {
          await addDish(dishForm.value)
          ElMessage.success('菜品添加成功')
        }
        dishDialogVisible.value = false
        loadAllDishes() // 重新加载菜品列表
      } catch (error) {
        console.error('保存菜品失败:', error)
        ElMessage.error('保存菜品失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const deleteDishItem = async (dish) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜品"${dish.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await deleteDish(dish.id)
    ElMessage.success('菜品删除成功')
    loadAllDishes() // 重新加载菜品列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除菜品失败:', error)
      ElMessage.error('删除菜品失败')
    }
  }
}

// 图片上传处理
const handleImageSuccess = (response) => {
  console.log('图片上传响应:', response)
  if (response.code === 200) {
    // 后端直接返回路径字符串在data字段中
    dishForm.value.image = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 分类管理方法
const showCategoryDialog = () => {
  categoryDialogVisible.value = true
}

const addCategory = async () => {
  if (!newCategoryName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  try {
    await addDishCategory({
      name: newCategoryName.value.trim(),
      sortOrder: categories.value.length + 1
    })
    ElMessage.success('分类添加成功')
    newCategoryName.value = ''
    loadCategories() // 重新加载分类列表
  } catch (error) {
    console.error('添加分类失败:', error)
    ElMessage.error('添加分类失败')
  }
}

const editCategory = (category) => {
  // 这里可以添加编辑分类的功能
  ElMessage.info('编辑分类功能待实现')
}

const deleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await deleteDishCategory(category.id)
    ElMessage.success('分类删除成功')
    loadCategories() // 重新加载分类列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }
}
</script>

<style scoped>
.advance-order-container {
  padding: 20px;
}

.management-section {
  margin-bottom: 20px;
}

.management-card {
  border: 1px solid #e4e7ed;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.management-actions {
  display: flex;
  gap: 10px;
}

.management-buttons {
  display: flex;
  gap: 5px;
  margin-top: 10px;
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-input {
  width: 300px;
}

.category-select {
  width: 200px;
}

.date-picker {
  flex: 1;
}

.time-picker {
  flex: 1;
  position: relative;
  z-index: 1;
}

.time-picker .el-input__wrapper {
  cursor: pointer !important;
}

.time-select {
  width: 100%;
  height: 32px;
  padding: 0 11px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  background-color: #ffffff;
  cursor: pointer;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.time-select:hover {
  border-color: #c0c4cc;
}

.time-select:focus {
  border-color: #409eff;
  outline: none;
}

.dishes-section {
  min-height: 400px;
}

.dish-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.dish-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.dish-unavailable {
  opacity: 0.8;
  border: 2px dashed #f56c6c;
}

.dish-unavailable:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(245, 108, 108, 0.3);
}

.dish-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.dish-info {
  padding: 15px;
}

.dish-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.description {
  margin: 0 0 10px 0;
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dish-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.meta-info {
  display: flex;
  align-items: center;
}

.stock {
  font-size: 12px;
  color: #909399;
}

.price-section {
  margin-bottom: 15px;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.action-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: column;
  gap: 10px;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.cart-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.empty-cart {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.item-info {
  display: flex;
  flex: 1;
  align-items: center;
}

.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 10px;
}

.item-details h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #303133;
}

.item-price {
  margin: 0;
  color: #f56c6c;
  font-weight: bold;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.date-info {
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.total-amount {
  margin-bottom: 20px;
  font-size: 18px;
  text-align: right;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 20px;
}

.floating-cart {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
}

/* 图片上传样式 */
.image-uploader .uploaded-image {
  width: 148px;
  height: 148px;
  display: block;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.2s;
}

.image-uploader .uploaded-image:hover {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: border-color 0.2s;
}

.image-uploader-icon:hover {
  border-color: #409eff;
}

/* 分类管理样式 */
.category-management {
  padding: 10px 0;
}

.add-category {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}
</style> 