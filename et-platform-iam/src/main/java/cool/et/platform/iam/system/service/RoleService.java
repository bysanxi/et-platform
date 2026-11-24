package cool.et.platform.iam.system.service;

import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.system.domain.dto.req.RoleSaveReq;
import cool.et.platform.iam.system.domain.dto.resp.RoleDetailResp;
import cool.et.platform.iam.system.domain.dto.resp.RolePermissionResp;
import cool.et.platform.iam.system.domain.entity.Role;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 角色
 * </p>
 *
 * @since 2019-07-03
 */
public interface RoleService extends SuperService<Role> {

    /**
     * 根据 scope 查询角色
     *
     * @return 查询结果
     */
    @Override
    List<Role> list();

    /**
     * 根据角色ID移除
     *
     * @param roleId roleId
     */
    void removeByRoleId(Long roleId);

    /**
     * 1，保存角色
     *
     * @param req req
     */
    void create(RoleSaveReq req);

    /**
     * 修改角色
     *
     * @param roleId 角色ID
     * @param req    req
     */
    void modify(Long roleId, RoleSaveReq req);

    /**
     * 给角色分配用户
     *
     * @param roleId     roleId
     * @param userIdList userIdList
     */
    void assignUser(Long roleId, List<Long> userIdList);

    /**
     * 根据角色ID查询资源码
     *
     * @param roleId 角色ID
     * @return 查询结果
     */
    RolePermissionResp findRolePermissionById(Long roleId);

    /**
     * 角色详情
     * @param id id
     * @return 查询结果
     */
    RoleDetailResp detail(Long id);

}
