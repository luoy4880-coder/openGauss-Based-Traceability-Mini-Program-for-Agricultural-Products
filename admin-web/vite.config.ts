import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('element-plus')) return 'element'
            if (id.includes('axios')) return 'axios'
            if (id.includes('vue') || id.includes('vue-router') || id.includes('pinia')) return 'vue'
          }
          return undefined
        },
      },
    },
  },
})
