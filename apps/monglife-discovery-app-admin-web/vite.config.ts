import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import path from 'node:path';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') },
  },
  server: {
    port: 5173,
    // MSW 를 끄고 로컬 common-api(:8010, context-path /api) 를 직접 붙일 때 사용.
    proxy: {
      '/api': { target: 'http://localhost:8010', changeOrigin: true },
    },
  },
});
