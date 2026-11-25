# ET Platform

[English](README.md) · 简体中文

ET Platform 是供 ET 内部团队复用的多租户 Java 后端底座，配套提供管理台。它提供统一登录、租户数据隔离、团队成员与组织管理、角色授权，以及登录和操作审计。

## 核心能力

- **租户隔离。** 团队以租户为单位初始化组织、管理员和默认角色。租户共用数据库，IAM 按 `tenant_id` 隔离用户、组织、角色和审计记录。
- **身份与权限。** 支持账号密码登录、成员与角色管理、菜单和按钮授权，以及按角色设置数据访问范围。
- **服务接入。** Gateway 提供 IAM 路由、限流和网关管理。业务服务可复用框架组件，并通过 Feign 调用 IAM 用户查询接口。

## 模块

- `et-platform-iam` 提供认证、租户管理和权限管理服务。
- [`et-platform-iam-common`](et-platform-iam-common/README.md) 定义用户查询、账户和 OAuth 客户端管理的 Java 业务接口及数据类型。
- `et-platform-gateway` 提供统一入口，将 `/iam/**` 请求转发到 IAM。
- `et-platform-framework` 提供公共核心、Web、MyBatis-Plus、Redis、安全和 Feign 组件。
- `et-platform-feign` 聚合 Feign 客户端，目前包含 `et-platform-iam-api`。
- `et-platform-dependencies` 统一管理依赖版本和 Maven 构建配置。
- `frontend` 提供 Vue 3 管理台，接入 ET Platform 的服务。

## 开发与部署

项目使用 JDK 21、Maven、Spring Boot 4.0.3 和 Spring Cloud 2025.1.0。本地开发环境通过 Docker Compose 启动 MySQL、Redis 和 Nacos。

- [项目概览](docs/design/project-overview.md)
- [IAM 接口与使用说明](et-platform-iam/README.md)
- [本地启动](docs/deployment/local-setup.md)
- [前端启动与构建](frontend/README.md)
- [服务、依赖与数据库升级](docs/deployment/upgrade.md)

## 项目来源与精简背景

ET Platform 后端基于 Wemirr Platform 的 `v4-dev` 分支提交 `0618bbd5` 迁移并定制，沿用 Apache License 2.0。配套前端迁自 Wemirr Platform UI，沿用 MIT 许可证。感谢上游作者及贡献者。[Wemirr Platform](https://github.com/battcn/wemirr-platform) · [基线提交](https://github.com/battcn/wemirr-platform/commit/0618bbd51e65bafb293282af497ab9e4a1c64564) · [后端许可证](LICENSE) · [前端来源与许可](frontend/README.md#来源与许可)

ET 内部应用需要共享登录和权限能力，同时隔离各团队的数据。基于这一需求，项目保留 IAM、网关和常用框架组件，移除了 Suite、工作流、监控、AI、仓储管理（WMS）、运输管理（TMS）和套餐订阅功能，减少底座需要部署和维护的模块，让各业务项目按需扩展。
