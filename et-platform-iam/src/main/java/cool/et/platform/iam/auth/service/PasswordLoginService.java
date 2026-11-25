package cool.et.platform.iam.auth.service;

import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.domain.dto.req.LoginReq;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.repository.RegisteredClientMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordLoginService {

    public static final String LOGIN_TYPE = "password";

    private final RegisteredClientMapper clientMapper;
    private final UserSessionService sessions;
    private final UserMapper userMapper;

    public LoginResp login(LoginReq req) {
        if (!LOGIN_TYPE.equalsIgnoreCase(req.getLoginType())) {
            throw CheckedException.badRequest("不支持的登录类型");
        }
        validateClient(req);
        Tenant tenant = sessions.requireEnabledTenant(req.getTenantCode());
        User user = TenantHelper.executeWithTenantDb(tenant.getCode(),
                () -> userMapper.selectUserByTenantId(req.getUsername(), tenant.getId()));
        if (user == null || !PasswordEncoderHelper.matches(req.getPassword(), user.getPassword())) {
            throw CheckedException.badRequest("用户名或密码错误");
        }
        return sessions.login(tenant, user, req.getClientId(), LOGIN_TYPE);
    }

    private void validateClient(LoginReq req) {
        RegisteredClient client = clientMapper.selectOne(Wraps.<RegisteredClient>lbQ()
                .eq(RegisteredClient::getClientId, req.getClientId()));
        if (client == null || !client.allowsGrant(LOGIN_TYPE) || !Boolean.TRUE.equals(client.getStatus())
                || req.getClientSecret() == null || !req.getClientSecret().equals(client.getClientSecret())) {
            throw CheckedException.badRequest("无效的登录客户端");
        }
        Instant now = Instant.now();
        if (client.getClientIdIssuedAt() != null && client.getClientIdIssuedAt().isAfter(now)) {
            throw CheckedException.badRequest("客户端密钥未生效");
        }
        if (client.getClientSecretExpiresAt() != null && !client.getClientSecretExpiresAt().isAfter(now)) {
            throw CheckedException.badRequest("客户端密钥已失效");
        }
    }
}
