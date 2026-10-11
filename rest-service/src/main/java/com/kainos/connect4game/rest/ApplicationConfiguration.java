package com.kainos.connect4game.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public RestClient storageRestClient(RestClient.Builder restClientBuilder, @Value("${storage-service.endpoint}") String endpoint) {
        return restClientBuilder
                .baseUrl(endpoint)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
