package com.devguna.curriculum.ch25.payment;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/** 한도(100만 원)를 넘는 결제는 거절하는 흉내용 구현. */
@Service
public class InMemoryPaymentService implements PaymentService {

    static final long LIMIT = 1_000_000;

    private final AtomicLong sequence = new AtomicLong();

    @Override
    public String charge(String customerId, long amount) {
        if (amount > LIMIT) {
            throw new PaymentDeclinedException(amount);
        }
        return "PAY-" + sequence.incrementAndGet();
    }

    @Override
    public void refund(String paymentId) {
        // 흉내용 구현이라 기록하지 않는다.
    }
}
