package cool.et.framework.boot.remote.configuration;

import cool.et.framework.boot.remote.RemoteService;
import cool.et.framework.boot.remote.dict.DictLoadService;
import cool.et.framework.boot.remote.properties.RemoteProperties;
import cool.et.framework.commons.remote.LoadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * 关联字段数据注入工具 自动配置类
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(RemoteProperties.class)
@ConditionalOnProperty(prefix = RemoteProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RemoteAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RemoteResultAspect getRemoteResultAspect(RemoteService remoteService) {
        return new RemoteResultAspect(remoteService);
    }

    @Bean
    @ConditionalOnMissingBean
    public DictLoadService dictLoadService(@Qualifier("remoteAloneRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new DictLoadService(redisTemplate);
    }

    /**
     * 回显服务
     *
     * @param strategyMap 回显查询实例
     * @return RemoteService
     */
    @Bean
    @ConditionalOnMissingBean
    public RemoteService getRemoteService(RemoteProperties remoteProperties, Map<String, LoadService> strategyMap) {
        return new RemoteService(remoteProperties, strategyMap);
    }
}
