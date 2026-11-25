package cool.et.platform.gateway.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerConfigurationTest {

    @Test
    void rendersMissingRouteAsGatewayJson() {
        try (var context = new GenericApplicationContext()) {
            context.refresh();
            var viewResolvers = ResolvableType.forClassWithGenerics(List.class, ViewResolver.class);
            var configuration = new ErrorHandlerConfiguration(null, null, new WebProperties(),
                    context.getBeanProvider(viewResolvers), ServerCodecConfigurer.create(), context);
            var handler = configuration.errorWebExceptionHandler(new DefaultErrorAttributes());
            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/missing-route"));
            handler.handle(exchange, new NoResourceFoundException(URI.create("/missing-route"), "missing-route")).block();

            assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(exchange.getResponse().getBodyAsString().block()).contains("\"code\":404", "\"successful\":false");
        }
    }
}
