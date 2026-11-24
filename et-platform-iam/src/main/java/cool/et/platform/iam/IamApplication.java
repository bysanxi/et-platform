package cool.et.platform.iam;

import cool.et.framework.boot.log.event.AccessLogListener;
import cool.et.framework.security.configuration.server.annotation.EnableOAuth2Server;
import cool.et.platform.iam.base.service.OptLogService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;

/**
 * 鉴权服务启动类.
 */
@Slf4j
@EnableCaching
@EnableOAuth2Server
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "cool.et")
@MapperScan(value = "cool.et.**.repository", annotationClass = Repository.class)
public class IamApplication {
    
    /**
     * 启动类.
     *
     * @param args 启动参数
     */
    @SneakyThrows
    public static void main(final String[] args) {
        final ConfigurableApplicationContext applicationContext = SpringApplication.run(IamApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        final String appName = env.getProperty("spring.application.name");
        String host = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("""
                
                ----------------------------------------------------------
                \tApplication '{}' is running! Access URLs:
                \tDoc: \thttp://{}:{}/doc.html
                ----------------------------------------------------------""",
                appName, host, port);
    }
    
    /**
     * 操作日志监听回调.
     *
     * @param optLogService 操作日志存储服务
     * @return AccessLogListener
     */
    @Bean
    public AccessLogListener accessLogListener(final OptLogService optLogService) {
        return new AccessLogListener(optLogService::listener);
    }
    
}
