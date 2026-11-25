package cool.et.platform.iam.auth.service;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import cool.et.platform.iam.tenant.repository.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    public static final String AUTHENTICATION = "authentication";
    public static final String LOGIN_METHOD = "iam.loginMethod";

    private final TenantMapper tenants;
    private final SaTokenConfig tokenConfig;

    public Tenant requireEnabledTenant(String code) {
        Tenant tenant = TenantHelper.executeWithMaster(() -> tenants.selectOne(Tenant::getCode, code));
        if (tenant == null) {
            throw CheckedException.notFound("租户不存在");
        }
        if (!Boolean.TRUE.equals(tenant.getStatus())) {
            throw CheckedException.badRequest("租户已被禁用");
        }
        return tenant;
    }

    public LoginResp login(Tenant tenant, User user, String clientId, String method) {
        if (!Boolean.TRUE.equals(user.getStatus())) {
            throw CheckedException.badRequest("用户已被禁用");
        }
        SaHolder.getStorage().set(AUTHENTICATION, UserTenantAuthentication.builder().tenant(tenant).user(user).build());
        SaHolder.getStorage().set(LOGIN_METHOD, method);
        StpUtil.login(user.getId(), clientId);
        SaTokenInfo token = StpUtil.getTokenInfo();
        return LoginResp.builder().accessToken(token.getTokenValue()).expiresIn(token.getTokenTimeout())
                .clientId(clientId).tokenType(tokenConfig.getTokenPrefix()).build();
    }
}
