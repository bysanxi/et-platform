<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { useFs } from '@fast-crud/fast-crud';

import * as api from './api';
import AssignResource from './assign-resource.vue';
import AssignUser from './assign-user.vue';
import createCrudOptions from './crud';

const [AssignUserModal, userModalApi] = useVbenModal({
  connectedComponent: AssignUser,
});
const assignResourceRef = ref<InstanceType<typeof AssignResource> | null>(null);

function assignModal() {
  function userModal(roleId: string) {
    api.getUserByRoleId(roleId).then((data) => {
      userModalApi.setData({ roleId, ...data });
      userModalApi.open();
    });
  }

  function resourceModal(roleId: string) {
    assignResourceRef.value?.open({ roleId });
  }

  return {
    userModal,
    resourceModal,
  };
}

const assign = assignModal();
const { crudRef, crudBinding, crudExpose } = useFs({
  createCrudOptions,
  context: { assign, permission: 'sys:role' },
});

// 页面打开后获取列表数据
onMounted(async () => {
  await crudExpose.doRefresh();
});
</script>

<template>
  <fs-page class="page-layout-card">
    <fs-crud ref="crudRef" v-bind="crudBinding">
      <template #cell_description="scope">
        <a-tooltip :title="scope.row.description" placement="top">
          {{ scope.row.description }}
        </a-tooltip>
      </template>
    </fs-crud>
    <AssignUserModal />
    <AssignResource ref="assignResourceRef" />
  </fs-page>
</template>
