# et-platform 项目命名规则

适用于本仓库自有 Maven 模块、Java 代码和服务配置。新增模块时先确定它提供的是可运行服务、业务 API 契约、基础库还是 Spring Boot 自动配置，再按下列约定命名。

## Maven 模块

- 模块目录名与 POM 的 `artifactId` 相同。名称以 `et-` 开头，使用小写英文和连字符，例如 `et-platform-gateway`。
- 平台级模块采用 `et-platform-<职责>`，如 `et-platform-dependencies`、`et-platform-framework`、`et-platform-feign`、`et-platform-iam` 和 `et-platform-gateway`。业务服务的 Feign 契约采用 `et-platform-<服务>-api`，例如 `et-platform-feign/et-platform-iam-api`。
- 自有 Maven 坐标以 `cool.et` 为根。根项目、依赖管理、IAM、Gateway 和 IAM API 使用 `cool.et`。框架父模块及其子模块使用 `cool.et.framework`，Feign 聚合 POM 使用 `cool.et.feign`。引用依赖时，以被引用模块自身声明或继承的 `groupId` 为准。

## 框架子模块

在 `et-platform-framework/` 下，可独立使用的基础类型放入 `et-framework-core`。提供 Spring Boot 自动配置的模块采用 `et-<能力>-spring-boot-starter`。`<能力>` 写实际职责或技术名，如 `redis`、`security`、`feign`、`mybatis-plus`。

- `et-framework-core` 提供基础实体、异常和工具类。
- `et-platform-spring-boot-starter` 是业务服务的统一接入依赖，聚合 Web、数据库、Redis、安全与 Feign 能力。
- `et-mybatis-plus-spring-boot-starter` 提供持久层、多租户隔离和数据权限。
- `et-redis-spring-boot-starter` 提供 Redis 缓存、分布式锁和限流。
- `et-security-spring-boot-starter` 提供认证和鉴权集成。
- `et-feign-spring-boot-starter` 提供 Feign 调用增强。IAM 的业务接口与传输对象放在 `et-platform-iam-api`。

## Java 包

自有源码和测试使用 `cool.et` 根包，目录层级与 `package` 声明逐段对应。框架代码位于 `cool.et.framework`，服务代码位于 `cool.et.platform.<服务>`，业务 Feign 契约跟随目标服务。以 IAM 为例，启动类为 `cool.et.platform.iam.IamApplication`，客户端接口为 `cool.et.platform.iam.feign.UserFeign`。

## 服务发现与配置

可运行服务的 `spring.application.name`、Nacos 注册名与该服务的 `artifactId` 一致。`@FeignClient` 的 `name` 和网关的 `lb://` 目标引用注册名。IAM 的注册名是 `et-platform-iam`，网关目标写作 `lb://et-platform-iam`，对外 HTTP 路径为 `/iam/**`。

服务专属配置使用 `<spring.application.name>.properties`。本地文件、Nacos Data ID、导入包内的文件名及其 `.metadata.yml` 条目要一致，例如 `et-platform-iam.properties`。共享配置沿用各自的 Data ID，例如 `redis.properties`。

## 重命名时核对

1. 改模块名时，同步修改目录、`artifactId`、聚合 POM 的 `<module>`、父 POM 引用、依赖管理和调用方依赖，更新受影响的 IDE 配置与文档示例。
2. 改 Java 包时，同步移动主源码和测试目录，并检查 `import`、启动类、Spring 和 Feign 扫描路径、`AutoConfiguration.imports`、MyBatis XML 命名空间、类型名和 Mapper 资源路径。
3. 改服务名时，同时核对 Feign 目标、网关路由、接口文档聚合配置，以及本地配置与 Nacos 导入包。构建并验证通过网关访问目标服务。
4. 从仓库根目录运行 `mvn clean package -DskipTests=false`，检查受影响模块的 JAR 名称、Maven 坐标和应用包内依赖。
