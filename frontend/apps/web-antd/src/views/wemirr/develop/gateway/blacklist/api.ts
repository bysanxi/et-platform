/**
 * 网关黑名单 API
 */
import { defHttp } from '#/api/request';

const BASE_URL = '/gateway/rules/blacklist';

/** 获取黑名单列表 */
export interface BlacklistRule {
  id: string;
  ip?: string;
  method?: string;
  path?: string;
  status: boolean;
}

export const GetList = () => defHttp.get<BlacklistRule[]>(BASE_URL);

/** 新增黑名单 */
export const AddObj = (data: any) => defHttp.post(BASE_URL, data);

/** 更新黑名单 */
export const UpdateObj = AddObj;

/** 删除黑名单 */
export const DelObj = (id: any) => defHttp.delete(`${BASE_URL}/${id}`);
