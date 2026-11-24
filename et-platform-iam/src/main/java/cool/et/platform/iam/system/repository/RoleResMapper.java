package cool.et.platform.iam.system.repository;

import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.system.domain.entity.RoleRes;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色资源
 */

@Repository
public interface RoleResMapper extends SuperMapper<RoleRes> {
    
    /**
     * 查询租户管理员资源
     *
     * @return 查询结果
     */
    List<Long> selectTenantAdminResIdList();
}
