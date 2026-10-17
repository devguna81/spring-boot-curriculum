package com.jqdom.curriculum.ch25.web;

import com.jqdom.curriculum.ch25.order.OrderFailedException;
import com.jqdom.curriculum.ch25.order.OrderOrchestrator;
import com.jqdom.curriculum.ch25.order.OrderResult;
import com.jqdom.curriculum.ch25.order.PlaceOrderCommand;

import jakarta.validation.Valid;

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
public class OrderController {

    private final OrderOrchestrator orchestrator;

    public OrderController(OrderOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<OrderResult> place(@Valid @RequestBody OrderRequest r) {
        OrderResult result = orchestrator.place(new PlaceOrderCommand(
                r.customerId(), r.grade(), r.sku(), r.quantity(), r.unitPrice(), r.firstOrder()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /** 실패한 단계에 따라 상태 코드를 나눈다: 재고 409, 결제 402, 그 밖에는 500. */
    @ExceptionHandler(OrderFailedException.class)
    ResponseEntity<Map<String, String>> failed(OrderFailedException e) {
        HttpStatus status = switch (e.step()) {
            case "재고" -> HttpStatus.CONFLICT;
            case "결제" -> HttpStatus.PAYMENT_REQUIRED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(Map.of("step", e.step(), "message", e.getMessage()));
    }
}
