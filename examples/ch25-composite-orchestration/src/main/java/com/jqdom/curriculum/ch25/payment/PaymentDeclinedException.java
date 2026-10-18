package com.jqdom.curriculum.ch25.payment;

public class PaymentDeclinedException extends RuntimeException {

    public PaymentDeclinedException(long amount) {
        super("결제가 거절되었습니다: " + amount);
    }
}
