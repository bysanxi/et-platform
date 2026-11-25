package cool.et.platform.iam.system.repository;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import cool.et.framework.commons.entity.Entity;
import cool.et.framework.db.mybatisplus.datascope.annotation.DataColumn;
import cool.et.framework.db.mybatisplus.datascope.annotation.DataScope;
import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.framework.db.mybatisplus.page.PageRequest;
import cool.et.platform.iam.contract.user.UserInfoResp;
import cool.et.platform.iam.contract.user.UserPageReq;
import cool.et.platform.iam.system.domain.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserMapper extends SuperMapper<User> {

    default IPage<User> selectUserPage(UserPageReq req, Wrapper<User> filters) {
        PageRequest page = new PageRequest();
        page.setCurrent(req.getCurrent());
        page.setSize(req.getSize());
        page.setColumn(req.getColumn());
        page.setAsc(!Boolean.FALSE.equals(req.getAsc()));
        return selectPage(page.buildPage(), filters);
    }

    /**
     * 分页查询用户
     *
     * @param page    page
     * @param wrapper wrapper
     * @return 查询结果
     */
    @DataScope(columns = @DataColumn(name = Entity.CREATE_USER_COLUMN))
    IPage<UserInfoResp> findPage(@Param("page") IPage<User> page, @Param(Constants.WRAPPER) Wrapper<User> wrapper);

    /**
     * 查询用户
     *
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 查询结果
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from t_user where username = #{username} and tenant_id = #{tenantId} AND deleted = false")
    User selectUserByTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);

    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM t_user WHERE tenant_id = #{tenantId} AND email = #{email} AND deleted = false")
    User findByEmail(@Param("tenantId") Long tenantId, @Param("email") String email);

    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM t_user WHERE tenant_id = #{tenantId} AND mobile = #{mobile} AND deleted = false")
    User findByMobile(@Param("tenantId") Long tenantId, @Param("mobile") String mobile);

    @InterceptorIgnore(tenantLine = "true")
    @Update("""
            UPDATE t_user SET password = #{password}
            WHERE tenant_id = #{tenantId} AND id = #{id} AND deleted = false AND status = true AND readonly = false
            """)
    int resetAccountPassword(@Param("tenantId") Long tenantId, @Param("id") Long id, @Param("password") String password);

    /**
     * 带数据权限用户列表
     *
     * @return 用户
     */
    @DataScope(columns = @DataColumn(name = Entity.CREATE_USER_COLUMN))
    List<User> list();

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 查询结果
     */
    List<Long> selectResByUserId(@Param("userId") Long userId);
}
