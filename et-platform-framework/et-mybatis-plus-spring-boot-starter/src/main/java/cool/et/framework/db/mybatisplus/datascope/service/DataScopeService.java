package cool.et.framework.db.mybatisplus.datascope.service;

import cool.et.framework.commons.security.DataPermission;

public interface DataScopeService {

    /**
     * 根据用户编号获取数据权限
     *
     * @param userId 用户ID
     * @return 查询结果
     */
    DataPermission getDataScopeById(Long userId);

    /**
     * 根据用户编号获取数据权限
     *
     * @param userId 用户ID
     * @param orgId  机构ID
     * @return 查询结果
     */
    default DataPermission getDataScopeById(Long userId, Long orgId) {
        throw new RuntimeException("暂未实现");
    }
}
