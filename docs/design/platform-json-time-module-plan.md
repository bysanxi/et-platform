# 平台 JSON 日期时间的读写约定

## 默认格式

`et-framework-core` 中的 `JacksonUtils.createJavaTimeModule()` 为每次调用创建一个标准 Jackson 2 `JavaTimeModule`。平台 Web Mapper 和工具 Mapper 各取一个模块，使用 `TimeConstants` 中的格式成对注册本地日期时间的序列化器和反序列化器。

| Java 类型 | 平台 Web Mapper | JacksonUtils 工具 Mapper |
| --- | --- | --- |
| `LocalDate` | `yyyy-MM-dd` | `yyyy-MM-dd` |
| `LocalTime` | `HH:mm:ss` | `HH:mm:ss` |
| `LocalDateTime` | `yyyy-MM-dd HH:mm:ss` | `yyyy-MM-dd HH:mm:ss` |
| `Instant` | ISO-8601，保留小数秒 | 按默认时区写出本地时间，精确到秒；可读本地秒级文本和 ISO-8601 |
| `java.util.Date` | 由 `spring.jackson.date-format` 指定，默认 `yyyy-MM-dd HH:mm:ss` | `yyyy-MM-dd HH:mm:ss` |

例如，在默认时区为 `Asia/Shanghai` 时，同一 `Instant` 在 Web Mapper 中写为 `"2026-11-28T05:28:22.257Z"`，在工具 Mapper 中写为 `"2026-11-28 13:28:22"`。工具 Mapper 按进程默认时区解释无偏移的历史文本；读回这段文本得到 `2026-11-28T05:28:22Z`，精度为秒。

## 模块注册与接入

此前自定义模块与标准时间模块使用了相同的模块 ID。Jackson 跳过了后注册的标准模块，导致显式配置的本地日期时间可以写出，`Instant` 的标准处理器却未生效。现行实现把本地格式配置在标准模块上。每个 Mapper 注册一次时间模块，工具 Mapper 在注册前添加自己的 `Instant` 处理器。

接入系统如需单独约定持久化格式，可为专用 Mapper 命名，并在读写组件上通过 `@Qualifier` 显式注入。升级时，用已有数据核对时间精度、时区以及参与摘要计算的 JSON 文本。对于 HTTP 和 Feign，核对实际选用的消息转换器；Spring 容器中存在某个 Mapper 本身无法证明传输时使用了它。

## 平台验证

2026-11-28，`PlatformJsonTimeTest` 的三项测试通过，覆盖两类 Mapper 的本地日期时间往返、Web 的毫秒精度、工具 Mapper 对历史文本及 ISO-8601 的读取，以及 `spring.jackson.date-format` 对 `java.util.Date` 的作用。另通过实际 MVC 写转换器和 Feign 读转换器，核对了含大整数标识符和时间字段的 DTO 往返结果。
