package cool.et.framework.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.TimeZone;

import static cool.et.framework.commons.times.TimeConstants.DEFAULT_DATE_FORMAT;
import static cool.et.framework.commons.times.TimeConstants.DEFAULT_DATE_TIME_FORMAT;
import static cool.et.framework.commons.times.TimeConstants.DEFAULT_TIME_FORMAT;

/**
 * Jackson JSON工具类
 * <p>提供统一的JSON序列化/反序列化方法，支持Java8时间API</p>
 *
 * <h3>线程安全</h3>
 * <p>ObjectMapper实例在初始化后不可变，多线程访问安全</p>
 *
 * @since 1.0.0
 */
public final class JacksonUtils {


    private JacksonUtils() {
        throw new UnsupportedOperationException();
    }

    private static final ObjectMapper defaultMapper;

    private static final ObjectMapper nonNullMapper;

    public static ObjectMapper getObjectMapper() {
        return createDefaultObjectMapper();
    }

    /** 创建带平台本地日期时间格式的标准模块，调用方可在注册前定制其他类型。 */
    public static JavaTimeModule createJavaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter date = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        DateTimeFormatter time = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

        module.addSerializer(LocalDate.class, new LocalDateSerializer(date));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(date));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(time));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(time));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTime));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTime));
        return module;
    }

    static {
        defaultMapper = createDefaultObjectMapper();
        ObjectMapper createNonNullMapper = createDefaultObjectMapper();
        // 序列化时，忽略值为null的属性
        createNonNullMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        nonNullMapper = createNonNullMapper;
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 反序列化时，忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 序列化时，如果是空对象，不抛出异常
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 时间处理
        mapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT));
        mapper.setLocale(Locale.CHINA);
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        JavaTimeModule javaTimeModule = createJavaTimeModule();
        // 工具 Mapper 沿用本地秒级 Instant 文本，并支持读回 ISO-8601。
        javaTimeModule.addSerializer(Instant.class, new GlobalInstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new CustomInstantDeserializer());
        SimpleModule longToStringModule = new SimpleModule();
        longToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
        longToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        longToStringModule.addSerializer(long.class, ToStringSerializer.instance);
        // 以字符串写出日期时间。
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(longToStringModule);
        mapper.registerModule(javaTimeModule);
        return mapper;
    }

    private static class GlobalInstantSerializer extends InstantSerializer {
        public GlobalInstantSerializer() {
            super(InstantSerializer.INSTANCE, false, false, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
        }
    }


    /**
     * 自定义的 Instant 反序列化器
     * 这个实现更加健壮，因为它直接处理没有时区信息的日期时间字符串。
     */
    private static class CustomInstantDeserializer extends JsonDeserializer<Instant> {
        // 创建一个不带时区信息的formatter
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText().trim();
            if (dateString.isEmpty()) {
                return null;
            }

            try {
                // 1. 尝试使用我们自定义的格式进行解析
                LocalDateTime localDateTime = LocalDateTime.parse(dateString, FORMATTER);
                // 2. 将解析出的 LocalDateTime 按照系统默认时区转换为 Instant
                //    (重要：请确保服务的部署时区是你期望的)
                return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException e) {
                // 继续尝试 ISO-8601 时间文本。
                try {
                    return Instant.parse(dateString);
                } catch (DateTimeParseException ex) {
                    // 两个都失败了，才抛出异常
                    throw new IOException(String.format(
                            "Failed to deserialize Instant from string \"%s\". Tried formats: [%s, ISO-8601].",
                            dateString, DEFAULT_DATE_TIME_FORMAT
                    ), ex);
                }
            }
        }
    }

    /**
     * 转json字符串
     *
     * @param obj obj
     * @return json string
     * @throws RuntimeException if transfer failed
     */
    public static String writeValueAsString(Object obj) {
        try {
            return defaultMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 转json字符串 忽略null属性
     *
     * @param obj
     * @return
     */
    public static String writeValueAsNonNullString(Object obj) {
        try {
            return nonNullMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对象转json字符串 忽略null属性
     *
     * @param obj
     * @return
     */
    public static String toNonNullJson(Object obj) {
        return writeValueAsNonNullString(obj);
    }

    /**
     * 对象转json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return writeValueAsString(obj);
    }

    public static String toJsonStr(Object obj) {
        return writeValueAsString(obj);
    }

    /**
     * Object to json string byte array.
     *
     * @param obj obj
     * @return json string byte array
     * @throws RuntimeException if transfer failed
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            return defaultMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(byte[] json, Class<T> cls) {
        try {
            return defaultMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(byte[] json, Class<T> cls) {
        try {
            return defaultMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(byte[] json, Type cls) {
        try {
            return defaultMapper.readValue(json, defaultMapper.constructType(cls));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(byte[] json, Type cls) {
        try {
            return defaultMapper.readValue(json, defaultMapper.constructType(cls));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param cls         class of object
     * @param <T>         General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(InputStream inputStream, Class<T> cls) {
        try {
            return defaultMapper.readValue(inputStream, cls);
        } catch (IOException e) {
            throw new java.lang.RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string byte array
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(byte[] json, TypeReference<T> typeReference) {
        try {
            return defaultMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(String json, Class<T> cls) {
        try {
            return defaultMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toBean(String json, Class<T> cls) {
        try {
            return defaultMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param type {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(String json, Type type) {
        try {
            return defaultMapper.readValue(json, defaultMapper.constructType(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(String json, TypeReference<T> typeReference) {
        try {
            return defaultMapper.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(InputStream stream, TypeReference<T> typeReference) {
        try {
            return defaultMapper.readValue(stream, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param type        {@link Type} of object
     * @param <T>         General type
     * @return object
     * @throws RuntimeException if deserialize failed
     */
    public static <T> T readValue(InputStream inputStream, Type type) {
        try {
            return defaultMapper.readValue(inputStream, defaultMapper.constructType(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Json string deserialize to Jackson {@link JsonNode}.
     *
     * @param json json string
     * @return {@link JsonNode}
     * @throws RuntimeException if deserialize failed
     */
    public static JsonNode toObj(String json) {
        try {
            return defaultMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return defaultMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toJsonNode(byte[] json) {
        try {
            return defaultMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toJsonNode(InputStream json) {
        try {
            return defaultMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        defaultMapper.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(defaultMapper.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(defaultMapper.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return defaultMapper.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return defaultMapper.constructType(type);
    }

    /**
     * tree to value
     * 树转换为指定对象
     *
     * @param n         TreeNode
     * @param valueType valueType
     * @param <T>
     * @return T
     */
    public static <T> T treeToValue(TreeNode n, Class<T> valueType) {
        try {
            return defaultMapper.treeToValue(n, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T treeToValue(TreeNode n, TypeReference<T> toValueTypeRef) {
        try {
            return defaultMapper.treeToValue(n, toValueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T treeToValue(TreeNode n, Type type) {
        try {
            return defaultMapper.treeToValue(n, defaultMapper.constructType(type));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends TreeNode> T readTree(JsonParser p) {
        try {
            return defaultMapper.readTree(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(String p) {
        try {
            return defaultMapper.readTree(p);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
