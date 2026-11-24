package cool.et.platform.iam.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import cool.et.framework.commons.BeanUtilPlus;
import cool.et.framework.commons.entity.enums.UserType;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.db.properties.DatabaseProperties;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.system.domain.entity.*;
import cool.et.platform.iam.system.repository.*;
import cool.et.platform.iam.tenant.domain.dto.req.TenantSaveReq;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import cool.et.platform.iam.tenant.repository.TenantMapper;
import cool.et.platform.iam.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant> implements TenantService {

    private final RoleMapper roleMapper;
    private final RoleResMapper roleResMapper;
    private final UserRoleMapper userRoleMapper;
    private final DatabaseProperties properties;
    private final UserMapper userMapper;
    private final OrgMapper orgMapper;

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void create(TenantSaveReq req) {
        // 随机生成租户编码
        long nameCount = this.baseMapper.selectCount(Tenant::getName, req.getName());
        if (nameCount > 0) {
            throw CheckedException.badRequest("租户名称重复");
        }
        long codeCount = this.baseMapper.selectCount(Tenant::getCode, req.getCode());
        if (codeCount > 0) {
            throw CheckedException.badRequest("租户编码重复");
        }
        Tenant tenant = BeanUtil.toBean(req, Tenant.class);
        this.baseMapper.insert(tenant);
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void modify(Long id, TenantSaveReq req) {
        Optional.ofNullable(this.baseMapper.selectById(id))
                .orElseThrow(() -> CheckedException.notFound("租户不存在"));
        Long nameCount = this.baseMapper.selectCount(Wraps.<Tenant>lbQ().eq(Tenant::getName, req.getName()).ne(Tenant::getId, id));
        if (nameCount > 0) {
            throw CheckedException.badRequest("租户名称重复");
        }
        Long codeCount = this.baseMapper.selectCount(Wraps.<Tenant>lbQ().eq(Tenant::getCode, req.getCode()).ne(Tenant::getId, id));
        if (codeCount > 0) {
            throw CheckedException.badRequest("租户编码重复");
        }
        Tenant bean = BeanUtilPlus.toBean(id, req, Tenant.class);
        this.baseMapper.updateById(bean);
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void initSqlScript(Long id) {
        var tenant = Optional.ofNullable(this.baseMapper.selectById(id)).orElseThrow(() -> CheckedException.notFound("租户信息不存在"));
        if (!tenant.getStatus()) {
            throw CheckedException.badRequest("租户未启用");
        }
        final DatabaseProperties.MultiTenant multiTenant = properties.getMultiTenant();
        if (isSuperTenant(tenant, multiTenant)) {
            throw CheckedException.badRequest("超级租户,禁止操作");
        }
        initColumnTypeTenant(tenant);
    }

    private boolean isSuperTenant(Tenant tenant, DatabaseProperties.MultiTenant multiTenant) {
        return StringUtils.equals(tenant.getCode(), multiTenant.getSuperTenantCode());
    }

    private Long platformTenantId() {
        String code = properties.getMultiTenant().getSuperTenantCode();
        return Optional.ofNullable(baseMapper.selectOne(Tenant::getCode, code))
                .orElseThrow(() -> CheckedException.notFound("平台租户不存在")).getId();
    }

    private Role selectTenantAdminRole() {
        return Optional.ofNullable(roleMapper.selectOne(Wraps.<Role>lbQ()
                        .eq(Role::getCode, "TENANT-ADMIN")
                        .eq(Role::getTenantId, platformTenantId())))
                .orElseThrow(() -> CheckedException.notFound("内置租户管理员角色不存在"));
    }

    private void initColumnTypeTenant(Tenant tenant) {
        TenantHelper.runWithIgnoreStrategy(() -> {
            if (userMapper.selectCount(User::getTenantId, tenant.getId()) > 0) {
                throw CheckedException.badRequest("该租户已有用户，无需重复初始化");
            }
            Role template = selectTenantAdminRole();
            Role role = Role.builder().tenantId(tenant.getId()).code(template.getCode())
                    .name(template.getName()).scopeType(template.getScopeType()).status(true)
                    .superRole(false).readonly(true).build();
            roleMapper.insert(role);
            List<RoleRes> grants = roleResMapper.selectList(RoleRes::getRoleId, template.getId()).stream()
                    .<RoleRes>map(grant -> RoleRes.builder().roleId(role.getId()).resId(grant.getResId()).build()).toList();
            if (!grants.isEmpty()) {
                roleResMapper.insertBatchSomeColumn(grants);
            }
            initializeTenantData(tenant, role);
        });
    }

    private void initializeTenantData(Tenant tenant, Role role) {
        log.warn("初始化租户 {} 的管理员和组织", tenant.getName());
        Org org = new Org();
        org.setLabel(tenant.getName());
        org.setTenantId(tenant.getId());
        org.setStatus(true);
        org.setDescription("不可删除不可修改");
        org.setParentId(0L);
        org.setTreePath(List.of(0L));
        org.setSequence(0);
        this.orgMapper.insert(org);

        User user = new User();
        String contactPhone = tenant.getContactPhone();
        user.setType(UserType.TENANT_ADMIN);
        user.setUsername(contactPhone);
        user.setEmail(tenant.getEmail());
        user.setAvatar(tenant.getLogo());
        user.setPassword(PasswordEncoderHelper.encode("123456"));
        user.setTenantId(tenant.getId());
        user.setOrgId(org.getId());
        user.setNickname(tenant.getContactPerson());
        user.setMobile(contactPhone);
        user.setStatus(true);
        this.userMapper.insert(user);
        this.userRoleMapper.insert(UserRole.builder().userId(user.getId()).roleId(role.getId()).build());
    }
}
