package com.jqdom.curriculum.ch34;

/** 배송비 계산에 필요한 값. 금액은 원 단위, 무게는 킬로그램. */
public record ShippingRequest(long orderAmount, int weightKg) {

    public ShippingRequest {
        if (orderAmount < 0 || weightKg < 0) {
            throw new IllegalArgumentException("금액과 무게는 0 이상이어야 합니다.");
        }
    }
}
