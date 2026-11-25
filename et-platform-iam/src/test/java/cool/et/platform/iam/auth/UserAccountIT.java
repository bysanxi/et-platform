package cool.et.platform.iam.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import cool.et.platform.iam.feign.UserAccountFeign;
import cool.et.platform.iam.contract.account.UserAccountApi.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.address=127.0.0.1", "server.port=15979", "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.nacos.discovery.enabled=false", "spring.cloud.openfeign.client.config.userAccount.url=http://127.0.0.1:15979"
})
@EnabledIfEnvironmentVariable(named = "ET_ACCOUNT_IT", matches = "true")
class UserAccountIT {
    @Autowired UserAccountFeign accounts;
    @Autowired JdbcTemplate db;
    @Autowired StringRedisTemplate redis;

    @Test
    void emailOnlySignupAndLookupDistinguishAbsenceFromFailure() throws Exception {
        String email = UUID.randomUUID() + "@example.test";
        assertNull(accounts.lookup(new Lookup("8888", LookupType.email, email)));
        assertThrows(RuntimeException.class, () -> accounts.lookup(new Lookup("absent", LookupType.email, email)));
        var id = accounts.register(new Registration("8888", email, null, null)).userId();
        assertNotNull(id);
        var account = accounts.lookup(new Lookup("8888", LookupType.email, email.toUpperCase(java.util.Locale.ROOT)));
        assertEquals(id, account.id());
        assertTrue(account.status());
        assertEquals(account, accounts.lookup(new Lookup("8888", LookupType.username, account.username())));
        assertNull(accounts.lookup(new Lookup("0000", LookupType.email, email)));
        assertNull(db.queryForObject("SELECT password FROM t_user WHERE id=?", String.class, id));
        assertFalse(passwordLogin(account.username(), "Example123!"));
        assertThrows(RuntimeException.class, () -> accounts.register(new Registration("8888", email, null, null)));
        assertThrows(RuntimeException.class, () -> accounts.register(new Registration("8888", email, "13900002222", null)));
        assertThrows(RuntimeException.class, () -> accounts.register(new Registration("8888", null, null, null)));
        assertThrows(RuntimeException.class, () -> accounts.register(new Registration("8888", "bad-address", null, null)));
        assertThrows(RuntimeException.class, () -> accounts.register(new Registration("0000", email, null, "")));
        assertNotEquals(id, accounts.register(new Registration("0000", email, null, null)).userId());
    }

    @Test
    void smsLoginPreservesPasswordAndRecoveryRevokesSession() throws Exception {
        String mobile = "139" + String.format("%08d", java.util.concurrent.ThreadLocalRandom.current().nextInt(100000000));
        String initial = "First123!";
        String updated = "Recovered123!";
        Long id = accounts.register(new Registration("8888", null, mobile, initial)).userId();
        String selectPassword = "SELECT password FROM t_user WHERE id=?";
        String digest = db.queryForObject(selectPassword, String.class, id);
        assertTrue(PasswordEncoderHelper.matches(initial, digest));
        var session = accounts.mobileLogin(new MobileLogin("8888", mobile));
        assertTrue(redis.hasKey("Authorization:login:token:" + session.getAccessToken()));
        assertTrue(redis.hasKey("Authorization:var:same-token"));
        assertEquals("mobile", session.getClientId());
        assertEquals(digest, db.queryForObject(selectPassword, String.class, id));
        assertTrue(session.getExpiresIn() > 0);
        var http = HttpClient.newHttpClient();
        var userInfo = HttpRequest.newBuilder(URI.create("http://127.0.0.1:15979/token/userinfo"))
                .header("Authorization", "Bearer " + session.getAccessToken()).GET().build();
        var json = new ObjectMapper();
        var body = json.readTree(http.send(userInfo, HttpResponse.BodyHandlers.ofString()).body());
        assertEquals(id.longValue(), body.path("data").path("userId").asLong());
        var untrusted = HttpRequest.newBuilder(URI.create("http://127.0.0.1:15979/token/mobile-login"))
                .header("Authorization", "Bearer " + session.getAccessToken()).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(new MobileLogin("8888", mobile)))).build();
        var denied = http.send(untrusted, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, json.readTree(denied.body()).path("code").asInt());
        assertThrows(RuntimeException.class, () -> accounts.resetPassword(id, new PasswordReset("0000", updated)));
        assertEquals(id, accounts.resetPassword(id, new PasswordReset("8888", updated)).userId());
        assertTrue(PasswordEncoderHelper.matches(updated, db.queryForObject(selectPassword, String.class, id)));
        assertEquals(401, http.send(userInfo, HttpResponse.BodyHandlers.discarding()).statusCode());
        String username = accounts.lookup(new Lookup("8888", LookupType.mobile, mobile)).username();
        assertFalse(passwordLogin(username, initial));
        assertTrue(passwordLogin(username, updated));
        db.update("UPDATE t_user SET status=false WHERE id=?", id);
        assertFalse(accounts.lookup(new Lookup("8888", LookupType.mobile, mobile)).status());
        assertThrows(RuntimeException.class, () -> accounts.mobileLogin(new MobileLogin("8888", mobile)));
    }

    private boolean passwordLogin(String username, String password) throws Exception {
        var clientSecret = db.queryForObject("SELECT client_secret FROM sys_registered_client WHERE client_id='pc-web'", String.class);
        var json = new ObjectMapper();
        var payload = Map.of("tenantCode", "8888", "username", username, "password", password,
                "loginType", "password", "clientId", "pc-web", "clientSecret", clientSecret);
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:15979/token/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload))).build();
        return json.readTree(HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body())
                .path("code").asInt() == 200;
    }
}
