package cool.et.framework.security.configuration;


import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties(SecurityExtProperties.class)
public class OAuth2AutoConfiguration implements WebMvcConfigurer {
    @Resource
    private SecurityExtProperties extProperties;
    @Resource
    HttpServletRequest request;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 兼容sse关闭请求时的异步回调处理
            if (request != null && request.getDispatcherType() == DispatcherType.ASYNC) {
                return;
            }
            // 拦截的 path 列表，可以写多个 /**
            SaRouter.match("/**")
                    .notMatch(extProperties.getDefaultIgnoreUrls())
                    .notMatch(extProperties.getIgnore().getResourceUrls())
                    .check(r -> {
                        // 校验 Same-Token 身份凭证     —— 以下两句代码可简化为：SaSameUtil.checkCurrentRequestToken();
                        String token = SaHolder.getRequest().getHeader(SaSameUtil.SAME_TOKEN);
                        if (StrUtil.isNotBlank(token)) {
                            SaSameUtil.checkToken(token);
                        } else {
                            StpUtil.checkLogin();
                        }
                    });
            // 设置较低优先级，确保全局异常处理器先执行
        })).excludePathPatterns("/error").addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
    }
}
