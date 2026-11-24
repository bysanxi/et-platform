import { env } from 'node:process';

import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => ({
  application: {},
  vite: {
    server: {
      proxy: {
        '/api': {
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
          target: env.ET_GATEWAY_URL || 'http://localhost:15000',
        },
      },
    },
  },
}));
