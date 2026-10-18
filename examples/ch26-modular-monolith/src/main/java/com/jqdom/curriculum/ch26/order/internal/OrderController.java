package com.jqdom.curriculum.ch26.order.internal;

import com.jqdom.curriculum.ch26.order.api.OrderApi;
import com.jqdom.curriculum.ch26.order.api.OrderPlaced;
import com.jqdom.curriculum.ch26.order.api.UnknownMemberException;
import com.jqdom.curriculum.ch26.order.api.UnknownProductException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    record OrderRequest(@Min(1) long memberId, @jakarta.validation.constraints.NotBlank String sku,
                        @Min(1) int quantity) {
    }

    private final OrderApi orders;

    OrderController(OrderApi orders) {
        this.orders = orders;
    }

    @PostMapping
    ResponseEntity<OrderPlaced> place(@Valid @RequestBody OrderRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.place(r.memberId(), r.sku(), r.quantity()));
    }

    @ExceptionHandler({UnknownMemberException.class, UnknownProductException.class})
    ResponseEntity<Map<String, String>> notFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
