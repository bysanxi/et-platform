# IAM 共享协议

`cool.et:et-platform-iam-common` 定义 IAM 服务端与调用方共用的 Java 业务接口、请求、返回值和参数校验规则。

- [UserAccountApi](src/main/java/cool/et/platform/iam/contract/account/UserAccountApi.java) 定义账户查询、注册、密码重置和短信登录。
- [OAuthClientApi](src/main/java/cool/et/platform/iam/contract/oauth/OAuthClientApi.java) 定义 OAuth 客户端登记、查询、绑定、密钥轮换、停用和撤销。
- [UserApi](src/main/java/cool/et/platform/iam/contract/user/UserApi.java) 定义按 ID 查询用户、按一组 ID 批查用户和分页查询。单查、批查和分页记录共用 `UserInfoResp`，批查结果以 `Long` 型用户 ID 为键。
- [UserPageReq](src/main/java/cool/et/platform/iam/contract/user/UserPageReq.java) 是 `POST /users/page` 的共享请求，支持用户名、昵称、邮箱、性别编码、组织、手机号和状态筛选，以及 `current`、`size`、`column`、`asc` 分页排序参数。

业务代码可以直接注入上述业务接口。使用 HTTP 调用时，在应用的启动模块引入 `cool.et:et-platform-iam-api` 并注册对应的 Feign 客户端。IAM Controller 和 Feign 客户端各自声明 HTTP 映射。

登录响应 [LoginResp](src/main/java/cool/et/platform/iam/contract/account/LoginResp.java) 与 `UserAccountApi` 同包。升级时，请将 Java 调用方的导入更新为 `cool.et.platform.iam.contract.account.LoginResp` 并重新编译。

已接入拆分前版本的 Java 调用方需要重新编译，并将显式导入的嵌套 DTO 改到其声明接口，例如 `cool.et.platform.iam.contract.account.UserAccountApi.Lookup` 和 `cool.et.platform.iam.contract.oauth.OAuthClientApi.Registration`。

原先使用 `UserFeignPageReq` 的调用方请改用 `cool.et.platform.iam.contract.user.UserPageReq` 并重新编译。默认查询第 1 页、每页 10 条；需要 20 条时显式传入 `size=20`。旧请求的 `startTime`、`endTime` 从未参与 IAM 查询，迁移时可移除。

用户查询的 `UserInfoResp` 和分页返回类型 `UserPage` 都位于 `cool.et.platform.iam.contract.user`。原先显式使用 MyBatis-Plus `Page` 的调用方请改用 `UserPage`，可继续用 `getRecords()`、`getTotal()` 等方法读取分页数据。字段回显可以使用 `@Remote(beanClass = UserApi.class)`，已使用 `UserFeign.class` 的注解也可继续使用。注册 `UserFeign` 后，客户端模块会自动提供对应的 `LoadService`。
