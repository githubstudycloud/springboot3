import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 布局
import DefaultLayout from '../layouts/DefaultLayout.vue';

// 路由配置
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: DefaultLayout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'el-icon-s-home',
          requiresAuth: true
        }
      },
      {
        path: '/tasks',
        name: 'Tasks',
        component: () => import('../views/tasks/Index.vue'),
        meta: {
          title: '任务管理',
          icon: 'el-icon-s-order',
          requiresAuth: true
        }
      },
      {
        path: '/tasks/:id',
        name: 'TaskDetail',
        component: () => import('../views/tasks/Detail.vue'),
        meta: {
          title: '任务详情',
          hideInMenu: true,
          requiresAuth: true
        }
      },
      {
        path: '/monitoring',
        name: 'Monitoring',
        component: () => import('../views/monitoring/Index.vue'),
        meta: {
          title: '系统监控',
          icon: 'el-icon-monitor',
          requiresAuth: true
        }
      },
      {
        path: '/nodes',
        name: 'Nodes',
        component: () => import('../views/nodes/Index.vue'),
        meta: {
          title: '节点管理',
          icon: 'el-icon-s-platform',
          requiresAuth: true
        }
      },
      {
        path: '/datasources',
        name: 'DataSources',
        component: () => import('../views/datasources/Index.vue'),
        meta: {
          title: '数据源管理',
          icon: 'el-icon-s-management',
          requiresAuth: true
        }
      },
      {
        path: '/logs',
        name: 'Logs',
        component: () => import('../views/logs/Index.vue'),
        meta: {
          title: '日志查询',
          icon: 'el-icon-document',
          requiresAuth: true
        }
      },
      {
        path: '/settings',
        name: 'Settings',
        component: () => import('../views/settings/Index.vue'),
        meta: {
          title: '系统设置',
          icon: 'el-icon-setting',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: {
      title: '登录',
      hideInMenu: true,
      requiresAuth: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/error/NotFound.vue'),
    meta: {
      title: '404',
      hideInMenu: true,
      requiresAuth: false
    }
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title} - 平台管理系统` || '平台管理系统';
  
  // 身份验证检查
  const token = localStorage.getItem('token');
  
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else {
    next();
  }
});

export default router;
