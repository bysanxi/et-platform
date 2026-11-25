package cool.et.platform.iam.system;

import cool.et.framework.boot.remote.RemoteService;
import cool.et.framework.boot.remote.properties.RemoteProperties;
import cool.et.framework.commons.annotation.remote.Remote;
import cool.et.framework.commons.remote.LoadService;
import cool.et.platform.iam.contract.user.UserApi;
import cool.et.platform.iam.contract.user.UserInfoResp;
import cool.et.platform.iam.feign.UserFeign;
import cool.et.platform.iam.configure.UserLookupAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class UserRemoteLookupTest {
    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UserLookupAutoConfiguration.class));

    @Test
    void loaderIsOnlyRegisteredWhenUserFeignIsEnabled() {
        context.run(app -> assertThat(app).doesNotHaveBean(LoadService.class));
    }

    @Test
    void currentAndLegacyAnnotationsResolveThroughTheSameLoader() {
        var users = mock(UserFeign.class, withSettings().mockMaker(MockMakers.PROXY));
        var member = new UserInfoResp();
        member.setNickname("Member");
        when(users.findByIds(Set.of(42L))).thenReturn(Map.of(42L, member));

        context.withBean(UserFeign.class.getName(), UserFeign.class, () -> users).run(app -> {
            assertThat(app).hasSingleBean(LoadService.class);
            var remote = new RemoteService(new RemoteProperties(), app.getBeansOfType(LoadService.class));
            var item = new Item();
            remote.action(item);
            assertThat(item.name).isEqualTo("Member");
            assertThat(item.legacyName).isEqualTo("Member");
        });
    }

    private static class Item {
        @Remote(beanClass = UserApi.class, fields = @Remote.FieldRef(source = "nickname", target = "name"))
        private Long id = 42L;
        private String name;

        @Remote(beanClass = UserFeign.class, fields = @Remote.FieldRef(source = "nickname", target = "legacyName"))
        private String legacyId = "42";
        private String legacyName;
    }
}
