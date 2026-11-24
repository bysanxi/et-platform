<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';

import { computed } from 'vue';

import { AuthenticationLogin, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
const formSchema = computed((): VbenFormSchema[] => [
  {
    component: 'VbenInput',
    componentProps: { placeholder: $t('page.auth.tenantCode') },
    fieldName: 'tenantCode',
    label: $t('page.auth.tenantCode'),
    rules: z.string().trim().min(1, $t('page.auth.tenantCodeRequired')),
  },
  {
    component: 'VbenInput',
    componentProps: { placeholder: $t('authentication.usernameTip') },
    fieldName: 'username',
    label: $t('authentication.username'),
    rules: z.string().min(1, $t('authentication.usernameTip')),
  },
  {
    component: 'VbenInputPassword',
    componentProps: { placeholder: $t('authentication.password') },
    fieldName: 'password',
    label: $t('authentication.password'),
    rules: z.string().min(1, $t('authentication.passwordTip')),
  },
]);

async function handleSubmit(values: Record<string, unknown>) {
  try {
    await authStore.authLogin({
      tenantCode: String(values.tenantCode ?? '').trim(),
      username: String(values.username ?? ''),
      password: String(values.password ?? ''),
    });
  } catch {
    // The request client displays the authentication error.
  }
}
</script>

<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    :show-code-login="false"
    :show-forget-password="false"
    :show-qrcode-login="false"
    :show-register="false"
    :show-third-party-login="false"
    @submit="handleSubmit"
  />
</template>
