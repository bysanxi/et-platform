package cool.et.framework.feign.plugin;

import cool.et.framework.feign.plugin.mock.MockProperties;
import feign.Logger;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = FeignPluginProperties.PLUGIN_PREFIX)
public class FeignPluginProperties {

    public static final String PLUGIN_PREFIX = "extend.feign.plugin";
    private boolean enabled = true;
    /**
     * Header 白名单
     * 直接在这里初始化默认值
     */
    private List<String> allowedHeaders = new ArrayList<>();
    /**
     * Feign 日志级别（FULL 意味着会输出详细日志,建议值在非生产环境使用）
     */
    private Logger.Level level = Logger.Level.FULL;
    private MockProperties mock = new MockProperties();

}