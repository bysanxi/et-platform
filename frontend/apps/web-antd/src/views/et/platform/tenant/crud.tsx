import type {
  AddReq,
  CreateCrudOptionsProps,
  CreateCrudOptionsRet,
  DelReq,
  EditReq,
  UserPageQuery,
} from '@fast-crud/fast-crud';

import { useAccess } from '@vben/access';

import { dict } from '@fast-crud/fast-crud';
import { Modal, notification } from 'ant-design-vue';

import { defHttp } from '#/api/request';
import { hiddenIdColumn, statusDict } from '#/plugin/fast-crud/shared';

export default function crud({
  crudExpose,
}: CreateCrudOptionsProps): CreateCrudOptionsRet {
  const { hasPermission } = useAccess();
  return {
    crudOptions: {
      request: {
        pageRequest: (query: UserPageQuery) =>
          defHttp.post('/iam/tenants/page', query),
        addRequest: async ({ form }: AddReq) =>
          await defHttp.post(`/iam/tenants/create`, form),
        editRequest: async ({ form }: EditReq) =>
          await defHttp.put(`/iam/tenants/${form.id}/modify`, form),
        delRequest: async ({ row }: DelReq) =>
          await defHttp.delete(`/iam/tenants/${row.id}`),
      },
      actionbar: {
        buttons: {},
      },
      rowHandle: {
        width: 180,
        fixed: 'right',
        dropdown: {
          // 操作列折叠
          atLeast: 1,
          more: {
            size: 'small',
            icon: '',
            text: '更多',
          },
        },
        buttons: {
          remove: { order: 5 },
          init: {
            type: 'link',
            title: '数据初始',
            text: '数据初始',
            size: 'small',
            order: 4,
            show: hasPermission('tenant:init-script'),
            click({ row }) {
              Modal.confirm({
                iconType: 'warning',
                title: '风险提示',
                content: `确定初始化 [${row.name}] 数据吗?`,
                onOk: async () => {
                  await defHttp.put(`/iam/tenants/${row.id}/init-sql-script`);
                  notification.success({ message: '租户数据初始化成功' });
                  await crudExpose.doRefresh();
                },
              });
            },
          },
        },
      },
      table: { scroll: { fixed: true } },
      columns: {
        id: hiddenIdColumn,
        code: {
          title: '编码',
          type: 'text',
          search: { show: true },
          column: { ellipsis: true, width: 90 },
          form: {
            rules: [
              { required: true, message: '请输入编码' },
              { min: 4, max: 6, message: '长度在 4 到 6 个字符' },
            ],
          },
        },
        name: {
          title: '名称',
          type: 'text',
          search: { show: true },
          column: { ellipsis: true, width: 200 },
          form: {
            rules: [
              { required: true, message: '请输入名称' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        alias: {
          title: '简称',
          type: 'text',
          column: { width: 100 },
          form: {
            rules: [
              { required: true, message: '请输入简称' },
              { min: 2, max: 8, message: '长度在 2 到 8 个字符' },
            ],
          },
        },
        contactPerson: {
          title: '联系人',
          type: 'text',
          column: { show: true, width: 120 },
          form: {
            rules: [
              { required: true, message: '联系人不能为空' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        contactPhone: {
          title: '联系方式',
          type: 'text',
          column: { show: true, width: 150 },
          form: {
            rules: [
              { required: true, message: '联系方式不能为空' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        type: {
          title: '类型',
          search: { show: true },
          column: { show: true, align: 'center', width: 80 }, // 表单配置
          type: 'dict-radio',
          addForm: { value: 0 },
          dict: dict({
            data: [
              { value: 0, label: '其它', color: 'warning' },
              { value: 1, label: '企业', color: 'success' },
            ],
          }),
        },
        status: {
          title: '状态',
          addForm: { value: false },
          column: { show: true, align: 'center', width: 80 },
          type: 'dict-radio',
          dict: statusDict(),
          form: {
            rules: [{ required: true, message: '状态不能为空' }],
          },
        },
        email: {
          title: '邮箱',
          type: 'text',
          column: { show: false, width: 200 },
          form: {
            rules: [{ min: 2, max: 30, message: '长度在 2 到 30 个字符' }],
          },
        },
        // 目的是为了用户体验更好,打开弹窗和进入页面更快速
        description: {
          title: '描述',
          type: ['textarea'],
          column: { ellipsis: true, show: false },
          form: {
            col: { span: 24 },
          },
        },
        createTime: {
          title: '创建时间',
          type: ['datetime', 'wp-readonly-time'],
        },
      },
    },
  };
}
