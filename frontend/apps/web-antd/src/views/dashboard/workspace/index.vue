<script setup lang="ts">
import { Page } from '@vben/common-ui';
import { useUserStore } from '@vben/stores';

import { Card } from 'ant-design-vue';

import { $t } from '#/locales';

const userStore = useUserStore();
</script>

<template>
  <Page :title="$t('page.dashboard.workspace')">
    <Card>
      <span class="font-medium">{{
        userStore.userInfo?.nickname || userStore.userInfo?.username
      }}</span>
      <span class="ml-4"
        >{{ $t('page.auth.tenantCode') }}
        {{ userStore.userInfo?.tenantCode }}</span
      >
    </Card>
  </Page>
</template>
