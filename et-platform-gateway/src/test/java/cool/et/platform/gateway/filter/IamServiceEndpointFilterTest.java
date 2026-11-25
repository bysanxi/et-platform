package cool.et.platform.gateway.filter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class IamServiceEndpointFilterTest {
    @ParameterizedTest
    @CsvSource({
            "/iam,et-platform-iam,POST,/users/lookup,true",
            "/et-platform-iam,et-platform-iam,POST,/users/register,true",
            "/iam,et-platform-iam,POST,/users/42/password-reset,true",
            "/et-platform-iam,et-platform-iam,POST,/token/mobile-login,true",
            "/iam,et-platform-iam,GET,/internal/oauth2/clients/abc,true",
            "/iam,et-platform-iam,POST,/token/login,false",
            "/iam,et-platform-iam,PUT,/users/42/reset-password,false",
            "/iam,et-platform-iam,GET,/users/lookup,false",
            "/other,other,POST,/users/register,false"
    })
    void filtersTheRewrittenDownstreamRoute(String prefix, String service, String method, String path, boolean blocked) {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.method(HttpMethod.valueOf(method), prefix + path));
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, URI.create("lb://" + service + path));
        var forwarded = new AtomicBoolean();
        new IamServiceEndpointFilter().filter(exchange, next -> {
            forwarded.set(true);
            return Mono.empty();
        }).block();
        assertEquals(!blocked, forwarded.get());
        if (blocked) {
            assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
        }
    }
}
