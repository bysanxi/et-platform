package cool.et.platform.iam.system.service;

import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.system.domain.dto.resp.UserRoleResp;
import cool.et.platform.iam.system.domain.entity.UserRole;

/**
 * <p>
 * 业务接口
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 * @since 2019-07-03
 */
public interface UserRoleService extends SuperService<UserRole> {
    
    /**
     * 根据劫色查询用户
     *
     * @param roleId 角色id
     * @return 查询结果
     */
    UserRoleResp findUserByRoleId(Long roleId);
    
}
