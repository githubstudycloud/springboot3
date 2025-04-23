import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import { fileURLToPath } from 'url';

// 获取当前文件的目录路径
const __dirname = path.dirname(fileURLToPath(import.meta.url));
// 获取父级目录的基础配置
const baseConfigPath = path.resolve(__dirname, '../vite.config.base.ts');
// 动态导入基础配置
const importBaseConfig = async () => {
  try {
    const module = await import(baseConfigPath);
    return module.default;
  } catch (error) {
    console.warn('未找到基础配置文件，使用默认配置');
    return {};
  }
};

// https://vitejs.dev/config/
export default defineConfig(async () => {
  const baseConfig = await importBaseConfig();
  
  return {
    ...baseConfig,
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
    },
    server: {
      port: 3000,
      cors: true,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
  };
});
