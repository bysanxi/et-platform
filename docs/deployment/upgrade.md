# 部署升级

## 服务与依赖升级

已有部署需将 IAM 和 Gateway 的自定义配置分别迁移到 `et-platform-iam.properties` 和 `et-platform-gateway.properties`。如有自定义网关路由指向 IAM，将目标地址改为 `lb://et-platform-iam`。迁移完成后重启两个服务。

使用 `/et/**` 业务路径的部署，在网关的 `NACOS_NAMESPACE`（默认 `et-platform`）中发布 `DEFAULT_GROUP/gateway-route.yaml`。首次部署可从 [Nacos 配置包](nacos/et-platform.zip) 导入。升级时以[路由配置](nacos/gateway-route.yaml)为参考，合入环境已有的自定义规则。远程 `spring.cloud.gateway.server.webflux.routes` 会整体替换包内路由列表，请保留新版 `/iam/**` 规则及该环境使用的业务路由。

升级网关后，确认服务发现的命名空间中存在对应的健康实例。例如 `/et/analysis/web/analysis/task/page` 去掉前两段后转发到 `et-stock-analysis-service` 的 `/web/analysis/task/page`。没有远程路由配置时，网关使用包内的 IAM 路由。

依赖 IAM Feign 或框架组件的项目也需更新 Maven 坐标和 Java 导入，并重新编译。

## 已有数据库升级

`t_gateway_route` 是旧版路由表。网关的动态路由使用 Redis，新环境的初始化脚本只创建 IAM 所需的表。

从上一版 ET 升级时，执行 [IAM 菜单升级脚本](mysql/upgrades/retire-optional-iam-menus.sql)。它停用 29 个退役的内置菜单、目录和按钮以及对应授权，保留原有业务表和数据。目录下有自定义有效子项时，脚本会保留该目录。脚本可重复执行。

更早版本还需先执行 [导出与多语言菜单升级脚本](mysql/upgrades/retire-export-and-i18n-menus.sql)。使用 Nacos 覆盖 IAM 配置的部署需同步更新 [配置包](nacos/et-platform.zip)。升级后重新登录以刷新会话权限。新环境直接使用 [初始化 SQL](mysql/et-platform.sql)。
