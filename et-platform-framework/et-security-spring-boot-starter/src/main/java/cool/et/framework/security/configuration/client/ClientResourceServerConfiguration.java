package cool.et.framework.security.configuration.client;

import cool.et.framework.security.configuration.SecurityExtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityExtProperties.class)
public class ClientResourceServerConfiguration {
    
    private final SecurityExtProperties properties;
    
}
