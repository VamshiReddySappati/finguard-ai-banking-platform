import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/auth': 'http://localhost:8081',
      '/api/accounts': 'http://localhost:8082',
      '/api/transactions': 'http://localhost:8083',
      '/api/fraud': 'http://localhost:8084',
      '/api/audit': 'http://localhost:8085'
    }
  }
})
