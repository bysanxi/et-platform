package cool.et.platform.iam.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.dto.req.RoleResSaveReq;
import cool.et.platform.iam.system.domain.entity.RoleRes;
import cool.et.platform.iam.system.repository.RoleResMapper;
import cool.et.platform.iam.system.service.RoleResService;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.platform.iam.system.repository.RoleMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * 角色资源
 */
@Service
@RequiredArgsConstructor
public class RoleResServiceImpl extends SuperServiceImpl<RoleResMapper, RoleRes> implements RoleResService {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final AuthenticationContext context;

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void assignResource(RoleResSaveReq req) {
        if (roleMapper.selectById(req.getRoleId()) == null) {
            throw CheckedException.notFound("角色不存在");
        }
        if (CollUtil.isNotEmpty(req.getResIdList())
                && !userMapper.selectResByUserId(context.userId()).containsAll(req.getResIdList())) {
            throw CheckedException.badRequest("只能分配自己已获授权的资源");
        }
        // 删除角色和资源的关联
        super.remove(Wraps.<RoleRes>lbQ().eq(RoleRes::getRoleId, req.getRoleId()));
        resHandler(req, req.getRoleId());
    }

    private void resHandler(RoleResSaveReq data, Long roleId) {
        final Set<Long> resIdList = data.getResIdList();
        if (CollUtil.isEmpty(resIdList)) {
            return;
        }
        final List<RoleRes> list = resIdList.stream().filter(Objects::nonNull)
                .map(resId -> RoleRes.builder().resId(resId).roleId(roleId).build()).collect(toList());
        this.baseMapper.insertBatchSomeColumn(list);
    }
}
