package cool.et.platform.iam.base.service;

import cool.et.framework.boot.log.AccessLogInfo;
import cool.et.framework.db.mybatisplus.ext.SuperService;
import cool.et.platform.iam.base.domain.entity.OptLog;

public interface OptLogService extends SuperService<OptLog> {
    
    /**
     * 保存操作日志
     *
     * @param logInfo logInfo
     */
    void listener(AccessLogInfo logInfo);
}
