package com.jqdom.curriculum.ch25.payment;

public interface PaymentService {

    /** 결제하고 결제 번호를 돌려준다. 거절되면 {@link PaymentDeclinedException}. */
    String charge(String customerId, long amount);

    /** 결제를 취소한다(보상). */
    void refund(String paymentId);
}
