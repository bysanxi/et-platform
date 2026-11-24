package cool.et.platform.iam.tenant.service;

import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.tenant.domain.dto.req.TenantSaveReq;
import cool.et.platform.iam.tenant.domain.entity.Tenant;

public interface TenantService extends SuperService<Tenant> {

    /**
     * 保存租户
     *
     * @param req 租户信息
     */
    void create(TenantSaveReq req);

    /**
     * 保存租户
     *
     * @param id  id
     * @param req 租户信息
     */
    void modify(Long id, TenantSaveReq req);

    /**
     * 初始化SQL脚本
     *
     * @param id id
     */
    void initSqlScript(Long id);
}
