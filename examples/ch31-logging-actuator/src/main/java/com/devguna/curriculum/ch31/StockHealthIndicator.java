package com.devguna.curriculum.ch31;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/** 빈 이름 stockHealthIndicator에서 접미사를 뺀 "stock"이 /actuator/health 응답의 구성 요소 이름이 된다. */
@Component
class StockHealthIndicator implements HealthIndicator {

    private final StockSwitch stock;

    StockHealthIndicator(StockSwitch stock) {
        this.stock = stock;
    }

    @Override
    public Health health() {
        return stock.available()
                ? Health.up().withDetail("reason", "stock service reachable").build()
                : Health.down().withDetail("reason", "stock service unreachable").build();
    }
}
