/**
 * 网关黑名单 - CRUD 配置
 */
import type {
  AddReq,
  DelReq,
  EditReq,
  UserPageQuery,
  CreateCrudOptionsRet,
} from '@fast-crud/fast-crud';

import {
  createTimeColumn,
  datetimeRangeColumn,
  descriptionColumn,
  httpMethodDict,
  statusDict,
} from '../shared/index';
import * as api from './api';

export default function crud(): CreateCrudOptionsRet {
  const pageRequest = async ({
    current,
    offset,
    size,
    status,
    ip,
    method,
    path,
  }: UserPageQuery) => {
    const rules = await api.GetList();
    const records = rules.filter(
      (item) =>
        (status == null || item.status === status) &&
        (!ip || item.ip?.includes(ip)) &&
        (!method || item.method === method) &&
        (!path || item.path?.includes(path)),
    );
    const start = offset ?? 0;
    return {
      current,
      size,
      total: records.length,
      records: records.slice(start, start + (size ?? 10)),
    };
  };

  const editRequest = ({ form, row }: EditReq) =>
    api.UpdateObj({ ...form, id: row.id });
  const addRequest = ({ form }: AddReq) => api.AddObj(form);
  const delRequest = ({ row }: DelReq) => api.DelObj(row.id);

  return {
    crudOptions: {
      request: { pageRequest, addRequest, editRequest, delRequest },
      columns: {
        ip: {
          title: 'IP',
          type: 'text',
          column: { width: 160 },
          search: { show: true },
        },
        status: {
          title: '状态',
          type: 'dict-radio',
          addForm: { value: true },
          column: { width: 100 },
          search: { show: true },
          dict: statusDict(),
        },
        visits: {
          title: '访问量',
          type: 'text',
          column: { width: 80 },
          form: { show: false },
        },
        path: {
          title: '路径',
          type: 'text',
          search: { show: true },
          column: { width: 150, ellipsis: true },
          form: {
            helper: '支持 ?, *, ** 通配符匹配，网关中需要去除微服务前缀',
          },
        },
        method: {
          title: '方法',
          type: 'dict-select',
          search: { show: true },
          column: { width: 100 },
          dict: httpMethodDict(),
          addForm: { value: 'ALL' },
          form: { rules: [{ required: true, message: '请选择拦截方法' }] },
        },
        datetimerange: datetimeRangeColumn,
        description: descriptionColumn,
        createTime: createTimeColumn,
      },
    },
  };
}
