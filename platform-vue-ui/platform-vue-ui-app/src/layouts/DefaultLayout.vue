<template>
  <div class="app-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="app-sidebar">
      <div class="logo-container">
        <img src="../assets/images/logo.png" alt="Logo" class="logo-image" v-if="!isCollapse" />
        <img src="../assets/images/logo-small.png" alt="Logo" class="logo-image-small" v-else />
      </div>
      
      <!-- 导航菜单 -->
      <el-menu
        :collapse="isCollapse"
        :collapse-transition="false"
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="route in routes" :key="route.path">
          <el-menu-item v-if="!route.meta?.hideInMenu && !route.children" :index="route.path">
            <el-icon><component :is="route.meta?.icon"></component></el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
          
          <el-sub-menu v-else-if="!route.meta?.hideInMenu && route.children && route.children.length > 0" :index="route.path">
            <template #title>
              <el-icon><component :is="route.meta?.icon"></component></el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            
            <el-menu-item 
              v-for="child in route.children.filter(item => !item.meta?.hideInMenu)" 
              :key="child.path" 
              :index="child.path"
            >
              <el-icon v-if="child.meta?.icon"><component :is="child.meta?.icon"></component></el-icon>
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="app-header">
        <div class="header-left">
          <el-icon 
            :class="isCollapse ? 'el-icon-expand' : 'el-icon-fold'" 
            @click="toggleSidebar"
          >
            <component :is="isCollapse ? 'Expand' : 'Fold'"></component>
          </el-icon>
          <breadcrumb />
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="avatar-container">
              <el-avatar :size="30" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
              <span class="username">{{ userInfo.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人信息</el-dropdown-item>
                <el-dropdown-item @click="goToSettings">系统设置</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessageBox } from 'element-plus';
import Breadcrumb from '../components/Breadcrumb.vue';

// 路由实例
const router = useRouter();
const route = useRoute();

// 侧边栏折叠状态
const isCollapse = ref(false);

// 用户信息
const userInfo = ref({
  username: 'Admin',
  avatar: ''
});

// 缓存的视图
const cachedViews = ref<string[]>([]);

// 获取可显示的路由
const routes = computed(() => {
  return router.options.routes[0].children || [];
});

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path;
});

// 切换侧边栏折叠状态
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// 跳转到个人信息页
const goToProfile = () => {
  router.push('/profile');
};

// 跳转到系统设置页
const goToSettings = () => {
  router.push('/settings');
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除本地存储的用户信息和token
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    
    // 跳转到登录页
    router.push('/login');
  }).catch(() => {
    // 取消操作
  });
};

// 页面挂载时加载用户信息
onMounted(() => {
  // 这里可以调用API获取用户信息
  // 示例代码仅做演示
  setTimeout(() => {
    userInfo.value = {
      username: 'Admin',
      avatar: ''
    };
  }, 500);
});
</script>

<style scoped>
.app-container {
  height: 100%;
  width: 100%;
  display: flex;
}

.app-sidebar {
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo-image {
  height: 40px;
  max-width: 180px;
}

.logo-image-small {
  height: 32px;
  width: 32px;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.app-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.el-icon-fold, .el-icon-expand {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}

.app-main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
