package com.jqdom.curriculum.ch25.order;

import com.jqdom.curriculum.ch25.inventory.InventoryService;
import com.jqdom.curriculum.ch25.payment.PaymentService;
import com.jqdom.curriculum.ch25.pricing.DiscountRule;
import com.jqdom.curriculum.ch25.pricing.PricingContext;
import com.jqdom.curriculum.ch25.shipping.ShippingService;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 재고 -> 결제 -> 배송의 순서와 실패 시 되돌리는 순서를 한곳에서 정한다.
 * 각 서비스는 자기 일만 알고 서로를 부르지 않는다. 흐름은 오직 이 클래스에 있다.
 */
@Service
public class OrderOrchestrator {

    private final DiscountRule discountRule;
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;
    private final AtomicLong orderSequence = new AtomicLong();

    public OrderOrchestrator(DiscountRule discountRule, InventoryService inventory,
                             PaymentService payment, ShippingService shipping) {
        this.discountRule = discountRule;
        this.inventory = inventory;
        this.payment = payment;
        this.shipping = shipping;
    }

    public OrderResult place(PlaceOrderCommand cmd) {
        long amount = cmd.unitPrice() * cmd.quantity();
        long discount = discountRule.discount(new PricingContext(cmd.grade(), amount, cmd.firstOrder()));
        long payable = amount - discount;
        long orderId = orderSequence.incrementAndGet();

        try {
            inventory.reserve(cmd.sku(), cmd.quantity());
        } catch (RuntimeException e) {
            throw new OrderFailedException("재고", e);
        }

        String paymentId;
        try {
            paymentId = payment.charge(cmd.customerId(), payable);
        } catch (RuntimeException e) {
            inventory.release(cmd.sku(), cmd.quantity());
            throw new OrderFailedException("결제", e);
        }

        String trackingNo;
        try {
            trackingNo = shipping.schedule(orderId, cmd.customerId());
        } catch (RuntimeException e) {
            // 일어난 순서의 반대로 되돌린다: 결제 취소 -> 재고 반환.
            payment.refund(paymentId);
            inventory.release(cmd.sku(), cmd.quantity());
            throw new OrderFailedException("배송", e);
        }

        return new OrderResult(orderId, amount, discount, payable, paymentId, trackingNo);
    }
}
