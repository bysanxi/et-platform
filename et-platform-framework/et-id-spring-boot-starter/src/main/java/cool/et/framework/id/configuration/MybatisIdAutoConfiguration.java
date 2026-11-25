package cool.et.framework.id.configuration;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import cool.et.framework.id.IdGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = IdAutoConfiguration.class,
        beforeName = "com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration")
@ConditionalOnClass(IdentifierGenerator.class)
public class MybatisIdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdentifierGenerator platformIdentifierGenerator(IdGenerator generator) {
        return entity -> generator.next();
    }
}
