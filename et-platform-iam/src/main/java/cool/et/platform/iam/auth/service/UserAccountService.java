package cool.et.platform.iam.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import cool.et.framework.commons.entity.enums.UserType;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.contract.account.UserAccountApi.*;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserMapper users;
    private final UserSessionService sessions;

    public Account lookup(Lookup request) {
        Tenant tenant = sessions.requireEnabledTenant(request.tenantCode());
        String value = request.value();
        User user = TenantHelper.executeWithTenantDb(tenant.getCode(), () -> switch (request.type()) {
            case username -> users.selectUserByTenantId(value, tenant.getId());
            case email -> users.findByEmail(tenant.getId(), value.strip().toLowerCase(Locale.ROOT));
            case mobile -> users.findByMobile(tenant.getId(), value.strip());
        });
        return user == null ? null : new Account(user.getId(), user.getUsername(), user.getStatus());
    }

    public Registered register(Registration request) {
        if ((request.email() == null) == (request.mobile() == null)
                || (request.email() != null && request.email().isBlank())) {
            throw CheckedException.badRequest("请选择邮箱或手机号其中一项注册");
        }
        Tenant tenant = sessions.requireEnabledTenant(request.tenantCode());
        String username = "u_" + UUID.randomUUID().toString().replace("-", "").substring(0, 28);
        User user = User.builder().tenantId(tenant.getId()).username(username).nickname(username)
                .email(request.email() == null ? null : request.email().toLowerCase(Locale.ROOT))
                .mobile(request.mobile()).type(UserType.OUT).status(true).deleted(false)
                .password(request.password() == null ? null : PasswordEncoderHelper.encode(request.password())).build();
        try {
            TenantHelper.executeWithTenantDb(tenant.getCode(), () -> users.insert(user));
        } catch (DuplicateKeyException ex) {
            throw new CheckedException(409, "该账号或联系方式已在此租户注册");
        }
        return new Registered(user.getId());
    }

    @DSTransactional(rollbackFor = Exception.class)
    public Registered resetPassword(Long id, PasswordReset request) {
        Tenant tenant = sessions.requireEnabledTenant(request.tenantCode());
        String hash = PasswordEncoderHelper.encode(request.newPassword());
        int updated = TenantHelper.executeWithTenantDb(tenant.getCode(),
                () -> users.resetAccountPassword(tenant.getId(), id, hash));
        if (updated != 1) {
            throw CheckedException.badRequest("用户不存在或不允许找回密码");
        }
        StpUtil.logout(id);
        return new Registered(id);
    }

    public LoginResp mobileLogin(MobileLogin request) {
        Tenant tenant = sessions.requireEnabledTenant(request.tenantCode());
        User user = TenantHelper.executeWithTenantDb(tenant.getCode(),
                () -> users.findByMobile(tenant.getId(), request.mobile()));
        if (user == null) {
            throw CheckedException.notFound("用户不存在");
        }
        return sessions.login(tenant, user, "mobile", "sms");
    }
}
