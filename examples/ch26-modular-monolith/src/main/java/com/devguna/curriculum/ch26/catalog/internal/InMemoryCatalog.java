package com.devguna.curriculum.ch26.catalog.internal;

import com.devguna.curriculum.ch26.catalog.api.CatalogApi;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
class InMemoryCatalog implements CatalogApi {

    private final Map<String, Long> prices = Map.of("BOOK", 15_000L, "PEN", 1_200L);

    @Override
    public Optional<Long> priceOf(String sku) {
        return Optional.ofNullable(prices.get(sku));
    }
}
