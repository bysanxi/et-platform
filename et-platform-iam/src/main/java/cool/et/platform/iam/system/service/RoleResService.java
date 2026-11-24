package cool.et.platform.iam.system.service;

import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.system.domain.dto.req.RoleResSaveReq;
import cool.et.platform.iam.system.domain.entity.RoleRes;

/**
 * <p>
 * 业务接口
 * 角色的资源
 * </p>
 *
 * @since 2019-07-03
 */
public interface RoleResService extends SuperService<RoleRes> {
    
    /**
     * 给角色重新分配 权限（资源/菜单）
     *
     * @param req req
     */
    void assignResource(RoleResSaveReq req);
}
