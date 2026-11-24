package cool.et.platform.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cool.et.platform.gateway.utils.MonoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Profile("demo")
public class DemoGlobalFilter implements GlobalFilter {


    private static final Map<HttpMethod, List<String>> IGNORE_URL = Map.of(HttpMethod.POST, List.of("/resources/create"),
            HttpMethod.DELETE, List.of("/token/logout"));

    private boolean isReject(ServerWebExchange exchange) {
        HttpMethod method = exchange.getRequest().getMethod();
        List<String> urlList = IGNORE_URL.get(method);
        if (CollUtil.isEmpty(urlList)) {
            return false;
        }
        String path = exchange.getRequest().getURI().getPath();
        return urlList.contains(path);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();
        if (method == HttpMethod.PUT || method == HttpMethod.DELETE || isReject(exchange)) {
            return MonoHelper.wrap(exchange, "演示环境,禁止破坏基础数据,请下载代码自行部署（部署后请移除 gateway 服务中的 DemoGlobalFilter ）");
        }
        return chain.filter(exchange);
    }

}
