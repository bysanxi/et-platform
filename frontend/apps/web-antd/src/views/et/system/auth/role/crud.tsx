import type {
  AddReq,
  CreateCrudOptionsProps,
  CreateCrudOptionsRet,
  DelReq,
  EditReq,
  InfoReq,
  UserPageQuery,
} from '@fast-crud/fast-crud';

import { useAccess } from '@vben/access';

import { compute, dict } from '@fast-crud/fast-crud';

import {
  booleanDict,
  createTimeReadonlyColumn,
  hiddenIdColumn,
  statusDict,
} from '#/plugin/fast-crud/shared';

import * as api from './api';

export default function crud(
  props: CreateCrudOptionsProps,
): CreateCrudOptionsRet {
  const { assign } = props.context;
  const { hasPermission } = useAccess();
  return {
    crudOptions: {
      request: {
        pageRequest: async (query: UserPageQuery) => await api.GetList(query),
        infoRequest: async ({ mode, row }: InfoReq) => {
          if (mode !== 'add') {
            return await api.GetObj(row.id);
          }
          return row;
        },
        addRequest: async ({ form }: AddReq) => await api.AddObj(form),
        editRequest: async ({ form }: EditReq) => await api.UpdateObj(form),
        delRequest: async ({ row }: DelReq) => await api.DelObj(row.id),
      },
      table: { size: 'small', scroll: { fixed: true } },
      rowHandle: {
        show: true,
        width: 300,
        align: 'right',
        buttons: {
          view: { show: false },
          edit: {
            show: compute(({ row }) => {
              return hasPermission('sys:role:edit') && !row.readonly;
            }),
          },
          remove: {
            show: compute(({ row }) => {
              return hasPermission('sys:role:remove') && !row.readonly;
            }),
          },
          distribution: {
            text: '分配用户',
            size: 'small',
            type: 'link',
            order: 4,
            show: hasPermission('sys:role:assign-users'),
            async click({ row }: any) {
              await assign.userModal(row.id);
            },
          },
          resource: {
            text: '分配权限',
            type: 'link',
            size: 'small',
            order: 5,
            show: hasPermission('sys:role:assign-resource'),
            async click({ row }: any) {
              await assign.resourceModal(row.id);
            },
          },
        },
      },
      columns: {
        id: hiddenIdColumn,
        code: {
          title: '编码',
          type: 'text',
          column: { width: 180 },
          form: {
            rules: [
              { required: true, message: '请输入编码' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        name: {
          title: '名称',
          type: 'text',
          column: { width: 180 },
          search: { show: true },
          form: {
            rules: [
              { required: true, message: '请输入名称' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        readonly: {
          title: '内置角色',
          type: 'dict-radio',
          column: { width: 90, align: 'center' },
          form: { show: false },
          dict: booleanDict(),
        },
        status: {
          title: '状态',
          type: 'dict-radio',
          search: { show: true },
          column: { width: 100, align: 'center' },
          addForm: { value: true },
          dict: statusDict(),
        },
        scopeType: {
          title: '数据范围',
          type: 'dict-select',
          addForm: { value: 10 },
          dict: dict({
            data: [
              { value: 10, label: '个人' },
              { value: 30, label: '本组织' },
              { value: 40, label: '本组织及下级' },
              { value: 20, label: '自定义组织' },
              { value: 50, label: '全租户' },
            ],
          }),
        },
        orgIdList: {
          title: '授权组织',
          type: 'dict-tree',
          column: { show: false },
          dict: dict({
            isTree: true,
            url: '/iam/org/trees',
            value: 'id',
            label: 'name',
          }),
          form: {
            show: compute(({ form }) => form.scopeType === 20),
            component: {
              fieldNames: { key: 'id', title: 'name', value: 'id' },
              multiple: true,
            },
            rules: [{ required: true, message: '请选择授权组织' }],
          },
        },
        description: {
          title: '描述',
          search: { show: false },
          column: { width: 230, ellipsis: true },
          type: ['textarea'],
          form: {
            col: { span: 24 },
          },
        },
        createTime: createTimeReadonlyColumn,
      },
    },
  };
}
