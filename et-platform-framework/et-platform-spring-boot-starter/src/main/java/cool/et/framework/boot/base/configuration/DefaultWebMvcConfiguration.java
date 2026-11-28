package cool.et.framework.boot.base.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import cool.et.framework.boot.base.HttpInterceptor;
import cool.et.framework.boot.base.converter.*;
import cool.et.framework.commons.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 基础配置类
 */
@Slf4j
@Configuration
public class DefaultWebMvcConfiguration implements WebMvcConfigurer {

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    /**
     * 枚举类的转换器工厂 addConverterFactory
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IntegerCodeToEnumConverterFactory());
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new HttpInterceptor());
    }

    /**
     * 提供全局 ObjectMapper Bean
     * Spring Boot 4 模块化了 Jackson 自动配置，手动提供并配置 ObjectMapper
     * 本地日期时间沿用平台格式，Instant 使用标准 ISO-8601。
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 基础配置
        objectMapper.setLocale(Locale.CHINA);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.setDateFormat(new java.text.SimpleDateFormat(pattern));
        // 反序列化时，忽略JSON字符串中存在而Java对象实际没有的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 序列化时，如果是空对象，不抛出异常
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 时间戳序列化为ISO-8601格式的字符串，而不是数字
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Long 类型序列化为 String，防止前端精度丢失（包含包装类型和原始类型）
        SimpleModule longModule = new SimpleModule();
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        longModule.addSerializer(long.class, ToStringSerializer.instance);
        objectMapper.registerModule(longModule);
        objectMapper.registerModule(JacksonUtils.createJavaTimeModule());
        return objectMapper;
    }

    /**
     * 将请求参数中的日期文本转换为 Date。
     */
    @Bean
    public Converter<String, Date> dateConvert() {
        return new String2DateConverter();
    }

    /**
     * 将请求参数中的日期文本转换为 LocalDate。
     */
    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new String2LocalDateConverter();
    }

    /**
     * 将请求参数中的时间文本转换为 LocalTime。
     */
    @Bean
    public Converter<String, LocalTime> localTimeConverter() {
        return new String2LocalTimeConverter();
    }

    /**
     * 将请求参数中的日期时间文本转换为 LocalDateTime。
     */
    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new String2LocalDateTimeConverter();
    }

}
