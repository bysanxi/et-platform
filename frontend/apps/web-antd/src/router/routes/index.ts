import type { RouteRecordRaw } from 'vue-router';

import { coreRoutes, fallbackNotFoundRoute } from './core';

const routes: RouteRecordRaw[] = [...coreRoutes, fallbackNotFoundRoute];
const publicRouteNames = ['Authentication', 'Login'];
const accessRoutes: RouteRecordRaw[] = [];

export { accessRoutes, publicRouteNames, routes };
