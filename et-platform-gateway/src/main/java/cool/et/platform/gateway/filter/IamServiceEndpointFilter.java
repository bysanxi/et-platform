package cool.et.platform.gateway.filter;

import cool.et.framework.commons.FeignConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
public class IamServiceEndpointFilter implements GlobalFilter, Ordered {
    private static final String MANAGEMENT_PATH = "/internal/oauth2/clients";

    @Override
    public int getOrder() {
        // Both StripPrefix and discovery's RewritePath have already produced the downstream path.
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI target = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if (target != null && FeignConstants.AUTH_FEIGN_NAME.equalsIgnoreCase(target.getHost())) {
            String path = StringUtils.cleanPath(target.getPath().replaceAll(";[^/]*", "").replaceAll("/{2,}", "/"))
                    .replaceFirst("/$", "");
            boolean accountOperation = HttpMethod.POST.equals(exchange.getRequest().getMethod())
                    && (path.equals("/users/lookup") || path.equals("/users/register")
                    || path.equals("/token/mobile-login") || path.matches("/users/[^/]+/password-reset"));
            if (accountOperation || path.equals(MANAGEMENT_PATH) || path.startsWith(MANAGEMENT_PATH + "/")) {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }
}
