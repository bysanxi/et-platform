package cool.et.platform.iam.auth.service;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.domain.dto.req.LoginReq;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.repository.RegisteredClientMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import cool.et.platform.iam.tenant.repository.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordLoginService {

    public static final String LOGIN_TYPE = "password";
    public static final String AUTHENTICATION = "authentication";

    private final RegisteredClientMapper clientMapper;
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;

    public void login(LoginReq req) {
        if (!LOGIN_TYPE.equalsIgnoreCase(req.getLoginType())) {
            throw CheckedException.badRequest("不支持的登录类型");
        }
        validateClient(req);
        Tenant tenant = Optional.ofNullable(TenantHelper.executeWithMaster(() -> tenantMapper.selectOne(Tenant::getCode, req.getTenantCode())))
                .orElseThrow(() -> CheckedException.notFound("租户不存在"));
        if (!Boolean.TRUE.equals(tenant.getStatus())) {
            throw CheckedException.badRequest("租户已被禁用");
        }
        User user = TenantHelper.executeWithTenantDb(tenant.getCode(),
                () -> userMapper.selectUserByTenantId(req.getUsername(), tenant.getId()));
        if (user == null || !PasswordEncoderHelper.matches(req.getPassword(), user.getPassword())) {
            throw CheckedException.badRequest("用户名或密码错误");
        }
        if (!Boolean.TRUE.equals(user.getStatus())) {
            throw CheckedException.badRequest("用户已被禁用");
        }
        SaHolder.getStorage().set(AUTHENTICATION, UserTenantAuthentication.builder().tenant(tenant).user(user).build());
        StpUtil.login(user.getId(), req.getClientId());
    }

    private void validateClient(LoginReq req) {
        RegisteredClient client = clientMapper.selectOne(Wraps.<RegisteredClient>lbQ()
                .eq(RegisteredClient::getClientId, req.getClientId()).eq(RegisteredClient::getClientSecret, req.getClientSecret()));
        if (client == null || !Boolean.TRUE.equals(client.getStatus())) {
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
