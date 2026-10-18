package com.jqdom.curriculum.ch25.pricing;

/** 첫 주문이면 2천 원을 깎는다. */
public class FirstOrderRule implements DiscountRule {

    @Override
    public long discount(PricingContext context) {
        return context.firstOrder() ? 2_000 : 0;
    }
}
