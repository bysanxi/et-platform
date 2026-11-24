package cool.et.platform.iam.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import cool.et.framework.commons.BeanUtilPlus;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.commons.security.DataResourceType;
import cool.et.framework.commons.security.DataScopeType;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.platform.iam.system.domain.dto.req.RoleSaveReq;
import cool.et.platform.iam.system.domain.dto.resp.RoleDetailResp;
import cool.et.platform.iam.system.domain.dto.resp.RolePermissionResp;
import cool.et.platform.iam.system.domain.entity.*;
import cool.et.platform.iam.system.domain.enums.ResourceType;
import cool.et.platform.iam.system.repository.*;
import cool.et.platform.iam.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 业务实现类
 * 角色
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {

    private final AuthenticationContext context;
    private final RoleResMapper roleResMapper;
    private final DataPermissionRefMapper dataPermissionRefMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final OrgMapper orgMapper;
    private final ResourceMapper resourceMapper;

    @Override
    public List<Role> list() {
        return baseMapper.list();
    }


    @Override
    public RoleDetailResp detail(Long id) {
        var role = Optional.ofNullable(this.baseMapper.selectById(id)).orElseThrow(() -> CheckedException.notFound("角色不存在"));
        var bean = BeanUtilPlus.toBean(role, RoleDetailResp.class);
        if (bean.getScopeType() != DataScopeType.CUSTOMIZE) {
            return bean;
        }
        var dataList = this.dataPermissionRefMapper.selectList(Wraps.<DataPermissionRef>lbQ()
                .eq(DataPermissionRef::getOwnerId, id)
                .eq(DataPermissionRef::getOwnerType, DataResourceType.ROLE)
                .eq(DataPermissionRef::getDataType, DataResourceType.ORG));
        if (CollUtil.isEmpty(dataList)) {
            return bean;
        }
        bean.setOrgIdList(dataList.stream().map(DataPermissionRef::getDataId).filter(java.util.Objects::nonNull).distinct().toList());
        return bean;
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void removeByRoleId(Long roleId) {
        final Role role = Optional.ofNullable(baseMapper.selectById(roleId)).orElseThrow(() -> CheckedException.notFound("角色不存在"));
        if (role.getReadonly()) {
            throw CheckedException.badRequest("内置角色无法删除");
        }
        if (role.getSuperRole()) {
            throw CheckedException.badRequest("超级角色无法删除");
        }
        baseMapper.deleteById(roleId);
        dataPermissionRefMapper.delete(Wraps.<DataPermissionRef>lbQ()
                .eq(DataPermissionRef::getOwnerId, roleId)
                .eq(DataPermissionRef::getOwnerType, DataResourceType.ROLE));
        roleResMapper.delete(Wraps.<RoleRes>lbQ().eq(RoleRes::getRoleId, roleId));
        userRoleMapper.delete(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId));
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void create(RoleSaveReq req) {
        Role role = BeanUtil.toBean(req, Role.class);
        role.setReadonly(false);
        role.setScopeType(req.getScopeType() == null || req.getScopeType() == DataScopeType.IGNORE ? DataScopeType.SELF : req.getScopeType());
        super.save(role);
        if (role.getScopeType() == DataScopeType.CUSTOMIZE) {
            addDataPermission(role.getId(), req.getOrgIdList());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void modify(Long roleId, RoleSaveReq req) {
        Role role = Optional.ofNullable(this.baseMapper.selectById(roleId)).orElseThrow(() -> CheckedException.notFound("角色不存在"));
        if (role.getReadonly() != null && role.getReadonly()) {
            throw CheckedException.badRequest("内置角色无法编辑");
        }
        if (role.getSuperRole() != null && role.getSuperRole()) {
            throw CheckedException.badRequest("超级角色无法编辑");
        }
        final long count = this.baseMapper.selectCount(Wraps.<Role>lbQ().ne(Role::getId, roleId).eq(Role::getCode, req.getCode()));
        if (count > 0) {
            throw CheckedException.badRequest("角色编码已存在");
        }
        var bean = BeanUtilPlus.toBean(roleId, req, Role.class);
        this.baseMapper.updateById(bean);
        DataScopeType scope = req.getScopeType() == null ? role.getScopeType() : req.getScopeType();
        if (scope == DataScopeType.CUSTOMIZE && req.getOrgIdList() != null) {
            addDataPermission(roleId, req.getOrgIdList());
        } else if (scope != DataScopeType.CUSTOMIZE && role.getScopeType() == DataScopeType.CUSTOMIZE) {
            addDataPermission(roleId, List.of());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void assignUser(Long roleId, List<Long> userIdList) {
        if (baseMapper.selectById(roleId) == null) {
            throw CheckedException.notFound("角色不存在");
        }
        var grants = roleResMapper.selectList(RoleRes::getRoleId, roleId).stream().map(RoleRes::getResId).toList();
        if (!userMapper.selectResByUserId(context.userId()).containsAll(grants)) {
            throw CheckedException.badRequest("只能为自己有权授予的角色分配成员");
        }
        if (CollUtil.isNotEmpty(userIdList) && userMapper.selectByIds(userIdList).size() != userIdList.size()) {
            throw CheckedException.badRequest("用户不属于当前租户或用户列表重复");
        }
        this.userRoleMapper.delete(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId));
        if (CollUtil.isEmpty(userIdList)) {
            return;
        }
        final List<UserRole> userRoles = userIdList.stream()
                .map(userId -> UserRole.builder().roleId(roleId).userId(userId).build())
                .collect(toList());
        this.userRoleMapper.insertBatchSomeColumn(userRoles);
    }

    private void addDataPermission(Long roleId, List<Long> orgList) {
        dataPermissionRefMapper.delete(Wraps.<DataPermissionRef>lbQ()
                .eq(DataPermissionRef::getOwnerId, roleId)
                .eq(DataPermissionRef::getOwnerType, DataResourceType.ROLE)
                .eq(DataPermissionRef::getDataType, DataResourceType.ORG));
        if (CollUtil.isEmpty(orgList)) {
            return;
        }
        if (orgMapper.selectByIds(orgList).size() != orgList.size()) {
            throw CheckedException.badRequest("组织不属于当前租户或组织列表重复");
        }
        List<DataPermissionRef> list = orgList.stream().map(orgId -> DataPermissionRef.builder()
                .ownerId(roleId)
                .ownerType(DataResourceType.ROLE)
                .dataType(DataResourceType.ORG)
                .scopeType(DataScopeType.CUSTOMIZE.getType())
                .dataId(orgId)
                .build()).collect(toList());
        dataPermissionRefMapper.insertBatchSomeColumn(list);
    }

    @Override
    public RolePermissionResp findRolePermissionById(Long roleId) {
        final List<Resource> resourceList = TenantHelper.executeWithMaster(resourceMapper::selectList);
        if (CollUtil.isEmpty(resourceList)) {
            return null;
        }
        List<Long> resIdList = this.roleResMapper.selectList(RoleRes::getRoleId, roleId)
                .stream().map(RoleRes::getResId).distinct().toList();
        List<Long> buttonIdList = resourceList.stream()
                .filter(x -> x.getType() == ResourceType.BUTTON)
                .filter(x -> resIdList.contains(x.getId()))
                .map(Resource::getId)
                .toList();
        List<Long> menuIdList = resourceList.stream()
                .filter(x -> x.getType() != ResourceType.BUTTON)
                .filter(x -> resIdList.contains(x.getId()))
                .map(Resource::getId)
                .toList();
        return RolePermissionResp.builder().menuIdList(menuIdList).buttonIdList(buttonIdList).build();
    }

}
