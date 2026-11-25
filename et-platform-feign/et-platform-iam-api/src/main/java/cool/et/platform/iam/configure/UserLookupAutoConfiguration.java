package cool.et.platform.iam.configure;

import cool.et.framework.commons.remote.LoadService;
import cool.et.platform.iam.contract.user.UserInfoResp;
import cool.et.platform.iam.feign.UserFeign;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@AutoConfiguration
@ConditionalOnBean(UserFeign.class)
public class UserLookupAutoConfiguration {

    // RemoteService 按 @Remote 指定的契约全限定名查找回显 Bean。
    private static final String USER_API = "cool.et.platform.iam.contract.user.UserApi";

    @Bean(name = USER_API)
    @ConditionalOnMissingBean(name = USER_API)
    LoadService<UserInfoResp> userLoadService(UserFeign users) {
        return keys -> {
            Set<Long> ids = keys.stream().map(Object::toString).map(Long::valueOf).collect(Collectors.toSet());
            var result = new HashMap<Object, UserInfoResp>();
            users.findByIds(ids).forEach((id, user) -> result.put(id.toString(), user));
            return result;
        };
    }
}
