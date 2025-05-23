<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <div class="header-left">
          <h2>校园订餐系统</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userInfo.realName || userInfo.username }}
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            :router="true"
          >
            <el-menu-item index="/dashboard">
              <el-icon><menu /></el-icon>
              <span>首页</span>
            </el-menu-item>
            
            <!-- 学生和员工共同菜单 -->
            <template v-if="userRole === 'student' || userRole === 'staff'">
              <el-sub-menu index="ordering">
                <template #title>
                  <el-icon><ShoppingCart /></el-icon>
                  <span>订餐服务</span>
                </template>
                <el-menu-item index="/advance-order">
                  <el-icon><Timer /></el-icon>
                  <span>提前订餐</span>
                </el-menu-item>
                <el-menu-item index="/dine-in">
                  <el-icon><Bowl /></el-icon>
                  <span>堂食点餐</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item index="/order-history">
                <el-icon><Document /></el-icon>
                <span v-if="userRole === 'student'">我的订单</span>
                <span v-else>订单记录</span>
              </el-menu-item>
            </template>

            <!-- 员工额外菜单 -->
            <template v-if="userRole === 'staff'">
              <el-menu-item index="/order-management">
                <el-icon><List /></el-icon>
                <span>订单管理</span>
              </el-menu-item>
            </template>

            <!-- 管理员菜单 -->
            <template v-if="userRole === 'admin'">
              <el-sub-menu index="ordering">
                <template #title>
                  <el-icon><ShoppingCart /></el-icon>
                  <span>订餐管理</span>
                </template>
                <el-menu-item index="/advance-order">
                  <el-icon><Timer /></el-icon>
                  <span>提前订餐</span>
                </el-menu-item>
                <el-menu-item index="/dine-in">
                  <el-icon><Bowl /></el-icon>
                  <span>堂食点餐</span>
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item index="/order-history">
                <el-icon><Document /></el-icon>
                <span>订单记录</span>
              </el-menu-item>
              <el-sub-menu index="management">
                <template #title>
                  <el-icon><Setting /></el-icon>
                  <span>系统管理</span>
                </template>
                <el-menu-item index="/dish-management">
                  <el-icon><Collection /></el-icon>
                  <span>菜品管理</span>
                </el-menu-item>
                <el-menu-item index="/order-management">
                  <el-icon><List /></el-icon>
                  <span>订单管理</span>
                </el-menu-item>
                <el-menu-item index="/user-management">
                  <el-icon><User /></el-icon>
                  <span>用户管理</span>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="statistics">
                <template #title>
                  <el-icon><DataAnalysis /></el-icon>
                  <span>数据统计</span>
                </template>
                <el-menu-item index="/dining-statistics">
                  <el-icon><TrendCharts /></el-icon>
                  <span>用餐统计</span>
                </el-menu-item>
                <el-menu-item index="/subsidy-management">
                  <el-icon><Money /></el-icon>
                  <span>补贴管理</span>
                </el-menu-item>
                <el-menu-item index="/reports">
                  <el-icon><Document /></el-icon>
                  <span>报表统计</span>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-menu>
        </el-aside>
        
        <el-main>
          <!-- 使用 router-view 来渲染子路由 -->
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowDown, 
  Menu, 
  ShoppingCart, 
  Timer, 
  Bowl, 
  Document,
  List,
  Setting,
  Collection,
  User,
  DataAnalysis,
  TrendCharts,
  Money
} from '@element-plus/icons-vue'
import { removeToken } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

// 获取用户信息
const userInfo = computed(() => {
  const storedInfo = localStorage.getItem('userInfo')
  return storedInfo ? JSON.parse(storedInfo) : {}
})

// 获取用户角色
const userRole = computed(() => {
  return localStorage.getItem('userRole') || ''
})

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      removeToken()
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userRole')
      router.push('/login')
      ElMessage.success('已退出登录')
      break
  }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left h2 {
  margin: 0;
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
}

.el-dropdown-link:hover {
  color: #1890ff;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}

.el-menu-vertical {
  border-right: none;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 