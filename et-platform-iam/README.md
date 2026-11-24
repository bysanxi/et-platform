# ET Platform IAM

IAM 为内部团队提供登录、租户隔离和角色授权。经 Gateway 调用时，以下路径统一加上 `/iam` 前缀。

- 通过 `POST /token/login` 使用租户编码、账号、密码和已注册的客户端凭据登录，`loginType` 取 `password`。`GET /token/userinfo` 返回会话用户，`GET /token/func_permissions` 返回功能权限，`DELETE /token/logout` 退出登录。
- 平台管理员在 `/tenants` 创建团队，并通过 `PUT /tenants/{id}/init-sql-script` 初始化根组织、管理员和团队角色。一个团队可以使用与其他团队相同的用户名。
- 团队管理员在 `/users`、`/org`、`/roles` 管理成员、组织与角色。`PUT /roles/{id}/assign-resources` 和 `PUT /roles/{id}/assign-users` 分别设置角色的完整资源和成员集合。角色授权变更后，成员重新登录即可刷新会话权限。客户端通过 `GET /resources/router` 获取当前成员的菜单树。
- 角色支持个人、本组织、本组织及下级、自定义组织和全租户的数据范围。创建角色时默认仅能查询本人。自定义范围通过角色的 `scopeType=20` 和 `orgIdList` 设置。
- 用户通过 `PUT /token/change_password` 校验原密码并更换密码。具有重置权限的管理员调用 `PUT /users/{id}/reset-password` 时，响应的 `data.temporaryPassword` 返回随机新密码。改密或重置会注销该用户现有会话。
- 登录审计按 `/login_logs` 查询，操作审计按 `/opt-logs` 查询。两者都受租户和权限约束。
