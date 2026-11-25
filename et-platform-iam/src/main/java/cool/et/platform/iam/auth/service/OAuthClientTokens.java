package cool.et.platform.iam.auth.service;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.DataPermission;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import cool.et.platform.iam.system.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static cool.et.platform.iam.auth.service.OAuthClientRegistry.GRANT;
import static cool.et.platform.iam.auth.service.OAuthClientRegistry.SCOPE;

@Service
public class OAuthClientTokens {
    public record Token(@JsonProperty("access_token") String accessToken, @JsonProperty("token_type") String tokenType,
                        @JsonProperty("expires_in") int expiresIn, String scope) {}

    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record Identity(String clientId, String tenantCode, Long userId, Collection<String> roles,
                           Collection<String> funcPermissions, DataPermission dataPermission) {}

    private final OAuthClientRegistry registry;
    private final UserService users;
    private final int ttl;

    public OAuthClientTokens(OAuthClientRegistry registry, UserService users,
                            @Value("${extend.security.server.client-token-ttl-seconds:3600}") int ttl) {
        if (ttl < 2) {
            throw new IllegalArgumentException("client-token-ttl-seconds must be at least 2");
        }
        this.registry = registry;
        this.users = users;
        this.ttl = ttl;
    }

    public Token issue(String id, String secret, String scope) {
        RegisteredClient row = registry.lookup(id);
        if (!active(row) || !id.equals(row.getClientId()) || !OAuthClientRegistry.validSecret(secret)
                || row.getClientSecret() == null || !row.getClientSecret().startsWith("{bcrypt}")
                || !PasswordEncoderHelper.matches(secret, row.getClientSecret())) {
            throw OAuthFailure.invalidClient();
        }
        if (!SCOPE.equals(scope) || !SCOPE.equals(row.getScopes())) {
            throw OAuthFailure.badRequest("invalid_scope");
        }
        var client = new SaClientModel().setClientId(row.getClientId()).setClientTokenTimeout(ttl);
        var token = SaOAuth2Manager.getDataConverter().convertSaClientToClientToken(client, List.of(SCOPE));
        // The version comes from the same row as the verified digest, even if a mutation commits during issuance.
        token.extraData.put("registrationId", row.getId().toString());
        token.extraData.put("authzVersion", row.getAuthzVersion().toString());
        SaOAuth2Manager.getDao().saveClientToken(token);
        return new Token(token.clientToken, "Bearer", ttl, SCOPE);
    }

    public Identity identity(String bearer) {
        ClientTokenModel token = SaOAuth2Manager.getDao().getClientToken(bearer);
        if (token == null || token.expiresTime <= System.currentTimeMillis() || !GRANT.equals(token.grantType)
                || token.extraData == null || token.scopes == null || !token.scopes.contains(SCOPE)) {
            throw OAuthFailure.invalidToken();
        }
        RegisteredClient row = registry.lookup(token.clientId);
        if (!active(row) || !SCOPE.equals(row.getScopes())
                || !row.getId().toString().equals(token.extraData.get("registrationId"))
                || !row.getAuthzVersion().toString().equals(token.extraData.get("authzVersion"))) {
            throw OAuthFailure.invalidToken();
        }
        try {
            var binding = registry.checkBinding(row.getUserId(), row.getTenantCode());
            if (row.getUserId() == null) {
                return new Identity(row.getClientId(), row.getTenantCode(), null, List.of(), List.of(), null);
            }
            var permissions = users.userinfo(binding);
            if (permissions == null || permissions.getRoles() == null || permissions.getFuncPermissions() == null
                    || permissions.getDataPermission() == null) {
                throw new OAuthFailure(500, "server_error", null);
            }
            return new Identity(row.getClientId(), row.getTenantCode(), row.getUserId(), permissions.getRoles(),
                    permissions.getFuncPermissions(), permissions.getDataPermission());
        } catch (CheckedException ex) {
            throw OAuthFailure.invalidToken();
        }
    }

    private static boolean active(RegisteredClient client) {
        Instant now = Instant.now();
        return client != null && Boolean.TRUE.equals(client.getStatus()) && client.allowsGrant(GRANT)
                && (client.getClientIdIssuedAt() == null || !client.getClientIdIssuedAt().isAfter(now))
                && (client.getClientSecretExpiresAt() == null || client.getClientSecretExpiresAt().isAfter(now));
    }
}
