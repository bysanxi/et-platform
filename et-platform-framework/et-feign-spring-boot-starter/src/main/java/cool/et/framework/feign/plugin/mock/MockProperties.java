package cool.et.framework.feign.plugin.mock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock 服务配置
 * <p>
 * 用于开发环境模拟远程服务调用，支持按服务名配置不同的 Mock 地址
 */
@Data
@ConfigurationProperties(prefix = MockProperties.MOCK_PREFIX)
public class MockProperties {

    public static final String MOCK_PREFIX = "extend.feign.plugin.mock";

    /**
     * 是否启用 Mock 模式
     */
    private boolean enabled;

    /**
     * 服务 Mock 映射配置
     * <p>
     * key: 服务名, value: Mock 服务器配置
     */
    private Map<String, Server> serverMap = new ConcurrentHashMap<>();

    /**
     * Mock 服务器配置
     */
    @Data
    public static class Server {
        /**
         * Mock 服务器 URL
         */
        private String serverUrl;
        /**
         * 是否使用旧版服务格式
         */
        private boolean oldServer = false;
        /**
         * 响应数据字段名
         */
        private String dataField = "data";
    }
}