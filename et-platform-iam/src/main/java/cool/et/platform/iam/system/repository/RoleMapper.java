package cool.et.platform.iam.system.repository;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import cool.et.framework.db.mybatisplus.datascope.annotation.DataColumn;
import cool.et.framework.db.mybatisplus.datascope.annotation.DataScope;
import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.system.domain.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleMapper extends SuperMapper<Role> {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 查询结果
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Role> findRoleByUserId(Long userId);

    /**
     * 根据范围查询角色
     *
     * @return 查询结果
     */
    @DataScope(columns = @DataColumn)
    List<Role> list();
}
