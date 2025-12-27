package com.gsclimbing.x.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração global do Jackson para serialização JSON
 *
 * ✅ Adiciona suporte para Java 8 Date/Time (LocalDateTime, LocalDate, etc)
 * ✅ Configura datas para serem serializadas como strings ISO-8601 em vez de timestamps
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // ✅ Registar módulo para suporte Java 8 Date/Time
        mapper.registerModule(new JavaTimeModule());

        // ✅ Serializar datas como strings (ISO-8601) em vez de timestamps numéricos
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
