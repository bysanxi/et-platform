import { defHttp } from '#/api/request';

/**
 * 根据应用ID获取菜单树
 * @param clientId 应用ID
 */
export function GetMenuTreeByClientId(clientId: string) {
  return defHttp.request('/iam/resources/trees', {
    method: 'get',
    params: { clientId },
  });
}

export function GetResourceList(query: any) {
  return defHttp.request('/iam/resources/page', {
    method: 'get',
    params: query,
  });
}
export function SaveOrUpdate(obj: any) {
  return obj.id ? UpdateObj(obj) : AddObj(obj);
}
export function AddObj(obj: any) {
  return defHttp.request('/iam/resources/create', {
    method: 'post',
    data: obj,
  });
}

export function UpdateObj(obj: any) {
  return defHttp.request(`/iam/resources/${obj.id}/modify`, {
    method: 'put',
    data: obj,
  });
}

export function DelObj(id: any) {
  return defHttp.request(`/iam/resources/${id}`, {
    method: 'delete',
    data: { id },
  });
}
