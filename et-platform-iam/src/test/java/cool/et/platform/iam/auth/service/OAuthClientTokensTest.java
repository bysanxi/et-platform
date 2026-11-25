package cool.et.platform.iam.auth.service;

import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.DataPermission;
import cool.et.framework.security.domain.UserInfoDetails;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuthClientTokensTest {
    private final OAuthClientRegistry registry = mock(OAuthClientRegistry.class, withSettings().mockMaker("mock-maker-subclass"));
    private final UserService users = mock(UserService.class, withSettings().mockMaker("mock-maker-proxy"));
    private final OAuthClientTokens tokens = new OAuthClientTokens(registry, users, 3600);

    private RegisteredClient row(long version) {
        return RegisteredClient.builder().id(1L).clientId("integration").clientSecret(PasswordEncoderHelper.encode("secret"))
                .scopes("server").grantTypes("client_credentials").authzVersion(version).status(true).build();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void mutationDuringCredentialVerificationInvalidatesTheIssuedSnapshot(boolean revoke) throws Exception {
        var before = row(1);
        var after = row(2);
        after.setUserId(8L);
        after.setStatus(!revoke);
        var current = new AtomicReference<>(before);
        var selected = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        when(registry.lookup("integration")).thenAnswer(call -> {
            var snapshot = current.get();
            selected.countDown();
            assertTrue(resume.await(5, TimeUnit.SECONDS));
            return snapshot;
        });
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var issuing = executor.submit(() -> tokens.issue("integration", "secret", "server"));
            try {
                assertTrue(selected.await(5, TimeUnit.SECONDS));
                current.set(after);
            } finally {
                resume.countDown();
            }
            var token = issuing.get(5, TimeUnit.SECONDS);
            assertEquals("invalid_token", assertThrows(OAuthFailure.class,
                    () -> tokens.identity(token.accessToken())).getMessage());
            verifyNoInteractions(users);
        }
    }

    @Test
    void sameTokenReadsPermissionsAgainAndFailsClosedWhenBindingIsLost() {
        var row = row(1);
        row.setUserId(8L);
        row.setTenantCode("8888");
        when(registry.lookup("integration")).thenReturn(row);
        var binding = new UserTenantAuthentication();
        when(registry.checkBinding(8L, "8888")).thenReturn(binding);
        var dataPermission = DataPermission.builder().build();
        when(users.userinfo(binding)).thenReturn(UserInfoDetails.builder().roles(List.of("editor")).dataPermission(dataPermission).build(),
                UserInfoDetails.builder().roles(List.of()).dataPermission(dataPermission).build(),
                UserInfoDetails.builder().build());
        var token = tokens.issue("integration", "secret", "server").accessToken();
        assertEquals(List.of("editor"), tokens.identity(token).roles());
        assertTrue(tokens.identity(token).roles().isEmpty());
        assertEquals(500, assertThrows(OAuthFailure.class, () -> tokens.identity(token)).getStatus());
        when(registry.checkBinding(8L, "8888")).thenThrow(CheckedException.badRequest("disabled"));
        assertEquals(401, assertThrows(OAuthFailure.class, () -> tokens.identity(token)).getStatus());
    }

    @Test
    void grantsScopeAndCredentialValidityAreChecked() {
        var row = row(1);
        when(registry.lookup("integration")).thenReturn(row);
        assertEquals("invalid_scope", assertThrows(OAuthFailure.class,
                () -> tokens.issue("integration", "secret", "other")).getMessage());
        row.setClientSecretExpiresAt(Instant.now().minusSeconds(1));
        assertEquals("invalid_client", assertThrows(OAuthFailure.class,
                () -> tokens.issue("integration", "secret", "server")).getMessage());
        row.setGrantTypes("not_password,password_extra");
        assertFalse(row.allowsGrant("password"));
        row.setGrantTypes("client_credentials,password");
        assertTrue(row.allowsGrant("password"));
    }
}
