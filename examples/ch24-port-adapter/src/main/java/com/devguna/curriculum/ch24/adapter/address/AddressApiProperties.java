package com.devguna.curriculum.ch24.adapter.address;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.address-api")
public record AddressApiProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}
