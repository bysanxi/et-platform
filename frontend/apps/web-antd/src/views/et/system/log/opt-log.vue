<script lang="ts" setup name="OptLogPage">
import { onMounted } from 'vue';

import { useFs } from '@fast-crud/fast-crud';

import createCrudOptions from './opt-log';

const { crudBinding, crudRef, crudExpose } = useFs({ createCrudOptions });
onMounted(async () => {
  await crudExpose.doRefresh();
});
</script>

<template>
  <fs-page class="page-layout-card">
    <fs-crud ref="crudRef" v-bind="crudBinding">
      <template #cell_action="scope">
        <a-tooltip :title="scope.row.action" placement="top">
          {{ scope.row.action }}
        </a-tooltip>
      </template>
    </fs-crud>
  </fs-page>
</template>
