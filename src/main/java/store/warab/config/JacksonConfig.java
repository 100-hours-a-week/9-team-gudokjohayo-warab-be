package store.warab.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    JavaTimeModule javaTimeModule = new JavaTimeModule();

    // ISO-8601 형식의 날짜 시간을 LocalDate로 변환하는 커스텀 Deserializer 추가
    SimpleModule customModule = new SimpleModule();
    customModule.addDeserializer(
        LocalDate.class,
        new JsonDeserializer<LocalDate>() {
          @Override
          public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
              throws IOException {
            String dateStr = p.getText();
            try {
              // ISO-8601 형식의 날짜 시간 문자열인 경우 (e.g. "2025-04-14T06:21:50+00:00")
              if (dateStr.contains("T")) {
                // OffsetDateTime으로 파싱한 후 LocalDate만 추출
                return OffsetDateTime.parse(dateStr).toLocalDate();
              } else {
                // 일반 날짜 문자열인 경우 (e.g. "2025-04-14")
                return LocalDate.parse(dateStr);
              }
            } catch (DateTimeParseException e) {
              throw new IOException("날짜 형식을 파싱할 수 없습니다: " + dateStr, e);
            }
          }
        });

    return Jackson2ObjectMapperBuilder.json()
        .modules(javaTimeModule, customModule)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .build();
  }
}
