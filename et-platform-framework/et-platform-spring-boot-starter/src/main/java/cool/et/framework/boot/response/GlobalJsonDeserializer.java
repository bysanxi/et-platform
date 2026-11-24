package cool.et.framework.boot.response;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cool.et.framework.commons.exception.CheckedException;
import org.springframework.boot.jackson.JacksonComponent;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

/**
 * JSON 全局反序列化器
 */
@JacksonComponent
public class GlobalJsonDeserializer {

    /**
     * 字符串反序列化器
     * 过滤特殊字符，解决 XSS 攻击
     */
    public static class StringDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            final String value = jsonParser.getValueAsString();
            if (SqlInjectionUtils.check(value)) {
                throw CheckedException.badRequest("存在SQL注入风险,已拦截");
            }
            // 替换 HTML 标签
            return HtmlUtils.htmlEscape(value);
        }
    }
}
