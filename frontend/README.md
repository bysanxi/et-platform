# ET Platform 前端

前端使用 Vue 3、Vben Admin 5、Ant Design Vue 和 FastCrud。应用位于 `apps/web-antd`，提供租户登录、个人资料和改密、租户初始化、组织和用户管理、角色与权限分配、菜单管理、审计日志及网关管理。

## 本地开发

使用 Node.js 22.12 或以上版本，以及项目锁定的 pnpm 10.22.0。先按[后端本地启动指南](../docs/deployment/local-setup.md)启动 IAM 和 Gateway，再从仓库根目录执行以下命令。

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm run dev:antd
```

开发页面默认访问 [http://localhost:5666](http://localhost:5666)。Vite 将 `/api/` 请求转发到本机 Gateway（端口 15000）。Gateway 在其他地址时，通过启动终端的 `ET_GATEWAY_URL` 环境变量设置代理目标。使用初始化 SQL 写入的租户和账号登录，具体信息见后端启动指南。

## 构建

```sh
pnpm -F @vben/web-antd run typecheck
pnpm run build:antd
```

构建产物在 `apps/web-antd/dist`。部署静态文件时，将 `/api/` 请求转发到同一环境的 Gateway，并在转发时去掉 `/api` 前缀。若变更浏览器端客户端配置，应与 IAM 的客户端注册记录对应。

## 来源与许可

迁自 Wemirr Platform UI 的 `v4-dev` 分支提交 `5bb802df0350dd244e14fe025adfa3777240d910`，基于 Vue Vben Admin，沿用 MIT 许可证。[Wemirr Platform UI](https://gitee.com/battcn/wemirr-platform-ui/tree/v4-dev) · [Vue Vben Admin](https://github.com/vbenjs/vue-vben-admin) · [MIT 许可证](LICENSE)

上游资源包括[开发文档](https://docs.battcn.com)、[上游演示](https://cloud.battcn.com/)、QQ群 789517089 和微信 battcn2022。

