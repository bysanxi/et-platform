package cool.et.framework.db.mybatisplus.datascope.service;

import cool.et.framework.commons.FeignConstants;
import cool.et.framework.commons.security.DataPermission;
import cool.et.framework.db.properties.DatabaseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Primary
@ConditionalOnProperty(prefix = DatabaseProperties.INTERCEPT_DATA_PERMISSION_PREFIX, name = "remote")
@FeignClient(name = FeignConstants.AUTH_FEIGN_NAME, dismiss404 = true)
public interface FeignDataScopeServiceImpl extends DataScopeService {

    /**
     * 获取用户的数据权限
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @GetMapping("/users/{user_id}/data_permission")
    DataPermission getDataScopeById(@PathVariable("user_id") Long userId);

}
