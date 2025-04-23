import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import baseConfig from '../vite.config.base';

// https://vitejs.dev/config/
export default defineConfig({
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
});
