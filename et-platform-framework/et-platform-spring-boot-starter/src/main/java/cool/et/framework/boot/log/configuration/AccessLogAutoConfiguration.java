package cool.et.framework.boot.log.configuration;

import cool.et.framework.boot.log.AccessLogProperties;
import cool.et.framework.boot.log.aspect.AccessLogAspect;
import cool.et.framework.boot.log.event.AccessLogListener;
import cool.et.framework.boot.log.feign.AccessLogFeign;
import cool.et.framework.boot.log.handler.AbstractLogHandler;
import cool.et.framework.boot.log.handler.DefaultHandlerAbstract;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 日志自动配置
 * <p>
 * 启动条件：
 * 1，存在web环境
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = AccessLogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class AccessLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AccessLogAspect accessLogAspect() {
        return new AccessLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractLogHandler.class)
    public AbstractLogHandler logHandle() {
        return new DefaultHandlerAbstract();
    }

    @Bean
    @Order
    @ConditionalOnExpression("'${extend.boot.log.strategy}'.equalsIgnoreCase('feign')")
    public AccessLogListener accessLogListener(AccessLogFeign feign) {
        return new AccessLogListener(feign::listener);
    }

}
