package cool.et.platform.iam.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cool.et.framework.commons.entity.Entity;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.dto.req.OrgSaveReq;
import cool.et.platform.iam.system.domain.entity.Org;
import cool.et.platform.iam.system.repository.OrgMapper;
import cool.et.platform.iam.system.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * 机构管理
 */
@Service
@RequiredArgsConstructor
public class OrgServiceImpl extends SuperServiceImpl<OrgMapper, Org> implements OrgService {
    
    @Override
    public void remove(Long id) {
        final Long count = this.baseMapper.selectCount(Org::getParentId, id);
        if (count != null && count > 0) {
            throw CheckedException.badRequest("当前组织下还存在子节点,请先移除子节点");
        }
        this.baseMapper.deleteById(id);
    }
    
    @Override
    public void create(OrgSaveReq req) {
        final Org bean = BeanUtil.toBean(req, Org.class);
        bean.setTreePath(buildNewTreePath(req.getParentId()));
        this.baseMapper.insert(bean);
    }
    
    @Override
    public List<Long> getFullTreeIdPath(Long id) {
        if (id == null) {
            return null;
        }
        final Org org = this.baseMapper.selectById(id);
        if (org == null) {
            return null;
        }
        String path = StrUtil.join(StrUtil.COMMA, buildNewTreePath(id));
        final List<Long> list = this.baseMapper.selectList(Wraps.<Org>lbQ()
                .and(q -> q.eq(Org::getTreePath, path).or().likeRight(Org::getTreePath, path + ",")))
                .stream()
                .map(Entity::getId)
                .distinct()
                .collect(toList());
        list.add(org.getId());
        return list;
    }
    
    private List<Long> buildNewTreePath(Long id) {
        final Org org = Optional.ofNullable(this.baseMapper.selectById(id)).orElseThrow(() -> CheckedException.notFound("父节点不存在"));
        final List<Long> treePath = new ArrayList<>(org.getTreePath() == null ? List.of(0L) : org.getTreePath());
        treePath.add(org.getId());
        return treePath;
    }
    
}
