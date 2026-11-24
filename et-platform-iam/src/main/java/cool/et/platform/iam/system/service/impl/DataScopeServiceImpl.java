package cool.et.platform.iam.system.service.impl;

import cool.et.framework.commons.entity.Entity;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.DataPermission;
import cool.et.framework.commons.security.DataResourceType;
import cool.et.framework.db.mybatisplus.datascope.service.DataScopeService;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.entity.DataPermissionRef;
import cool.et.platform.iam.system.domain.entity.Role;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.repository.DataPermissionRefMapper;
import cool.et.platform.iam.system.repository.RoleMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.system.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static cool.et.framework.commons.security.DataScopeType.*;

@Service
@RequiredArgsConstructor
public class DataScopeServiceImpl implements DataScopeService {

    private final RoleMapper roleMapper;
    private final DataPermissionRefMapper dataPermissionRefMapper;
    private final UserMapper userMapper;
    private final OrgService orgService;

    @Override
    public DataPermission getDataScopeById(Long userId) {
        final User user = Optional.ofNullable(this.userMapper.selectById(userId)).orElseThrow(() -> CheckedException.notFound("用户不存在"));
        return getDataScopeById(userId, user.getOrgId());
    }

    /**
     * 开发者可以根据自己企业需求动态扩展数据权限（默认就支撑多维度数据权限，此处以用户维护演示）
     *
     * @param userId 用户ID
     * @param orgId  用户当前机构
     * @return 数据权限
     */
    @Override
    public DataPermission getDataScopeById(Long userId, Long orgId) {
        List<Role> list = roleMapper.findRoleByUserId(userId);
        if (CollectionUtils.isEmpty(list)) {
            return DataPermission.builder().scopeType(SELF).orgId(orgId).build();
        }
        list.forEach(item -> {
            if (item.getScopeType() == null) {
                item.setScopeType(SELF);
            }
        });
        // 按角色中最大的数据范围计算可见用户。
        Role role = list.stream().max(Comparator.comparingInt(item -> item.getScopeType().getType())).get();
        // 收集所有角色ID（用于 EXISTS 子查询）
        List<Long> roleIds = list.stream().map(Role::getId).toList();
        DataPermission permission = DataPermission.builder().scopeType(role.getScopeType()).roleIds(roleIds).orgId(orgId).build();

        List<Long> userIdList = null;
        if (role.getScopeType() == CUSTOMIZE) {
            List<Long> orgIdList = dataPermissionRefMapper.selectList(Wraps.<DataPermissionRef>lbQ().select(DataPermissionRef::getDataId)
                    .in(DataPermissionRef::getOwnerId, list.stream().filter(r -> r.getScopeType() == CUSTOMIZE).map(Role::getId).toList())
                    .eq(DataPermissionRef::getOwnerType, DataResourceType.ROLE)
                    .eq(DataPermissionRef::getDataType, DataResourceType.ORG))
                    .stream().map(DataPermissionRef::getDataId).filter(java.util.Objects::nonNull).distinct().toList();
            userIdList = orgIdList.isEmpty() ? List.of() : this.userMapper.selectList(Wraps.<User>lbQ().select(User::getId).eq(User::getTenantId, role.getTenantId()).in(User::getOrgId, orgIdList))
                    .stream().map(Entity::getId).toList();
        } else if (role.getScopeType() == THIS_LEVEL) {
            userIdList = orgId == null ? List.of() : this.userMapper.selectList(Wraps.<User>lbQ().select(User::getId)
                    .eq(User::getTenantId, role.getTenantId()).eq(User::getOrgId, orgId))
                    .stream().map(Entity::getId).toList();
        } else if (role.getScopeType() == THIS_LEVEL_CHILDREN) {
            final List<Long> orgIdList = orgService.getFullTreeIdPath(orgId);
            userIdList = orgIdList == null || orgIdList.isEmpty() ? List.of() : this.userMapper.selectList(Wraps.<User>lbQ().select(User::getId).eq(User::getTenantId, role.getTenantId()).in(User::getOrgId, orgIdList))
                    .stream().map(Entity::getId).toList();
        }
        if (userIdList != null) {
            permission.getDataPermissionMap().put(DataResourceType.USER, userIdList.stream().map(x -> (Object) x).toList());
        }
        return permission;
    }
}
