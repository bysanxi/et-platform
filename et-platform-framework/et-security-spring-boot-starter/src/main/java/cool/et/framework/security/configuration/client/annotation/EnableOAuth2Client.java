package cool.et.framework.security.configuration.client.annotation;

import cool.et.framework.security.configuration.client.ClientResourceServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 资源服务注解
 *
 * @since 2023-08-24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@Import({ClientResourceServerConfiguration.class})
public @interface EnableOAuth2Client {
    
}
