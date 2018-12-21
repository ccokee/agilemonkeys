package com.agilemonkeys;

import com.agilemonkeys.repository.CustomerRepositoryImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CustomerRepositoryImpl customerRepository() {
        return new CustomerRepositoryImpl();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        return objectMapper;
    }
}
