import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import dts from 'vite-plugin-dts';
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
    server: {
      port: 3100
    }
  };
});
