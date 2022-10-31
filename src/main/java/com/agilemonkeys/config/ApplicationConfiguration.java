package com.agilemonkeys.config;

import com.agilemonkeys.repository.CustomerRelationalRepository;
import com.agilemonkeys.repository.CustomerRepository;
import com.agilemonkeys.repository.FileStorageRepository;
import com.agilemonkeys.repository.impl.CustomerRepositoryImpl;
import com.agilemonkeys.repository.impl.GoogleCloudStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CustomerRepository customerRepository(CustomerRelationalRepository customerRelationalRepository, FileStorageRepository fileStorageRepository) {
        return new CustomerRepositoryImpl(customerRelationalRepository, fileStorageRepository);
    }

    @Bean
    public FileStorageRepository googleCloudStorage() throws Exception{
        return new GoogleCloudStorage( "crm-photos");
    }

    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        return objectMapper;
    }
}
