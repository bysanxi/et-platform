package cool.et.platform.iam.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.framework.security.domain.UserInfoDetails;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.base.domain.dto.req.ChangeUserInfoReq;
import cool.et.platform.iam.system.domain.dto.req.UserPageReq;
import cool.et.platform.iam.system.domain.dto.req.UserSaveReq;
import cool.et.platform.iam.system.domain.dto.req.UserUpdateReq;
import cool.et.platform.iam.system.domain.dto.resp.UserPageResp;
import cool.et.platform.iam.system.domain.entity.User;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 账号
 * </p>
 *
 * @since 2019-07-03
 */
public interface UserService extends SuperService<User> {
    
    /**
     * 添加用户
     *
     * @param req 用户信息
     */
    void create(UserSaveReq req);
    
    /**
     * 修改用户信息
     *
     * @param id  id
     * @param req req
     */
    void modify(Long id, UserUpdateReq req);
    
    /**
     * 数据权限 分页
     *
     * @param req req
     * @return 查询结果
     */
    IPage<UserPageResp> pageList(UserPageReq req);

    List<User> visibleUsersByIds(Collection<Long> ids);

    User requireVisibleUser(Long id);
    
    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param orgPassword 原始密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String orgPassword, String newPassword);
    
    /**
     * 根据ID删除用户
     *
     * @param id id
     */
    void delete(Long id);
    
    /**
     * 修改用户信息
     *
     * @param req req
     */
    void changeInfo(ChangeUserInfoReq req);
    
    /**
     * 重置密码
     *
     * @param id ID
     */
    String resetPassword(Long id);
    
    UserInfoDetails userinfo(UserTenantAuthentication authentication);
}
