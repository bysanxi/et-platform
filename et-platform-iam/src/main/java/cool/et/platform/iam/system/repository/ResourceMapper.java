package cool.et.platform.iam.system.repository;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.system.domain.entity.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 资源
 * </p>
 */
@Repository
public interface ResourceMapper extends SuperMapper<Resource> {
    
    /**
     * 查询指定用户资源权限
     *
     * @param userId 用户ID
     * @return 查询结果
     */
    @InterceptorIgnore(tenantLine = "true", dataPermission = "false")
    List<String> selectPermissionByUserId(Long userId);
}
