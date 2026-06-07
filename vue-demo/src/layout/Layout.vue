<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <el-icon :size="28"><OfficeBuilding /></el-icon>
          <span v-if="!sidebarCollapsed" class="logo-text">寝室管理系统</span>
        </div>
        <el-button 
          class="collapse-btn" 
          @click="sidebarCollapsed = !sidebarCollapsed"
          round
        >
          <el-icon>{{ sidebarCollapsed ? ArrowRight : ArrowLeft }}</el-icon>
        </el-button>
      </div>
      
      <nav class="sidebar-nav">
        <el-menu 
          :default-active="activeMenu" 
          mode="vertical"
          class="sidebar-menu"
        >
          <el-menu-item 
            v-for="item in menuItems" 
            :key="item.path"
            :index="item.path"
            @click="handleMenuClick(item.path)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </nav>
    </aside>
    
    <!-- 主内容区域 -->
    <main class="main-content">
      <!-- 顶部导航 -->
      <header class="top-header">
        <div class="header-left">
          <el-button 
            class="menu-toggle" 
            @click="sidebarCollapsed = !sidebarCollapsed"
            round
          >
            <el-icon><Menu /></el-icon>
          </el-button>
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon :size="20"><User /></el-icon>
              <span>{{ userInfo.name || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><WindPower /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, markRaw } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  OfficeBuilding, 
  ArrowLeft, 
  ArrowRight, 
  Menu, 
  User, 
  WindPower,
  HomeFilled,
  Grid,
  Check,
  GobletSquare,
  Wallet,
  Lock
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const sidebarCollapsed = ref(false)

const userInfo = ref({
  name: '管理员',
  role: 'admin'
})

const menuItems = [
  { path: '/dashboard', title: '首页', icon: markRaw(HomeFilled) },
  { path: '/students', title: '学生管理', icon: markRaw(User) },
  { path: '/dormitories', title: '寝室信息', icon: markRaw(OfficeBuilding) },
  { path: '/allocation', title: '寝室安排', icon: markRaw(Grid) },
  { path: '/sanitation', title: '卫生检查', icon: markRaw(Check) },
  { path: '/civilized', title: '文明寝室', icon: markRaw(GobletSquare) },
  { path: '/payment', title: '缴费管理', icon: markRaw(Wallet) },
  { path: '/permission', title: '权限管理', icon: markRaw(Lock) }
]

const activeMenu = computed(() => {
  return route.path
})

const currentTitle = computed(() => {
  const item = menuItems.find(i => i.path === route.path)
  return item ? item.title : '首页'
})

const handleMenuClick = (path) => {
  router.push(path)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  
  &.collapsed {
    width: 60px;
  }
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
}

.collapse-btn {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  border: none;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 0;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
  
  :deep(.el-menu-item) {
    color: rgba(255, 255, 255, 0.8);
    margin: 4px 8px;
    border-radius: 8px;
    
    &:hover, &.is-active {
      background: rgba(255, 150, 200, 0.2);
      color: #ff95d1;
    }
  }
}

/* 主内容 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  overflow: hidden;
}

.top-header {
  height: 60px;
  background: #fff;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-toggle {
  background: #f0f0f0;
  border: none;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
  
  :deep(.el-icon) {
    color: #666;
  }
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
