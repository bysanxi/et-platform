package cool.et.framework.db.mybatisplus.datascope.core;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import cool.et.framework.commons.security.AuthenticationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * MyBatis-Plus 数据权限处理器
 * <p>
 * 优先级顺序：
 * 1. API方式（DataScope.with(...).execute(...)）
 * 2. 注解方式（@DataScope）
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class DataScopeHandler implements MultiDataPermissionHandler {

    private final AuthenticationContext context;

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 匿名用户不进行数据权限过滤
        if (context.anonymous()) {
            return null;
        }

        // 获取数据权限规则（优先级：API > 注解）
        DataScopeRule rule = resolveRule(mappedStatementId);
        if (rule == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("DataScope processing - statementId: {}, table: {}, ignored: {}",
                    mappedStatementId, table.getName(), rule.isIgnored());
        }

        // 构建SQL条件
        return DataScopeSqlBuilder.build(context, table, rule);
    }

    /**
     * 解析数据权限规则
     * <p>优先从上下文获取（API方式），其次从注解解析</p>
     */
    private DataScopeRule resolveRule(String mappedStatementId) {
        // 1. 优先从线程上下文获取（API方式）
        DataScopeRule contextRule = DataScopeContext.current();
        if (contextRule != null) {
            return contextRule;
        }

        // 2. 从注解解析
        return DataScopeAnnotationParser.parse(mappedStatementId);
    }
}
