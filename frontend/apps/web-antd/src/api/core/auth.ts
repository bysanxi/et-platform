import { requestClient } from '#/api/request';

export namespace AuthApi {
  export interface LoginParams {
    password: string;
    tenantCode: string;
    username: string;
  }

  export interface LoginResult {
    accessToken: string;
  }
}

export function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<AuthApi.LoginResult>('/iam/token/login', {
    ...data,
    loginType: 'password',
    clientId: import.meta.env.VITE_GLOB_IAM_CLIENT_ID,
    clientSecret: import.meta.env.VITE_GLOB_IAM_CLIENT_SECRET,
  });
}

export function logoutApi() {
  return requestClient.delete('/iam/token/logout');
}

export function getAccessCodesApi() {
  return requestClient.get<string[]>('/iam/token/func_permissions');
}
