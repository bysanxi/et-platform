# 本地启动

先运行 `mvn -version`，确认 Maven 使用 JDK 21。如果显示 Java 17，在当前终端将 `JAVA_HOME` 指向 JDK 21 的安装目录，并将其 `bin` 目录放在 `PATH` 首位。使用 IntelliJ IDEA 时，将 Project SDK 设为 JDK 21，并在 Maven Runner 中选择 Project SDK。

在仓库根目录构建并启动 MySQL、Redis 和 Nacos。首次使用空 MySQL 数据卷时，Compose 会创建 `et_platform` 数据库并执行 [初始化 SQL](mysql/et-platform.sql)。

```shell
mvn -DskipTests package
docker compose -f docs/deployment/docker/docker-compose.yml up -d
```

在仓库根目录打开两个终端，分别启动 IAM 和 Gateway。

```shell
java -jar et-platform-iam/target/et-platform-iam.jar
```

```shell
java -jar et-platform-gateway/target/et-platform-gateway.jar
```

Gateway 默认监听 `15000`，将 `/iam/**` 转发到 IAM。IAM 默认监听 `15002`。开发环境预置平台租户 `0000` 和内部团队租户 `8888`。两者的演示账号均为 `admin`，初始密码为 `123456`。部署到实际团队环境时应更换演示凭据。

服务包含本地开发配置。使用配置中心时，可将 [Nacos 配置包](nacos/et-platform.zip) 导入两个服务共用的 Nacos 命名空间。支持通过环境变量设置 `NACOS_ADDR`、`NACOS_NAMESPACE`、`ET_DB_URL`、`ET_DB_USER`、`ET_DB_PASSWORD`、`ET_REDIS_HOST`、`ET_REDIS_PORT` 以及 `SPRING_DATA_REDIS_PASSWORD`。
