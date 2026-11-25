# ET Platform IAM

IAM 为内部团队提供登录、租户隔离和角色授权。经 Gateway 调用时，以下路径统一加上 `/iam` 前缀。

- 通过 `POST /token/login` 使用租户编码、账号、密码和已注册的客户端凭据登录，`loginType` 取 `password`。`GET /token/userinfo` 返回会话用户，`GET /token/func_permissions` 返回功能权限，`DELETE /token/logout` 退出登录。
- 平台管理员在 `/tenants` 创建团队，并通过 `PUT /tenants/{id}/init-sql-script` 初始化根组织、管理员和团队角色。一个团队可以使用与其他团队相同的用户名。
- 团队管理员在 `/users`、`/org`、`/roles` 管理成员、组织与角色。`PUT /roles/{id}/assign-resources` 和 `PUT /roles/{id}/assign-users` 分别设置角色的完整资源和成员集合。角色授权变更后，成员重新登录即可刷新会话权限。客户端通过 `GET /resources/router` 获取当前成员的菜单树。
- 角色支持个人、本组织、本组织及下级、自定义组织和全租户的数据范围。创建角色时默认仅能查询本人。自定义范围通过角色的 `scopeType=20` 和 `orgIdList` 设置。
- 用户通过 `PUT /token/change_password` 校验原密码并更换密码。具有重置权限的管理员调用 `PUT /users/{id}/reset-password` 时，响应的 `data.temporaryPassword` 返回随机新密码。改密或重置会注销该用户现有会话。
- 登录审计按 `/login_logs` 查询，操作审计按 `/opt-logs` 查询。两者都受租户和权限约束。

## 业务服务调用账户接口

业务后端通过 `UserAccountFeign` 直连 IAM。四个方法均使用 POST，Feign 自动附带平台的 Same-Token，Gateway 屏蔽这些入口。验证码、邀请码和找回凭证由业务后端验证。IAM 按请求中的 `tenantCode` 定位启用的租户。

| 方法路径 | 业务后端传入 | Feign 调用结果 |
| --- | --- | --- |
| `/users/lookup` | `tenantCode`、`type`（`username`、`email`、`mobile`）、`value` | 精确匹配后返回 `id`、`username`、`status`。未找到返回 `null`，租户无效或查询失败抛出异常 |
| `/users/register` | `tenantCode`、已验证的 `email` 或 `mobile` 二选一、可选 `password` | 返回 `userId`。IAM 生成用户名，创建启用的普通外部用户。租户内有效用户的邮箱和手机号分别唯一，重复注册的业务错误码为 409 |
| `/users/{userId}/password-reset` | 从已核销凭证取得的用户 ID、`tenantCode`、`newPassword` | 返回 `userId` 并注销该用户已有登录会话。用户必须属于该租户、处于启用状态且允许重置 |
| `/token/mobile-login` | `tenantCode`、通过短信验证的 `mobile` | 返回常规 IAM 登录响应，含 `accessToken`、`tokenType`、`expiresIn` 和 `clientId`（`mobile`） |

省略注册密码时，密码字段为 SQL NULL，密码登录会被拒绝。需要用邮箱和密码登录时，按邮箱查询用户名，再调用原有密码登录接口。短信登录保留账户原密码。个人资料页只展示邮箱和手机号。

新库使用初始化脚本，已有开发库单独执行一次[账户联系方式升级脚本](../docs/deployment/mysql/upgrades/user-account-contacts.sql)。
