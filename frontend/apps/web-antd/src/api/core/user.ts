import type { UserInfo } from '@vben/types';

import { defHttp } from '#/api/request';

export interface UserProfileUpdateParams {
  birthday?: string;
  description?: string;
  nickname: string;
}

export interface UserPasswordUpdateParams {
  confirmPassword: string;
  currentPassword: string;
  newPassword: string;
}

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  return defHttp.get<UserInfo>('/iam/token/userinfo');
}

/**
 * 修改当前用户基本资料
 */
export async function updateUserProfileApi(data: UserProfileUpdateParams) {
  return defHttp.put('/iam/token/change_info', data);
}

/**
 * 修改当前用户密码
 */
export async function updateUserPasswordApi(data: UserPasswordUpdateParams) {
  return defHttp.put('/iam/token/change_password', data);
}
