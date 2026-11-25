package cool.et.framework.db.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.db.mybatisplus.audit.AuditInterceptor;
import cool.et.framework.db.mybatisplus.datascope.core.DataScopeHandler;
import cool.et.framework.db.mybatisplus.handler.MyBatisMetaObjectHandler;
import cool.et.framework.db.mybatisplus.injector.MySqlInjector;
import cool.et.framework.db.properties.DatabaseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Mybatis 常用重用拦截器
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 * @since 2018/10/24
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class BaseMybatisConfiguration {

    private final DatabaseProperties properties;
    private final AuthenticationContext context;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false
     * 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    @Order(5)
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        final DatabaseProperties.MultiTenant multiTenant = properties.getMultiTenant();
        // 新增多租户拦截器
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                // 租户ID
                log.debug("当前租户ID - {}", context.tenantId());
                return context.tenantId() == null ? null : new LongValue(context.tenantId());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                final List<String> tables = multiTenant.getIncludeTables();
                // 判断哪些表不需要进行多租户判断,返回false表示都需要进行多租户判断
                return context.anonymous() || !tables.contains(tableName);
            }

            @Override
            public String getTenantIdColumn() {
                return multiTenant.getTenantIdColumn();
            }

        }));
        // 加载其它插件
        loadInnerInterceptor(interceptor);
        return interceptor;
    }

    /**
     * mybatis-plus 分页插件
     *
     * @param pagination 参数配置
     * @return 插件
     */
    public PaginationInnerInterceptor paginationInnerInterceptor(final DatabaseProperties.Pagination pagination) {
        // 新增MYSQL分页拦截器,一定要先设置租户判断后才进行分页拦截设置
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(pagination.getDbType());
        paginationInnerInterceptor.setOverflow(pagination.isOverflow());
        paginationInnerInterceptor.setDialect(pagination.getDialect());
        return paginationInnerInterceptor;
    }

    protected void loadInnerInterceptor(MybatisPlusInterceptor interceptor) {
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        final DatabaseProperties.Intercept intercept = properties.getIntercept();
        if (properties.getDataPermission().isEnabled()) {
            // 分页拦截器之前的插件 => 数据权限插件
            interceptor.addInnerInterceptor(new DataPermissionInterceptor(new DataScopeHandler(context)));
        }
        if (intercept.isBlockAttack()) {
            // 防止全表更新与删除插件: BlockAttackInnerInterceptor
            interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        }
        if (properties.getAudit().isEnabled()) {
            interceptor.addInnerInterceptor(new AuditInterceptor(properties.getAudit()));
        }
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor(intercept.getPagination()));
    }

    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getMySqlInjector() {
        return new MySqlInjector();
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new MyBatisMetaObjectHandler(context);
    }
}
