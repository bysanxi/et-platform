import { defHttp } from '#/api/request';

export function GetList(data: any) {
  return defHttp.post('/iam/roles/page', data);
}
export function GetObj(id: any) {
  return defHttp.get(`/iam/roles/${id}/detail`);
}
export function AddObj(obj: any) {
  return defHttp.post('/iam/roles/create', obj);
}

export function UpdateObj(data: any) {
  return defHttp.put(`/iam/roles/${data.id}`, data);
}

export function DelObj(id: string) {
  return defHttp.delete(`/iam/roles/${id}`);
}

export function getUserByRoleId(roleId: string) {
  return defHttp.get(`/iam/roles/${roleId}/users`);
}
export function getRolePermissions(roleId: string) {
  return defHttp.get(`/iam/roles/${roleId}/permissions`);
}
export function assignUser(obj: any) {
  return defHttp.put(`/iam/roles/${obj.roleId}/assign-users`, obj);
}

export function assignResource(obj: any) {
  return defHttp.put(`/iam/roles/${obj.roleId}/assign-resources`, obj);
}
