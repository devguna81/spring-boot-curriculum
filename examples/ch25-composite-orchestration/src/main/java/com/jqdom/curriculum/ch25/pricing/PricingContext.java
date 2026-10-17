package com.jqdom.curriculum.ch25.pricing;

/** 할인 규칙이 판단에 쓰는 주문 정보. 금액 단위는 원이다. */
public record PricingContext(Grade grade, long amount, boolean firstOrder) {
}
