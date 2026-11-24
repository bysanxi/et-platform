# et-platform 项目规则索引

本文件是 Codex、Cursor 等编码工具在本仓库的共同入口。提出方案、修改或评审代码前，按目标路径和任务内容打开对应的规则正文。跨模块任务合并读取命中的规则。系统、开发者和用户的显式指令优先于仓库规则。

## 按任务加读

- 新增、移动或重命名 Maven 模块、框架组件、Java 包、服务或 Feign 契约，或修改引用这些名称的 POM、Spring 配置、MyBatis 映射、网关路由及 Nacos 配置时，先读[项目命名规则](rules/general/et-platform-naming.md)。

## 文档分层

- 根 `AGENTS.md` 维护任务范围和阅读入口，长期适用的规则正文放在 `rules/`，每条规则只维护一份。
- 根 [README](README.md) 提供项目概览、启动和升级入口。各模块的 README 说明本模块的用途和用法。
- 跨模块设计与方案放在 `docs/design/`，部署与运维文档放在 `docs/deployment/`。
