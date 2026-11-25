package cool.et.platform.iam.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cool.et.framework.commons.BeanUtilPlus;
import cool.et.framework.commons.entity.enums.UserType;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.DataResourceType;
import cool.et.framework.commons.security.DataScopeType;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.db.mybatisplus.datascope.service.DataScopeService;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.db.mybatisplus.wrap.query.LbqWrapper;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.domain.UserInfoDetails;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.base.domain.dto.req.ChangeUserInfoReq;
import cool.et.platform.iam.contract.user.UserPageReq;
import cool.et.platform.iam.system.domain.dto.req.UserSaveReq;
import cool.et.platform.iam.system.domain.dto.req.UserUpdateReq;
import cool.et.platform.iam.system.domain.entity.Role;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.domain.entity.UserRole;
import cool.et.platform.iam.system.domain.enums.Sex;
import cool.et.platform.iam.system.repository.ResourceMapper;
import cool.et.platform.iam.system.repository.RoleMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.system.repository.UserRoleMapper;
import cool.et.platform.iam.system.service.OrgService;
import cool.et.platform.iam.system.service.UserService;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final AuthenticationContext context;
    private final OrgService orgService;
    private final ResourceMapper resourceMapper;
    private final RoleMapper roleMapper;
    private final DataScopeService dataScopeService;

    @Override
    public void create(UserSaveReq req) {
        final long count = super.count(Wraps.<User>lbQ().eq(User::getUsername, req.getUsername()));
        if (count > 0) {
            throw CheckedException.badRequest("账号已存在");
        }
        requireTenantOrg(req.getOrgId());
        var bean = BeanUtil.toBean(req, User.class);
        bean.setPassword(PasswordEncoderHelper.encode(req.getPassword()));
        bean.setTenantId(context.tenantId());
        bean.setType(UserType.IN);
        this.baseMapper.insert(bean);
    }

    @Override
    public void modify(Long id, UserUpdateReq req) {
        requireVisibleUser(id);
        requireTenantOrg(req.getOrgId());
        User newUser = BeanUtilPlus.toBean(id, req, User.class);
        this.baseMapper.updateById(newUser);
    }

    private void requireTenantOrg(Long orgId) {
        if (orgId != null && orgService.getById(orgId) == null) {
            throw CheckedException.badRequest("组织不属于当前租户");
        }
    }

    @Override
    public List<User> list() {
        return baseMapper.list();
    }

    @Override
    public IPage<User> pageList(UserPageReq req) {
        var query = visibleUserQuery()
                .eq(User::getStatus, req.getStatus())
                .like(User::getUsername, req.getUsername()).like(User::getNickname, req.getNickname()).like(User::getEmail, req.getEmail())
                .eq(User::getSex, Sex.of(req.getSex())).in(User::getOrgId, orgService.getFullTreeIdPath(req.getOrgId()))
                .eq(User::getMobile, req.getMobile());
        return baseMapper.selectUserPage(req, query);
    }

    @Override
    public List<User> visibleUsersByIds(Collection<Long> ids) {
        return ids.isEmpty() ? List.of() : baseMapper.selectList(visibleUserQuery().in(User::getId, ids));
    }

    @Override
    public User requireVisibleUser(Long id) {
        return Optional.ofNullable(baseMapper.selectOne(visibleUserQuery().eq(User::getId, id)))
                .orElseThrow(() -> CheckedException.notFound("用户不存在"));
    }

    private LbqWrapper<User> visibleUserQuery() {
        var query = Wraps.<User>lbQ().eq(User::getTenantId, context.tenantId());
        var permission = context.dataPermission();
        if (permission == null || permission.getScopeType() == null || permission.getScopeType() == DataScopeType.SELF
                || permission.getScopeType() == DataScopeType.IGNORE) {
            query.eq(User::getId, context.userId());
        } else if (permission.getScopeType() != DataScopeType.ALL) {
            var ids = permission.getDataPermissionMap().get(DataResourceType.USER);
            if (ids == null || ids.isEmpty()) {
                query.apply("1 = 0");
            } else {
                query.in(User::getId, ids);
            }
        }
        return query;
    }

    @Override
    public void changePassword(Long userId, String orgPassword, String newPassword) {
        final User user = Optional.ofNullable(this.baseMapper.selectById(userId)).orElseThrow(() -> CheckedException.notFound("用户不存在"));
        if (!PasswordEncoderHelper.matches(orgPassword, user.getPassword())) {
            throw CheckedException.badRequest("原始密码错误");
        }
        int updated = baseMapper.update(User.builder().password(PasswordEncoderHelper.encode(newPassword)).build(),
                Wraps.<User>lbQ().eq(User::getId, userId).eq(User::getPassword, user.getPassword()));
        if (updated != 1) {
            throw CheckedException.badRequest("密码已变更，请重新登录");
        }
        StpUtil.logout(userId);
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        final User user = requireVisibleUser(id);
        if (user.getReadonly()) {
            throw CheckedException.badRequest("内置用户不允许删除");
        }
        baseMapper.deleteById(id);
        userRoleMapper.delete(Wraps.<UserRole>lbQ().eq(UserRole::getUserId, id));
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void changeInfo(ChangeUserInfoReq req) {
        final Long userId = context.userId();
        if (req.getEmail() != null || req.getMobile() != null) {
            User current = getById(userId);
            if (current == null || (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(current.getEmail()))
                    || (req.getMobile() != null && !req.getMobile().equals(current.getMobile()))) {
                throw CheckedException.badRequest("联系方式须经验证后变更");
            }
        }
        User bean = User.builder().id(userId)
                .nickname(req.getNickname()).birthday(req.getBirthday())
                .description(req.getDescription()).build();
        this.baseMapper.updateById(bean);
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String resetPassword(Long id) {
        final User user = requireVisibleUser(id);
        if (user.getReadonly()) {
            throw CheckedException.badRequest("禁止重置内置用户密码");
        }
        String password = UUID.randomUUID().toString().replace("-", "");
        this.baseMapper.updateById(User.builder().id(id).password(PasswordEncoderHelper.encode(password)).build());
        StpUtil.logout(id);
        return password;
    }

    @Override
    public UserInfoDetails userinfo(UserTenantAuthentication authentication) {
        User user = authentication.getUser();
        Tenant tenant = authentication.getTenant();
        return TenantHelper.executeWithTenantDb(tenant.getCode(), () -> {
            final UserInfoDetails info = new UserInfoDetails();
            info.setTenantCode(tenant.getCode());
            info.setTenantName(tenant.getName());
            info.setType(user.getType());
            info.setTenantId(user.getTenantId());
            info.setUserId(user.getId());
            info.setUsername(user.getUsername());
            info.setNickname(user.getNickname());
            info.setOrgId(user.getOrgId());
            info.setMobile(user.getMobile());
            info.setEmail(user.getEmail());
            info.setDescription(user.getDescription());
            info.setBirthday(user.getBirthday());
            info.setEnabled(user.getStatus());
            info.setAvatar(user.getAvatar());
            final List<Role> roles = this.roleMapper.findRoleByUserId(user.getId());
            info.setRoles(roles.stream().map(Role::getCode).toList());
            setFuncPermissions(info);
            // 为了减少一次数据库查询,所以用了这个不规范写法
            info.setDataPermission(dataScopeService.getDataScopeById(user.getId(), user.getOrgId()));
            return info;
        });
    }

    public void setFuncPermissions(UserInfoDetails info) {
        info.setFuncPermissions(resourceMapper.selectPermissionByUserId(info.getUserId()));
    }
}
