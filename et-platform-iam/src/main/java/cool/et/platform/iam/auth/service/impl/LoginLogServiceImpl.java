package cool.et.platform.iam.auth.service.impl;

import cool.et.framework.db.mybatisplus.ext.SuperServiceImpl;
import cool.et.platform.iam.auth.domain.entity.LoginLog;
import cool.et.platform.iam.auth.repository.LoginLogMapper;
import cool.et.platform.iam.auth.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 登录日志
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl extends SuperServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    
}
