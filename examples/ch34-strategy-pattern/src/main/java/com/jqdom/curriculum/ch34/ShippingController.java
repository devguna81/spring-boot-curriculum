package com.jqdom.curriculum.ch34;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
class ShippingController {

    private final ShippingFeeCalculator calculator;

    ShippingController(ShippingFeeCalculator calculator) {
        this.calculator = calculator;
    }

    /** GET /api/shipping/fee?method=express&orderAmount=30000&weightKg=5 */
    @GetMapping("/fee")
    FeeResponse fee(@RequestParam String method,
                    @RequestParam long orderAmount,
                    @RequestParam(defaultValue = "1") int weightKg) {
        long fee = calculator.calculate(method, new ShippingRequest(orderAmount, weightKg));
        return new FeeResponse(method, fee);
    }

    @GetMapping("/methods")
    List<String> methods() {
        return calculator.methods();
    }

    @ExceptionHandler({UnknownShippingMethodException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, String>> badRequest(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }

    record FeeResponse(String method, long fee) {
    }
}
