package cool.et.framework.commons.security;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPermission implements java.io.Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 权限范围
     */
    @Builder.Default
    private DataScopeType scopeType = DataScopeType.IGNORE;
    
    /**
     * 角色ID列表（用于 EXISTS 子查询）
     */
    private List<Long> roleIds;
    
    /**
     * 用户当前机构ID（用于本级/本级及子级权限）
     */
    private Long orgId;
    
    /**
     * 多维度数据权限（小数据量时使用 IN 列表，大数据量时使用 EXISTS 子查询）
     * <p>
     * 优化策略：
     * - 数据量 < 1000：使用此 Map 构建 IN 列表
     * - 数据量 >= 1000：使用 roleIds 构建 EXISTS 子查询
     * </p>
     */
    @Builder.Default
    private Map<DataResourceType, List<Object>> dataPermissionMap = Maps.newHashMap();
    
    /**
     * 判断是否应该使用 EXISTS 子查询
     */
    public boolean shouldUseExistsQuery(DataResourceType resourceType) {
        List<Object> values = dataPermissionMap.get(resourceType);
        return values != null && values.size() >= 1000;
    }
    
}
