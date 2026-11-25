package cool.et.framework.coordination.leadership.configuration;

import cool.et.framework.coordination.leadership.common.LeaderSelectorFactory;
import cool.et.framework.coordination.leadership.redis.RedisLeaderSelector;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@AutoConfiguration
@EnableConfigurationProperties(LeadershipProperties.class)
@ConditionalOnMissingBean(LeaderSelectorFactory.class)
public class LeadershipAutoConfiguration {

    // Keep the election client private so application Redis beans retain their own settings.
    record ElectionClient(RedissonClient client) implements AutoCloseable {
        @Override
        public void close() {
            client.shutdown();
        }
    }

    @Bean
    ElectionClient electionClient(LeadershipProperties properties) {
        Assert.notEmpty(properties.getServers(), "extend.coordination.redis.servers is required");
        long ttl = properties.getLeadershipTtlInMilliseconds();
        Assert.isTrue(ttl >= 300, "leadership TTL must be at least 300 ms for watchdog renewal");
        Config config = new Config();
        config.setLockWatchdogTimeout(ttl);
        String[] addresses = properties.getServers().stream()
                .map(String::trim).map(address -> "redis://" + address).toArray(String[]::new);
        String password = StringUtils.hasText(properties.getPassword()) ? properties.getPassword() : null;
        config.setPassword(password);
        switch (properties.getMode()) {
            case "single" -> config.useSingleServer().setAddress(addresses[0]);
            case "cluster" -> config.useClusterServers().addNodeAddress(addresses);
            case "sentinel" -> {
                Assert.hasText(properties.getMaster(), "extend.coordination.redis.master is required in sentinel mode");
                config.useSentinelServers().setMasterName(properties.getMaster()).addSentinelAddress(addresses)
                        .setSentinelPassword(password);
            }
            default -> throw new IllegalArgumentException("Unsupported Redis mode: " + properties.getMode());
        }
        return new ElectionClient(Redisson.create(config));
    }

    @Bean
    LeaderSelectorFactory redisLeadershipSelectorFactory(ElectionClient electionClient, LeadershipProperties properties) {
        return (lockId, leaderId, selected, removed) -> new RedisLeaderSelector(electionClient.client(),
                lockId, leaderId, properties.getLeadershipTtlInMilliseconds(), selected, removed);
    }
}
