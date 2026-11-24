package cool.et.platform.iam.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import cool.et.framework.commons.BeanUtilPlus;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.dto.req.ResourceQueryReq;
import cool.et.platform.iam.system.domain.dto.req.ResourceSaveReq;
import cool.et.platform.iam.system.domain.dto.resp.VisibleResourceResp;
import cool.et.platform.iam.system.domain.entity.Resource;
import cool.et.platform.iam.system.domain.entity.Role;
import cool.et.platform.iam.system.domain.entity.RoleRes;
import cool.et.platform.iam.system.domain.enums.ResourceType;
import cool.et.platform.iam.system.repository.ResourceMapper;
import cool.et.platform.iam.system.repository.RoleMapper;
import cool.et.platform.iam.system.repository.RoleResMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.system.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 资源
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends SuperServiceImpl<ResourceMapper, Resource> implements ResourceService {

    private static final String SPEL = "/";
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final RoleResMapper roleResMapper;

    @Override
    public List<VisibleResourceResp> findVisibleResource(ResourceQueryReq req) {
        List<Long> resIdList = userMapper.selectResByUserId(req.getUserId());
        if (resIdList.isEmpty()) {
            return List.of();
        }
        List<Resource> list = baseMapper.selectList(Wraps.<Resource>lbQ()
                .in(Resource::getId, resIdList)
                .eq(Resource::getStatus, req.getStatus()).eq(Resource::getClientId, req.getClientId())
                .eq(Resource::getParentId, req.getParentId()).eq(Resource::getType, req.getType()));
        return BeanUtilPlus.toBeans(list, VisibleResourceResp.class);
    }


    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void create(ResourceSaveReq req) {
        final Resource resource = BeanUtil.toBean(req, Resource.class);
        if (ResourceType.MENU == resource.getType()) {
            if (!StringUtils.startsWith(resource.getPath(), SPEL)) {
                resource.setPath(SPEL + resource.getPath());
            }
        }
        this.baseMapper.insert(resource);
        final List<Role> roles = this.roleMapper.selectList(Wraps.<Role>lbQ().eq(Role::getSuperRole, true)
                .eq(Role::getStatus, true));
        if (CollUtil.isEmpty(roles)) {
            return;
        }
        // 给管理员角色挂载权限
        final List<RoleRes> roleResList = roles.stream()
                .map(role -> RoleRes.builder().roleId(role.getId()).resId(resource.getId()).build())
                .collect(Collectors.toList());
        roleResMapper.insertBatchSomeColumn(roleResList);
    }

    @Override
    public void modify(Long id, ResourceSaveReq req) {
        final Resource resource = BeanUtilPlus.toBean(id, req, Resource.class);
        this.baseMapper.updateById(resource);
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        final long count = this.baseMapper.selectCount(Resource::getParentId, id);
        if (count > 0) {
            throw CheckedException.badRequest("当前节点存在子节点,请先移除子节点");
        }
        // 删除菜单和按钮
        this.baseMapper.deleteById(id);
        // 删除对应的资源权限
        this.roleResMapper.delete(Wraps.<RoleRes>lbQ().eq(RoleRes::getResId, id));
    }
}
