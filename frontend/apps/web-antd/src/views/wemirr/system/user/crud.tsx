import type {
  AddReq,
  CreateCrudOptionsProps,
  CreateCrudOptionsRet,
  DelReq,
  EditReq,
} from '@fast-crud/fast-crud';

import { useAccess } from '@vben/access';

import { QuestionCircleOutlined } from '@ant-design/icons-vue';
import { dict } from '@fast-crud/fast-crud';

import { Modal } from 'ant-design-vue';
import { hiddenIdColumn, statusDict } from '#/plugin/fast-crud/shared';

import * as api from './api';

export default function crud(
  props: CreateCrudOptionsProps,
): CreateCrudOptionsRet {
  const { nodeRef } = props.context;
  const { hasPermission } = useAccess();
  return {
    crudOptions: {
      request: {
        pageRequest: async (query: any) => {
          query.orgId = query.orgId > 0 ? null : nodeRef?.value?.id;
          return await api.GetList(query);
        },
        addRequest: async ({ form }: AddReq) => await api.AddObj(form),
        editRequest: async ({ form }: EditReq) => await api.UpdateObj(form),
        delRequest: async ({ row }: DelReq) => await api.DelObj(row.id),
      },
      rowHandle: {
        width: 240,
        // 固定右侧
        fixed: 'right',
        buttons: {
          remove: { order: 2 },
          resetPassword: {
            type: 'link',
            order: 1,
            text: '重置密码',
            size: 'small',
            title: '重置密码',
            show: hasPermission('sys:user:reset'),
            render(scope: any) {
              async function confirm() {
                const { temporaryPassword } = await api.ResetPassword(
                  scope.row.id,
                );
                Modal.success({
                  title: '密码已重置',
                  content: temporaryPassword,
                });
              }
              return (
                <a-popconfirm
                  cancel-text="取消"
                  ok-text="确认重置"
                  onConfirm={confirm}
                  placement="bottom"
                  title={`确定重置用户 ${scope.row.nickname} 密码吗?`}
                  v-slots={{
                    icon: () => (
                      <QuestionCircleOutlined style={{ color: 'orange' }} />
                    ),
                  }}
                >
                  <a-button size="small" type="link">
                    重置密码
                  </a-button>
                </a-popconfirm>
              );
            },
          },
        },
      },
      search: {
        // 设置搜索表单 name 前缀，避免与编辑表单 ID 冲突
        formConfig: { name: 'search' },
        onReset() {
          nodeRef.value = null;
        },
      },
      table: { scroll: { fixed: true } },
      columns: {
        id: hiddenIdColumn,
        username: {
          title: '账号',
          type: 'text',

          column: { width: 155, showTitle: true },
          search: { show: true, fixed: 'left' },
          editForm: {
            component: { disabled: true },
          },
          form: {
            rules: [
              { required: true, message: '请输入账号名' },
              { min: 4, max: 30, message: '长度在 4 到 30 个字符' },
            ],
          },
        },
        password: {
          title: '密码',
          type: 'password',
          column: { show: false },
          viewForm: {
            show: false,
          },
          editForm: {
            show: false,
          },
          form: {
            rules: [
              { required: true, message: '请输入密码' },
              { min: 8, max: 30, message: '长度在 8 到 30 个字符' },
              {
                pattern:
                  /^(?![A-Za-z0-9]+$)(?![a-z0-9\W]+$)(?![A-Za-z\W]+$)(?![A-Z0-9\W]+$)[a-zA-Z0-9\W]{8,30}$/,
                message: '需同时包含大写字母、小写字母、数字和特殊字符',
              },
            ],
          },
        },
        nickname: {
          title: '昵称',
          type: 'text',
          column: { width: 155, ellipsis: true },
          search: { show: true, fixed: 'left' },
          form: {
            rules: [
              { required: true, message: '请输入昵称' },
              { min: 2, max: 30, message: '长度在 2 到 30 个字符' },
            ],
          },
        },
        mobile: {
          title: '手机号',
          type: 'text',
          search: { show: true },
          column: { width: 155, align: 'center' },
          form: {
            rules: [
              { required: true, message: '请输入手机号' },
              { pattern: /^1\d{10}$/, message: '手机号格式错误' },
            ],
          },
        },
        sex: {
          title: '性别',
          type: 'dict-radio',
          dict: dict({
            data: [
              { value: '0', label: '未知' },
              { value: '1', label: '男' },
              { value: '2', label: '女' },
            ],
          }),
          column: { width: 100, align: 'center' },
          addForm: { value: '1' },
        },
        status: {
          title: '状态',
          search: { show: true },
          type: 'dict-radio',
          dict: statusDict(),
          addForm: { value: true },
          column: { width: 80 },
        },
        email: {
          title: '邮箱',
          type: 'text',
          search: { show: false },
          column: { width: 180 },
        },
        orgId: {
          title: '组织',
          type: 'dict-tree',
          column: { width: 180, component: { color: 'auto' } },
          dict: dict({
            isTree: true,
            url: '/iam/org/trees',
            value: 'id',
            label: 'name',
          }),
          form: {
            component: {
              fieldNames: {
                children: 'children',
                title: 'name',
                key: 'id',
                value: 'id',
              },
              showSearch: true,
              filterTreeNode: (val: any, treeNode: any) => {
                return treeNode.props.title
                  .toLowerCase()
                  .includes(val.toLowerCase());
              },
            },
          },
        },
        description: {
          title: '描述',
          column: { show: false },
          type: ['textarea'],
          form: { col: { span: 24 } },
        },
        createTime: {
          title: '创建时间',
          type: ['datetime', 'wp-readonly-time'],
        },
      },
    },
  };
}
