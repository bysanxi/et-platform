package cool.et.framework.feign.plugin.token;

import lombok.Data;

/**
 * 自动刷新 Token 配置
 * <p>
 * 用于 Feign 内部服务调用时自动获取和刷新认证 Token
 */
@Data
public class AutoRefreshTokenProperties {

    public static final String TOKEN_PREFIX = "extend.feign.plugin.token";
    public static final String X_AUTO_TOKEN = "X-Auto-Token=true";
    public static final String X_AUTO_TOKEN_KEYWORD = "X-Auto-Token";
    public static final String AUTHORIZATION = "Authorization";

}
