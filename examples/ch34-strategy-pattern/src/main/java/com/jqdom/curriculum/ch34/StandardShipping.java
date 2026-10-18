package com.jqdom.curriculum.ch34;

import org.springframework.stereotype.Component;

/** 일반 배송: 기본 3,000원, 주문 금액이 50,000원 이상이면 무료. */
@Component
class StandardShipping implements ShippingFeeStrategy {

    static final long BASE_FEE = 3_000;
    static final long FREE_THRESHOLD = 50_000;

    @Override
    public String key() {
        return "standard";
    }

    @Override
    public long fee(ShippingRequest request) {
        return request.orderAmount() >= FREE_THRESHOLD ? 0 : BASE_FEE;
    }
}
