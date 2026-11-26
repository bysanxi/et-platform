# 项目概览

ET 内部团队使用的 Java 21 多租户后端，采用 Apache License 2.0，项目来源见 [README](../../README.zh-CN.md#项目来源与精简背景)。项目模块和服务注册名使用 ET 前缀，Java 包和 Maven 组织标识统一以 `cool.et` 开头。

## 代码与数据边界

- `et-platform-gateway` 提供 IAM 路由、限流和网关管理。
- `et-platform-iam` 负责账号密码登录、租户初始化、团队用户、组织、角色、菜单与按钮授权，以及登录和操作审计。新团队初始化时复制租户管理员角色模板及其授权。
- `et-platform-framework` 保留公共核心、Web、数据库、Redis、安全和 Feign 六个组件。`et-platform-feign` 提供 IAM 用户查询契约。
- 租户共用数据库。IAM 使用 `tenant_id` 隔离用户、角色、组织和审计记录。用户角色关联按所属租户校验，菜单定义由平台维护。团队管理员只能授出自己已有的资源。
