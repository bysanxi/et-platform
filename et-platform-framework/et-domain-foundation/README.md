# et-domain-foundation

业务模块可以通过 Maven 引入领域模型和接口 DTO 的公共基类。使用平台 BOM 时可省略版本。

```xml
<dependency>
    <groupId>cool.et.framework</groupId>
    <artifactId>et-domain-foundation</artifactId>
</dependency>
```

- `cool.et.framework.foundation.domain` 提供实体标识、变更状态、并发版本字段、值对象和断言方法。
- `cool.et.framework.foundation.dto` 提供命令、查询、分页参数及返回类型。`MultiResult` 和 `PageResult` 复用平台的 `Result` 响应结构。
