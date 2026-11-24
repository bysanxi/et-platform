package cool.et.platform.iam.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cool.et.framework.boot.log.AccessLogInfo;
import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.platform.iam.base.domain.entity.OptLog;
import cool.et.platform.iam.base.repository.OptLogMapper;
import cool.et.platform.iam.base.service.OptLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptLogServiceImpl extends SuperServiceImpl<OptLogMapper, OptLog> implements OptLogService {
    
    private final OptLogMapper optLogMapper;
    
    @Override
    public void listener(AccessLogInfo info) {
        TenantHelper.executeWithTenantDb(info.getTenantCode(), () -> {
            return this.optLogMapper.insert(BeanUtil.toBean(info, OptLog.class));
        });
    }
    
}
