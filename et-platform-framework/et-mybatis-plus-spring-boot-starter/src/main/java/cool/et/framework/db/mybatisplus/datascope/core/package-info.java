/**
 * 数据权限核心包
 *
 * <h2>使用方式</h2>
 *
 * <h3>1. 注解方式</h3>
 * <pre>{@code
 * // 在Mapper方法上添加注解
 * @DataScope(columns = @DataColumn(alias = "u", name = "created_by"))
 * List<User> selectList();
 *
 * // 忽略数据权限
 * @DataScope(ignore = true)
 * List<User> selectAll();
 * }</pre>
 *
 * <h3>2. API方式（推荐）</h3>
 * <pre>{@code
 * // 使用默认规则
 * List<User> users = DataScope.run(() -> userMapper.selectList(null));
 *
 * // 忽略数据权限
 * List<User> allUsers = DataScope.skip(() -> userMapper.selectList(null));
 *
 * // 自定义规则
 * List<Order> orders = DataScope.column("org_id").execute(() -> orderMapper.selectList(null));
 *
 * // 组织机构权限
 * List<Order> orgOrders = DataScope.org().execute(() -> orderMapper.selectList(null));
 *
 * // 只看自己的数据
 * List<Task> myTasks = DataScope.self().execute(() -> taskMapper.selectList(null));
 *
 * // 复杂规则配置
 * DataScope.with(rule -> rule
 *         .column(Column.builder()
 *             .alias("t")
 *             .name("department_id")
 *             .resourceType(DataResourceType.ORG)
 *             .build()))
 *     .execute(() -> mapper.selectList(null));
 * }</pre>
 *
 * <h3>3. 嵌套调用</h3>
 * <pre>{@code
 * DataScope.run(() -> {
 *     List<User> users = userMapper.selectList(null);
 *     // 嵌套忽略权限
 *     DataScope.skip(() -> {
 *         // 这里不会应用数据权限过滤
 *         return allDataMapper.selectList(null);
 *     });
 *     return users;
 * });
 * }</pre>
 *
 * @see cool.et.framework.db.mybatisplus.datascope.core.DataScope
 * @see cool.et.framework.db.mybatisplus.datascope.core.DataScopeRule
 */
package cool.et.framework.db.mybatisplus.datascope.core;
