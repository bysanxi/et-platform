package cool.et.platform.iam.system.service;

import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.system.domain.dto.req.OrgSaveReq;
import cool.et.platform.iam.system.domain.entity.Org;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 组织
 * </p>
 *
 * @since 2019-07-22
 */
public interface OrgService extends SuperService<Org> {
    
    /**
     * 批量删除以及删除其子节点
     *
     * @param id id
     */
    void remove(Long id);
    
    /**
     * 添加组织
     *
     * @param req req
     */
    void create(OrgSaveReq req);
    
    /**
     * 获取当前机构本级及子级的ID
     *
     * @param id id
     * @return 本级及子级ID
     */
    List<Long> getFullTreeIdPath(Long id);
}
