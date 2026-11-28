package cool.et.framework.boot.base.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.et.framework.commons.JacksonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlatformJsonTimeTest {

    @Test
    void bothMappersUseTheSameLocalTimeFormats() throws Exception {
        try (var context = new AnnotationConfigApplicationContext(DefaultWebMvcConfiguration.class)) {
            for (ObjectMapper mapper : List.of(context.getBean(ObjectMapper.class), JacksonUtils.getObjectMapper())) {
                assertJsonRoundTrip(mapper, LocalDate.of(2026, 11, 28), "\"2026-11-28\"");
                assertJsonRoundTrip(mapper, LocalTime.of(13, 28, 22), "\"13:28:22\"");
                assertJsonRoundTrip(mapper, LocalDateTime.of(2026, 11, 28, 13, 28, 22), "\"2026-11-28 13:28:22\"");
            }
        }
    }

    @Test
    void webKeepsInstantPrecisionAndStorageReadsBothFormats() throws Exception {
        var localTime = LocalDateTime.of(2026, 11, 28, 13, 28, 22, 257_000_000);
        var instant = localTime.atZone(ZoneId.systemDefault()).toInstant();
        String isoJson = "\"" + instant + "\"";
        String storedJson = "\"2026-11-28 13:28:22\"";
        try (var context = new AnnotationConfigApplicationContext(DefaultWebMvcConfiguration.class)) {
            assertJsonRoundTrip(context.getBean(ObjectMapper.class), instant, isoJson);
        }

        var storage = JacksonUtils.getObjectMapper();
        assertEquals(storedJson, storage.writeValueAsString(instant));
        assertEquals(instant.truncatedTo(ChronoUnit.SECONDS), storage.readValue(storedJson, Instant.class));
        assertEquals(instant, storage.readValue(isoJson, Instant.class));
    }

    @Test
    void webDateFormatPropertyStillAppliesToDate() throws Exception {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                    "test", Map.of("spring.jackson.date-format", "dd/MM/yyyy HH:mm:ss")));
            context.register(DefaultWebMvcConfiguration.class);
            context.refresh();
            var localTime = LocalDateTime.of(2026, 11, 28, 13, 28, 22);
            var date = Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant());
            assertJsonRoundTrip(context.getBean(ObjectMapper.class), date, "\"28/11/2026 13:28:22\"");
        }
    }

    private static void assertJsonRoundTrip(ObjectMapper mapper, Object value, String json) throws Exception {
        assertEquals(json, mapper.writeValueAsString(value));
        assertEquals(value, mapper.readValue(json, value.getClass()));
    }
}
