package cool.et.framework.id;

import com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import cool.et.framework.id.configuration.IdAutoConfiguration;
import cool.et.framework.id.configuration.MybatisIdAutoConfiguration;
import cool.et.framework.id.contract.IdGeneratorOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class IdAutoConfigurationTest {

    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IdAutoConfiguration.class, MybatisIdAutoConfiguration.class));

    @ParameterizedTest
    @ValueSource(shorts = {1, 2})
    void manualAndMybatisIdsShareOneSequence(short method) {
        context.withPropertyValues("extend.id.generator.method=" + method, "extend.id.generator.worker-id=17",
                        "extend.id.generator.seq-bit-length=4", "extend.id.generator.max-seq-number=11")
                .run(app -> {
                    IdGenerator manual = app.getBean(IdGenerator.class);
                    IdentifierGenerator mybatis = app.getBean(IdentifierGenerator.class);
                    Set<Long> ids = ConcurrentHashMap.newKeySet();
                    IntStream.range(0, 512).parallel().forEach(i -> {
                        long id = i % 2 == 0 ? manual.next() : mybatis.nextId(null).longValue();
                        assertThat((id >>> 4) & 63).isEqualTo(17);
                        assertThat(id & 15).isBetween(5L, 11L);
                        assertThat(ids.add(id)).isTrue();
                    });
                });
    }

    @Test
    void independentWorkersKeepTheirOwnNodeBitsAndDefaultLayout() {
        IdGenerator first = new IdGenerator(new IdGeneratorOptions((short) 1));
        IdGenerator second = new IdGenerator(new IdGeneratorOptions((short) 2));
        long before = System.currentTimeMillis();
        long firstId = first.next();
        assertThat((firstId >>> 6) & 63).isEqualTo(1);
        assertThat((second.next() >>> 6) & 63).isEqualTo(2);
        assertThat((firstId >>> 12) + 1582136402000L).isBetween(before, System.currentTimeMillis());
    }

    @Test
    void manualIdsAreAvailableWithoutMybatis() {
        context.withClassLoader(new FilteredClassLoader(IdentifierGenerator.class)).run(app ->
                assertThat(app).hasSingleBean(IdGenerator.class).doesNotHaveBean("platformIdentifierGenerator"));
    }

    @Test
    void platformSequenceTakesPrecedenceOverMybatisCloudDefault() {
        context.withConfiguration(AutoConfigurations.of(IdentifierGeneratorAutoConfiguration.class))
                .withBean(InetUtils.class, () -> new InetUtils(new InetUtilsProperties()))
                .run(app -> {
                    assertThat(app).hasSingleBean(IdentifierGenerator.class);
                    assertThat(app.getBean(IdentifierGenerator.class)).isSameAs(app.getBean("platformIdentifierGenerator"));
                });
    }

    @Test
    void respectsApplicationIdentifierGenerator() {
        IdentifierGenerator custom = entity -> 42L;
        context.withBean(IdentifierGenerator.class, () -> custom).run(app ->
                assertThat(app.getBean(IdentifierGenerator.class)).isSameAs(custom));
    }
}
