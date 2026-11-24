package cool.et.platform.iam;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.StrUtil;
import cool.et.framework.security.utils.PasswordEncoderHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器测试
 */
@Slf4j
public class PasswordEncoderHelperTest {

    private static final String RAW_PASSWORD = "123456";
    private static final String BCRYPT_ENCODED = "{bcrypt}$2a$10$R2AdNVf402GnqcJejdjY..wOHP5hFt5x0vz5qXdTVG.udcdFmqu.K";

    @Test
    void shouldEncodeAndMatchWithSpringPasswordEncoder() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        // 编码密码
        String encoded1 = passwordEncoder.encode(RAW_PASSWORD);
        String encoded2 = passwordEncoder.encode(RAW_PASSWORD);
        log.info("编码结果1: {}", encoded1);
        log.info("编码结果2: {}", encoded2);

        // 验证密码
        boolean matches = passwordEncoder.matches(RAW_PASSWORD, BCRYPT_ENCODED);
        log.info("密码匹配结果: {}", matches);
    }

    @Test
    void shouldEncodeAndMatchWithSaTokenBCrypt() {
        // BCrypt编码
        String hashed = BCrypt.hashpw(RAW_PASSWORD);
        log.info("BCrypt编码: {}", hashed);

        // BCrypt验证
        boolean checkNew = BCrypt.checkpw(RAW_PASSWORD, hashed);
        boolean checkOld = BCrypt.checkpw(RAW_PASSWORD, "$2a$10$R2AdNVf402GnqcJejdjY..wOHP5hFt5x0vz5qXdTVG.udcdFmqu.K");
        log.info("新密码验证: {}, 旧密码验证: {}", checkNew, checkOld);
    }

    @Test
    void shouldEncodeAndMatchWithPasswordEncoderHelper() {
        // 使用工具类编码
        String encoded = PasswordEncoderHelper.encode(RAW_PASSWORD);
        log.info("PasswordEncoderHelper编码: {}", encoded);

        // 验证不同格式的密码
        boolean matchBcrypt = PasswordEncoderHelper.matches(RAW_PASSWORD, BCRYPT_ENCODED);
        boolean matchNoop = PasswordEncoderHelper.matches(RAW_PASSWORD, "{noop}123456");
        boolean matchWrong = PasswordEncoderHelper.matches(RAW_PASSWORD, "{noop}1234567");

        log.info("bcrypt匹配: {}, noop匹配: {}, 错误匹配: {}", matchBcrypt, matchNoop, matchWrong);
    }

    @Test
    void shouldExtractPathPrefix() {
        String path = "/sys/message/publish";
        String prefix = StrUtil.subBefore(path, "/", true);
        log.info("路径前缀: {}", prefix);
    }
}
