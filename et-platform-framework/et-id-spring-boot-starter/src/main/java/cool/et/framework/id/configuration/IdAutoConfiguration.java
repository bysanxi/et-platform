package cool.et.framework.id.configuration;

import cool.et.framework.id.IdGenerator;
import cool.et.framework.id.contract.IdGeneratorOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties
public class IdAutoConfiguration {

    @Bean
    @ConfigurationProperties("extend.id.generator")
    public IdGeneratorOptions platformIdGeneratorOptions() {
        return new IdGeneratorOptions();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdGenerator platformIdGenerator(IdGeneratorOptions options) {
        return new IdGenerator(options);
    }
}
