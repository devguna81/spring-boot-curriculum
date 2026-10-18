package com.jqdom.curriculum.ch25;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jqdom.curriculum.ch25.inventory.InventoryService;
import com.jqdom.curriculum.ch25.inventory.OutOfStockException;
import com.jqdom.curriculum.ch25.order.OrderFailedException;
import com.jqdom.curriculum.ch25.order.OrderOrchestrator;
import com.jqdom.curriculum.ch25.order.OrderResult;
import com.jqdom.curriculum.ch25.order.PlaceOrderCommand;
import com.jqdom.curriculum.ch25.payment.PaymentDeclinedException;
import com.jqdom.curriculum.ch25.payment.PaymentService;
import com.jqdom.curriculum.ch25.pricing.Grade;
import com.jqdom.curriculum.ch25.shipping.ShippingService;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/** 서비스를 호출 순서를 기록하는 가짜로 바꿔, 흐름과 보상 순서를 검사한다. */
class OrderOrchestratorTests {

    private final List<String> calls = new ArrayList<>();
    private boolean outOfStock;
    private boolean declined;
    private boolean shippingFails;

    private final InventoryService inventory = new InventoryService() {
        @Override
        public void reserve(String sku, int quantity) {
            calls.add("reserve:" + sku + "x" + quantity);
            if (outOfStock) {
                throw new OutOfStockException(sku);
            }
        }

        @Override
        public void release(String sku, int quantity) {
            calls.add("release:" + sku + "x" + quantity);
        }
    };

    private final PaymentService payment = new PaymentService() {
        @Override
        public String charge(String customerId, long amount) {
            calls.add("charge:" + amount);
            if (declined) {
                throw new PaymentDeclinedException(amount);
            }
            return "PAY-1";
        }

        @Override
        public void refund(String paymentId) {
            calls.add("refund:" + paymentId);
        }
    };

    private final ShippingService shipping = (orderId, customerId) -> {
        calls.add("ship:" + orderId);
        if (shippingFails) {
            throw new IllegalStateException("택배사 장애");
        }
        return "TRK-" + orderId;
    };

    private final OrderOrchestrator orchestrator = new OrderOrchestrator(
            ctx -> 1_000, inventory, payment, shipping); // 할인은 항상 1,000원인 규칙으로 고정

    private static final PlaceOrderCommand CMD =
            new PlaceOrderCommand("c1", Grade.NORMAL, "BOOK", 2, 10_000, false);

    @Test
    void successRunsStepsInOrderAndReturnsAmounts() {
        OrderResult result = orchestrator.place(CMD);

        assertEquals(List.of("reserve:BOOKx2", "charge:19000", "ship:1"), calls);
        assertEquals(20_000, result.amount());
        assertEquals(1_000, result.discount());
        assertEquals(19_000, result.payable());
        assertEquals("TRK-1", result.trackingNo());
    }

    @Test
    void outOfStockStopsBeforePayment() {
        outOfStock = true;

        OrderFailedException e = assertThrows(OrderFailedException.class, () -> orchestrator.place(CMD));

        assertEquals("재고", e.step());
        assertEquals(List.of("reserve:BOOKx2"), calls);
    }

    @Test
    void declinedPaymentReleasesInventory() {
        declined = true;

        OrderFailedException e = assertThrows(OrderFailedException.class, () -> orchestrator.place(CMD));

        assertEquals("결제", e.step());
        assertEquals(List.of("reserve:BOOKx2", "charge:19000", "release:BOOKx2"), calls);
    }

    @Test
    void shippingFailureRefundsThenReleasesInReverseOrder() {
        shippingFails = true;

        OrderFailedException e = assertThrows(OrderFailedException.class, () -> orchestrator.place(CMD));

        assertEquals("배송", e.step());
        assertEquals(List.of("reserve:BOOKx2", "charge:19000", "ship:1", "refund:PAY-1", "release:BOOKx2"), calls);
    }
}
