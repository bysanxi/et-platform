package cool.et.framework.security.configuration.client.annotation;

import java.lang.annotation.*;

/**
 * 加上该注解，接口地址将无法获得安全保护
 * 升级 spring7 后作废
 * @since 2019-04-08
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreFeignAuthorize {

    /**
     * 默认全局忽略(如果是 false 那么就是内网忽略,外网保护)
     *
     * @return true | false
     */
    boolean global() default true;

}
