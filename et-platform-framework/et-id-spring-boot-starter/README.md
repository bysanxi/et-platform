# 分布式 ID

Spring Boot 服务引入 `cool.et.framework:et-id-spring-boot-starter` 后，注入 `cool.et.framework.id.IdGenerator`，调用 `next()` 得到 long 型 ID。MyBatis-Plus 在类路径上时，自动主键与手工发号共用这个实例。应用自定义的 `IdentifierGenerator` 优先。纯 Java 应用可依赖 `et-framework-core` 并自行创建 `IdGenerator`。

配置前缀为 `extend.id.generator`。从 hisi 迁移时，请将配置中心或环境中 `hisi.id.generator` 下已有的设置原值迁至新前缀，再启动服务。同一 ID 空间内并行发号的实例必须使用不同的 `worker-id`。切换实例时应避免新旧进程同时使用同一节点号。

| 配置项 | 默认值 | 用途 |
| --- | --- | --- |
| method | 1 | 1 为漂移算法，2 为传统雪花算法 |
| base-time | 1582136402000 | 毫秒时间基准 |
| worker-id | 0 | 实例节点号 |
| worker-id-bit-length | 6 | 节点位数 |
| seq-bit-length | 6 | 毫秒内序列位数 |
| min-seq-number | 5 | 正常发号的起始序号，0–4 为保留位 |
| max-seq-number | 0 | 0 表示使用序列位可表示的最大值 |
| top-over-cost-count | 2000 | 漂移算法的最大漂移次数 |

ID 从高位到低位依次为距基准时间的毫秒数、节点号和序列号。迁移时应沿用原时间基准和位宽。
