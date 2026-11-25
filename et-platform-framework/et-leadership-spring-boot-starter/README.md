# Redis 分布式选主

需要多个实例共同竞争一项任务时，引入 `cool.et.framework:et-leadership-spring-boot-starter`。使用平台依赖管理时可省略版本。各实例连接同一组 Redis 节点，使用相同的 `lockId` 和各自的 `leaderId`。

```yaml
extend:
  coordination:
    redis:
      mode: single
      servers: 127.0.0.1:6379
      leadership-ttl-in-milliseconds: 10000
```

`servers` 支持逗号分隔的节点地址。单机模式取第一个节点，`cluster` 模式填写集群节点，`sentinel` 模式填写哨兵节点并通过 `master` 指定主节点名称。需要认证时设置 `password`，哨兵模式使用同一密码访问哨兵和数据节点。选主使用独立的 Redis 连接，原配置前缀迁移为 `extend.coordination.redis`。

在业务配置类中声明选择器，Spring 会在启动和关闭时调用对应方法。

```java
@Bean(initMethod = "start", destroyMethod = "stop")
LeaderSelector taskLeader(LeaderSelectorFactory factory, TaskConsumer consumer) {
    return factory.create("orders:task:leader", UUID.randomUUID().toString(),
            leadership -> consumer.start(), consumer::stop);
}
```

两个选主接口位于 `cool.et.framework.coordination.leadership.common`。当选回调返回后继续持有领导权。调用回调参数的 `leadership.stop()` 可结束本轮任期并重新参与竞争，调用选择器的 `stop()` 则退出竞争。阻塞式当选回调需要响应线程中断，失去领导权的回调负责停止当选时启动的业务任务。

Redisson 自动续租，租期默认 10 秒。进程异常退出后其他实例可在锁过期后接管。Redis 故障切换或进程长暂停时，业务侧仍应按任务需要处理重复执行。
