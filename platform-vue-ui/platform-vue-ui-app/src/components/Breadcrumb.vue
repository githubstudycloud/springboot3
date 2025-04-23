<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index" :to="item.path">
      {{ item.meta.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import type { RouteLocationMatched } from 'vue-router';

// 使用 route
const route = useRoute();

// 面包屑导航项
const breadcrumbs = ref<RouteLocationMatched[]>([]);

// 获取面包屑导航项
const getBreadcrumbs = () => {
  // 过滤掉没有 meta.title 的路由项
  const matched = route.matched.filter(item => item.meta && item.meta.title);
  breadcrumbs.value = matched;
};

// 监听路由变化，更新面包屑导航
watch(
  () => route.path,
  () => {
    getBreadcrumbs();
  },
  { immediate: true }
);
</script>

<style scoped>
.app-breadcrumb {
  display: inline-block;
  line-height: 60px;
  margin-left: 8px;
  font-size: 14px;
}
</style>
