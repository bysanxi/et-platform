package cool.et.platform.iam.auth.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.contract.oauth.OAuthClientApi.*;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.repository.RegisteredClientMapper;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import cool.et.platform.iam.tenant.repository.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class OAuthClientRegistry {
    public static final String GRANT = "client_credentials";
    public static final String SCOPE = "server";

    private final RegisteredClientMapper clients;
    private final TenantMapper tenants;
    private final UserMapper users;
    private final TransactionTemplate transactions;

    public RegisteredClient lookup(String id) {
        return TenantHelper.executeWithMaster(() -> clients.selectOne(RegisteredClient::getClientId, id));
    }

    public Client find(String id) {
        RegisteredClient row = lookup(id);
        if (row == null || !row.allowsGrant(GRANT)) {
            throw CheckedException.notFound("客户端不存在");
        }
        return view(row);
    }

    public Client register(Registration req) {
        if (req.clientId() == null || !req.clientId().matches("[A-Za-z0-9_~-][A-Za-z0-9._~-]{0,99}")) {
            throw CheckedException.badRequest("客户端 ID 格式无效");
        }
        String digest = digest(req.clientSecret());
        return write(() -> {
            checkBinding(req.userId(), req.tenantCode());
            RegisteredClient row = new RegisteredClient();
            row.setClientId(req.clientId());
            row.setClientName(req.clientId());
            row.setClientSecret(digest);
            row.setClientAuthenticationMethods("client_secret_basic");
            // MySQL TIMESTAMP(0) can round a fractional instant into the next second.
            row.setClientIdIssuedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
            row.setGrantTypes(GRANT);
            row.setScopes(SCOPE);
            row.setTokenSettings("{}");
            row.setStatus(true);
            row.setDeleted(false);
            row.setCreateTime(Instant.now());
            row.setAuthzVersion(1L);
            row.setUserId(req.userId());
            row.setTenantCode(req.tenantCode());
            try {
                clients.insert(row);
            } catch (DuplicateKeyException ex) {
                throw CheckedException.badRequest("客户端 ID 已登记");
            }
            return view(row);
        });
    }

    public Client bind(String id, Binding binding) {
        return change(id, update -> {
            checkBinding(binding.userId(), binding.tenantCode());
            update.set(RegisteredClient::getUserId, binding.userId())
                    .set(RegisteredClient::getTenantCode, binding.tenantCode());
        }, true);
    }

    public Client disable(String id) {
        return change(id, update -> update.set(RegisteredClient::getStatus, false), true);
    }

    public Client revoke(String id) {
        return change(id, update -> update.set(RegisteredClient::getStatus, false)
                .set(RegisteredClient::getClientSecret, null), true);
    }

    public Client rotate(String id, String rawSecret) {
        String digest = digest(rawSecret);
        return change(id, update -> update.set(RegisteredClient::getClientSecret, digest), false);
    }

    private Client change(String id, Consumer<LambdaUpdateWrapper<RegisteredClient>> mutation, boolean invalidate) {
        return write(() -> {
            var update = Wrappers.<RegisteredClient>lambdaUpdate().eq(RegisteredClient::getClientId, id)
                    .eq(RegisteredClient::getGrantTypes, GRANT);
            mutation.accept(update);
            if (invalidate) {
                update.setSql("authz_version = authz_version + 1");
            }
            if (clients.update(null, update) != 1) {
                throw CheckedException.notFound("客户端不存在");
            }
            return find(id);
        });
    }

    private Client write(Supplier<Client> action) {
        return TenantHelper.executeWithMaster(() -> transactions.execute(status -> action.get()));
    }

    static boolean validSecret(String secret) {
        // BCrypt processes at most 72 bytes.
        return secret != null && !secret.isBlank() && secret.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    private String digest(String secret) {
        if (!validSecret(secret)) {
            throw CheckedException.badRequest("客户端密钥必须非空且最多为 72 个 UTF-8 字节");
        }
        return PasswordEncoderHelper.encode(secret);
    }

    UserTenantAuthentication checkBinding(Long userId, String tenantCode) {
        if (tenantCode == null && userId == null) {
            return null;
        }
        if (tenantCode == null || tenantCode.isBlank()) {
            throw CheckedException.badRequest("绑定用户必须指定有效租户");
        }
        Tenant tenant = TenantHelper.executeWithMaster(() -> tenants.selectOne(Tenant::getCode, tenantCode));
        if (tenant == null || !Boolean.TRUE.equals(tenant.getStatus())) {
            throw CheckedException.badRequest("绑定租户无效");
        }
        if (userId == null) {
            return null;
        }
        // An internal caller may also carry a browser session. Use the tenant of the binding explicitly.
        User user = TenantHelper.executeWithTenantDb(tenantCode, () -> TenantHelper.withIgnoreStrategy(
                () -> users.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, userId)
                        .eq(User::getTenantId, tenant.getId()))));
        if (user == null || !Boolean.TRUE.equals(user.getStatus())) {
            throw CheckedException.badRequest("绑定用户无效");
        }
        return new UserTenantAuthentication(user, tenant);
    }

    private static Client view(RegisteredClient row) {
        return new Client(row.getClientId(), row.getUserId(), row.getTenantCode(),
                Boolean.TRUE.equals(row.getStatus()), row.getAuthzVersion());
    }
}
