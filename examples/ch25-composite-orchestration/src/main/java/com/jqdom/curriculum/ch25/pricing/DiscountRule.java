package com.jqdom.curriculum.ch25.pricing;

/**
 * 할인 규칙 하나. 단일 규칙(leaf)과 규칙 묶음(composite)이 같은 인터페이스를 구현하므로
 * 호출하는 쪽은 둘을 구별하지 않고 discount()만 부른다.
 */
public interface DiscountRule {

    long discount(PricingContext context);
}
