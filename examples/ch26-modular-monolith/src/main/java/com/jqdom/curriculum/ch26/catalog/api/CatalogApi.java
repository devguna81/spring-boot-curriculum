package com.jqdom.curriculum.ch26.catalog.api;

import java.util.Optional;

public interface CatalogApi {

    Optional<Long> priceOf(String sku);
}
