import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import createVitePlugins from './vite/plugins'

// https://vite.dev/config/
export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV, VITE_APP_BASE_API } = env
  return {
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: createVitePlugins(env, command === 'build'),
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '~': fileURLToPath(new URL('./', import.meta.url))
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    build: {
      sourceMap: command === 'build' ? false : 'inline',
      outDir: 'dist',
      assetsDir: 'assets',
      chunkSizeWarningLimit: 2000,
      rolldownOptions: {
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
        }
      }
    },
    // 开发服务器配置
    server: {
      port: 82,
      host: true,
      open: true,
      // 配置代理，用于后端接口调用
      proxy: {
        // 开发环境路径前缀
        [VITE_APP_BASE_API]: {
          // 目标后端服务器地址（Tomcat 默认端口）
          target: 'http://localhost:8080/gps-auth-1.0-SNAPSHOT',
          // 是否修改请求头 Origin
          changeOrigin: true,
          // 路径重写，将 /dev-api 前缀去掉
          rewrite: (path) => path.replace(new RegExp(`^${VITE_APP_BASE_API}`), '')
        }
      }
    }
  }
})