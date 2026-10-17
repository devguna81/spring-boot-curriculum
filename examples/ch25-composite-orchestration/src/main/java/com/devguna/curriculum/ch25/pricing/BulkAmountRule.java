package com.devguna.curriculum.ch25.pricing;

/** 5만 원 이상이면 3천 원을 깎는다. */
public class BulkAmountRule implements DiscountRule {

    @Override
    public long discount(PricingContext context) {
        return context.amount() >= 50_000 ? 3_000 : 0;
    }
}
