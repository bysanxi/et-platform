package cool.et.platform.iam.configure;

import cn.dev33.satoken.same.SaSameUtil;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/** 仅供通过服务凭据调用 IAM 的 Feign 客户端使用。 */
public class IamServiceFeignConfiguration {
    @Bean
    public RequestInterceptor iamServiceSameToken() {
        return template -> {
            template.removeHeader(SaSameUtil.SAME_TOKEN);
            template.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
        };
    }
}
