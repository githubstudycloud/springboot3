import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import App from './App.vue';
import router from './router';

// 创建 Vue 应用实例
const app = createApp(App);

// 使用 Pinia 状态管理
app.use(createPinia());

// 注册路由
app.use(router);

// 使用 Element Plus
app.use(ElementPlus, { size: 'default' });

// 挂载应用
app.mount('#app');
