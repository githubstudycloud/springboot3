import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import dts from 'vite-plugin-dts';
import baseConfig from '../vite.config.base';

// https://vitejs.dev/config/
export default defineConfig({
  ...baseConfig,
  plugins: [
    vue(),
    dts({
      entryRoot: 'src',
      outDir: 'dist',
      tsconfigPath: path.resolve(__dirname, 'tsconfig.json'),
    }),
  ],
  build: {
    lib: {
      entry: path.resolve(__dirname, 'src/index.ts'),
      name: 'PlatformUICommon',
      fileName: (format) => `index.${format === 'es' ? 'mjs' : 'js'}`,
    },
    rollupOptions: {
      external: ['vue', 'element-plus', 'axios'],
      output: {
        globals: {
          vue: 'Vue',
          'element-plus': 'ElementPlus',
          axios: 'axios',
        },
      },
    },
  },
});
