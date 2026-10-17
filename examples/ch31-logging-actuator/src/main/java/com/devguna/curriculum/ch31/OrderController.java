package com.devguna.curriculum.ch31;

import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final Counter created;
    private final Counter rejected;
    private final StockSwitch stock;

    OrderController(MeterRegistry registry, StockSwitch stock) {
        // 이름은 점으로 구분하고, 구분이 필요한 값은 태그가 아니라 이름이나 tag로 나눈다.
        this.created = Counter.builder("orders.placed").tag("result", "created").register(registry);
        this.rejected = Counter.builder("orders.placed").tag("result", "rejected").register(registry);
        this.stock = stock;
    }

    record OrderRequest(String item, int quantity) {
    }

    @PostMapping("/orders")
    ResponseEntity<Map<String, Object>> place(@RequestBody OrderRequest request) {
        log.debug("order requested item={} quantity={}", request.item(), request.quantity());
        if (!stock.available()) {
            rejected.increment();
            log.warn("order rejected: stock service unavailable item={}", request.item());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("code", "STOCK_UNAVAILABLE"));
        }
        created.increment();
        log.info("order created item={} quantity={}", request.item(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("item", request.item(), "quantity", request.quantity()));
    }
}
