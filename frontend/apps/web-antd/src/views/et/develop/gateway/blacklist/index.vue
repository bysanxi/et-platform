<script lang="ts" setup>
/**
 * 网关黑名单管理
 * 数据存储在 Redis 中，前端做本地分页
 */
import { onMounted } from 'vue';

import { useFs } from '@fast-crud/fast-crud';

import createCrudOptions from './crud';

defineOptions({ name: 'GatewayBlackPageList' });

const { crudRef, crudBinding, crudExpose } = useFs({
  createCrudOptions,
});

onMounted(async () => {
  await crudExpose.doRefresh();
});
</script>

<template>
  <fs-page class="page-layout-card">
    <fs-crud ref="crudRef" v-bind="crudBinding" />
  </fs-page>
</template>
