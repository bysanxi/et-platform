package cool.et.platform.iam.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.dto.resp.UserRoleResp;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.domain.entity.UserRole;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.system.repository.UserRoleMapper;
import cool.et.platform.iam.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 * @since 2019-07-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends SuperServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    
    private final UserMapper userMapper;
    
    @Override
    public UserRoleResp findUserByRoleId(Long roleId) {
        final List<Long> userIdList = super.list(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId))
                .stream().map(UserRole::getUserId).distinct().collect(Collectors.toList());
        final List<User> users = userMapper.selectList(Wraps.lbQ());
        if (CollectionUtil.isEmpty(users)) {
            return null;
        }
        final List<UserRoleResp.UserRoleDetail> userRoleDetails = users.stream().map(user -> UserRoleResp.UserRoleDetail.builder()
                .id(user.getId()).nickname(user.getNickname()).username(user.getUsername()).build()).collect(Collectors.toList());
        return UserRoleResp.builder().userRoleDetails(userRoleDetails).originTargetKeys(userIdList).build();
    }
}
