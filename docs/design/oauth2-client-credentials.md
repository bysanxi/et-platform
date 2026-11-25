# et-platform OAuth 2.0 客户端凭证接入方案

**状态：已实现，默认关闭公开 OAuth 端点。** et-platform 面向多个业务系统，通过 IAM 提供统一的客户端登记、令牌签发和身份解析接口。各业务系统按同一契约接入，使用各自的客户端凭据，并自行决定已验证的调用者可以操作哪些业务资源。

首版采用 OAuth 2.0 的 `client_credentials` 模式。接入方颁发客户端凭据，通过 IAM 内部管理接口登记凭据及可选的用户绑定。管理台使用 Sa-Token 用户会话，内部 Feign 继续使用 Same-Token。[OAuth 2.0 客户端凭证模式](https://www.rfc-editor.org/rfc/rfc6749#section-4.4)

## 一、系统职责与授权范围

**首版业务授权采用一个全量 scope `server`。** 已登记客户端可访问本次开放给集成的 IAM 资源。业务操作暂不细分读写 scope，也不建立逐接口、逐资源的客户端授权清单。全量指集成获得的业务操作范围。用户归属、租户隔离、资源自身的访问规则继续生效。凭据管理走内部 Feign 的 Same-Token 链路。

| 客户端类型 | 注册关系 | IAM 解析结果 |
| --- | --- | --- |
| 绑定用户 | 客户端绑定一个有效的 `user_id` 及其所属 `tenant_code` | 返回客户端、绑定用户、租户，以及该用户当前的权限和数据范围 |
| 纯应用 | `user_id` 为空，可登记所属租户 | 返回客户端及已登记的租户，用户 ID 为空 |

## 二、与现有登录和 Same-Token 共存

当前项目锁定 Sa-Token 1.45.0。IAM 引入同版本的 OAuth 2.0 模块。现行 `POST /iam/token/login` 调用 `StpUtil.login` 签发用户会话。现行 Feign 拦截器在请求模板缺少 `Authorization` 时添加 Same-Token；接收端在受现有 MVC 规则保护的路径上优先校验非空 Same-Token，否则检查用户登录态。[依赖版本](../../et-platform-dependencies/pom.xml) · [现有登录](../../et-platform-iam/src/main/java/cool/et/platform/iam/auth/service/PasswordLoginService.java) · [Feign 发送逻辑](../../et-platform-framework/et-security-spring-boot-starter/src/main/java/cool/et/framework/security/configuration/feign/AutoSaSameConfiguration.java) · [MVC 校验逻辑](../../et-platform-framework/et-security-spring-boot-starter/src/main/java/cool/et/framework/security/configuration/OAuth2AutoConfiguration.java)

首版在 IAM 注册精确的取令牌方法，并为明确向集成客户端开放的资源方法接入 OAuth 校验。IAM 的 `/oauth2/**` 已列入默认会话拦截豁免路径，放在此处的资源方法必须自行强制验证 OAuth Bearer 和 scope。取令牌和身份解析分别由具体的控制器方法处理。Same-Token 单独出现在 OAuth 资源请求上，不能满足 Bearer 要求。[现有豁免配置](../../et-platform-framework/et-security-spring-boot-starter/src/main/java/cool/et/framework/security/configuration/SecurityExtProperties.java)

OAuth 端点签发独立的 client token。用户登录监听器 `WpTokenListener` 依赖数字用户 ID 和密码登录请求中的用户、租户信息。绑定用户的 OAuth 请求在资源入口解析执行身份，向调用方返回该身份与当前权限，无需创建用户登录会话。[IAM 登录监听器](../../et-platform-iam/src/main/java/cool/et/platform/iam/auth/listener/WpTokenListener.java)

## 三、客户端登记与授权变更

IAM 通过内部 Feign 提供客户端登记、查询、改绑、解绑、停用、撤销和密钥轮换接口。管理接口校验 Same-Token，对外网关排除这些内部路径。登记或改绑时，若提供 `user_id`，IAM 校验用户有效且属于指定租户。

注册记录存放在主库 `sys_registered_client`，由 `RegisteredClientMapper` 读写。`client_id` 唯一约束保证同一客户端的登记请求重试时只产生一条记录。[建表脚本](../deployment/mysql/et-platform.sql) · [注册实体](../../et-platform-iam/src/main/java/cool/et/platform/iam/auth/domain/entity/RegisteredClient.java)

| 字段 | 首版约定 |
| --- | --- |
| `client_id`、`grant_types`、`scopes` | 在初始化建表 SQL 中为 `client_id` 定义唯一索引。集成客户端登记 `client_credentials` 和 `server`，按完整项匹配。现有登录客户端不自动获得这项授权 |
| `user_id`、`tenant_code`（可空） | 一个客户端最多绑定一个用户，同一用户可有多个客户端。绑定时校验用户有效且属于该租户。纯应用可只填租户；空租户不代表所有租户 |
| `authz_version` | 凭据对应的授权版本。改绑、解绑、停用、紧急撤销及泄露处置在同一事务内推进版本。常规密钥轮换只替换校验摘要，已有 token 自然到期 |

机器客户端使用独立注册记录。现有密码登录按客户端 ID、密钥和状态查表；接入时明确检查该登录记录允许 `password`，并回归现有 `pc-web` 登录。新增机器密钥只在发放时展示，服务端存校验摘要。1.45.0 的默认 `checkClientSecret` 按字符串相等比较，IAM 对机器客户端使用 BCrypt 摘要校验，认证失败统一返回 `invalid_client`。[Sa-Token 1.45.0 密钥校验](https://raw.githubusercontent.com/dromara/Sa-Token/v1.45.0/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/template/SaOAuth2Template.java)

### 内部管理契约

内部服务通过 `OAuthClientApi` 调用管理操作，由 `OAuthClientFeign` 发送 HTTP 请求。IAM 控制器实现同一业务接口。下表路径相对于 IAM 的 `/internal/oauth2/clients`，各方法都校验 Same-Token。Feign 接收的管理结果包含 `clientId`、`tenantCode`、`userId`、`enabled` 和 `authzVersion`。同一 ID 重复登记会返回已登记错误，调用方可以查询当前记录。[业务接口](../../et-platform-iam-common/src/main/java/cool/et/platform/iam/contract/oauth/OAuthClientApi.java) · [Feign 客户端](../../et-platform-feign/et-platform-iam-api/src/main/java/cool/et/platform/iam/feign/OAuthClientFeign.java)

| 方法 | 相对路径 | 参数或效果 |
| --- | --- | --- |
| POST | 根路径 | JSON 提交 `clientId`、`clientSecret`，可选 `userId` 和 `tenantCode` |
| GET | `/{clientId}` | 查询登记和绑定状态 |
| PUT | `/{clientId}/binding` | JSON 提交 `userId`、`tenantCode`。两者为 null 时清除绑定；仅传租户时转为租户内纯应用 |
| PUT | `/{clientId}/secret` | JSON 提交新的 `clientSecret`，正常轮换沿用授权版本 |
| POST | `/{clientId}/disable` | 停用并推进授权版本 |
| POST | `/{clientId}/revoke` | 停用、清空密钥摘要并推进授权版本；密钥泄露时调用此方法 |

客户端 ID 长度为 1 至 100，使用 ASCII 字母、数字、下划线、短横线、波浪线，非首位也可使用点。密钥非空，按 UTF-8 编码最多 72 字节，以适配 BCrypt。生产接入方应在可信运行环境保存随机生成的密钥，通过 HTTPS 传输。Basic 的两个字段先分别进行表单 URL 编码，再用冒号连接后做 Base64 编码。取令牌的 grant 和 scope 通过表单正文提交。

## 四、OAuth 令牌接口

平台取令牌端点为 **`POST /iam/oauth2/token`**。通过 HTTPS 发送 HTTP Basic 客户端凭据，表单显式传入首版 scope `server`。以下为协议示例，令牌有效期由服务端配置。

~~~http
POST /iam/oauth2/token
Authorization: Basic <编码后的客户端凭据>
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&scope=server
~~~

~~~json
{
  "access_token": "<短期令牌>",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "server"
}
~~~

1. IAM 验证凭据、生效及到期时间、状态、授权类型和申请的 scope。首版要求显式申请 scope。验证申请的 `server` 已登记后签发独立的 client token，并在服务端令牌记录中保存同一次凭据校验对应的客户端记录 ID 和授权版本。令牌记录写入成功后才响应。
2. 成功响应的顶层字段为 `access_token`、`token_type`、`expires_in` 和 `scope`。服务端按配置的有效期校验令牌，过期后调用方可使用有效的客户端凭据重新申请。
3. 访问 OAuth 资源时，调用方通过 `Authorization: Bearer <access_token>` 提交令牌。凭据和令牌通过 HTTPS 传输，错误响应只返回协议错误字段。

IAM 在检查 Basic 凭据后，把同一行登记记录传给签发逻辑。Sa-Token 1.45.0 的转换器生成独立的 client token，IAM 将登记 ID 和授权版本写入服务端扩展字段，调用 OAuth DAO 保存到 Redis。响应只序列化协议字段。取令牌和身份响应均携带 `Cache-Control: no-store` 和 `Pragma: no-cache`。认证失败按协议返回 HTTP 401 和对应的认证挑战。存储或权限服务异常返回 HTTP 500 和 `server_error`。[令牌服务](../../et-platform-iam/src/main/java/cool/et/platform/iam/auth/service/OAuthClientTokens.java) · [HTTP 端点](../../et-platform-iam/src/main/java/cool/et/platform/iam/auth/controller/OAuthClientTokenController.java)

## 五、IAM 身份解析与令牌生命周期

**`GET /iam/oauth2/identity`** 接收待解析的 Bearer token。IAM 返回经验证的客户端 ID、租户、可空的用户 ID，以及该身份的当前权限信息。服务代为查询时应透传原请求的 Bearer；IAM 始终解析实际提交的令牌，忽略调用方自行声明的执行身份。

1. IAM 通过 Sa-Token OAuth 的 client-token DAO 读取 Bearer 记录，校验到期时间、grant 和实际 scope，再从主库核对注册记录 ID、有效期、启用状态和授权版本。令牌或注册存储不可用时终止身份解析。
2. 有用户绑定时，在登记的租户内校验用户状态及归属，复用 `UserService.userinfo(UserTenantAuthentication)` 计算当前功能权限和数据范围。用户不存在、被禁用或解析失败时拒绝。纯应用身份仅在成功读取注册记录且 `user_id` 为空时建立。[用户权限组装](../../et-platform-iam/src/main/java/cool/et/platform/iam/system/service/impl/UserServiceImpl.java)

| 变更 | 新的换票请求 | 已签发 token |
| --- | --- | --- |
| 改绑或解绑 | 按新绑定换票 | 变更提交后立即失效，不能切换到另一位用户，也不能退回纯应用继续执行 |
| 停用客户端、撤销凭据或报告密钥泄露 | 停用或撤销的凭据立即停止换票；泄露后用安全渠道重新发放 | 立即失效，包括与变更并发、基于旧凭据或旧绑定签发的 token |
| 调整用户角色或缩小数据范围 | 绑定关系沿用 | token 可继续使用，从下一个请求起执行新的业务权限。资源端按请求获取当前权限，不能把权限快照缓存到 token 到期 |
| 常规轮换密钥 | 新密钥可以换票，旧密钥停止换票 | 仍按原到期时间有效；轮换由泄露触发时按密钥泄露规则处理 |

## 六、启用与验收

**首版使用新的初始化 SQL 建库。** IAM 配置如下，单位为秒，令牌默认存活 3600 秒。

~~~properties
extend.security.enabled-oauth2=true
extend.security.server.client-token-ttl-seconds=3600
~~~

默认关闭公开取令牌与身份解析端点。已有管理台登录、内部 Same-Token 调用以及凭据登记可以独立使用。联调可将 TTL 调低至 2 至 30 秒以验证真实到期。用途为查看集成调用身份时，读取身份响应中的 `roles`、`funcPermissions` 和 `dataPermission`。纯应用的两个权限列表为空，数据范围及用户 ID 为 null。

| 验收项 | 通过条件 |
| --- | --- |
| 发放与登记 | 从初始化库启动，受信任的内部调用方使用有效 Same-Token 可完成首次登记。管理接口拒绝缺失或无效的 Same-Token，也拒绝仅携带普通 `server` Bearer 的请求。个人注册校验租户和用户，纯应用可不传用户 |
| 协议与全量授权 | 标准客户端可直接读取顶层 `access_token` 和 `scope=server`。Basic 凭据错误返回 401 和 `invalid_client`，并带 Basic 认证挑战；不支持的 scope 返回 400 `invalid_scope`，其他授权类型返回 400 `unsupported_grant_type` |
| 身份接口 | 以有效的 Bearer 调用 `GET /iam/oauth2/identity` 可取得该令牌对应的身份；缺失、过期或已撤销的令牌返回 401。请求中另传用户或租户参数不能改变解析结果 |
| 两种身份 | 两个绑定不同用户的客户端分别返回各自的用户、租户和权限。纯应用返回自身客户端 ID 和空用户 ID。已绑定用户被禁用或绑定读取失败时拒绝请求 |
| 撤销及轮换 | 验证改绑、解绑、停用、泄露及并发签发时旧 token 立即失效。常规轮换后旧密钥无法换票、原 token 可用至到期。用户权限缩小后，同一有效 token 的下一次调用执行新权限 |
| 既有链路 | 管理台登录及租户上下文正常。内部 Feign 继续接受有效 Same-Token、拒绝无效 Same-Token；仅凭 Same-Token 不能访问要求 OAuth Bearer 的身份接口 |

### 回归测试

JDK 21 下在仓库根目录运行 `mvn -DskipTests=false package`。常规测试覆盖撤销或改绑与换票交错执行时的授权版本，以及权限查询失败时的拒绝行为。

[真实链路测试](../../et-platform-iam/src/test/java/cool/et/platform/iam/auth/OAuthClientCredentialsIT.java) 使用独立初始化库和运行中的 IAM、Gateway、Nacos、Redis。测试进程与 IAM 使用同一测试库、Redis 和 Nacos 命名空间。设置 `ET_OAUTH_IT_GATEWAY` 和 `ET_OAUTH_IT_IAM` 为对应服务的根 URL，以 `mvn -pl et-platform-iam -am -DskipTests=false -Dsurefire.failIfNoSpecifiedTests=false -Dtest=OAuthClientCredentialsIT test` 运行。验证真实过期时将 IAM 的 TTL 设为 2 至 30 秒。测试会登记临时客户端，并使用初始化脚本中的两个租户和演示账号。

## 附：业务场景示例

例如，文档管理系统为编辑员颁发客户端凭据，并在 IAM 中将凭据绑定到该编辑员的账号和租户。编辑工具用凭据向 IAM 换取令牌，携带令牌向文档管理系统提交修改。文档管理系统调用 IAM 解析编辑员身份，确认其有权修改目标文档后保存内容。
