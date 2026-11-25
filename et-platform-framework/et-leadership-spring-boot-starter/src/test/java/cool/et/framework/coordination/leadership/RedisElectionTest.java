package cool.et.framework.coordination.leadership;

import cool.et.framework.coordination.leadership.common.LeaderSelectorFactory;
import cool.et.framework.coordination.leadership.configuration.LeadershipAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

class RedisElectionTest {
    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LeadershipAutoConfiguration.class));

    @Test
    void customFactoryReplacesDefaultWithoutConnectingToRedis() {
        LeaderSelectorFactory custom = (key, id, selected, removed) -> null;
        context.withBean(LeaderSelectorFactory.class, () -> custom)
                .run(ctx -> assertSame(custom, ctx.getBean(LeaderSelectorFactory.class)));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ET_LEADERSHIP_TEST_REDIS", matches = ".+")
    void contendersRenewRelinquishAndTransferLeadership() {
        var configured = context.withPropertyValues("extend.coordination.redis.servers=" +
                System.getenv("ET_LEADERSHIP_TEST_REDIS"), "extend.coordination.redis.leadership-ttl-in-milliseconds=1500");
        configured.run(first -> configured.run(second -> {
            String key = "election-test:" + UUID.randomUUID();
            CountDownLatch elected = new CountDownLatch(1);
            CountDownLatch successor = new CountDownLatch(1);
            CountDownLatch retired = new CountDownLatch(1);
            AtomicInteger terms = new AtomicInteger();
            var primary = first.getBean(LeaderSelectorFactory.class)
                    .create(key, "first", term -> {
                        if (terms.incrementAndGet() == 1) {
                            term.stop();
                        } else {
                            elected.countDown();
                        }
                    }, () -> {});
            var standby = second.getBean(LeaderSelectorFactory.class)
                    .create(key, "second", term -> successor.countDown(), retired::countDown);
            try {
                primary.start();
                assertTrue(elected.await(8, SECONDS), "Candidate should be elected again after relinquishing");
                standby.start();
                assertFalse(successor.await(3200, MILLISECONDS));
                primary.stop();
                assertTrue(successor.await(8, SECONDS));
            } finally {
                primary.stop();
                standby.stop();
            }
            assertEquals(0, retired.getCount());
        }));
    }
}
