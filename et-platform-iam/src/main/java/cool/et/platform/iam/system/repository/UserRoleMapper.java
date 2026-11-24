package cool.et.platform.iam.system.repository;

import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.system.domain.entity.UserRole;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 * @since 2019-07-03
 */

@Repository
public interface UserRoleMapper extends SuperMapper<UserRole> {
    
}
