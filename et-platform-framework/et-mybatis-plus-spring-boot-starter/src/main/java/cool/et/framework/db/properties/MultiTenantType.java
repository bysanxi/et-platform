package cool.et.framework.db.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 多租户类型
 *
 * @since 2018/11/20
 */
@Getter
@AllArgsConstructor
public enum MultiTenantType {

    COLUMN("字段模式");

    private final String description;
}
