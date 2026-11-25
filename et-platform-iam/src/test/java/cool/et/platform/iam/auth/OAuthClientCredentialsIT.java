package cool.et.platform.iam.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.et.platform.iam.feign.OAuthClientFeign;
import cool.et.platform.iam.feign.UserFeign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import cn.dev33.satoken.same.SaSameUtil;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static cool.et.platform.iam.contract.oauth.OAuthClientApi.*;
import static cool.et.platform.iam.feign.OAuthClientFeign.PATH;
import static org.junit.jupiter.api.Assertions.*;

/** Run against a freshly initialized isolated stack; Feign resolves the running IAM through Nacos. */
@SpringBootTest(properties = {"spring.cloud.nacos.discovery.register-enabled=false", "extend.security.enabled-oauth2=false"})
@EnabledIfEnvironmentVariable(named = "ET_OAUTH_IT_GATEWAY", matches = ".+")
class OAuthClientCredentialsIT {
    @Autowired OAuthClientFeign clients;
    @Autowired UserFeign users;
    @Autowired JdbcTemplate db;
    @Autowired org.springframework.context.ApplicationContext context;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper();
    private final String gateway = System.getenv("ET_OAUTH_IT_GATEWAY");
    private final String iam = System.getenv("ET_OAUTH_IT_IAM");
    private final String prefix = "it-" + UUID.randomUUID();
    private final String secret = UUID.randomUUID() + ":+%";

    @Test
    void registerThroughFeignAndExerciseGatewayAndLifecycle() throws Exception {
        var first = clients.register(new Registration(prefix+"-a", secret, 1L, "0000"));
        var second = clients.register(new Registration(prefix+"-b", secret, 8L, "8888"));
        var app = clients.register(new Registration(prefix, secret, null, null));
        assertEquals(app, clients.find(prefix));
        assertThrows(RuntimeException.class, () -> clients.register(new Registration(prefix, secret, null, null)));
        assertTrue(context.getBeansOfType(cool.et.platform.iam.auth.controller.OAuthClientTokenController.class).isEmpty());
        String digest = db.queryForObject("SELECT client_secret FROM sys_registered_client WHERE client_id=?", String.class, prefix);
        assertTrue(digest.startsWith("{bcrypt}"));
        assertTrue(cool.et.framework.security.utils.PasswordEncoderHelper.matches(secret, digest));
        assertNotNull(users.findById(1L));
        assertThrows(RuntimeException.class, () -> clients.bind(prefix, new Binding(1L, "8888")));

        var a = token(first.clientId(), secret);
        var b = token(second.clientId(), secret);
        var machine = token(prefix, secret);
        assertEquals("1", identity(a).path("userId").asText());
        var secondIdentity = identity(b);
        assertEquals("8", secondIdentity.path("userId").asText());
        assertEquals("8888", secondIdentity.path("tenantCode").asText());
        assertFalse(secondIdentity.path("roles").isEmpty());
        assertTrue(identity(machine).get("userId").isNull());
        var rebound = clients.bind(prefix, new Binding(1L, "0000"));
        assertEquals(app.authzVersion()+1, rebound.authzVersion());
        invalid(machine);
        var bound = token(prefix, secret);
        clients.bind(prefix, new Binding(null, null));
        invalid(bound);
        var unbound = token(prefix, secret);
        clients.rotate(prefix, new NewSecret(secret+"2"));
        error(exchange(prefix, secret, "client_credentials", "server"), 401, "invalid_client");
        assertTrue(identity(unbound).get("userId").isNull());
        String rotated = token(prefix, secret+"2");
        clients.revoke(prefix);
        invalid(rotated);
        error(exchange(prefix, secret+"2", "client_credentials", "server"), 401, "invalid_client");
        clients.disable(first.clientId());
        invalid(a);
        error(exchange(second.clientId(), secret, "password", "server"), 400, "unsupported_grant_type");
        error(exchange(second.clientId(), secret, "client_credentials", "other"), 400, "invalid_scope");
        error(exchange(second.clientId(), secret, "client_credentials", ""), 400, "invalid_scope");
        assertEquals(404, send(gateway+"/iam"+PATH, null).statusCode());
        assertEquals(404, send(gateway+"/et-platform-iam"+PATH+"/"+prefix, null).statusCode());
        assertEquals(401, send(iam+PATH+"/"+prefix, null).statusCode());
        var badSame = HttpRequest.newBuilder(URI.create(iam+PATH+"/"+prefix))
                .header(SaSameUtil.SAME_TOKEN, "invalid-same-token").GET().build();
        var denied = http.send(badSame, HttpResponse.BodyHandlers.ofString());
        assertFalse(json.readTree(denied.body()).path("successful").asBoolean(true));
        assertEquals(401, send(iam+PATH+"/"+prefix, "Bearer "+b).statusCode());
        invalid("missing-token");
        assertEquals(401, send(gateway+"/iam/oauth2/identity", null).statusCode());
    }

    @Test
    void existingSessionAndInternalCallsRemainSeparate() throws Exception {
        var login = json.createObjectNode().put("username", "admin").put("tenantCode", "0000")
                .put("password", "123456").put("loginType", "password")
                .put("clientId", "pc-web").put("clientSecret", "pc-web");
        String session = login(login).path("data").path("accessToken").asText();
        assertFalse(session.isBlank());
        assertEquals("1", json.readTree(send(gateway+"/iam/token/userinfo", "Bearer "+session).body())
                .path("data").path("userId").asText());
        invalid(session);
        clients.register(new Registration(prefix, secret, null, null));
        var management = send(iam+PATH+"/"+prefix, "Bearer "+session);
        assertFalse(json.readTree(management.body()).path("successful").asBoolean(true));
        var internalBinding = HttpRequest.newBuilder(URI.create(iam + PATH + "/" + prefix + "/binding"))
                .header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()).header("Authorization", "Bearer " + session)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString("{\"userId\":8,\"tenantCode\":\"8888\"}")).build();
        assertTrue(json.readTree(http.send(internalBinding, HttpResponse.BodyHandlers.ofString()).body())
                .path("successful").asBoolean());
        assertFalse(login(login.put("clientId", prefix).put("clientSecret", secret)).path("successful").asBoolean(true));
        var onlySame = HttpRequest.newBuilder(URI.create(iam+"/oauth2/identity"))
                .header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken()).GET().build();
        error(http.send(onlySame, HttpResponse.BodyHandlers.ofString()), 401, "invalid_token");
    }

    private JsonNode login(JsonNode request) throws Exception {
        var response = http.send(HttpRequest.newBuilder(URI.create(gateway+"/iam/token/login"))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                .build(), HttpResponse.BodyHandlers.ofString());
        return json.readTree(response.body());
    }

    @Test
    void tenantDisableRejectsPreviouslyIssuedTenantToken() throws Exception {
        String code = prefix.substring(0, 18);
        db.update("INSERT INTO t_tenant (code, name, status) VALUES (?, ?, true)", code, prefix);
        try {
            clients.register(new Registration(prefix, secret, null, code));
            String bearer = token(prefix, secret);
            assertEquals(code, identity(bearer).get("tenantCode").asText());
            db.update("UPDATE t_tenant SET status=false WHERE code=?", code);
            invalid(bearer);
        } finally {
            db.update("DELETE FROM t_tenant WHERE code=?", code);
        }
    }

    @Test
    void sameBearerReflectsLivePermissionsAndUserStatus() throws Exception {
        String username = prefix.substring(0, 26);
        db.update("INSERT INTO t_user (tenant_id, username, status) VALUES (8, ?, true)", username);
        long userId = db.queryForObject("SELECT id FROM t_user WHERE tenant_id=8 AND username=?", Long.class, username);
        try {
            db.update("INSERT INTO sys_user_role (role_id, user_id) VALUES (8, ?)", userId);
            clients.register(new Registration(prefix, secret, userId, "8888"));
            String bearer = token(prefix, secret);
            var before = identity(bearer);
            assertFalse(before.path("roles").isEmpty());
            db.update("UPDATE sys_user_role SET deleted=true WHERE user_id=?", userId);
            var after = identity(bearer);
            assertTrue(after.path("roles").isEmpty());
            assertTrue(after.path("funcPermissions").isEmpty());
            assertNotEquals(before.path("dataPermission"), after.path("dataPermission"));
            db.update("UPDATE t_user SET status=false WHERE id=?", userId);
            invalid(bearer);
        } finally {
            db.update("DELETE FROM sys_user_role WHERE user_id=?", userId);
            db.update("DELETE FROM t_user WHERE id=?", userId);
        }
    }

    @Test
    void tokenExpiresInRedis() throws Exception {
        clients.register(new Registration(prefix, secret, null, null));
        JsonNode body = json.readTree(exchange(prefix, secret, "client_credentials", "server").body());
        int ttl = body.get("expires_in").asInt();
        org.junit.jupiter.api.Assumptions.assumeTrue(ttl <= 30, "联调配置短 TTL 后验证到期");
        String bearer = body.get("access_token").asText();
        assertTrue(identity(bearer).get("userId").isNull());
        Thread.sleep(java.time.Duration.ofSeconds(ttl + 1L));
        invalid(bearer);
    }

    private String token(String id, String secret) throws Exception {
        var reply = exchange(id, secret, "client_credentials", "server");
        assertEquals(200, reply.statusCode());
        assertEquals("no-store", reply.headers().firstValue("cache-control").orElseThrow());
        JsonNode body = json.readTree(reply.body());
        assertEquals(4, body.size());
        assertEquals("Bearer", body.get("token_type").asText());
        assertTrue(body.get("expires_in").isIntegralNumber());
        assertEquals("server", body.get("scope").asText());
        return body.get("access_token").asText();
    }

    private HttpResponse<String> exchange(String id, String secret, String grant, String scope) throws Exception {
        var pair = id+":"+URLEncoder.encode(secret, StandardCharsets.UTF_8);
        var basic = Base64.getEncoder().encodeToString(pair.getBytes(StandardCharsets.UTF_8));
        return http.send(HttpRequest.newBuilder(URI.create(gateway+"/iam/oauth2/token"))
                .header("Authorization","Basic "+basic).header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type="+grant+"&scope="+scope)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode identity(String token) throws Exception {
        var reply = send(gateway+"/iam/oauth2/identity?userId=999&tenantCode=other", "Bearer "+token);
        assertEquals(200, reply.statusCode());
        return json.readTree(reply.body());
    }

    private void invalid(String token) throws Exception {
        error(send(gateway+"/iam/oauth2/identity", "Bearer "+token), 401, "invalid_token");
    }

    private void error(HttpResponse<String> reply, int status, String name) throws Exception {
        assertEquals(status, reply.statusCode());
        assertEquals(name, json.readTree(reply.body()).path("error").asText());
        if (status == 401) assertTrue(reply.headers().firstValue("WWW-Authenticate").isPresent());
    }

    private HttpResponse<String> send(String url, String authorization) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(url));
        if (authorization != null) builder.header("Authorization", authorization);
        return http.send(builder.GET().build(), HttpResponse.BodyHandlers.ofString());
    }
}
