package com.devguna.curriculum.ch24.adapter.address;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AddressApiProperties.class)
class AddressApiConfig {
}
