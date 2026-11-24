import { defineOverridesPreferences } from '@vben/preferences';

export const overridesPreferences = defineOverridesPreferences({
  app: {
    name: import.meta.env.VITE_APP_TITLE,
    accessMode: 'backend',
    contentCompact: 'wide',
    enableCheckUpdates: false,
    enableRefreshToken: false,
    layout: 'sidebar-mixed-nav',
  },
  logo: {
    enable: true,
    source: import.meta.env.BASE_URL + 'favicon.ico',
  },
  footer: {
    enable: false,
  },
  sidebar: {
    extraCollapse: true,
  },
  widget: {
    notification: false,
  },
});
