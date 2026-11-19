import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'
import { compression } from 'vite-plugin-compression2';

export default defineConfig({
  plugins: [
    react(),
    compression({
      algorithms: ['brotliCompress', 'gzip'],
      threshold: 1024,
    }),
  ],
  server: {
    allowedHosts: ["policysim.charlesokeke.com"]
  },
  build: { 
    minify: 'esbuild',
    cssMinify: true,
    sourcemap: true
 }
})
