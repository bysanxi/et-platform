# ET Platform

English · [简体中文](README.zh-CN.md)

ET Platform is a shared multi-tenant Java backend and administration console for internal ET teams. It provides centralized sign-in, tenant data isolation, team and organization management, role-based authorization, and audit logs for sign-ins and user actions.

## Core capabilities

- **Tenant isolation.** Each team is provisioned as a tenant with an organization, an administrator, and default roles. Tenants share a database, and IAM uses `tenant_id` to isolate users, organizations, roles, and audit records.
- **Identity and access management.** Users sign in with an account and password. Administrators manage members and roles, grant access to menus and actions, and define each role's data access scope.
- **Service integration.** The gateway routes requests to IAM and provides rate limiting and route management. Business services can reuse framework components and look up IAM users through Feign.

## Modules

- `et-platform-iam` provides authentication, tenant administration, and access management.
- `et-platform-gateway` is the shared entry point and forwards `/iam/**` requests to IAM.
- `et-platform-framework` provides shared core, web, MyBatis-Plus, Redis, security, and Feign components.
- `et-platform-feign` groups service client contracts and currently contains `et-platform-iam-api`.
- `et-platform-dependencies` centralizes dependency versions and Maven build configuration.
- `frontend` contains the Vue 3 administration console for the ET Platform services.

## Development and deployment

The project uses JDK 21, Maven, Spring Boot 4.0.3, and Spring Cloud 2025.1.0. Docker Compose starts MySQL, Redis, and Nacos for local development.

The following guides are in Chinese.

- [Project overview](docs/design/project-overview.md)
- [IAM API and usage](et-platform-iam/README.md)
- [Local setup](docs/deployment/local-setup.md)
- [Frontend setup and build](frontend/README.md)
- [Service, dependency, and database upgrades](docs/deployment/upgrade.md)

## Origin and scope

The ET Platform backend was adapted from commit `0618bbd5` on the Wemirr Platform `v4-dev` branch and retains the Apache License 2.0. Thanks to the upstream authors and contributors. The frontend derives from Wemirr Platform UI and retains its MIT license. [Wemirr Platform](https://github.com/battcn/wemirr-platform) · [Baseline commit](https://github.com/battcn/wemirr-platform/commit/0618bbd51e65bafb293282af497ab9e4a1c64564) · [Backend license](LICENSE) · [Frontend source and license](frontend/README.md#来源与许可)

Internal ET applications share sign-in and authorization while keeping each team's data isolated. The project keeps IAM, the gateway, and shared framework components. It removes Suite, workflow, monitoring, AI, warehouse management (WMS), transportation management (TMS), and subscription plan features to reduce how much the team has to deploy and maintain. Each business application can add the capabilities it needs.
