<script lang="ts" setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';

import { BasicLayout, LockScreen, UserDropdown } from '@vben/layouts';
import { preferences } from '@vben/preferences';
import { useUserStore } from '@vben/stores';

import { $t } from '#/locales';
import { useAuthStore } from '#/store';

const router = useRouter();
const userStore = useUserStore();
const authStore = useAuthStore();
const avatar = computed(
  () => userStore.userInfo?.avatar || preferences.app.defaultAvatar,
);
const menus = computed(() => [
  {
    handler: () => router.push({ name: 'Profile' }),
    icon: 'lucide:user',
    text: $t('page.auth.profile'),
  },
]);

function handleLogout() {
  return authStore.logout(false);
}
</script>

<template>
  <BasicLayout @clear-preferences-and-logout="handleLogout">
    <template #user-dropdown>
      <UserDropdown
        :avatar="avatar"
        :menus="menus"
        :text="userStore.userInfo?.nickname"
        :description="userStore.userInfo?.description"
        :tag-text="userStore.userInfo?.tenantCode"
        @logout="handleLogout"
      />
    </template>
    <template #lock-screen>
      <LockScreen :avatar="avatar" @to-login="handleLogout" />
    </template>
  </BasicLayout>
</template>
