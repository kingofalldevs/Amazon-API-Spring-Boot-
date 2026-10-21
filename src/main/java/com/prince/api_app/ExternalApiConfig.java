package com.prince.api_app;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalApiConfig {

    @Bean
    RestClient weatherClient(RestClient.Builder builder,
                             @Value("${weather.api.base-url}") String baseUrl,
                             @Value("${weather.api.key:}") String apiKey,
                             @Value("${weather.api.connect-timeout}") Duration connectTimeout,
                             @Value("${weather.api.read-timeout}") Duration readTimeout) {

        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout);

        RestClient.Builder configured = builder
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings));

        if (!apiKey.isBlank()) {
            configured = configured.defaultHeader("Authorization", "Bearer " + apiKey);
        }

        return configured.build();
    }
}
