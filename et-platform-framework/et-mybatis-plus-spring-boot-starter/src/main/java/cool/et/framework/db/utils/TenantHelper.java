package cool.et.framework.db.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.db.properties.DatabaseProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * 租户工具类
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantHelper {

    /**
     * 获取多租户配置
     */
    private static DatabaseProperties.MultiTenant getMultiTenant() {
        return SpringUtil.getBean(DatabaseProperties.class).getMultiTenant();
    }

    /**
     * 判断当前用户是否为超级租户
     */
    public static boolean isSuperTenant() {
        var context = SpringUtil.getBean(AuthenticationContext.class);
        var tenantCode = context.tenantCode();
        return StringUtils.isNotBlank(tenantCode) && StringUtils.equals(tenantCode, getMultiTenant().getSuperTenantCode());
    }

    // ==================== 主数据源执行 ====================

    /**
     * 使用主数据源执行（有返回值）
     *
     * @param supplier 待执行的动作
     * @param <T>      返回类型
     * @return 执行结果
     */
    public static <T> T executeWithMaster(Supplier<T> supplier) {
        try {
            DynamicDataSourceContextHolder.push(getMultiTenant().getDefaultDsName());
            return supplier.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 使用主数据源执行（无返回值）
     *
     * @param runnable 待执行的动作
     */
    public static void runWithMaster(Runnable runnable) {
        executeWithMaster(() -> {
            runnable.run();
            return null;
        });
    }

    // ==================== 租户数据源执行 ====================

    /**
     * 使用指定租户 DB 执行（有返回值）
     *
     * @param tenantCode 租户编码
     * @param supplier   待执行的动作
     * @param <T>        返回类型
     * @return 执行结果
     */
    public static <T> T executeWithTenantDb(String tenantCode, Supplier<T> supplier) {
        return executeWithMaster(supplier);
    }

    /**
     * 使用指定租户 DB 执行（无返回值）
     *
     * @param tenantCode 租户编码
     * @param runnable   待执行的动作
     */
    public static void runWithTenantDb(String tenantCode, Runnable runnable) {
        executeWithTenantDb(tenantCode, () -> {
            runnable.run();
            return null;
        });
    }

    // ==================== 忽略策略执行 ====================

    /**
     * 忽略租户策略执行（有返回值）
     *
     * @param block 待执行的动作
     * @param <T>   返回类型
     * @return 执行结果
     */
    public static <T> T withIgnoreStrategy(Supplier<T> block) {
        return withIgnoreStrategy(IgnoreStrategy.builder().tenantLine(true).build(), block);
    }

    /**
     * 忽略指定策略执行（有返回值）
     *
     * @param strategy 忽略策略
     * @param block    待执行的动作
     * @param <T>      返回类型
     * @return 执行结果
     */
    public static <T> T withIgnoreStrategy(IgnoreStrategy strategy, Supplier<T> block) {
        try {
            InterceptorIgnoreHelper.handle(strategy);
            return block.get();
        } finally {
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }
    }

    /**
     * 忽略租户策略执行（无返回值）
     *
     * @param runnable 待执行的动作
     */
    public static void runWithIgnoreStrategy(Runnable runnable) {
        runWithIgnoreStrategy(IgnoreStrategy.builder().tenantLine(true).build(), runnable);
    }

    /**
     * 忽略指定策略执行（无返回值）
     *
     * @param strategy 忽略策略
     * @param runnable 待执行的动作
     */
    public static void runWithIgnoreStrategy(IgnoreStrategy strategy, Runnable runnable) {
        withIgnoreStrategy(strategy, () -> {
            runnable.run();
            return null;
        });
    }
}
